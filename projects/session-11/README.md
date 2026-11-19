# MediaFlow to UML activity diagrams with ATL and ETL

Code for session 11 (Model-to-Model Transformation) of IT30213 Advanced Software Engineering & DevOps. It transforms MediaFlow pipeline models into UML activity diagrams, and it does so twice: once with ATL, which is compiled to bytecode and runs on the EMFTVM virtual machine, and once with ETL from Epsilon, which interprets its rules. Both read the same XMI models and write UML models that UML tools such as Papyrus can open. The result is again a model, conforming to another metamodel, which is what makes this a model-to-model transformation. Session 12 turns the same MediaFlow models into text instead.

## How the pieces fit together

```
input/flow1.xmi, input/flow2.xmi    (instances of ../mediaflow/metamodels/mediaflow.ecore)
        |
        +--> transformer/mediaflow2activity.atl --CompileAtl--> transformer/mediaflow2activity.emftvm
        |        MediaflowToActivityATLRunner (EMFTVM) --> output/activity<N>atl.uml
        |
        +--> transformer/mediaflow2activity.etl
                 MediaflowToActivityETLRunner (Epsilon) --> output/activity<N>etl.uml
```

The two rule files implement the same mapping, with the same rule names:

| MediaFlow | UML | Rule |
|---|---|---|
| `Graph` named `demo` | `Model` named `demoModel`, holding an `Activity` named `demoActivity` | `Graph2Model` in ATL, the `post` block `BuildActivityModel` in ETL |
| `Resource` with no incoming edge (entry) | `InitialNode` | `EntryResource2Initial` |
| `Resource` with no outgoing edge (exit) | `ActivityFinalNode` | `ExitResource2Final` |
| `Resource` with both (internal) | `OpaqueAction`, language `resource`, body: the URI | `InternalResource2Action` |
| `Scaler`, `Transcoder` | `OpaqueAction`, language `shell`, body: the command | `Scaler2OpaqueAction`, `Transcoder2OpaqueAction` |
| `Edge` | `ControlFlow` | `Edge2ControlFlow` |

A MediaFlow edge joins two ports, while a UML control flow joins two nodes. Both transformations therefore look up the node that owns each port, with the helpers `sourceNode()` and `targetNode()`. Ports themselves have no counterpart in UML.

| Model | MediaFlow nodes | MediaFlow edges | UML nodes | UML edges |
|---|---|---|---|---|
| `flow1.xmi` | 3 | 2 | 1 `InitialNode`, 1 `OpaqueAction`, 1 `ActivityFinalNode` | 2 `ControlFlow` |
| `flow2.xmi` | 6 | 5 | 1 `InitialNode`, 3 `OpaqueAction`, 2 `ActivityFinalNode` | 5 `ControlFlow` |

```
m2m/                                    the M2M project, an Eclipse plug-in project
  src/m2m/MediaflowToActivityATLRunner.java  runs the compiled ATL module on EMFTVM
  src/m2m/MediaflowToActivityETLRunner.java  runs the ETL module with Epsilon
  transformer/mediaflow2activity.atl    the ATL rules
  transformer/mediaflow2activity.etl    the ETL rules
  transformer/mediaflow2activity.emftvm the compiled ATL module, generated and ignored by Git
  tools/CompileAtl.java                 compiles the .atl file outside Eclipse
  run-m2m.sh                            compiles the Java code and the ATL module and runs both transformations
  input/flow1.xmi, flow2.xmi            the models the runners read
  input/flow1.mediaflow, flow2.mediaflow  the same models in the textual MediaFlow syntax of session 9
  output/activity{1,2}{atl,etl}.uml     the four UML models, also kept in Git
  output/activity{1,2}atl.notation      Papyrus diagrams of the two ATL results
  output/output.aird, .di, .notation    Papyrus and Sirius session files
  *.launch                              Eclipse launch configurations (step 6)
  META-INF/, build.properties           plug-in settings for Eclipse (PDE)
  org/eclipse/m2m/atl/emftvm/impl/resource/EMFTVMResourceFactoryImpl.class  a copy of a class of the EMFTVM jar, not used
mediaflow/                              the metamodel project: metamodels/mediaflow.ecore and the code EMF generated from it
mediaflow.edit/, mediaflow.editor/, mediaflow.tests/  the EMF edit, editor and test projects of session 8
mediaflow.xtext.parent/                 the Xtext projects of the textual MediaFlow syntax of session 9
mediaflow.ecore.generator/              ExportToXmi, which turns a .mediaflow file into XMI (session 9)
example/                                flow1.mediaflow and flow2.mediaflow, for the Xtext editor of session 9
```

The XMI files in `input/` are the output of `ExportToXmi` for `flow1.mediaflow` and `flow2.mediaflow`, which is why the graph is called `demo`. Step 6 shows how to run it. `module` and `slides` are links to the book and the slides folders of the repository.

## Before you start

- A JDK, version 21 or newer. `javac -version` should print a version number. This guide was tested with OpenJDK 27.0.1.
- An Eclipse installation with EMF, UML2, ATL with EMFTVM, and Epsilon 2.9. The script in step 3 does not start Eclipse. It only takes the jars from the `plugins` folder of the installation. The Eclipse Modeling Tools package contains EMF and UML2. Add ATL from the update site of the Eclipse release, under Modeling, and Epsilon from `https://download.eclipse.org/epsilon/updates/`, both with Help > Install New Software. Steps 2 to 5 were tested with the plugins of Eclipse 4.39 (2026-03): ATL 4.12, Epsilon 2.9.0, UML2 5.6 and EMF 2.42.
- Python 3 for the checks in steps 4 and 5. `python3 --version` should print a version number.

## Step 1. Go to the project folder

```bash
cd projects/session-11/m2m
```

Run every command in this guide from this folder. The runners look for `input/`, `transformer/`, `output/` and `../mediaflow/` relative to it.

## Step 2. Tell the script where Eclipse is

Set `ECLIPSE_HOME` to the Eclipse folder that contains `plugins`, then check that ATL's EMFTVM and Epsilon's ETL engine are there:

```bash
export ECLIPSE_HOME=$HOME/eclipse/modeling-2026-03/eclipse
ls "$ECLIPSE_HOME"/plugins/org.eclipse.m2m.atl.emftvm_*.jar "$ECLIPSE_HOME"/plugins/org.eclipse.epsilon.etl.engine_*.jar
```

Use the path of your own installation. On macOS it ends in `Eclipse.app/Contents/Eclipse`. `ls` should print at least one path for each of the two patterns. An Eclipse that was updated can hold several versions of a plugin, and the script takes the newest. `export` lasts until you close the terminal, so repeat it in every new terminal.

## Step 3. Run both transformations

```bash
./run-m2m.sh
```

```
Compiled transformer/mediaflow2activity.atl to transformer/mediaflow2activity.emftvm
'ATL transformation completed: demoActivity built with 3 node(s) and 2 edge(s).'
ATL transformation complete: output/activity1atl.uml
Timing data:
	Loading finished at 0.101695 seconds (duration: 0.101695 seconds)
	Matching finished at 0.154057 seconds (duration: 0.052362 seconds)
	Applying finished at 0.170054 seconds (duration: 0.015997 seconds)
	Post-applying finished at 0.172170 seconds (duration: 0.002116 seconds)
	Recursive stage finished at 0.172199 seconds (duration: 0.000029 seconds)
	Execution finished at 0.172255 seconds (duration: 0.000085 seconds)

ETL transformation completed: demoActivity built with 3 node(s) and 2 edge(s).
'ATL transformation completed: demoActivity built with 6 node(s) and 5 edge(s).'
ATL transformation complete: output/activity2atl.uml
Timing data:
	Loading finished at 0.096602 seconds (duration: 0.096602 seconds)
	Matching finished at 0.146059 seconds (duration: 0.049457 seconds)
	Applying finished at 0.161607 seconds (duration: 0.015548 seconds)
	Post-applying finished at 0.164062 seconds (duration: 0.002455 seconds)
	Recursive stage finished at 0.164111 seconds (duration: 0.000049 seconds)
	Execution finished at 0.164160 seconds (duration: 0.000098 seconds)

ETL transformation completed: demoActivity built with 6 node(s) and 5 edge(s).
```

The script takes about 7 seconds. It compiles the two runners and `tools/CompileAtl.java` into `target/classes`, which Git ignores. Then it compiles the ATL module into `transformer/mediaflow2activity.emftvm`, because the ATL runner loads bytecode, not the `.atl` file. Inside Eclipse, the ATL builder does that step each time you save the `.atl` file. Last, it runs ATL and ETL on both models. ETL needs no compile step, because Epsilon parses the `.etl` file at every run. Your timing numbers will differ.

The `... completed: ...` lines come from the rules themselves: the `do` block of `Graph2Model` in ATL and the `post` block in ETL. The ATL line is in quotes because ATL's `debug()` prints the string as a value.

## Step 4. Check the UML models

Count the nodes and edges of each activity by their UML type:

```bash
python3 -c 'import sys, collections, xml.etree.ElementTree as ET
TYPE = "{http://www.omg.org/spec/XMI/20131001}type"
for path in sys.argv[1:]:
    activity = ET.parse(path).getroot().find("packagedElement")
    nodes = collections.Counter(n.get(TYPE)[4:] for n in activity.iter("node"))
    edges = collections.Counter(e.get(TYPE)[4:] for e in activity.iter("edge"))
    print(path, activity.get("name"), dict(nodes), dict(edges))' output/*.uml
```

```
output/activity1atl.uml demoActivity {'InitialNode': 1, 'ActivityFinalNode': 1, 'OpaqueAction': 1} {'ControlFlow': 2}
output/activity1etl.uml demoActivity {'InitialNode': 1, 'ActivityFinalNode': 1, 'OpaqueAction': 1} {'ControlFlow': 2}
output/activity2atl.uml demoActivity {'InitialNode': 1, 'ActivityFinalNode': 2, 'OpaqueAction': 3} {'ControlFlow': 5}
output/activity2etl.uml demoActivity {'InitialNode': 1, 'ActivityFinalNode': 2, 'OpaqueAction': 3} {'ControlFlow': 5}
```

These are the numbers in the table of models above. Open `output/activity2etl.uml` to see how the domain data survives the transformation. The scaler, for example, became an action that keeps its command (the ids in your file differ, see step 5):

```
    <node xmi:type="uml:OpaqueAction" xmi:id="__yEZ8OP4EfGjkJtVDhDoGQ" name="resize720" incoming="__yGPIOP4EfGjkJtVDhDoGQ" outgoing="__yGPIeP4EfGjkJtVDhDoGQ __yGPIuP4EfGjkJtVDhDoGQ">
      <language>shell</language>
      <body>ffmpeg -i input.mp4 -vf scale=1280:720 
      scaled.mp4</body>
```

`resize720` has two outgoing control flows, one to each transcoder. The line break in the command comes from the model: the XMI stores it as `&#xA;`.

## Step 5. Compare the ATL and the ETL result

The ATL and the ETL file of one model are not equal byte for byte. UML gives every element a random `xmi:id` when it is created, so the ids differ between the two files, and between two runs. Number the ids in the order they appear, and the two results become identical:

```bash
canon() {
  python3 -c 'import re, sys
ids = {}
text = open(sys.argv[1]).read()
print(re.sub(r"_[A-Za-z0-9_-]{22}", lambda m: ids.setdefault(m.group(0), "id%d" % len(ids)), text), end="")' "$1"
}
diff <(canon output/activity1atl.uml) <(canon output/activity1etl.uml) && echo "flow1: ATL and ETL give the same model"
diff <(canon output/activity2atl.uml) <(canon output/activity2etl.uml) && echo "flow2: ATL and ETL give the same model"
```

```
flow1: ATL and ETL give the same model
flow2: ATL and ETL give the same model
```

`diff` prints nothing when the files match, and `&&` then prints the message. The two languages work differently, but with the same mapping they produce the same model, element for element and in the same order.

Because of the random ids, `git status` lists the four `.uml` files in `output/` as changed after every run, even when nothing else changed. The two Papyrus diagrams, `activity1atl.notation` and `activity2atl.notation`, point at the ids of the versions in Git, so they lose their elements after a run. To get the versions from Git back, for example to open the diagrams:

```bash
git checkout -- output
```

## Step 6 (optional). Run the transformations in Eclipse

1. In Eclipse, choose File > Import > General > Existing Projects into Workspace. Select `projects/session-11` as the root directory, tick `mediaflow` and `m2m`, and leave "Copy projects into workspace" unticked. The runners reach the metamodel through `../mediaflow`, so both projects have to stay side by side. Do not import `example` into the same workspace: it is also named `mediaflow`, and it is meant for the second Eclipse that session 9's Xtext editor runs in.
2. When the workspace builds, the ATL builder compiles `mediaflow2activity.atl` into `mediaflow2activity.emftvm`, because the first line of the file is `-- @atlcompiler emftvm`.
3. Right-click `MediaflowToActivityATLRunner.launch` or `MediaflowToActivityETLRunner.launch` and choose Run As. Without arguments, the ATL runner turns `flow1.xmi` into `output/activity1atl.uml`, and the ETL runner turns `flow2.xmi` into `output/activity2etl.uml`.
4. `mediaflow2activity1.launch` and `mediaflow2activity2.launch` run the ETL file through Epsilon's own launcher, on `flow1.xmi` and `flow2.xmi`. They look the MediaFlow metamodel up by its namespace URI, `mediaflow`, so register it first: right-click `mediaflow/metamodels/mediaflow.ecore` and choose Register EPackages.
5. To see a result as a diagram, open `output/activity1atl.notation` or `output/activity2atl.notation` with Papyrus, using the versions from Git (see the end of step 5).
6. To make the XMI input from the textual model again, also import `mediaflow.xtext.parent/mediaflow.xtext` and `mediaflow.ecore.generator` (Xtext must be installed, see session 9's guide), then right-click `mediaflow.ecore.generator/ExportToXmi.launch` and choose Run As. It reads `mediaflow.ecore.generator/input/flow2.mediaflow` and writes `mediaflow.ecore.generator/output/flow2.xmi`, which is the same as `m2m/input/flow2.xmi`. For `flow1`, change the two file names at the top of `ExportToXmi.java`.

The `m2m` project is set to Java 25 (`JavaSE-25` in `META-INF/MANIFEST.MF`). If Eclipse reports that no JRE matches `JavaSE-25`, add a JDK 25 or newer under Window > Preferences > Java > Installed JREs.

Sessions 8 to 13 each have a project named `mediaflow`, and a workspace holds only one project of a name. Use a separate workspace for each session.

## Changing the settings

`run-m2m.sh` without arguments runs both transformations on both models. With three arguments it runs one transformation on one model:

```bash
./run-m2m.sh etl input/flow1.xmi output/flow1-etl-test.uml
```

```
Compiled transformer/mediaflow2activity.atl to transformer/mediaflow2activity.emftvm
ETL transformation completed: demoActivity built with 3 node(s) and 2 edge(s).
```

The runners take up to four arguments, in this order:

| Argument | ATL runner default | ETL runner default |
|---|---|---|
| input model | `input/flow1.xmi` | `input/flow2.xmi` |
| output model | `output/activity1atl.uml` | `output/activity2etl.uml` |
| rules | `transformer/` (the folder of `mediaflow2activity.emftvm`) | `transformer/mediaflow2activity.etl` |
| metamodel | `../mediaflow/metamodels/mediaflow.ecore` | `../mediaflow/metamodels/mediaflow.ecore` |

- **Change a rule.** Change it in both `mediaflow2activity.atl` and `mediaflow2activity.etl`, run step 3, and check with step 5 that the two languages still agree. `run-m2m.sh` compiles the `.atl` file again at every run, so it never runs old bytecode.
- **Add a model.** Make an XMI file of the MediaFlow metamodel, for example with session 9's `ExportToXmi` or session 10's diagram editor, put it into `input/`, and run it with the three-argument form. A graph needs at least one resource without incoming edges and one without outgoing edges to give an `InitialNode` and an `ActivityFinalNode`. Session 13 checks such rules before a transformation runs.

## Troubleshooting

| Problem | What to do |
|---|---|
| `ECLIPSE_HOME is not set` | Do step 2 in this terminal. |
| `.../plugins does not exist` | `ECLIPSE_HOME` points to the wrong folder. It must be the folder that contains `plugins`. |
| `... has no org.eclipse.m2m.atl.emftvm jar`, or the same for an `org.eclipse.epsilon` or `org.eclipse.uml2` jar | That part is not installed in the Eclipse. Install it as described in "Before you start". |
| `Usage: ./run-m2m.sh [atl\|etl INPUT.xmi OUTPUT.uml]` | Give no argument, or exactly three, the first being `atl` or `etl`. |
| `transformer/mediaflow2activity.atl:37:19: error: ...` and the script stops | The ATL file has a syntax error at that line and column. The script stops before it runs anything, and it deletes the empty `.emftvm` file the compiler leaves behind. |
| `Property 'nme' not found in object org.eclipse.uml2.uml.internal.impl.InitialNodeImpl...` | An ETL rule uses a feature that the UML metamodel does not have. Epsilon finds this only when the rule runs. |
| `FileNotFoundException: .../input/....xmi (No such file or directory)` | The input path is wrong. Paths are relative to the `m2m` folder. |
| `NoClassDefFoundError: org/eclipse/uml2/uml/profile/standard/StandardPackage` | The UML standard profile jar is missing from a classpath you built yourself. `run-m2m.sh` adds it, together with the other jars UML2, ATL and Epsilon need. |
| A Papyrus diagram in `output/` shows no elements, or unresolved ones | The `.uml` file was generated again, with new ids. Run `git checkout -- output` (step 5). |
| `VMException: Error during module loading: Module mediaflow2activity not found` | `transformer/mediaflow2activity.emftvm` does not exist. Outside Eclipse, run the ATL runner through `run-m2m.sh`, which compiles it. In Eclipse, check that ATL is installed and choose Project > Clean on `m2m`, so the ATL builder runs. |
