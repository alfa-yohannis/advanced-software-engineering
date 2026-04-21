# m2t — Mediaflow → HTML Viewer (Epsilon EGX/EGL)

Session 12 of the Advanced Software Engineering course. This project
demonstrates **Model-to-Text (M2T)** transformation with
[Epsilon EGX/EGL](https://www.eclipse.org/epsilon/), taking a
`mediaflow` XMI instance and producing a self-contained web viewer that
renders the graph with [elk.js](https://github.com/kieler/elkjs) and
shows statistics / node+edge tables.

The prior session (`session-11/m2m`) covered Model-to-Model with ETL and
ATL on the same `mediaflow` metamodel. This session reuses that input and
adds a textual/presentational output pipeline.

## Directory layout

```
m2t/
├── src/m2t/
│   └── MediaflowToHtmlEGXRunner.java   # Java driver for EGX + index generation
├── transformer/
│   ├── m2t.egx                         # Coordinator: two rules per Graph
│   ├── elk.egl                         # Rule 1 template → <graph.name>.json
│   ├── tables.egl                      # Rule 2 template → <graph.name>.tables.html
│   └── helpers.eol                     # Shared operations imported by both EGLs
├── input/                              # Input XMI models (mediaflow instances)
│   ├── flow1.xmi
│   └── flow2.xmi
├── output/                             # Generated artefacts (gitignored)
│   ├── flow1.json          flow2.json          # ELK layout input + metadata (named after graph.name)
│   ├── flow1.tables.html   flow2.tables.html   # stats + node/edge tables
│   └── index.html                      # single-file viewer with flow switcher
├── META-INF/MANIFEST.MF                # PDE bundle dependencies (Epsilon, EMF)
├── pom.xml                             # Maven sourceDirectory only (PDE handles deps)
└── .classpath / .project               # Eclipse PDE metadata
```

The `mediaflow` metamodel lives at `../mediaflow/metamodels/mediaflow.ecore`
and is shared across sessions 9–14. Do **not** vendor a copy into this
project — always resolve via that relative path.

## Mediaflow metamodel (quick reference)

```
Graph { nodes: Node[], edges: Edge[] }
Node  (abstract) { name, ports: Port[] }
├── Resource   { mediaType, uri, external }
└── Transformer (abstract) { backend, command, replicas }
    ├── Scaler     { width, height }
    └── Transcoder { videoCodec, audioCodec, container, bitrate }
Port  { name, direction: {IN, OUT} }
Edge  { name, source: Port, target: Port }
```

## How the transformation is wired

### `m2t.egx` — two per-graph rules

1. **`Graph2ElkJson`** → `<graph.name>.json`
   The JSON contains three top-level keys consumed by the viewer:
   - `elk` — ready-to-feed input for `ELK.layout()` (children + edges
     with ports and layout options)
   - `nodeMeta` — per-node metadata (type, uri, command, codec…) used
     for tooltips
   - `edgeMeta` — per-edge metadata (name, source/target node + port)
2. **`Graph2Tables`** → `<graph.name>.tables.html`
   A self-contained HTML **fragment** (not a full document) containing
   the stats panel (nodes/edges/ports/types/topology) and the node &
   edge tables. Designed to be injected into the index page.

Both rules receive `graph` as a parameter. Output files are named after
`graph.name` (the model-level name), so each model must have a unique
name to avoid collisions.

### `helpers.eol` — shared operations

Imported by both EGL templates via `import "helpers.eol";`. Provides:

- `jsonEscape()` / `jsonValue()` — JSON-safe string formatting.
  Implemented with a `while`-loop over characters because Epsilon's
  `String.replace(a, b)` routes to Java's `replaceAll` (regex),
  making literal replacement of `\\` and `"` awkward.
- `kindLabel()` — returns `"Resource" | "Scaler" | "Transcoder"`.
- `isOut()` — true if a `Port` has direction OUT.
- `sourceNode()` / `targetNode()` — resolve an `Edge` to its owning
  node via port containment (the metamodel only stores `Port` refs).

### `MediaflowToHtmlEGXRunner.java`

1. Enumerates `input/*.xmi`, sorted.
2. For each file: loads it as an `EmfModel` (name = `Mediaflow`,
   metamodel = `../mediaflow/metamodels/mediaflow.ecore`), extracts
   `graph.name` from the model root, constructs an `EgxModule` with an
   `EglFileGeneratingTemplateFactory` whose output root is `output/`,
   and executes the EGX. This produces `<graph.name>.json` and
   `<graph.name>.tables.html`.
3. After all flows: reads every `.json` and `.tables.html`, inlines them
   into `index.html` as `<script type="application/json">` /
   `<script type="text/html">` blocks, and writes it to `output/`.

### Why `index.html` inlines the data

The viewer has to work when opened directly from disk (`file://`).
`fetch()` against sibling `.json` files is blocked by most browsers
under `file://`. Inlining via typed `<script>` tags sidesteps that while
keeping the per-flow `.json` / `.tables.html` available as standalone
artefacts.

## Running

The project is configured as an Eclipse PDE plug-in: the runner resolves
Epsilon, EMF, and Apache Commons Collections through `MANIFEST.MF`'s
`Require-Bundle` entries against the workspace's target platform. In
Eclipse, just run `MediaflowToHtmlEGXRunner` as a Java Application with
the working directory set to the project root.

From a shell (bypasses PDE, requires the Eclipse plugins directory):

```bash
cd projects/session-12/m2t
PLUGINS=/data1/programs/eclipse/plugins        # adjust to your install
CP="target/classes:$(ls \
  $PLUGINS/org.eclipse.epsilon.{common,eol.engine,erl.engine,egl.engine,emc.emf}_2.9.0*.jar \
  $PLUGINS/org.eclipse.emf.{common,ecore,ecore.xmi}_*.jar \
  $PLUGINS/org.antlr.runtime_*.jar \
  $PLUGINS/org.apache.commons.collections_*.jar \
  $PLUGINS/com.google.guava_*.jar | tr '\n' ':')"

javac -d target/classes -cp "$CP" src/m2t/MediaflowToHtmlEGXRunner.java
java  -cp "$CP" m2t.MediaflowToHtmlEGXRunner
# open output/index.html in a browser
```

CLI arguments (all optional):
`inputDir outputDir egxPath metamodelPath` — defaults:
`input output transformer/m2t.egx ../mediaflow/metamodels/mediaflow.ecore`.

## Gotchas learned the hard way

These are sharp edges future work is likely to hit — they saved ~an hour
of debugging each.

1. **Operation declarations inside an EGL `[% %]` block silently
   suppress all template output.** No parse problem is reported;
   `template.process()` just returns `""`. Put operations in a
   separate `.eol` file and import it with `import "x.eol";`.
   This is why `helpers.eol` exists.
2. **Operations declared at the very top of an EGL's dynamic section,
   before any main statements, yield a classcast `ForStatement cannot
   be cast to Expression` at execute time.** If you inline operations
   for some reason, put them *after* the main body. `import` avoids
   both bugs at once, so prefer it.
3. **Epsilon's `String.replace(a, b)` is regex-based** (it calls
   Java's `String.replaceAll`, not `String.replace(CharSequence, …)`).
   Escaping `\` or `"` via `.replace("\\", "\\\\")` throws
   `PatternSyntaxException: Unescaped trailing backslash`. The safe
   approach is a `while` loop with `substring(i, i+1)` comparisons
   (see `jsonEscape` in `helpers.eol`).
4. **Each XMI input must have a unique `graph.name`** since output
   files are named after the model-level name. If two inputs share the
   same name, the second will overwrite the first.
5. **`EmfModel` wraps instance collections in `UnmodifiableList`** from
   `org.apache.commons.collections`. Running outside Eclipse requires
   `org.apache.commons.collections_*.jar` on the classpath; otherwise
   `EGX` execution fails with `NoClassDefFoundError` once it touches
   `getAllOfType`.
6. `EglFileGeneratingTemplateFactory.setOutputRoot(...)` is set on the
   factory, *before* you hand it to `new EgxModule(factory)`. Targets
   in EGX (`target : baseName + ".json"`) are resolved relative to
   that root.

## Extending

- **More statistics.** Add them to `tables.egl` and they show up in the
  right-hand panel automatically. Keep operations out of the EGL — put
  them in `helpers.eol`.
- **Richer metadata.** Extend the per-type branches in `elk.egl` (the
  `attrs.add(...)` lines). Everything in `nodeMeta[id]` besides `name`
  and `type` is rendered as a tooltip line on hover.
- **A different layout.** `elk.layoutOptions` in `elk.egl` is the only
  place you need to touch to switch algorithms (e.g. `"elk.algorithm":
  "mrtree"` or `"force"`).
- **New rule (e.g. a Graphviz `.dot` exporter).** Add another rule to
  `m2t.egx` pointing to a new `*.egl`, and drop the generated artefact
  into `output/`. No Java changes required.

## Related sessions

- `../mediaflow/` — metamodel, plugin, sample instances.
- `../session-09/` — hand-written Mediaflow DSL (Xtext).
- `../session-11/m2m/` — ETL and ATL M2M transformations that consume
  the same XMI inputs and produce UML Activity diagrams.
- `../session-14/` — downstream usage of the generated artefacts.
