# MediaFlow model validation with EVL and OCL

Code for session 13 (Model Validation) of IT30213 Advanced Software Engineering & DevOps. It checks MediaFlow models before a transformation uses them. The rules are written twice over the same metamodel: in EVL, the Epsilon Validation Language, which runs them and prints a message for the modeller, and in OCL, the Object Constraint Language of the OMG, as a formal companion. Two Java runners load the XMI models with EMF, apply the rules and exit with status 1 when a model has an error. A script or a pipeline can therefore stop before the M2M transformation of session 11 or the M2T generator of session 12 runs on a broken model.

## How the pieces fit together

```
input/valid/*.xmi, input/invalid/*.xmi
        |
        +--> run-evl-validation.sh --> MediaflowEvlValidationRunner --> validator/mediaflow.evl
        |                                --> a report per model, a summary, exit status 0 or 1
        |
        +--> run-ocl-validation.sh --> MediaflowOclValidationRunner --> validator/mediaflow.ocl
                                         --> a report per model, a summary, exit status 0 or 1

Both runners load every model with ../mediaflow/metamodels/mediaflow.ecore.
```

The scripts compile a runner and put the jars of an Eclipse installation on its classpath. A runner takes one XMI file, or a folder that it searches for `*.xmi` files, including subfolders. It validates the models in name order, prints a report for each and a summary at the end, and exits with status 1 if any model has an error. Warnings do not change the exit status.

| | EVL runner | OCL runner |
|---|---|---|
| Rules | `validator/mediaflow.evl`: helper operations, 26 `constraint`s and one `critique` | `validator/mediaflow.ocl`: the same 27 rules as `inv` invariants |
| Engine | Epsilon's `EvlModule` | Eclipse OCL with the Ecore binding |
| A failed rule prints | the message the rule writes, for example `Edge 'e2' must connect an OUT port to an IN port.` | the expression of the invariant that failed |
| Warnings | `critique TooManyWorkers` is a warning by itself | OCL has no warnings, so the runner reports the invariant `TooManyWorkers` as one |

The rules, grouped by the class of the metamodel they check:

| Class | Rules |
|---|---|
| `Graph` | A name, at least one node and edge, unique node and edge names, an external entry resource and an external exit resource, no isolated nodes. |
| `Node` | A name, at least one port, unique port names within the node. |
| `Port` | A name, and at least one edge that uses it. |
| `Edge` | A name, both ends set, from an `OUT` port to an `IN` port, between two different nodes. |
| `Resource` | A URI that starts with `file://`, `s3://`, `http://` or `https://`. An external resource is either an input or an output, not both. |
| `Transformer` | A command, at least one replica, incoming and outgoing edges. More than 4 replicas is a warning. |
| `Scaler` | Width and height above 0 and at most 8K (7680x4320). |
| `Transcoder` | Codecs and container set, a bitrate from 128 to 50000 kbps, and a container that suits the codecs (WebM with VP8, VP9 or AV1 and Opus or Vorbis; MP4 with H.264, H.265 or AV1 and AAC, MP3 or ALAC). |

The four models and what the runners find in them:

| Model | Errors | Warnings | Content |
|---|---|---|---|
| `input/valid/linear-resize.xmi` | 0 | 0 | The straight pipeline of session 12: input video, one FFMPEG scaler, output video. |
| `input/valid/adaptive-delivery.xmi` | 0 | 0 | The branching pipeline of session 12: one scaled stream, encoded to H.264/MP4 and to VP9/WebM. |
| `input/invalid/broken-direction.xmi` | 8 | 0 | Edge `e2` runs from an `IN` port to an `OUT` port, the scaler has an empty command, 0 replicas and width 0, and the output resource has an empty URI. |
| `input/invalid/duplicate-and-isolated.xmi` | 9 | 1 | Two nodes named `inputVideo`, two edges named `duplicate`, two ports named `stream` on one node, an `ftp://` URI, an edge from a node to itself, a bitrate of 64, libx264 and opus in a WebM container, and 8 replicas. |
| All four | 17 | 1 | |

```
model-validation/                          the validation project, an Eclipse plug-in project
  src/validation/MediaflowEvlValidationRunner.java   runs the EVL rules
  src/validation/MediaflowOclValidationRunner.java   runs the OCL invariants
  validator/mediaflow.evl                  the rules in EVL
  validator/mediaflow.ocl                  the same rules as OCL invariants
  input/valid/, input/invalid/             the four models, each as .xmi and as .mediaflow text
  run-evl-validation.sh                    compiles and runs the EVL runner outside Eclipse
  run-ocl-validation.sh                    compiles and runs the OCL runner outside Eclipse
  run-validation.sh                        the older name of run-evl-validation.sh, kept for scripts that call it
  evl_*.launch                             Eclipse launch configurations that run the EVL file on one model each
  Mediaflow*ValidationRunner.launch        Eclipse launch configurations for the two Java runners
  META-INF/, build.properties, pom.xml     plug-in and Maven settings for Eclipse (PDE and m2e)
mediaflow/                                 the metamodel project: metamodels/mediaflow.ecore and the code EMF generated from it
```

The `.mediaflow` files show each model in the textual syntax of session 9, which is easier to read. The runners only read the `.xmi` files.

## Before you start

- A JDK, version 21 or newer. `javac -version` should print a version number. This guide was tested with OpenJDK 27.0.1.
- An Eclipse installation with EMF, OCL and Epsilon 2.9. The scripts do not start Eclipse. They only take the jars from the `plugins` folder of the installation. The Eclipse Modeling Tools package contains EMF and OCL. Add Epsilon with Help > Install New Software and the update site `https://download.eclipse.org/epsilon/updates/`. Steps 2 to 7 were tested with the plugins of Eclipse 4.39 (2026-03): Epsilon 2.9.0, EMF 2.42 and OCL 3.23.
- For step 7, the session 12 project next to this one, in `projects/session-12/m2t`.

## Step 1. Go to the project folder

```bash
cd projects/session-13/model-validation
```

Run every command in this guide from this folder. The runners look for `input/`, `validator/` and `../mediaflow/` relative to it.

## Step 2. Tell the scripts where Eclipse is

Set `ECLIPSE_HOME` to the Eclipse folder that contains `plugins`, then check that Epsilon's EVL engine and the Ecore binding of OCL are there:

```bash
export ECLIPSE_HOME=$HOME/eclipse/modeling-2026-03/eclipse
ls "$ECLIPSE_HOME"/plugins/org.eclipse.epsilon.evl.engine_*.jar "$ECLIPSE_HOME"/plugins/org.eclipse.ocl.ecore_*.jar
```

Use the path of your own installation. On macOS it ends in `Eclipse.app/Contents/Eclipse`. `ls` should print at least one path for each of the two patterns. An Eclipse that was updated can hold several versions of a plugin, and the scripts take the newest. `export` lasts until you close the terminal, so repeat it in every new terminal.

## Step 3. Validate the valid models with EVL

```bash
./run-evl-validation.sh input/valid
echo $?
```

```
Running EVL validation over 1 graph(s)

== input/valid/adaptive-delivery.xmi ==
OK
Running EVL validation over 1 graph(s)

== input/valid/linear-resize.xmi ==
OK

Validation summary: 0 error(s), 0 warning(s).
0
```

The script compiles the runner into `target/classes`, which Git ignores, and runs it. That takes a second or two. `Running EVL validation over 1 graph(s)` comes from the `pre` block at the top of `mediaflow.evl`, which runs once for every model. The last line is the exit status that `echo $?` prints: 0, because no model has an error. The valid models make sure the rules are not too strict.

## Step 4. Validate the invalid models with EVL

```bash
./run-evl-validation.sh input/invalid
echo $?
```

```
Running EVL validation over 1 graph(s)

== input/invalid/broken-direction.xmi ==
1. ERROR HasExternalEntryAndExit on Graph 'brokenDirection': Graph 'brokenDirection' must have at least one external entry resource and one external exit resource.
2. ERROR ConnectsOutputToInput on Edge 'e2': Edge 'e2' must connect an OUT port to an IN port.
3. ERROR UriIsDeclared on Resource 'outputVideo': Resource 'outputVideo' must declare a URI.
4. ERROR UriUsesKnownScheme on Resource 'outputVideo': Resource 'outputVideo' URI should use file://, s3://, http:// or https://.
5. ERROR CommandIsDeclared on Scaler 'resize720': Transformer 'resize720' must declare the command it will execute.
6. ERROR ReplicasArePositive on Scaler 'resize720': Transformer 'resize720' must request at least one replica.
7. ERROR TransformersHaveInputAndOutputFlow on Scaler 'resize720': Transformer 'resize720' must have incoming and outgoing edges.
8. ERROR DimensionsArePositive on Scaler 'resize720': Scaler 'resize720' must have positive width and height.
Displayed 8 error(s), 0 warning(s), 8 total issue(s).
Running EVL validation over 1 graph(s)

== input/invalid/duplicate-and-isolated.xmi ==
1. ERROR UniqueNodeNames on Graph 'duplicateAndIsolated': Graph 'duplicateAndIsolated' has duplicate or missing node names.
2. ERROR UniqueEdgeNames on Graph 'duplicateAndIsolated': Graph 'duplicateAndIsolated' has duplicate or missing edge names.
3. ERROR HasExternalEntryAndExit on Graph 'duplicateAndIsolated': Graph 'duplicateAndIsolated' must have at least one external entry resource and one external exit resource.
4. ERROR UniquePortNamesPerNode on Resource 'inputVideo': Node 'inputVideo' has duplicate or missing port names.
5. ERROR PortParticipatesInAnEdge on Port 'stream': Port 'stream' is not used by any edge.
6. ERROR ConnectsDifferentNodes on Edge 'duplicate': Edge 'duplicate' connects two ports on the same node.
7. ERROR UriUsesKnownScheme on Resource 'inputVideo': Resource 'inputVideo' URI should use file://, s3://, http:// or https://.
W1. WARN TooManyWorkers on Transcoder 'inputVideo': Transformer 'inputVideo' requests 8 worker replicas. Use 4 or fewer workers for the course examples.
8. ERROR BitrateIsPlausible on Transcoder 'inputVideo': Transcoder 'inputVideo' bitrate must be between 128 and 50000 kbps.
9. ERROR ContainerMatchesCodecs on Transcoder 'inputVideo': Transcoder 'inputVideo' uses an implausible container/codec combination.
Displayed 9 error(s), 1 warning(s), 10 total issue(s).

Validation summary: 17 error(s), 1 warning(s).
1
```

Each line gives a number, the severity, the name of the rule, the model element and the message of the rule. Errors are numbered 1, 2, 3, …, and warnings W1, W2, …. The exit status is now 1.

Some errors follow from others. In `broken-direction`, edge `e2` alone breaks three rules: `ConnectsOutputToInput`, and, because it runs backwards, `resize720` has no outgoing edge (`TransformersHaveInputAndOutputFlow`) and `outputVideo` is no longer an exit (`HasExternalEntryAndExit`). When you read a report, look for the cause before you fix each line on its own.

## Step 5. Run the OCL invariants on the same models

```bash
./run-ocl-validation.sh input/invalid
```

The output has the same shape, but each line ends with the OCL expression that failed instead of a message. Some expressions span several lines. Shortened, with `...` where lines are left out:

```
== input/invalid/broken-direction.xmi ==
1. ERROR HasExternalEntryAndExit on Graph 'brokenDirection': self.nodes->exists(node |
        node.oclIsTypeOf(Resource) and
...
2. ERROR ConnectsOutputToInput on Edge 'e2': self.source <> null and self.target <> null implies
    (self.source.direction = Direction::OUT and self.target.direction = Direction::IN)
3. ERROR UriIsDeclared on Resource 'outputVideo': self.uri <> null and self.uri.size() > 0
...
6. ERROR ReplicasArePositive on Scaler 'resize720': self.replicas >= 1
...
8. ERROR DimensionsArePositive on Scaler 'resize720': self.width > 0 and self.height > 0
Displayed 8 error(s), 0 warning(s), 8 total issue(s).

== input/invalid/duplicate-and-isolated.xmi ==
1. ERROR UniqueNodeNames on Graph 'duplicateAndIsolated': self.nodes->isUnique(name)
...
W1. WARN TooManyWorkers on Transcoder 'inputVideo': self.replicas <= 4
8. ERROR BitrateIsPlausible on Transcoder 'inputVideo': self.bitrate >= 128 and self.bitrate <= 50000
...
Displayed 9 error(s), 1 warning(s), 10 total issue(s).

OCL validation summary: 17 error(s), 1 warning(s).
```

The OCL runner finds the same errors on the same elements, in the same order. Its reports are exact but less friendly: a modeller has to read the expression to see what is wrong, while EVL says it in a sentence. The OCL runner prints no `Running ...` line, because OCL has no `pre` block.

## Step 6. Validate all four models with both runners

```bash
./run-evl-validation.sh input | tail -n 1
./run-ocl-validation.sh input | tail -n 1
```

```
Validation summary: 17 error(s), 1 warning(s).
OCL validation summary: 17 error(s), 1 warning(s).
```

`input` holds both subfolders, so each runner validates all four models. Both find 2 valid models and 17 errors and 1 warning in the other two.

## Step 7 (optional). Validate before you generate

Validation is meant to run before a transformation. The models of session 12 use the same metamodel, so check them first and start the session 12 generator only if they pass:

```bash
./run-evl-validation.sh ../../session-12/m2t/input && (cd ../../session-12/m2t && ./run-m2t.sh)
```

```
Running EVL validation over 1 graph(s)

== ../../session-12/m2t/input/flow1.xmi ==
OK
Running EVL validation over 1 graph(s)

== ../../session-12/m2t/input/flow2.xmi ==
OK

Validation summary: 0 error(s), 0 warning(s).
Generated output/elk/flow1.json and output/tables/flow1.tables.html
Generated output/elk/flow2.json and output/tables/flow2.tables.html
```

`&&` runs the generator only when the validation exits with 0. If a model had an error, the runner would exit with 1 and the generator would not start. `run-m2t.sh` uses the same `ECLIPSE_HOME` as this session's scripts. Session 12's README explains its output.

## Step 8 (optional). Run the rules in Eclipse

1. In Eclipse, choose File > Import > General > Existing Projects into Workspace. Select `projects/session-13` as the root directory, tick `mediaflow` and `model-validation`, and leave "Copy projects into workspace" unticked. The runners reach the metamodel through `../mediaflow`, so both projects have to stay side by side.
2. Right-click `evl_broken-direction.launch` and choose Run As > evl_broken-direction. The four `evl_*.launch` configurations run `mediaflow.evl` on one model each, through Epsilon's own launcher. They load the metamodel from the file `/mediaflow/metamodels/mediaflow.ecore`, so it does not have to be registered. Epsilon lists the failed rules in its Validation view.
3. Right-click `MediaflowEvlValidationRunner.launch` or `MediaflowOclValidationRunner.launch` and choose Run As. The Console shows the report for the whole `input` folder, as in step 6.

The projects are set to Java 21 (`JavaSE-21` in `META-INF/MANIFEST.MF` and `.classpath`), and `pom.xml` compiles for Java 17. If Eclipse reports that no JRE matches `JavaSE-21`, add a JDK 21 or newer under Window > Preferences > Java > Installed JREs.

Sessions 8 to 13 each have a project named `mediaflow`, and a workspace holds only one project of a name. Use a separate workspace for each session.

## Changing the settings

Both runners take three optional arguments, in this order:

| Argument | EVL runner default | OCL runner default |
|---|---|---|
| model file or folder | `input` | `input` |
| rule file | `validator/mediaflow.evl` | `validator/mediaflow.ocl` |
| metamodel | `../mediaflow/metamodels/mediaflow.ecore` | `../mediaflow/metamodels/mediaflow.ecore` |

For example, `./run-evl-validation.sh input/invalid/broken-direction.xmi` validates one model.

**Add an EVL rule.** Add a `constraint` with a `check` and a `message` to the `context` block of the class it is about. A `guard` limits the rule to the elements where it makes sense. For a warning instead of an error, write `critique` instead of `constraint`. This rule, added to the end of `mediaflow.evl`, rejects a VP9 transcoder below 1000 kbps:

```
context Mediaflow!Transcoder {

    constraint Vp9BitrateIsHighEnough {
        guard : self.videoCodec == "vp9"
        check : self.bitrate >= 1000
        message : "Transcoder '" + self.label() + "' encodes VP9 at " + self.bitrate +
            " kbps. Use at least 1000 kbps."
    }
}
```

The valid models still pass, because `encodeVp9` in `adaptive-delivery.xmi` uses 2500 kbps. With 800 kbps in a copy of that model, the runner reports `1. ERROR Vp9BitrateIsHighEnough on Transcoder 'encodeVp9': Transcoder 'encodeVp9' encodes VP9 at 800 kbps. Use at least 1000 kbps.`

**Add an OCL invariant.** Write it before `endpackage`, with `context` and the class name on one line and `inv`, the name and a colon on the next:

```
context Transcoder
inv Vp9BitrateIsHighEnough:
    self.videoCodec = 'vp9' implies self.bitrate >= 1000
```

The OCL runner finds invariants with a pattern instead of a full OCL parser, so it only sees invariants written in this layout. To report an invariant as a warning, add its name to `WARNING_INVARIANTS` in `MediaflowOclValidationRunner.java`. The two rule files are separate, so a rule you add to one is not in the other until you write it there too.

**Add a model.** Put the `.xmi` file into `input/valid` or `input/invalid`, or pass its path as the first argument. The runners also pick up files in new subfolders of `input`.

## Troubleshooting

| Problem | What to do |
|---|---|
| `ECLIPSE_HOME is not set` | Do step 2 in this terminal. |
| `.../plugins does not exist` | `ECLIPSE_HOME` points to the wrong folder. It must be the folder that contains `plugins`. |
| `... has no org.eclipse.epsilon.evl.engine jar` | Epsilon is not installed in that Eclipse. Install it as described in "Before you start". |
| `... has no org.eclipse.ocl.ecore jar` or `... has no lpg.runtime.java jar` | The classic Ecore OCL is missing. It comes with the Eclipse Modeling Tools package. In another Eclipse, install OCL from the release update site under Help > Install New Software. |
| `Permission denied` when you start a script | The script lost its executable bit, for example after a copy. Run `chmod +x *.sh`, or start it with `bash run-evl-validation.sh`. |
| `No .xmi inputs found at ...` | The path is wrong or holds no `.xmi` file. The runner exits with 2, so a check like the one in step 7 stops there. Check the path. |
| `EVL parse failed:` | `mediaflow.evl`, or the rule file you passed, has a syntax error. The message names the line. |
| `No OCL invariants found in ...` | The OCL runner found no invariant in the `context` and `inv` layout described above. |
| `Could not parse invariant ... in context ...` | An OCL expression does not compile against the metamodel, for example because it names a feature that does not exist. The message names the invariant. |
| `NoClassDefFoundError` for a class in `org/apache/commons/collections`, `com/google/common` or `org/antlr/runtime` | A jar is missing from a classpath you built yourself. Epsilon needs Apache Commons Collections, Guava and ANTLR 3 next to its own jars. `run-evl-validation.sh` adds all three. |
| EVL and OCL report different numbers | One rule file changed and the other did not. Keep the two in step, or note on purpose which rules exist in only one of them. |
