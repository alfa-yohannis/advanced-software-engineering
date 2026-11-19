# A diagram editor for MediaFlow with Eclipse Sirius

Code for session 10 (Visual Domain-Specific Language) of IT30213 Advanced Software Engineering & DevOps. It is a graphical editor for MediaFlow models, made with Eclipse Sirius. A viewpoint specification, `mediaflow.odesign`, says how each class of the metamodel appears in a diagram and which tools the palette offers. Sirius reads it and gives you an editor in which you draw a pipeline instead of typing it: resources, scalers and transcoders as boxes and ellipses, ports on their borders, and edges as arrows between ports. Every change in the diagram changes the model file behind it. Session 9 gives the same models a textual syntax.

## How the pieces fit together

```
mediaflow (plug-in)                 mediaflow.sirius (plug-in)
  metamodels/mediaflow.ecore  <----   description/mediaflow.odesign     viewpoint "graph", diagram "MediaFlow diagram"
  src/ (generated EMF code)           icons/*.png                       palette and diagram icons
                                      src/mediaflow/sirius/Activator.java   registers the viewpoint when the plug-in starts
                                      src/mediaflow/sirius/Services.java    place for Java methods that AQL expressions can call

second Eclipse with both plug-ins:  flow1.mediaflow, flow2.mediaflow (models, XMI)
                                    representations.aird (the diagrams: "flow1 diagram", "flow2 diagram")
```

Sirius needs the metamodel and the viewpoint inside the running Eclipse. You therefore develop the two plug-ins in one Eclipse and draw diagrams in a second Eclipse that Eclipse starts with both plug-ins installed (step 5).

How the viewpoint shows each class:

| Class | Mapping | Shape | Label |
|---|---|---|---|
| `Resource` | `ResourceContainer` | light blue box, a container | `name [mediaType]`, for example `input_video [VIDEO]` |
| `Scaler` | `ScalerNode` | light green ellipse | `name (widthxheight)`, for example `scaler_720 (720x1080)` |
| `Transcoder` | `TranscoderNode` | light orange ellipse | `name [videoCodec/audioCodec]`, for example `transcoder_h264 [h264/aac]` |
| `Port` | `PortInResource`, `PortInScaler`, `PortInTranscoder` | small square on the border of its node | `name` |
| `Edge` | `EdgeMapping` | arrow from the source port to the target port | `name` |

The palette has two sections:

| Section | Tool | Creates |
|---|---|---|
| Nodes | Resource | a `Resource` named `newResource`, media type `VIDEO` |
| Nodes | Scaler | a `Scaler` named `newScaler`, 1920x1080, 1 replica |
| Nodes | Transcoder | a `Transcoder` named `newTranscoder`, 1 replica |
| Connections | Port | a `Port` named `newPort`, direction `IN`, on the node you click |
| Connections | Edge | an `Edge` named `edge_<source port>_to_<target port>`, from the port you start on to the port you end on |

```
mediaflow/                          the metamodel project of session 8: metamodels/, the generated src/, models/
mediaflow.sirius/                   the Sirius project, a plug-in and a modeling project
  description/mediaflow.odesign     the viewpoint specification
  icons/                            PNG icons, with their SVG sources
  src/mediaflow/sirius/             Activator.java and Services.java
  flow1.mediaflow, flow2.mediaflow  two models; despite the extension, these are XMI files
  representations.aird              the Sirius session: which models and viewpoints are used, and the two diagrams
  flow1 diagram.svg, flow2 diagram.svg  the two diagrams exported as images
  plugin.xml, META-INF/, build.properties   plug-in settings for Eclipse (PDE)
```

The `.mediaflow` files here are XMI, because Sirius works on EMF models in any file format EMF can read. In session 9, the same extension is used for the textual syntax. `module` and `slides` are links to the book and the slides folders of the repository.

## Before you start

- An Eclipse installation with EMF and Sirius, and Java 21 or newer to run it. The Eclipse Modeling Tools package contains both. The Sirius files of this project load without errors in Sirius 7.5 with EMF 2.42 (Eclipse 4.39, 2026-03).
- Python 3 for the check in step 2. `python3 --version` should print a version number.

## Step 1. Go to the project folder

```bash
cd projects/session-10/mediaflow.sirius
```

## Step 2. Look at the two models

```bash
python3 -c 'import sys, collections, xml.etree.ElementTree as ET
XSI = "{http://www.w3.org/2001/XMLSchema-instance}type"
for path in sys.argv[1:]:
    graph = ET.parse(path).getroot()
    nodes = collections.Counter(n.get(XSI).split(":")[1] for n in graph.findall("nodes"))
    ports = sum(len(n.findall("ports")) for n in graph.findall("nodes"))
    print(path, graph.get("name"), dict(nodes), ports, "ports", len(graph.findall("edges")), "edges")' flow1.mediaflow flow2.mediaflow
```

```
flow1.mediaflow Simple {'Resource': 2, 'Scaler': 1} 4 ports 2 edges
flow2.mediaflow Example {'Resource': 3, 'Scaler': 1, 'Transcoder': 2} 10 ports 5 edges
```

`flow1`, named `Simple`, is the straight pipeline: input resource, scaler, output resource. `flow2`, named `Example`, is the branching one: input resource, scaler, two transcoders and two output resources. The diagrams in step 6 must show exactly these nodes and edges.

## Step 3. Look at the viewpoint specification

```bash
grep -o -E '<(containerMappings|nodeMappings|borderedNodeMappings|edgeMappings) name="[A-Za-z]+"' description/mediaflow.odesign
grep -o -E 'ownedTools xsi:type="tool:[A-Za-z]+" name="[A-Za-z]+"' description/mediaflow.odesign
```

```
<edgeMappings name="EdgeMapping"
<containerMappings name="ResourceContainer"
<borderedNodeMappings name="PortInResource"
<nodeMappings name="ScalerNode"
<borderedNodeMappings name="PortInScaler"
<nodeMappings name="TranscoderNode"
<borderedNodeMappings name="PortInTranscoder"
ownedTools xsi:type="tool:ContainerCreationDescription" name="Resource"
ownedTools xsi:type="tool:NodeCreationDescription" name="Scaler"
ownedTools xsi:type="tool:NodeCreationDescription" name="Transcoder"
ownedTools xsi:type="tool:NodeCreationDescription" name="Port"
ownedTools xsi:type="tool:EdgeCreationDescription" name="Edge"
```

These are the mappings and tools of the two tables above. A mapping takes elements from the model, for example `feature:nodes` of the `Graph`, and draws them. A tool runs model operations, for example `CreateInstance` of `mediaflow::Scaler` in `nodes`, followed by `SetValue` for the defaults. In Eclipse, open the file with its own editor (step 4) rather than as XML.

## Step 4. Import the two plug-ins

1. In Eclipse, choose File > Import > General > Existing Projects into Workspace. Select `projects/session-10` as the root directory, tick `mediaflow` and `mediaflow.sirius`, and leave "Copy projects into workspace" unticked.
2. Both projects should build without errors. Double-click `mediaflow.sirius/description/mediaflow.odesign` to see the viewpoint in Sirius's specification editor: the viewpoint `graph`, the diagram `MediaFlow diagram` for `mediaflow::Graph`, its default layer with the mappings, and the two tool sections.

Sessions 8 to 13 each have a project named `mediaflow`, and a workspace holds only one project of a name. Use a separate workspace for each session.

## Step 5. Start a second Eclipse with the plug-ins

1. Choose Run > Run Configurations, select Eclipse Application, and click New launch configuration. The default plug-in selection, all workspace and enabled target plug-ins, includes `mediaflow` and `mediaflow.sirius`.
2. Click Run. A second Eclipse window opens, with its own workspace. In it, the metamodel is registered under its namespace URI `mediaflow`, and `Activator` has registered the viewpoint `graph`.

## Step 6. Open the two diagrams

1. In the second Eclipse, choose File > Import > General > Existing Projects into Workspace, select `projects/session-10/mediaflow.sirius`, and tick "Copy projects into workspace", so the two Eclipse windows do not edit the same files.
2. In the Model Explorer view, expand `representations.aird` and open `flow1 diagram` and `flow2 diagram`.

What to check, as in chapter 10:

| Diagram | What to check |
|---|---|
| `flow1 diagram` | Three nodes from left to right: the box `input_video [VIDEO]`, the ellipse `scaler_720 (720x1080)` and the box `output_video [VIDEO]`. Four small ports on their borders, and two arrows named `edge_input_video_to_scaler_720` and `edge_scaler_720_to_output_video`. |
| `flow2 diagram` | Six nodes: `input_master [VIDEO]`, `scaler_1080 (1920x1080)`, `transcoder_h264 [h264/aac]`, `transcoder_vp9 [vp9/opus]`, `output_mp4 [VIDEO]` and `output_webm [VIDEO]`, and five arrows. The scaler has two outgoing arrows, one to each transcoder. |

`flow1 diagram.svg` and `flow2 diagram.svg` show what the diagrams should look like.

## Step 7. Draw with the palette

1. Right-click the project and choose Viewpoints Selection. The viewpoint `graph` should be ticked. Sirius enables it for files with the extension `mediaflow`.
2. Create a new model: copy `flow1.mediaflow` to `flow3.mediaflow`, expand it in the Model Explorer, right-click the `Graph` and choose New Representation > MediaFlow diagram.
3. Use the palette to add a `Scaler`, a `Port` on it, and an `Edge` from that port to a port of another node. The Properties view shows the new elements. Open `flow3.mediaflow` with a text editor: it now contains a node `newScaler` with `width="1920" height="1080"`, and an edge named `edge_<source>_to_<target>`.
4. Rename a node in the Properties view and check that the label in the diagram changes, then change a value in the text editor, save, and refresh the diagram (F5) to see the change there too.

Sirius lets you connect two `OUT` ports, because the edge mapping only says which kinds of ports an edge may join, not in which direction. Session 13 adds rules that catch such a model.

## Changing the settings

| To change | Where |
|---|---|
| A label | `labelExpression` of the style of the mapping, an AQL expression such as `aql:self.name + ' (' + self.width + 'x' + self.height + ')'` |
| A color or shape | the style of the mapping: `FlatContainerStyleDescription` for resources, `EllipseNodeDescription` for scalers and transcoders |
| The defaults of a new element | the `SetValue` operations of the tool in the `Nodes` or `Connections` section |
| A computation AQL cannot express | a public method in `Services.java`, called from AQL as `aql:self.myService('arg')`; the specification lists the class under Java extensions |

Edit `mediaflow.odesign` with its own editor in the first Eclipse. A workspace specification is picked up by Sirius, but the second Eclipse runs the plug-in version, so restart it after a change. Edit models through the diagram rather than as XML: if a file changes outside Sirius, refresh the diagram.

## Troubleshooting

| Problem | What to do |
|---|---|
| The second Eclipse says `Package with uri 'mediaflow' not found` | The `mediaflow` plug-in is not part of the launch. In the run configuration, check that the workspace plug-ins `mediaflow` and `mediaflow.sirius` are selected. |
| The viewpoint `graph` is missing from Viewpoints Selection | `mediaflow.sirius` did not start, or the project is not a modeling project. Check the run configuration, and that `mediaflow.sirius/.project` has the nature `org.eclipse.sirius.nature.modelingproject`. |
| New Representation does not offer `MediaFlow diagram` | The viewpoint is not enabled for the project (step 7, point 1), or you right-clicked something other than a `Graph`. |
| A node or an edge does not appear in a diagram | A `semanticCandidatesExpression` or a source or target mapping does not reach it. Sirius reports no error in that case. An edge between two ports appears only if both port mappings are listed in the source and target mappings of `EdgeMapping`. |
| The palette or the diagram shows no icons | The icons live in `mediaflow.sirius/icons`. Their paths in `mediaflow.odesign` start with `/mediaflow.sirius/icons/`. |
| Git shows `representations.aird` as changed after you only looked at a diagram | Sirius saves positions and sizes in the `.aird` file. Check the diff before you commit it. |
