# MediaFlow viewer generated with Epsilon EGX and EGL

Code for session 12 (Model-to-Text Transformation) of IT30213 Advanced Software Engineering & DevOps. It turns MediaFlow models into text files that another program reads. For every pipeline graph, an EGX script runs two EGL templates: one writes the graph as JSON for the layout library elk.js, the other writes an HTML fragment with statistics and tables. A small web page in `m2t/output/` reads both files and draws the pipeline in the browser. The models are the XMI files of the earlier sessions. Session 11 turned the same models into UML activity diagrams, which is a model-to-model transformation. Here the result is text.

## How the pieces fit together

```
input/flow1.xmi, input/flow2.xmi
        |
        v
MediaflowToHtmlEGXRunner --loads each model with--> ../mediaflow/metamodels/mediaflow.ecore
        |
        v
transformer/m2t.egx --Graph2ElkJson--> elk.egl ----> output/elk/<graph name>.json
                    --Graph2Tables---> tables.egl --> output/tables/<graph name>.tables.html
                    (both templates import helpers.eol)

browser --HTTP--> output/index.html, js/app.js, css/style.css
js/app.js --fetch--> manifest.json, elk/<name>.json, tables/<name>.tables.html
js/app.js --ELK.layout()--> node positions --> SVG drawing on the left, fragment in the panel on the right
```

The runner reads every `*.xmi` file in `input/` in name order and loads it as an EMF model named `Mediaflow`. EGX then runs both of its rules once for each `Graph` in the model. The output files are named after the `name` attribute of the graph, `flow1` and `flow2`, so every model needs a name of its own.

| Part | What it does |
|---|---|
| `MediaflowToHtmlEGXRunner.java` | Finds the `*.xmi` files in `input/`, loads each one with the metamodel `../mediaflow/metamodels/mediaflow.ecore` and runs `m2t.egx` on it. It only reads the models and never saves them. |
| `m2t.egx` | Two rules for every `Graph`. `Graph2ElkJson` runs `elk.egl` and writes `output/elk/<name>.json`. `Graph2Tables` runs `tables.egl` and writes `output/tables/<name>.tables.html`. |
| `elk.egl` | Writes the JSON. Its `elk` part is the graph in the form `ELK.layout()` expects: children, ports, edges and layout options. `nodeMeta` and `edgeMeta` hold the values the tooltips show. Nodes get the ids `n0`, `n1`, …, ports `n0_p0`, `n1_p0`, …, and edges `e0`, `e1`, …. |
| `tables.egl` | Writes an HTML fragment: one `<section>`, not a whole page, with counts, node types, entry and exit nodes, a node table and an edge table. |
| `helpers.eol` | Operations that both templates import: `jsonEscape()` and `jsonValue()` for JSON text, `kindLabel()`, `isOut()`, and `sourceNode()` and `targetNode()`, which find the node that owns the port at each end of an edge. |
| `output/index.html`, `js/app.js`, `css/style.css`, `manifest.json` | The viewer. These files are written by hand, and the transformation does not change them. `manifest.json` lists the flows of the drop-down. |

The two models:

| Model | Nodes | Ports | Edges | Pipeline |
|---|---|---|---|---|
| `flow1.xmi` | 3 | 4 | 2 | A straight line: input video, a scaler to 1280x720, output video. |
| `flow2.xmi` | 6 | 10 | 5 | A branch: the scaled video goes to an H.264/MP4 transcoder and a VP9/WebM transcoder, and each writes its own output video. |

```
m2t/                                    the M2T project, an Eclipse plug-in project
  src/m2t/MediaflowToHtmlEGXRunner.java the Java runner
  transformer/m2t.egx                   the two EGX rules
  transformer/elk.egl, tables.egl       the two EGL templates
  transformer/helpers.eol               the operations both templates import
  input/flow1.xmi, flow2.xmi            the models the runner reads
  input/flow1.mediaflow, flow2.mediaflow  the same models in the textual MediaFlow syntax, to read
  output/index.html, js/, css/          the viewer
  output/manifest.json                  the flows the viewer offers
  output/elk/, output/tables/           the generated files, also kept in Git
  run-m2t.sh                            compiles and runs the runner outside Eclipse
  *.launch                              Eclipse launch configurations (step 6)
  META-INF/, build.properties, pom.xml  plug-in and Maven settings for Eclipse (PDE and m2e)
  representations.aird                  a Sirius session file, not used in this guide
mediaflow/                              the metamodel project: metamodels/mediaflow.ecore and the Java code EMF generated from it
```

## Before you start

- A JDK, version 21 or newer. `javac -version` should print a version number. This guide was tested with OpenJDK 27.0.1.
- An Eclipse installation with EMF and Epsilon 2.9. The script in step 3 does not start Eclipse. It only takes the jars from the `plugins` folder of the installation. The Eclipse Modeling Tools package contains EMF. Add Epsilon with Help > Install New Software and the update site `https://download.eclipse.org/epsilon/updates/`. Steps 2 to 5 were tested with the plugins of Eclipse 4.39 (2026-03): Epsilon 2.9.0 and EMF 2.42.
- Python 3 for a small web server in step 5. `python3 --version` should print a version number. Any other static web server works too.
- A browser with internet access. The viewer loads elk.js 0.9.3 from `cdn.jsdelivr.net`.

## Step 1. Go to the project folder

```bash
cd projects/session-12/m2t
```

Run every command in this guide from this folder. The runner looks for `input/`, `transformer/` and `../mediaflow/` relative to it.

## Step 2. Tell the script where Eclipse is

`run-m2t.sh` puts the Epsilon and EMF jars from the `plugins` folder of your Eclipse on the classpath. Set `ECLIPSE_HOME` to the folder that contains `plugins`, then check that Epsilon is there:

```bash
export ECLIPSE_HOME=$HOME/eclipse/modeling-2026-03/eclipse
ls "$ECLIPSE_HOME"/plugins/org.eclipse.epsilon.egl.engine_*.jar
```

Use the path of your own installation. On macOS it ends in `Eclipse.app/Contents/Eclipse`. `ls` should print one or more paths that end in `.jar`. An Eclipse that was updated can hold several versions of a plugin, and the script takes the newest. `export` lasts until you close the terminal, so repeat it in every new terminal.

## Step 3. Generate the JSON and HTML files

```bash
./run-m2t.sh
```

```
Generated output/elk/flow1.json and output/tables/flow1.tables.html
Generated output/elk/flow2.json and output/tables/flow2.tables.html
```

The script compiles the runner into `target/classes`, which Git ignores, and then runs it. The whole run takes a second or two. For each model, both EGX rules write their file. The rules name their targets `../output/elk/...` and `../output/tables/...`, because the runner sets the output root to the folder of `m2t.egx`, which is `transformer/`.

## Step 4. Check the generated files

Each model gives one JSON file and one HTML fragment:

```bash
ls output/elk output/tables
```

```
output/elk:
flow1.json  flow2.json

output/tables:
flow1.tables.html  flow2.tables.html
```

Check that the JSON parses and that each graph has the size of its model:

```bash
python3 -c 'import json, sys
for path in sys.argv[1:]:
    elk = json.load(open(path))["elk"]
    ports = sum(len(node["ports"]) for node in elk["children"])
    print(path, len(elk["children"]), "nodes,", ports, "ports,", len(elk["edges"]), "edges")' output/elk/*.json
```

```
output/elk/flow1.json 3 nodes, 4 ports, 2 edges
output/elk/flow2.json 6 nodes, 10 ports, 5 edges
```

These are the numbers in the table of models above. Open `output/elk/flow1.json` and look at two details:

```
    "edges": [
    {"id":"e0","sources":["n0_p0"],"targets":["n1_p0"]},
        {"id":"e1","sources":["n1_p1"],"targets":["n2_p0"]}
    ]
...
        "n1":{"name":"resize720","type":"Scaler","backend":"FFMPEG","replicas":1,"command":"ffmpeg -i input.mp4 -vf scale=1280:720 \n      scaled.mp4","width":1280,"height":720},
```

- An edge points at ports, not at nodes, because that is what the metamodel stores. ELK draws the arrow from the port, and the tooltip finds the node through `sourceNode()` and `targetNode()`.
- The command of the scaler contains a line break, written `&#xA;` in `flow1.xmi`. `jsonEscape()` turns it into `\n`. Without it, the JSON would hold a raw line break inside a string and would not parse.

The generated files are in Git as well. The same models and templates always give the same text, so `git status --short output` prints nothing after a run. After you change a template or a model, it lists the files that changed.

## Step 5. Open the viewer

The viewer loads its files with `fetch()`. Browsers block that for a page opened from disk (`file://`), so serve the `output` folder over HTTP:

```bash
python3 -m http.server 8012 --bind 127.0.0.1 --directory output
```

```
Serving HTTP on 127.0.0.1 port 8012 (http://127.0.0.1:8012/) ...
```

Open http://127.0.0.1:8012/ in a browser on the same machine.

| What | What to check |
|---|---|
| The page | The title "Mediaflow Viewer", a drop-down `flow:` with `flow1` and `flow2`, and the text "rendered with elk.js". `flow1` is selected. |
| The graph, on the left | Three boxes from left to right, `inputVideo` (Resource), `resize720` (Scaler) and `outputVideo` (Resource), joined by two arrows. The dots on the boxes are the ports: inputs on the left side of a box, outputs on the right. |
| The tooltips | Hover over `resize720`. The tooltip lists `backend: FFMPEG`, `replicas: 1`, the command, `width: 1280` and `height: 720`. Hover over an arrow to see the edge name and its two ends as `node:port`. |
| The panel, on the right | Overview: Nodes 3, Edges 2, Ports 4, IN 2, OUT 2. Node Types: Resource 2, external 2, Scaler 1, Transcoder 0. Topology: Entry 1, Exit 1, Avg degree 1.3333334. Below them the Nodes table (`n0` to `n2`) and the Edges table (`e0` and `e1`). |
| `flow2` | Choose `flow2` in the drop-down. The scaler now splits into `encodeH264` and `encodeVp9`, which lead to `outputVideo1` and `outputVideo2`. The panel shows Nodes 6, Edges 5, Ports 10, Transcoder 2, Exit 2 and Avg degree 1.6666666. |

The server prints one line for every file the page asks for:

```
127.0.0.1 - - [19/Nov/2026 15:48:08] "GET / HTTP/1.1" 200 -
127.0.0.1 - - [19/Nov/2026 15:48:08] "GET /css/style.css HTTP/1.1" 200 -
127.0.0.1 - - [19/Nov/2026 15:48:08] "GET /js/app.js HTTP/1.1" 200 -
127.0.0.1 - - [19/Nov/2026 15:48:09] "GET /manifest.json HTTP/1.1" 200 -
127.0.0.1 - - [19/Nov/2026 15:48:09] "GET /elk/flow1.json HTTP/1.1" 200 -
127.0.0.1 - - [19/Nov/2026 15:48:09] code 404, message File not found
127.0.0.1 - - [19/Nov/2026 15:48:09] "GET /favicon.ico HTTP/1.1" 404 -
127.0.0.1 - - [19/Nov/2026 15:48:09] "GET /tables/flow1.tables.html HTTP/1.1" 200 -
127.0.0.1 - - [19/Nov/2026 15:48:17] "GET /elk/flow2.json HTTP/1.1" 200 -
127.0.0.1 - - [19/Nov/2026 15:48:17] "GET /tables/flow2.tables.html HTTP/1.1" 200 -
```

That is the order of `js/app.js`: the manifest first, then the JSON and the fragment of the selected flow. The `404` for `/favicon.ico` is harmless, because the viewer has no icon. Press Ctrl+C to stop the server.

## Step 6 (optional). Run the transformation in Eclipse

1. In Eclipse, choose File > Import > General > Existing Projects into Workspace. Select `projects/session-12` as the root directory, tick `mediaflow` and `m2t`, and leave "Copy projects into workspace" unticked. The runner reaches the metamodel through `../mediaflow`, so both projects have to stay side by side.
2. Right-click `MediaflowToHtmlEGXRunner.launch` in the `m2t` project and choose Run As > MediaflowToHtmlEGXRunner. The Console shows the two `Generated ...` lines of step 3. Press F5 on the `output` folder to see the new files.
3. `m2t_flow1.launch` and `m2t_flow2.launch` run `m2t.egx` through Epsilon's own launcher, on one model each. They look the metamodel up by its namespace URI, `mediaflow`, so register it first: right-click `mediaflow/metamodels/mediaflow.ecore` and choose Register EPackages.

The projects are set to Java 25 (`JavaSE-25` in `META-INF/MANIFEST.MF` and `.classpath`). If Eclipse reports that no JRE matches `JavaSE-25`, add a JDK 25 or newer under Window > Preferences > Java > Installed JREs.

Sessions 8 to 13 each have a project named `mediaflow`, and a workspace holds only one project of a name. Use a separate workspace for each session.

## Changing the settings

The runner takes four optional arguments, in this order:

| Argument | Default | Meaning |
|---|---|---|
| input folder | `input` | The runner reads every `*.xmi` file in it, sorted by name. |
| output folder | `output` | The runner only creates this folder and prints its name. Where the files go is decided by the `target` lines of the EGX rules, relative to the folder of the EGX file. To write somewhere else, change those lines. |
| EGX file | `transformer/m2t.egx` | The rules to run. |
| metamodel | `../mediaflow/metamodels/mediaflow.ecore` | The metamodel, loaded from the file, so it does not have to be registered. |

For example, `./run-m2t.sh input output transformer/m2t.egx ../mediaflow/metamodels/mediaflow.ecore` is the same as `./run-m2t.sh`.

- **Add a model.** Copy an XMI file into `input/` and give its `mediaflow:Graph` a `name` that no other model uses. Add the name to `output/manifest.json`, which the viewer reads for its drop-down, and run step 3 again. Two models with the same name write the same two files, and the second overwrites the first.
- **Change the layout.** `layoutOptions` in `elk.egl` is the only place to change. For example, `"elk.direction": "DOWN"` draws the pipeline from top to bottom.
- **Add a statistic or a column.** Change `tables.egl`, and put any new operation in `helpers.eol` (see the next section).
- **Add an artefact.** Add a rule to `m2t.egx` with a new template, for example one that writes an `ffmpeg` command for every transformer. The runner needs no change.

## Pitfalls in EGL and EOL

These cost time when the templates were written. The first three were checked again with Epsilon 2.9.

| Pitfall | What happens | What to do |
|---|---|---|
| An `operation` declared inside the `[% %]` block of an EGL template, after the statements | The template writes an empty file and reports no error. | Put operations in an `.eol` file and import it with `import "helpers.eol";`, as both templates do. |
| An `operation` declared at the top of the `[% %]` block, before the statements | The run stops with a parse error that says `class org.eclipse.epsilon.eol.dom.ForStatement cannot be cast to class org.eclipse.epsilon.eol.dom.Expression`. | The same. |
| `String.replace(a, b)` in EOL | It treats `a` as a regular expression. `.replace("\\", "\\\\")` stops with `Unescaped trailing backslash`. | `jsonEscape()` walks through the string one character at a time instead. |
| Two models with the same `graph.name` | The second overwrites the files of the first. | Give every model its own name. Session 13 checks names before a transformation runs. |
| Names that contain `<` or `&` | `tables.egl` writes names into HTML without escaping them, which is fine for these course models only. | For models from other people, add an `htmlEscape()` operation to `helpers.eol`. |

## Troubleshooting

| Problem | What to do |
|---|---|
| `ECLIPSE_HOME is not set` | Do step 2 in this terminal. |
| `.../plugins does not exist` | `ECLIPSE_HOME` points to the wrong folder. It must be the folder that contains `plugins`. |
| `... has no org.eclipse.epsilon.egl.engine jar` | Epsilon is not installed in that Eclipse. Install it as described in "Before you start", or point `ECLIPSE_HOME` to an Eclipse that has it. |
| `javac: command not found` | Only a Java runtime is installed. Install a JDK. |
| `NoClassDefFoundError: org/apache/commons/collections/list/UnmodifiableList`, `com/google/common/collect/TreeBasedTable` or `org/antlr/runtime/TokenSource` | A jar is missing from a classpath you built yourself. Epsilon needs Apache Commons Collections, Guava and ANTLR 3 next to its own jars. `run-m2t.sh` adds all three. |
| `No .xmi inputs found in input` | You are not in the `m2t` folder, or `input/` holds no XMI file. Go back to step 1. |
| `Parse error(s) in file ...` or `EGX parse failed` | A template or the EGX file has a syntax error. The message names the file and the line. See also the pitfalls above. |
| The page shows only its header, and the drop-down is empty | Either the page was opened from disk, and the browser console says `has been blocked by CORS policy`, or elk.js did not load, and the console says `Uncaught ReferenceError: ELK is not defined`. Open the page through the server of step 5, on a machine that can reach `cdn.jsdelivr.net`. |
| A new model is missing from the drop-down | Add its graph name to `output/manifest.json` and reload the page. |
| `OSError: [Errno 98] Address already in use` when the server starts | Another program uses port 8012. Start the server on another port, for example 8013, and open that port instead. |
