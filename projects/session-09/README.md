# A textual syntax for MediaFlow with ANTLR, Xtext and Langium

Code for session 9 (Textual Domain-Specific Language) of IT30213 Advanced Software Engineering & DevOps. It gives MediaFlow models a textual syntax, so a pipeline can be written as text like the one below instead of being edited as XMI. The same syntax is built three times: with ANTLR, a parser generator, where you write the model classes and the linking of names yourself; with Xtext, which binds the grammar to the EMF metamodel of session 8 and gives you a parser, a linker, a validator and an Eclipse editor; and with Langium, the TypeScript counterpart of Xtext, with a command-line tool and a VS Code extension. `ExportToXmi` turns a textual model into the XMI file that sessions 11 to 13 read.

```
graph demo {
  resource inputVideo [mediaType=VIDEO] uri="file:///home/alfa/videos/input.mp4" external=true {
    port inputOut OUT
  }
  scaler resize720 backend=FFMPEG command="..." replicas=1 width=1280 height=720 {
    port resizeIn IN
    port resizeOut OUT
  }
  resource outputVideo [mediaType=VIDEO] uri="file:///home/alfa/videos/output.mp4" external=true {
    port outputIn IN
  }
  edge e1 from inputOut to resizeIn
  edge e2 from resizeOut to outputIn
}
```

## How the pieces fit together

```
a .mediaflow text file
  |
  +--> ANTLR:   antlr/MediaFlow.g4 --antlr4-maven-plugin--> lexer, parser, visitor
  |             --AstBuilder--> own Java classes (Graph, Node, Port, Edge) --> Main prints the parse tree and counts
  |
  +--> Xtext:   MediaFlow.xtext --MWE2 workflow--> src-gen/ (parser, linker, serializer)
  |             --> EMF objects of the mediaflow metamodel --ExportToXmi--> .xmi (input of sessions 11 to 13)
  |             --> an Eclipse editor with highlighting, completion and error markers (mediaflow.xtext.ui)
  |
  +--> Langium: media-flow.langium --langium generate--> TypeScript AST types
                --> CLI "generate" --> ELK graph in JSON; a language server and a VS Code extension
```

| | ANTLR | Xtext | Langium |
|---|---|---|---|
| Grammar | `mediaflow.antlr/antlr/MediaFlow.g4` | `mediaflow.xtext.parent/mediaflow.xtext/src/yohannis/alfa/mediaflow/MediaFlow.xtext` | `mediaflow-langium/mediaflow/packages/language/src/media-flow.langium` |
| Model it builds | hand-written Java classes in `mediaflow.antlr/src/.../ast/` | objects of the EMF classes generated from `mediaflow.ecore` | TypeScript types generated from the grammar |
| Port references | names, linked by hand in `AstBuilder` | names, linked by Xtext through `MediaFlowScopeProvider` | names or `node.port`, linked by Langium through its scope provider |
| Runs with | Maven | Eclipse | Node.js |
| Gives you | a parse tree and counts | XMI through `ExportToXmi`, and an editor | ELK JSON, and an editor in VS Code |

The three grammars accept almost the same text. The differences: Langium also accepts a port as `node.port`, and makes `[mediaType=...]` and `external=...` optional; ANTLR and Xtext require both and accept only plain port names. Langium's sample input uses `input1.inputOut`, so Xtext rejects it.

```
mediaflow.antlr/                    the ANTLR implementation, a Maven project
  antlr/MediaFlow.g4                the grammar
  src/.../antlr/Main.java           reads a file, prints the parse tree, builds the AST and prints counts
  src/.../antlr/ast/                the AST classes and AstBuilder, the visitor that builds them and links the ports
  src/.../antlr/generated/          the lexer and parser that ANTLR generates at build time (ignored by Git)
  input/test.mediaflow              the sample input
  pom.xml                           ANTLR 4.13.2, generation during the build, and exec:java for Main
mediaflow.xtext.parent/             the Xtext implementation, Eclipse plug-ins with Maven/Tycho files
  mediaflow.xtext/                  grammar, MWE2 workflow, scope provider, validator, and the generated src-gen/
  mediaflow.xtext.ide/, .ui/, .web/ language server, Eclipse editor and web editor support
  mediaflow.xtext.tests/, .ui.tests/  test projects
  mediaflow.xtext.target/, .feature/, .repository/  Tycho target platform, feature and update site
mediaflow.ecore.generator/          ExportToXmi: input/test.mediaflow to output/test.xmi
mediaflow-langium/mediaflow/        the Langium implementation, an npm workspace
  packages/language/                grammar, scope provider, validator and tests
  packages/cli/                     the command-line tool, generating ELK JSON
  packages/extension/               the VS Code extension
  input/test.mediaflow              the sample input
example/                            flow1, flow2 and test as .mediaflow files, for the Xtext editor; flow1 and flow2 also as .xmi
mediaflow/, mediaflow.edit/, mediaflow.editor/, mediaflow.tests/  the metamodel of session 8 with its EMF edit, editor and test projects
```

## Before you start

- For ANTLR: a JDK, version 25 or newer, because `pom.xml` compiles for Java 25, and Maven. `javac -version` and `mvn -v` should print version numbers. This guide was tested with OpenJDK 27.0.1 and Maven 3.10.0. Maven downloads the ANTLR plug-in and runtime on the first build.
- For Langium: Node.js 20.10 or newer with npm 10.2 or newer. `node -v` and `npm -v` should print version numbers. This guide was tested with Node.js 22.23.4 and npm 10.9.9. `npm install` downloads about 170 packages.
- For Xtext and `ExportToXmi`: an Eclipse installation with Xtext, for example the Eclipse IDE for Java and DSL Developers, or the Modeling Tools package with Xtext added. The projects were generated with Xtext 2.40; the parser and `ExportToXmi` were also checked with the Xtext 2.42 runtime.

## Step 1. Go to the project folder

```bash
cd projects/session-09
```

The commands of steps 2 and 3 start in this folder.

## Step 2. Build and run the ANTLR parser

```bash
mvn -f mediaflow.antlr/pom.xml compile exec:java
```

Maven first generates the lexer and parser from the grammar, then compiles everything and runs `Main` on `mediaflow.antlr/input/test.mediaflow`. Between its `[INFO]` lines you see:

```
[INFO] --- antlr4:4.13.2:antlr4 (default) @ mediaflow.antlr ---
[INFO] Processing grammar: MediaFlow.g4
...
[INFO] Compiling 20 source files with javac [debug release 25] to target/classes
...
[INFO] --- exec:3.2.0:java (default-cli) @ mediaflow.antlr ---
Parse tree:
model
  graph
    graph
    demo
    {
    node
      resource
        resource
        inputVideo
...
    edge
      edge
      e2
      from
      resizeOut
      to
      outputIn
    }
  <EOF>
Parsed graph: demo
Nodes: 3
Edges: 2
[INFO] BUILD SUCCESS
```

The parse tree has one line per rule and per token, so it is about a hundred lines long. It shows what ANTLR gives you: a tree of the text, with every keyword and name. The last three lines come from `AstBuilder`, which turned that tree into the model classes and linked each edge to its ports by name. The first build takes about 30 seconds, most of it for downloads.

The parser takes another file as `-Dexec.args`, relative to this folder:

```bash
mvn -q -f mediaflow.antlr/pom.xml exec:java -Dexec.args=example/flow2.mediaflow
```

```
Parse tree:
...
Parsed graph: demo
Nodes: 6
Edges: 5
```

If an edge names a port that does not exist, `AstBuilder` stops with `Unknown target port: resizeInn` (or `Unknown source port`). A syntax error makes ANTLR print the position, for example `line 8:29 mismatched input 'widht' expecting 'width'`. ANTLR then carries on with a partial tree, and `AstBuilder` fails on the missing part, for example with `Cannot invoke "org.antlr.v4.runtime.Token.getText()" because "ctx.width" is null`. Better error handling is up to you, which is one of the differences chapter 9 discusses.

## Step 3. Build and run the Langium implementation

```bash
cd mediaflow-langium/mediaflow
npm install
npm run langium:generate
npm run build
node ./packages/cli/bin/cli.js generate ./input/test.mediaflow ./output/test.json
```

```
added 168 packages in 1m
...
[16:31:48] Langium generator finished successfully in 171ms
...
[16:31:51] Build succeeded
Code generated succesfully: /.../projects/session-09/mediaflow-langium/mediaflow/output/test.json
```

`npm install` is needed once. `langium:generate` writes the TypeScript AST types and the grammar access into `packages/language/src/generated`, which Git ignores, so run it after every change of the grammar. `build` compiles the three packages and bundles the VS Code extension. The CLI then parses the sample, links the ports, and writes an ELK graph (the `children`, `ports` and `edges` that elk.js lays out) to `output/test.json`, which Git ignores. The message ends with the absolute path of the file on your machine.

Look at what it wrote:

```bash
python3 -c 'import json
g = json.load(open("output/test.json"))
print(g["id"], [c["id"] for c in g["children"]], [(e["sources"][0], e["targets"][0]) for e in g["edges"]])'
```

```
demo ['input1', 'resize720', 'output1'] [('inputOut', 'resizeIn'), ('resizeOut', 'outputIn')]
```

The sample writes its edges as `from input1.inputOut to resize720.resizeIn`, with the node in front of the port, and both edges were linked. `generate` also takes a folder, and then writes one JSON file per `.mediaflow` file into the destination folder. With a wrong port name, the CLI prints `There are validation errors:` and `line 19: Could not resolve reference to Port named 'resize720.resizeInn'.`, and writes nothing.

To try the editor, open the folder `mediaflow-langium/mediaflow` in VS Code and press F5 (launch configuration "Run Extension"). In the new window, open a `.mediaflow` file: it gets highlighting, completion and error markers from the language server.

Go back to the session folder before step 4:

```bash
cd ../..
```

## Step 4. Build the Xtext language in Eclipse

1. In Eclipse with Xtext, choose File > Import > General > Existing Projects into Workspace. Select `projects/session-09` as the root directory and leave "Copy projects into workspace" unticked. Import `mediaflow`, `mediaflow.xtext`, `mediaflow.xtext.ide`, `mediaflow.xtext.ui` and `mediaflow.ecore.generator`. The other Xtext projects (tests, web, target, feature, repository) and the EMF edit and editor projects are optional. Do not import `example`: it is also named `mediaflow`, and it belongs in the second Eclipse of point 4.
2. Eclipse builds the projects. Xtend compiles `MediaFlowGenerator.xtend` and `MediaFlowFormatter.xtend` into `xtend-gen/`, which Git ignores. The parser and the other generated code in `src-gen/` are in Git already.
3. After a change to `MediaFlow.xtext`, generate `src-gen/` again: right-click `mediaflow.xtext/.launch/Generate MediaFlow (mf) Language Infrastructure.launch` and choose Run As. It runs the MWE2 workflow `GenerateMediaFlow.mwe2`.
4. Right-click `mediaflow.xtext/.launch/Run MediaFlow.launch` and choose Run As. A second Eclipse starts with the language installed. There, import `projects/session-09/example` with "Copy projects into workspace" ticked, and open `flow1.mediaflow`. The editor shows highlighting, offers completion with Ctrl+Space, and marks a misspelled port name in an edge with `Couldn't resolve reference to Port 'resizeInn'.`

The Xtext projects also have Maven/Tycho files, and chapter 9 builds them with `mvn -f mediaflow.xtext.parent/pom.xml clean verify`. That command does not work as the projects are now; see Troubleshooting.

## Step 5. Export a textual model to XMI

`ExportToXmi` reads `mediaflow.ecore.generator/input/test.mediaflow` with the Xtext parser and saves the model as `mediaflow.ecore.generator/output/test.xmi`, an EMF file that the transformations of sessions 11 and 12 and the validation of session 13 read.

1. In the first Eclipse, right-click `mediaflow.ecore.generator/ExportToXmi.launch` and choose Run As.
2. The Console shows `Saved to` followed by the absolute path of `output/test.xmi`.

The `output/test.xmi` in Git came from this input. With the file unchanged, the export writes the same bytes again, so Git shows no change. To export another model, change the two file names at the top of `ExportToXmi.java`.

`ExportToXmi` registers the MediaFlow package before it creates the Xtext injector. Outside Eclipse, Xtext does not know the package `mediaflow` that the grammar imports, and parsing would stop with `Unresolved proxy classpath:/yohannis/alfa/mediaflow/mediaflow#//Graph. Make sure the EPackage has been registered.`

## Changing the settings

| To change | Where |
|---|---|
| The syntax | the grammar of each implementation (see the first table). For ANTLR, update `AstBuilder` as well; for Xtext, run the MWE2 workflow (step 4, point 3); for Langium, run `npm run langium:generate` and `npm run build`. |
| Which ports an edge may name | `AstBuilder.resolveEdges()` for ANTLR, `MediaFlowScopeProvider` for Xtext, the scope provider in `packages/language/src` for Langium |
| Domain rules, such as an edge from an OUT port to an IN port | `MediaFlowValidator.java` (Xtext) and `media-flow-validator.ts` (Langium); both are empty scaffolds now. ANTLR has no validator; add checks to `AstBuilder`. |
| The input of ANTLR | `-Dexec.args=<file>` (step 2) |
| The input of `ExportToXmi` | the two file names at the top of `ExportToXmi.java` |

## Troubleshooting

| Problem | What to do |
|---|---|
| ANTLR: `release version 25 not supported` | Maven runs with an older JDK. Point `JAVA_HOME` to a JDK 25 or newer, and check with `mvn -v`. |
| ANTLR: `package yohannis.alfa.mediaflow.antlr.generated does not exist` | The parser was not generated. ANTLR regenerates only when the grammar is newer than its output, so if you deleted some generated files, delete all files in `src/.../antlr/generated/` except `.gitignore` and build again. |
| ANTLR: `An exception occurred while executing the Java class. ... (No such file or directory)` or `NoSuchFileException` | The path given in `-Dexec.args` does not exist. It is relative to the folder you started Maven in. |
| Langium: `Cannot find module` or many TypeScript errors in `npm run build` | `npm install` or `npm run langium:generate` has not run. Run them in `mediaflow-langium/mediaflow`. |
| Langium: `npm test` fails with `No test found in suite` | The three test files in `packages/language/test` are empty scaffolds. Writing the parsing, linking and validation tests of chapter 9 is left to you. |
| Xtext in Eclipse: errors such as `MediaFlowGenerator cannot be resolved` | Xtend has not compiled the `.xtend` files. Check that Xtend is installed (it comes with Xtext) and choose Project > Clean. |
| Xtext: `mvn -f mediaflow.xtext.parent/pom.xml clean verify` fails | On Maven 3.10, Tycho 4.0.13 stops at once with `No implementation for MavenTargetLocationFactory was bound`; Tycho 4.0 is made for Maven 3.9. On top of that, the bundle `mediaflow`, which the language requires, is not a module of the Tycho build, so Tycho cannot resolve it. Build the Xtext projects in Eclipse instead (step 4). |
| The second Eclipse does not open `.mediaflow` files with the MediaFlow editor | Start it from `Run MediaFlow.launch`, so it includes the workspace plug-ins `mediaflow`, `mediaflow.xtext` and `mediaflow.xtext.ui`. |
| `ExportToXmi`: `java.io.FileNotFoundException` for `input/test.mediaflow` | Run it with the working directory `mediaflow.ecore.generator`, which is what `ExportToXmi.launch` does. |
