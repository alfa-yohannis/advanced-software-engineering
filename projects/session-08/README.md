# The MediaFlow metamodel and its first models

Code for session 8 (Model-Driven Software Engineering) of IT30213 Advanced Software Engineering & DevOps. It holds the MediaFlow metamodel, the Java code that EMF generated from it, and two models that conform to it. MediaFlow describes a media processing pipeline as a graph: resources such as video files, transformers such as a scaler or a transcoder, ports on each node, and edges from port to port. Sessions 9 to 13 build on this metamodel: a textual and a visual syntax, transformations into UML and into text, and validation rules.

## How the pieces fit together

```
metamodels/mediaflow.emf ----Emfatic----> metamodels/mediaflow.ecore <----Ecore Tools---- representations.aird
     (the metamodel as text)                 (the metamodel)                 ("mediaflow class diagram")
                                                    |
                                     metamodels/mediaflow.genmodel
                                                    | EMF code generator
                                                    v
                                         src/mediaflow/**/*.java
                                                    |
models/flow1.model, models/flow2.model --loaded by--+--> tools/CheckModels.java (check-models.sh)
```

The metamodel defines these classes:

| Class | Kind | Features |
|---|---|---|
| `NamedElement` | abstract | `name` |
| `Graph` | | contains `nodes` and `edges` |
| `Node` | abstract, a `NamedElement` | contains `ports` |
| `Resource` | a `Node` | `mediaType` (enum `MediaType`), `uri`, `external` |
| `Transformer` | abstract, a `Node` | `backend` (enum `Backend`: FFMPEG, GSTREAMER, CUSTOM), `command`, `replicas` |
| `Scaler` | a `Transformer` | `width`, `height` |
| `Transcoder` | a `Transformer` | `videoCodec`, `audioCodec`, `container`, `bitrate` |
| `Port` | a `NamedElement` | `direction` (enum `Direction`: IN, OUT), reference `owner` to a `Node` |
| `Edge` | a `NamedElement` | references `source` and `target`, each to a `Port` |

The two models:

| Model | Nodes | Ports | Edges | Pipeline |
|---|---|---|---|---|
| `flow1.model` | 3: 2 `Resource`, 1 `Scaler` | 4 | 2 | A straight line: `input_video`, `scaler_720`, `output_video`. |
| `flow2.model` | 6: 3 `Resource`, 1 `Scaler`, 2 `Transcoder` | 10 | 5 | A branch: `input_master`, `scaler_1080`, then `transcoder_h264` to `output_mp4` and `transcoder_vp9` to `output_webm`. |

```
mediaflow/                          an Eclipse plug-in project
  metamodels/mediaflow.ecore        the metamodel
  metamodels/mediaflow.emf          the same metamodel in Emfatic, a textual notation for Ecore
  metamodels/mediaflow.genmodel     settings of the EMF code generator: code goes to /mediaflow/src, Java 21
  src/mediaflow/                    generated interfaces, one per class, plus MediaflowPackage and MediaflowFactory
  src/mediaflow/impl/               generated implementations
  src/mediaflow/util/               generated MediaflowSwitch and MediaflowAdapterFactory
  models/flow1.model, flow2.model   two models, stored as XMI
  representations.aird              a Sirius session that holds the class diagram of the metamodel
  mediaflow_class_diagram.svg       that class diagram as an image
  plugin.xml                        registers the generated package under its namespace URI, mediaflow
  tools/CheckModels.java            loads models with the generated code and prints them
  check-models.sh                   compiles the generated code and runs CheckModels outside Eclipse
  META-INF/, build.properties, .project, .classpath   plug-in settings for Eclipse (PDE)
```

## Before you start

- A JDK, version 21 or newer. `javac -version` should print a version number. This guide was tested with OpenJDK 27.0.1.
- An Eclipse installation with EMF. The script in step 3 does not start Eclipse. It only takes the EMF jars from the `plugins` folder of the installation. For step 5 you also need Ecore Tools, Sirius and Emfatic. The Eclipse Modeling Tools package contains EMF, Ecore Tools and Sirius. Add Emfatic with Help > Install New Software and the update site `https://download.eclipse.org/emfatic/update`. Steps 2 to 4 were tested with the plugins of Eclipse 4.39 (2026-03) and EMF 2.42.

## Step 1. Go to the project folder

```bash
cd projects/session-08/mediaflow
```

Run every command in this guide from this folder.

## Step 2. Tell the script where Eclipse is

Set `ECLIPSE_HOME` to the Eclipse folder that contains `plugins`, then check that EMF is there:

```bash
export ECLIPSE_HOME=$HOME/eclipse/modeling-2026-03/eclipse
ls "$ECLIPSE_HOME"/plugins/org.eclipse.emf.ecore_*.jar
```

Use the path of your own installation. On macOS it ends in `Eclipse.app/Contents/Eclipse`. `ls` should print one or more paths that end in `.jar`. `export` lasts until you close the terminal, so repeat it in every new terminal.

## Step 3. Load the models with the generated code

```bash
./check-models.sh
```

```
models/flow1.model: graph flow1, 3 nodes {Resource=2, Scaler=1}, 4 ports, 2 edges
  edge_input_video_to_scaler_720: input_video.input_video_out (OUT) -> scaler_720.scaler_720_video_in (IN)
  edge_scaler_720_to_output_video: scaler_720.scaler_720_video_out (OUT) -> output_video.output_video_in (IN)
  EMF validation: OK
models/flow2.model: graph flow2, 6 nodes {Resource=3, Scaler=1, Transcoder=2}, 10 ports, 5 edges
  edge_input_to_scaler: input_master.input_master_out (OUT) -> scaler_1080.scaler_1080_in (IN)
  edge_scaler_to_h264: scaler_1080.scaler_1080_out_h264 (OUT) -> transcoder_h264.transcoder_h264_in (IN)
  edge_scaler_to_vp9: scaler_1080.scaler_1080_out_vp9 (OUT) -> transcoder_vp9.transcoder_vp9_in (IN)
  edge_h264_to_output: transcoder_h264.transcoder_h264_out (OUT) -> output_mp4.output_mp4_in (IN)
  edge_vp9_to_output: transcoder_vp9.transcoder_vp9_out (OUT) -> output_webm.output_webm_in (IN)
  EMF validation: OK
```

The script compiles the 27 generated Java files in `src/` together with `tools/CheckModels.java` into `target/classes`, which Git ignores, and runs `CheckModels`. That takes a second or two. `CheckModels` uses the generated code the way any Java program would: `MediaflowPackage.eINSTANCE` registers the metamodel under its namespace URI, `mediaflow`, and the loaded model consists of objects of the generated classes, such as `Graph`, with methods such as `getNodes()` and `getEdges()`.

## Step 4. Compare the models with the metamodel

Every line of the output comes from the model, typed by the metamodel:

- The node counts per class are the numbers in the table of models above. A model can only contain classes that the metamodel defines, and only concrete ones: `Resource`, `Scaler` and `Transcoder`, never `Node` or `Transformer`.
- An edge joins two ports, not two nodes, because `Edge.source` and `Edge.target` are references to `Port`. The node in front of each port is the node that contains it.
- `(OUT)` and `(IN)` are values of the enum `Direction`. A port without a direction in the XMI file has the default, `IN`, which is the first literal of the enum.
- `EMF validation: OK` means the model satisfies what the metamodel states: the types, the multiplicities, and references that point at existing objects. EMF does not know domain rules such as "an edge goes from an OUT port to an IN port". Session 13 adds such rules.

To see the generated code, open `src/mediaflow/Scaler.java`: it has `getWidth()`, `setWidth(int)`, `getHeight()` and `setHeight(int)` for the two attributes of `Scaler`, and inherits the rest from `Transformer`.

## Step 5 (optional). Work with the metamodel in Eclipse

1. In Eclipse, choose File > Import > General > Existing Projects into Workspace, select `projects/session-08/mediaflow`, and leave "Copy projects into workspace" unticked.
2. Double-click `metamodels/mediaflow.ecore` to see the metamodel as a tree. Double-click `representations.aird` and open the diagram `mediaflow class diagram` to see it as a class diagram, the one in chapter 8.
3. Open `metamodels/mediaflow.emf`, the Emfatic version. After a change there, right-click the file and choose Generate Ecore Model to write `mediaflow.ecore` again.
4. Open `metamodels/mediaflow.genmodel`, right-click the root and choose Generate Model Code to write `src/` again. With the files as they are, nothing changes: the code in `src/` is exactly what the generator of EMF 2.45 writes from this genmodel.
5. To open `models/flow1.model` in the tree editor of EMF (Open With > Sample Reflective Ecore Model Editor), Eclipse has to know the namespace URI `mediaflow`. Either register the metamodel first (with Epsilon installed: right-click `mediaflow.ecore` > Register EPackages), or start a second Eclipse with the plug-in (Run As > Eclipse Application) and open the model there.

The project is a plug-in that later sessions copy. Sessions 8 to 13 each have a project named `mediaflow`, and a workspace holds only one project of a name. Use a separate workspace for each session.

## Changing the settings

`check-models.sh` takes model files as arguments. Without arguments, it checks the two models in `models/`. To check a model of your own, for example for the exercises in chapter 8:

```bash
cp models/flow1.model models/my-flow.model
# edit models/my-flow.model
./check-models.sh models/my-flow.model
```

The script exits with status 1 when a model cannot be loaded or EMF reports an error.

After a change to the metamodel, generate the code again (step 5, points 3 and 4) before you run the script. The script compiles whatever is in `src/`. The model files only store the namespace URI `mediaflow`, so a model that uses a removed class or attribute no longer loads.

## Troubleshooting

| Problem | What to do |
|---|---|
| `ECLIPSE_HOME is not set` | Do step 2 in this terminal. |
| `.../plugins does not exist` | `ECLIPSE_HOME` points to the wrong folder. It must be the folder that contains `plugins`. |
| `... cannot be loaded: org.eclipse.emf.ecore.xmi.FeatureNotFoundException: Feature 'widht' not found. (file:..., 9, 153)` | The model uses an attribute that the metamodel does not define, here a misspelled `width`. The two numbers are the line and the column. |
| `... cannot be loaded: org.eclipse.emf.ecore.xmi.UnresolvedReferenceException: Unresolved reference '_doesNotExist'.` | An edge or a port points at an `xmi:id` that no element of the file has. |
| `... cannot be loaded: org.eclipse.emf.ecore.xmi.PackageNotFoundException: Package with uri 'mediaflw' not found.` | The `xmlns:mediaflow` of the file is not the namespace URI of the metamodel. It must be `mediaflow`. |
| Eclipse says `Package with uri 'mediaflow' not found` when you open a model | The metamodel is not registered in this Eclipse. See step 5, point 5. |
