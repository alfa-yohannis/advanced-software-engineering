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
| Runs with | Maven | Maven with Tycho (build and tests), Eclipse (editor and `ExportToXmi`) | Node.js |
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
mediaflow.xtext.parent/             the Xtext implementation, Eclipse plug-ins built with Maven and Tycho
  pom.xml                           the parent: Tycho 5.0.4, Xtext 2.40, the modules (including ../mediaflow)
  .mvn/maven.config                 a setting that Tycho needs on Maven 3.10 (see step 4)
  mvnw, mvnw.cmd                    the Maven wrapper, which runs Maven 3.9.9 if yours is older than 3.9.9
  mediaflow.xtext/                  grammar, MWE2 workflow, scope provider, validator, and the generated src-gen/
  mediaflow.xtext.ide/, .ui/, .web/ language server, Eclipse editor and web editor support
  mediaflow.xtext.tests/, .ui.tests/  test projects; MediaFlowParsingTest.xtend parses and links a sample graph
  mediaflow.xtext.target/, .feature/, .repository/  Tycho target platform, feature and update site
mediaflow.ecore.generator/          ExportToXmi: input/test.mediaflow to output/test.xmi
mediaflow-langium/mediaflow/        the Langium implementation, an npm workspace
  packages/language/                grammar, scope provider, validator and tests
  packages/cli/                     the command-line tool, generating ELK JSON
  packages/extension/               the VS Code extension
  input/test.mediaflow              the sample input
example/                            flow1, flow2 and test as .mediaflow files, for the Xtext editor; flow1 and flow2 also as .xmi
mediaflow/, mediaflow.edit/, mediaflow.editor/, mediaflow.tests/  the metamodel of session 8 with its EMF edit, editor and test projects;
                                    mediaflow/pom.xml makes the metamodel bundle a module of the Tycho build
```

## Before you start

- For ANTLR: a JDK, version 17 or newer, because `pom.xml` compiles for Java 17, and Maven. `javac -version` and `mvn -v` should print version numbers. This guide was tested with OpenJDK 27.0.1 and Maven 3.10.0. Maven downloads the ANTLR plug-in and runtime on the first build.
- For Langium: Node.js 20.10 or newer with npm 10.2 or newer. `node -v` and `npm -v` should print version numbers. This guide was tested with Node.js 22.23.4 and npm 10.9.9. `npm install` downloads about 170 packages.
- For the Maven build of Xtext: a JDK from version 21 to 27 and Maven 3.9.9 or newer, which Tycho 5 needs. With an older Maven, use the Maven wrapper of step 4, which downloads Maven 3.9.9 once. The build was tested with OpenJDK 27.0.1 on Maven 3.10.0 and on the wrapper's Maven 3.9.9, and with OpenJDK 25.0.4 on Maven 3.10.0. Eclipse is not needed: Tycho downloads Eclipse, EMF and Xtext from download.eclipse.org.
- For the Xtext editor and `ExportToXmi`: an Eclipse installation with Xtext, for example the Eclipse IDE for Java and DSL Developers, or the Modeling Tools package with Xtext added. The projects were generated with Xtext 2.40; the parser and `ExportToXmi` were also checked with the Xtext 2.42 runtime.

## Step 1. Go to the project folder

```bash
cd projects/session-09
```

The commands of steps 2 to 4 start in this folder.

## Step 2. Build and run the ANTLR parser

```bash
mvn -f mediaflow.antlr/pom.xml compile exec:java
```

Maven first generates the lexer and parser from the grammar, then compiles everything and runs `Main` on `mediaflow.antlr/input/test.mediaflow`. Between its `[INFO]` lines you see:

```
[INFO] --- antlr4:4.13.2:antlr4 (default) @ mediaflow.antlr ---
[INFO] Processing grammar: MediaFlow.g4
...
[INFO] Compiling 20 source files with javac [debug release 17] to target/classes
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

Run the tests of the language package:

```bash
npm test
```

```
 ✓ test/linking.test.ts (4 tests) 45ms
 ✓ test/parsing.test.ts (5 tests) 47ms
 ✓ test/validating.test.ts (3 tests) 46ms

 Test Files  3 passed (3)
      Tests  12 passed (12)
```

The tests in `packages/language/test` use the helpers that Langium provides in `langium/test` (`parseHelper`, `validationHelper`, `expectNoIssues`, `expectError`) on `input/test.mediaflow` and on small graphs written in the test files. `parsing.test.ts` checks the AST of the sample and two syntax errors, a misspelled keyword and an unknown backend. `linking.test.ts` checks which port names link: `node.port` always, a plain port name only when no other node has a port with that name, and an unknown name never. `validating.test.ts` checks that the sample has no diagnostics and that an unknown port and a syntax error are reported as errors. Vitest compiles the test files itself, so `npm test` needs `npm run langium:generate` but not `npm run build`; `build` type-checks the tests as well.

To try the editor, open the folder `mediaflow-langium/mediaflow` in VS Code and press F5 (launch configuration "Run Extension"). In the new window, open a `.mediaflow` file: it gets highlighting, completion and error markers from the language server.

Go back to the session folder before step 4:

```bash
cd ../..
```

## Step 4. Build and test the Xtext language with Maven

```bash
mvn -f mediaflow.xtext.parent/pom.xml clean verify
```

Tycho resolves the target platform of `mediaflow.xtext.target` (Eclipse 2025-09 with EMF, MWE2 and Xtext 2.40) and builds the eleven modules in order: the metamodel bundle `mediaflow`; `mediaflow.xtext`, where the MWE2 workflow generates `src-gen/` of all Xtext projects again and Xtend compiles `MediaFlowGenerator.xtend` and `MediaFlowFormatter.xtend` into `xtend-gen/`; the IDE, UI and web projects; the feature; an update site in `mediaflow.xtext.repository/target/repository`; and the two test projects. `mediaflow.xtext.tests` runs `MediaFlowParsingTest` in an OSGi runtime. The output ends with:

```
Running yohannis.alfa.mediaflow.tests.MediaFlowParsingTest
...
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0

[INFO] All tests passed
...
[INFO] --- tycho-surefire:5.0.4:test (default-test) @ mediaflow.xtext.ui.tests ---
[WARNING] No tests found
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for mediaflow.xtext.parent 1.0.0-SNAPSHOT:
[INFO] 
[INFO] mediaflow.xtext.parent ............................. SUCCESS [  0.073 s]
[INFO] mediaflow .......................................... SUCCESS [  5.229 s]
[INFO] mediaflow.xtext .................................... SUCCESS [  7.232 s]
[INFO] mediaflow.xtext.ide ................................ SUCCESS [  6.271 s]
[INFO] mediaflow.xtext.ui ................................. SUCCESS [  0.810 s]
[INFO] mediaflow.xtext.web ................................ SUCCESS [  1.907 s]
[INFO] mediaflow.xtext.target ............................. SUCCESS [  0.003 s]
[INFO] mediaflow.xtext.feature ............................ SUCCESS [  0.184 s]
[INFO] mediaflow.xtext.repository ......................... SUCCESS [  0.817 s]
[INFO] mediaflow.xtext.tests .............................. SUCCESS [  4.541 s]
[INFO] mediaflow.xtext.ui.tests ........................... SUCCESS [ 13.616 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  46.061 s
```

That run took 46 seconds because everything it needs was already in `~/.m2/repository`. The first build on a machine downloads about 640 MB into that folder, the bundles of Eclipse, EMF and Xtext from download.eclipse.org and the Maven plug-ins and libraries from Maven Central, and took 12 minutes here; most of it goes to the modules `mediaflow` (which resolves the target platform), `mediaflow.xtext`, `mediaflow.xtext.ui` and `mediaflow.xtext.repository`. `mediaflow.xtext.ui.tests` has no tests, hence `No tests found`. Most of the other `[WARNING]` lines come from `maven-shade-plugin` in `mediaflow.xtext.ide`, which reports overlapping classes while it packs the language server into one jar; they do no harm.

The three tests in `mediaflow.xtext.tests/src/.../MediaFlowParsingTest.xtend` parse the pipeline of `example/flow1.mediaflow` with Xtext's `ParseHelper`: `loadModel` expects no errors and the three nodes, `linkEdgesToPorts` expects each edge to link to the right ports, and `reportUnknownPort` expects `Couldn't resolve reference to Port 'resizeInn'.` for a misspelled port.

The build writes `src-gen/` again from the grammar. While `MediaFlow.xtext` is unchanged, the files come out the same as the ones in Git, so `git status` shows no change after a build. Git ignores `target/` and the Java files that Xtend writes to `xtend-gen/`.

Two settings let this build run on current tools:

- `mediaflow.xtext.parent/.mvn/maven.config` holds `-Dmaven.resolver.validation=MILD`. Maven 3.10 checks the coordinates of every dependency more strictly than Maven 3.9 and rejects the ones Tycho creates for jars inside a bundle, such as `lib/ant-antlr.jar` of `org.apache.ant`. Maven reads the file because the `.mvn` folder sits next to the parent pom; Maven 3.9 ignores the setting.
- `asmVersion` in the parent pom gives the MWE2 workflow and the Xtend compiler ASM 9.10.1. Both read the class files of the JDK that runs Maven, and ASM 9.8, which comes with Xtext 2.40, reads them only up to Java 25.

If `mvn -v` shows a Maven older than 3.9.9, build with the Maven wrapper instead. It downloads Maven 3.9.9 into `~/.m2/wrapper` the first time, and takes the same arguments (on Windows, use `mvnw.cmd`):

```bash
sh mediaflow.xtext.parent/mvnw -f mediaflow.xtext.parent/pom.xml clean verify
```

## Step 5. Use the Xtext editor in Eclipse

1. In Eclipse with Xtext, choose File > Import > General > Existing Projects into Workspace. Select `projects/session-09` as the root directory and leave "Copy projects into workspace" unticked. Import `mediaflow`, `mediaflow.xtext`, `mediaflow.xtext.ide`, `mediaflow.xtext.ui` and `mediaflow.ecore.generator`. The other Xtext projects (tests, web, target, feature, repository) and the EMF edit and editor projects are optional. Do not import `example`: it is also named `mediaflow`, and it belongs in the second Eclipse of point 4.
2. Eclipse builds the projects. Xtend compiles `MediaFlowGenerator.xtend` and `MediaFlowFormatter.xtend` into `xtend-gen/`, which Git ignores. The parser and the other generated code in `src-gen/` are in Git already.
3. After a change to `MediaFlow.xtext`, generate `src-gen/` again: right-click `mediaflow.xtext/.launch/Generate MediaFlow (mf) Language Infrastructure.launch` and choose Run As. It runs the MWE2 workflow `GenerateMediaFlow.mwe2`.
4. Right-click `mediaflow.xtext/.launch/Run MediaFlow.launch` and choose Run As. A second Eclipse starts with the language installed. There, import `projects/session-09/example` with "Copy projects into workspace" ticked, and open `flow1.mediaflow`. The editor shows highlighting, offers completion with Ctrl+Space, and marks a misspelled port name in an edge with `Couldn't resolve reference to Port 'resizeInn'.`

## Step 6. Export a textual model to XMI

`ExportToXmi` reads `mediaflow.ecore.generator/input/test.mediaflow` with the Xtext parser and saves the model as `mediaflow.ecore.generator/output/test.xmi`, an EMF file that the transformations of sessions 11 and 12 and the validation of session 13 read.

1. In the first Eclipse, right-click `mediaflow.ecore.generator/ExportToXmi.launch` and choose Run As.
2. The Console shows `Saved to` followed by the absolute path of `output/test.xmi`.

The `output/test.xmi` in Git came from this input. With the file unchanged, the export writes the same bytes again, so Git shows no change. To export another model, change the two file names at the top of `ExportToXmi.java`.

`ExportToXmi` registers the MediaFlow package before it creates the Xtext injector. Outside Eclipse, Xtext does not know the package `mediaflow` that the grammar imports, and parsing would stop with `Unresolved proxy classpath:/yohannis/alfa/mediaflow/mediaflow#//Graph. Make sure the EPackage has been registered.`

## Changing the settings

| To change | Where |
|---|---|
| The syntax | the grammar of each implementation (see the first table). For ANTLR, update `AstBuilder` as well; for Xtext, run the MWE2 workflow (step 5, point 3) or the Maven build of step 4, which runs it too; for Langium, run `npm run langium:generate` and `npm run build`. |
| Which ports an edge may name | `AstBuilder.resolveEdges()` for ANTLR, `MediaFlowScopeProvider` for Xtext, the scope provider in `packages/language/src` for Langium |
| Domain rules, such as an edge from an OUT port to an IN port | `MediaFlowValidator.java` (Xtext) and `media-flow-validator.ts` (Langium); both are empty scaffolds now. ANTLR has no validator; add checks to `AstBuilder`. |
| The input of ANTLR | `-Dexec.args=<file>` (step 2) |
| The input of `ExportToXmi` | the two file names at the top of `ExportToXmi.java` |
| The tests | `MediaFlowParsingTest.xtend` in `mediaflow.xtext.tests` for Xtext (run by step 4), the files in `packages/language/test` for Langium (run by `npm test`) |
| The versions of the Maven build of Xtext | `tycho-version`, `xtextVersion` and `asmVersion` in `mediaflow.xtext.parent/pom.xml`, and the update sites in `mediaflow.xtext.target/mediaflow.xtext.target.target` |

## Troubleshooting

| Problem | What to do |
|---|---|
| ANTLR: `release version 17 not supported` | Maven runs with an older JDK. Point `JAVA_HOME` to a JDK 17 or newer, and check with `mvn -v`. |
| ANTLR: `package yohannis.alfa.mediaflow.antlr.generated does not exist` | The parser was not generated. ANTLR regenerates only when the grammar is newer than its output, so if you deleted some generated files, delete all files in `src/.../antlr/generated/` except `.gitignore` and build again. |
| ANTLR: `An exception occurred while executing the Java class. ... (No such file or directory)` or `NoSuchFileException` | The path given in `-Dexec.args` does not exist. It is relative to the folder you started Maven in. |
| Langium: `Cannot find module` or many TypeScript errors in `npm run build` | `npm install` or `npm run langium:generate` has not run. Run them in `mediaflow-langium/mediaflow`. |
| Langium: `npm test` fails with `Cannot find module './generated/module.js'` | The AST types have not been generated. Run `npm run langium:generate` first. |
| Xtext Maven build: `Internal error: com.google.inject.ProvisionException` and `No implementation for ... was bound` before any module is built | Your Maven is older than 3.9.9, the oldest that Tycho 5 runs on (check with `mvn -v`). Use the wrapper of step 4. |
| Xtext Maven build: `Invalid Collect Request`, followed by `Invalid classifier: must not contain '..', '/', '\' or ':': lib/ant-antlr.jar` | Maven 3.10 ran without `mediaflow.xtext.parent/.mvn/maven.config`, for example because a copy of the folder left out hidden folders. Restore the file, or add `-Dmaven.resolver.validation=MILD` to the command. |
| Xtext Maven build: `ERROR AbstractClassMirror - resource is empty: java:/Objects/java.lang.Object` in the `mwe2Launcher` step | ASM cannot read the class files of your JDK, which is newer than Java 27. Point `JAVA_HOME` to a JDK 21 to 27, or set `asmVersion` in the parent pom to an ASM release that supports your JDK. |
| Xtext Maven build: `There are test failures.` in `mediaflow.xtext.tests` | The reports are in `mediaflow.xtext.parent/mediaflow.xtext.tests/target/surefire-reports`. After a change of the grammar, change the sample in `MediaFlowParsingTest.xtend` to match. |
| Xtext in Eclipse: errors such as `MediaFlowGenerator cannot be resolved` | Xtend has not compiled the `.xtend` files. Check that Xtend is installed (it comes with Xtext) and choose Project > Clean. |
| The second Eclipse does not open `.mediaflow` files with the MediaFlow editor | Start it from `Run MediaFlow.launch`, so it includes the workspace plug-ins `mediaflow`, `mediaflow.xtext` and `mediaflow.xtext.ui`. |
| `ExportToXmi`: `java.io.FileNotFoundException` for `input/test.mediaflow` | Run it with the working directory `mediaflow.ecore.generator`, which is what `ExportToXmi.launch` does. |
