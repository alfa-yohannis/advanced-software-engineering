# model-validation - MediaFlow validation with EVL and OCL

Session 13 of the Advanced Software Engineering course. This project
validates `mediaflow` models with the Epsilon Validation Language (EVL)
and provides a matching OCL specification for comparison.

The project deliberately continues the same case study used in the
previous sessions:

- `session-11/m2m` treats MediaFlow models as executable activity graphs.
  The validation rules therefore check entry/exit resources, edge
  direction and transformer input/output flow.
- `session-12/m2t` generates files from graph, node and edge names. The
  validation rules therefore check stable names and duplicate names
  before transformation/generation.

The shared metamodel is reused from `../mediaflow/metamodels/mediaflow.ecore`.
Do not copy the metamodel into this project.

## Directory layout

```text
model-validation/
|-- src/validation/
|   |-- MediaflowEvlValidationRunner.java
|   `-- MediaflowOclValidationRunner.java
|-- validator/
|   |-- mediaflow.evl                    # executable EVL constraints
|   `-- mediaflow.ocl                    # companion Complete OCL constraints
|-- input/
|   |-- valid/
|   |   |-- linear-resize.xmi/.mediaflow
|   |   `-- adaptive-delivery.xmi/.mediaflow
|   `-- invalid/
|       |-- broken-direction.xmi/.mediaflow
|       `-- duplicate-and-isolated.xmi/.mediaflow
|-- META-INF/MANIFEST.MF                 # PDE dependencies
`-- pom.xml                              # Maven source layout only
```

## Case studies

`linear-resize` is the simple resize pipeline from session 12: external
video input, one FFMPEG scaler, external video output.

`adaptive-delivery` is the branching pipeline from session 12: one
scaled stream is encoded into H.264/MP4 and VP9/WebM variants.

`broken-direction` demonstrates deployment and graph-flow errors:
an empty command, zero replicas, zero width, an empty output URI, and an
edge that incorrectly connects an `IN` port to an `OUT` port.

`duplicate-and-isolated` demonstrates modelling-quality errors:
duplicate node/edge/port names, unsupported URI scheme, a self-looping
edge, a too-low bitrate, an implausible WebM/libx264/opus combination,
and a warning for asking for too many teaching-cluster replicas.

## EVL rules

`validator/mediaflow.evl` validates:

- graph shape: non-empty graph, unique names, external entry/exit resources
- node/port hygiene: declared names, ports, per-node unique port names
- edge semantics: declared endpoints, `OUT -> IN`, no same-node edge
- resources: URI presence, URI scheme, directional external boundaries
- transformers: command, replica count, incoming and outgoing flow
- scalers: positive dimensions, 8K upper bound
- transcoders: codec/container/bitrate sanity

One rule is an EVL `critique`, so it is reported as a warning rather than
a hard error.

## OCL rules

`validator/mediaflow.ocl` contains OCL equivalents for the EVL hard-error
constraints. The Java OCL runner also reports `TooManyWorkers`
as a warning so its summary aligns with EVL. This is useful for teaching
the difference between:

- OCL as a compact declarative constraint notation
- EVL as an executable validation language with messages, guards,
  critiques/warnings and Java-friendly automation

## Running

From Eclipse, import `session-13/mediaflow` and this project, then run
`validation.MediaflowEvlValidationRunner` or
`validation.MediaflowOclValidationRunner` as a Java Application with the
working directory set to this project root.

You can also run the checked-in EVL launch configurations directly:
`evl_linear-resize.launch`, `evl_adaptive-delivery.launch`,
`evl_broken-direction.launch` and `evl_duplicate-and-isolated.launch`.
The Java runner launch files are `MediaflowEvlValidationRunner.launch`
and `MediaflowOclValidationRunner.launch`.

From a shell:

```bash
cd projects/session-13/model-validation
./run-evl-validation.sh input/valid
./run-evl-validation.sh input/invalid

./run-ocl-validation.sh input/valid
./run-ocl-validation.sh input/invalid
```

`run-validation.sh` is kept as a compatibility wrapper for
`run-evl-validation.sh`.

The EVL script includes Apache Commons Collections on the classpath. Epsilon
uses `org.apache.commons.collections.list.UnmodifiableList` internally
when evaluating expressions such as `Mediaflow!Graph.all`.

CLI arguments:

```text
MediaflowEvlValidationRunner [inputFileOrDirectory] [evlPath] [metamodelPath]
MediaflowOclValidationRunner [inputFileOrDirectory] [oclPath] [metamodelPath]
```

Defaults:

```text
EVL: input validator/mediaflow.evl ../mediaflow/metamodels/mediaflow.ecore
OCL: input validator/mediaflow.ocl ../mediaflow/metamodels/mediaflow.ecore
```

Both runners exit with status `1` when validation errors are found. EVL
uses `critique` for warnings; the OCL runner maps the matching
too-many-workers invariant to warning severity in Java.

For failing models, both runners number errors with numeric counters and
warnings with `W1`, `W2`, etc., then print a per-model displayed counter
before the final cross-model summary.
