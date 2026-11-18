# The MediaFlow metamodel

Code for session 8 (Model-Driven Software Engineering) of IT30213 Advanced Software Engineering & DevOps. Sessions 1 to 7 built a text-to-speech system by writing its code and its infrastructure by hand. From here on the course works one level up: the domain is described once as a metamodel, and everything else is derived from it.

This session holds that metamodel. MediaFlow describes a media processing pipeline as a directed graph: resources are files going in and out, transformers scale or transcode what passes through them, ports are the connection points on each node, and edges carry media from one port to another. Sessions 9 to 13 reuse this metamodel without changing it, which is the point: a textual DSL, a visual editor, two model-to-model transformations, a model-to-text generator and a validator all sit on the same twelve classes.

## How the pieces fit together

```
metamodels/mediaflow.emf ──Emfatic──> metamodels/mediaflow.ecore ──┬──> metamodels/mediaflow.genmodel ──EMF codegen──> src/mediaflow/
                                                                   │
                                                                   └──EcoreTools──> representations.aird (class diagram)

models/flow1.model  }  instances that conform to the .ecore and are
models/flow2.model  }  loaded through the generated Java classes
```

| File | What it holds |
|---|---|
| `metamodels/mediaflow.emf` | The metamodel in Emfatic, the textual syntax for Ecore. This is the file to edit; it reviews well in git, unlike the XML. |
| `metamodels/mediaflow.ecore` | The same metamodel as Ecore XML, generated from the Emfatic file. Every later session resolves the namespace `mediaflow` against this file. |
| `metamodels/mediaflow.genmodel` | The code generation settings: output into `/mediaflow/src`, package prefix `Mediaflow`, compliance level 21. |
| `src/mediaflow/` | The Java API that EMF generates: interfaces, `impl` classes, the factory, the package and the `util` switch and adapter factory. |
| `models/flow1.model` | A linear pipeline: one input resource, one scaler, one output resource. |
| `models/flow2.model` | A branching pipeline: one scaler feeding an H.264 and a VP9 transcoder, each with its own output resource. |
| `representations.aird` | The EcoreTools class diagram of the metamodel, the source of the figure in chapter 8. |
| `mediaflow_class_diagram.svg` | That diagram exported for the book. |
| `plugin.xml` | Registers the generated `MediaflowPackage` under the namespace URI `mediaflow`, so other plug-ins can use the metamodel without a file path. |

The twelve classifiers are three enumerations (`MediaType`, `Backend`, `Direction`) and nine classes. `NamedElement` is the abstract parent that carries `name`. `Graph` contains `nodes` and `edges`. `Node` is abstract and splits into `Resource` and the abstract `Transformer`, which splits again into `Scaler` and `Transcoder`. `Port` has a `direction` and an `owner`; `Edge` references a source and a target `Port`. Separating the nodes from the flow between them is what makes the later validation and transformation rules short.

```
mediaflow/              the Eclipse plug-in project (import this one)
├── metamodels/         Emfatic source, Ecore, genmodel
├── models/             the two instance models
├── src/mediaflow/      generated Java
├── META-INF/           the OSGi manifest
└── representations.aird  the Sirius/EcoreTools diagram
```

A `mediaflow.backup` folder may sit next to it on your machine. It is ignored by git and is not part of the session.

## Before you start

- Eclipse with the modeling tools. This session was last checked with Eclipse Platform 4.39 (2026-03). The project declares `JavaSE-21` as its execution environment, so register a JDK 21 in **Preferences > Java > Installed JREs** if Eclipse reports a missing one.
- Three features from the Eclipse Modeling package: **EMF** (SDK), **Emfatic**, and **EcoreTools**. Without Emfatic the `.emf` file opens as plain text; without EcoreTools the `representations.aird` diagram will not open.
- For the command-line check in step 5, a JDK and the path to your Eclipse `plugins` folder. Nothing else: there is no Maven or Gradle build here, because the project is an Eclipse plug-in and resolves its dependencies through `META-INF/MANIFEST.MF`.

Sessions 9 to 13 each carry their own copy of this metamodel, so this folder is the only place to change it when you are following chapter 8.

## Step 1. Import the project

In Eclipse: **File > Import > Existing Projects into Workspace**, select `projects/session-08`, and import the `mediaflow` project. Import it as an existing project rather than copying it into the workspace, so your edits land in the repository.

The project has three natures: Java, PDE plug-in, and Sirius modeling. Eclipse needs all three, and they come from the `.project` file in the repository.

## Step 2. Read the metamodel in Emfatic

Open `metamodels/mediaflow.emf`. The whole domain is 68 lines:

```
class Graph extends NamedElement {
  val Node[*] nodes;
  val Edge[*] edges;
}

abstract class Node extends NamedElement {
  val Port[*] ports;
}

class Resource extends Node {
  attr MediaType mediaType;
  attr String uri;
  attr boolean external;
}
```

`val` is containment and `ref` is a plain reference. `Graph` owns its nodes and edges, a `Node` owns its ports, but `Port.owner` and `Edge.source`/`Edge.target` only point at elements owned elsewhere. That distinction decides what gets copied when a model is transformed, and it is why the edges in the XMI carry identifiers instead of nested elements.

## Step 3. Regenerate the Ecore and the Java code

Both generated artefacts are already in the repository, so this step is only needed after you change the Emfatic file.

Right-click `metamodels/mediaflow.emf` and choose **Generate Ecore Model**. Emfatic overwrites `mediaflow.ecore`.

Then open `metamodels/mediaflow.genmodel`, right-click the root **Mediaflow** node, and choose **Generate Model Code**. EMF writes into `src/mediaflow/` as the genmodel's `modelDirectory` says. The genmodel keeps `mediaflow.ecore` as its `foreignModel`, so reload it from the Ecore file first if you added or removed a class:

right-click the root node, **Reload...**, then pick **Ecore model** and the same `.ecore` file.

## Step 4. Open the models

This project ships the model code only, not the `.edit` and `.editor` plug-ins, so there is no MediaFlow-specific editor yet. Right-click `models/flow1.model` and choose **Open With > Sample Reflective Ecore Model Editor**, which builds its tree from `mediaflow.ecore` directly. To get the generated editor instead, open the genmodel and run **Generate Edit Code** and **Generate Editor Code**; EMF creates the `mediaflow.edit` and `mediaflow.editor` projects beside this one, which is how sessions 9 and 11 are set up.

Either way the tree shows a `Graph` root named `flow1`:

```
Graph flow1
├── Resource input_video      port input_video_out (OUT)
├── Resource output_video     port output_video_in (IN)
├── Scaler scaler_720         ports scaler_720_video_in (IN), scaler_720_video_out (OUT)
├── Edge edge_input_video_to_scaler_720
└── Edge edge_scaler_720_to_output_video
```

Three nodes, four ports, two edges. `flow2.model` has six nodes, ten ports and five edges, with the scaler fanning out to two transcoders.

Select an element and use the **Properties** view to see its attributes. On `scaler_720` the command is `scale=1080:720`, with `width` 1080 and `height` 720 to match. On an `Edge`, `source` and `target` offer only ports, because the metamodel types them as `Port`.

To add an element: right-click the `Graph` root, **New Child**, and pick the class. Edges are created the same way, then wired by picking a source and target port in the Properties view. **Validate** on the context menu runs EMF's own structural checks; the domain rules that catch a reversed edge or a missing URI arrive in session 13.

## Step 5. Check the generated code outside Eclipse

The generated Java compiles against three EMF bundles and nothing else, which is worth confirming after a regeneration:

```bash
cd projects/session-08/mediaflow
PLUGINS=/data1/programs/eclipse/plugins        # adjust to your install
javac -d bin \
  -cp "$(ls $PLUGINS/org.eclipse.emf.{common,ecore,ecore.xmi}_*.jar | tr '\n' ':')" \
  $(find src -name '*.java')
```

A silent run means the metamodel, the genmodel and the generated code still agree. It writes 29 class files into `bin/`, which `.gitignore` already keeps out of the repository. Sessions 12 and 13 use the same `PLUGINS` variable for their runners, so setting it once in your shell profile saves repeating it.

## Step 6. Open the class diagram

Double-click `representations.aird`. Under **mediaflow class diagram** is the EcoreTools diagram of the metamodel itself: the inheritance from `NamedElement`, the containment from `Graph` into `nodes` and `edges`, and the references on `Port.owner`, `Edge.source` and `Edge.target`. It is the figure printed in chapter 8, exported to `mediaflow_class_diagram.svg`.

This diagram comes from the EcoreTools viewpoints, not from a hand-written Sirius definition. Building a diagram editor for the MediaFlow models themselves is session 10.

## Changing the settings

| What to change | Where |
|---|---|
| A class, attribute, enumeration or relation | `metamodels/mediaflow.emf`, then regenerate as in step 3 |
| Where the Java lands, or the package prefix | `metamodels/mediaflow.genmodel` |
| The namespace URI | the `@namespace` annotation at the top of the `.emf` file, then the `uri` in `plugin.xml`, then every later session that resolves `mediaflow` |
| The example pipelines | `models/flow1.model` and `models/flow2.model`, through the tree editor |
| Which folders ship in the built plug-in | `build.properties` |

Changing the metamodel breaks the sessions that depend on it. The grammars in session 9, the Sirius mappings in session 10, the ATL and ETL rules in session 11, the EGL templates in session 12 and the EVL constraints in session 13 all name these classes and attributes directly.

## Troubleshooting

| Problem | What to do |
|---|---|
| `metamodels/mediaflow.emf` opens without syntax colouring, and **Generate Ecore Model** is missing | The Emfatic feature is not installed. Add it from the Eclipse Modeling tools, then reopen the file. |
| `representations.aird` opens empty, or Eclipse asks for a viewpoint | EcoreTools is not installed. The diagram uses the viewpoints in `org.eclipse.emf.ecoretools.design`. |
| The project shows a red marker on `META-INF/MANIFEST.MF` about the execution environment | No JDK 21 is registered. Add one under **Preferences > Java > Installed JREs**, or lower `Bundle-RequiredExecutionEnvironment` to the JDK you have. |
| `models/flow1.model` opens as raw XML | Eclipse is using the plain XML editor. Right-click, **Open With > Sample Reflective Ecore Model Editor**, or generate the editor plug-ins as in step 4. |
| A model refuses to load with "package not registered" | The namespace URI is the bare name `mediaflow`, not a URL. Either run inside Eclipse with this plug-in in the workspace, or load the `.ecore` first and register its `EPackage`, which is what the runners in sessions 11 to 13 do. |
| **Generate Model Code** writes nothing | The genmodel's `modelDirectory` is `/mediaflow/src`, an absolute workspace path. It only resolves if the project is named `mediaflow` in the workspace. |
| `javac` in step 5 reports `cannot find symbol` on EMF classes | The `PLUGINS` path is wrong, or your Eclipse ships the EMF bundles under different version suffixes. Check with `ls $PLUGINS/org.eclipse.emf.ecore_*.jar`. |
