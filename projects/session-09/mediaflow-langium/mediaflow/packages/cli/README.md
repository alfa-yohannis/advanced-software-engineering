# Command-line interface (CLI)

Check [this part](https://langium.org/docs/learn/minilogo/customizing_cli/) of the Langium Minilogo Tutorial as a useful guide to the CLI.

## What's in the folder?

- [package.json](./package.json) - The manifest file of your cli package.
- [tsconfig.json](./tsconfig.json) - The package specific TypeScript compiler configuration extending the [base config](../../tsconfig.json).
- [bin/cli.js](bin/cli.js) - Script referenced in the [package.json](./package.json) and used to execute the command-line interface.
- [src/main.ts](src/main.ts) - The entry point of the command line interface (CLI) of your language.
- [src/generator.ts](src/generator.ts) - The code generator used by the CLI to write an ELK graph in JSON from a DSL document.
- [src/util.ts](src/util.ts) - Utility code for the CLI.

## Instructions

Build the workspace first (`npm run langium:generate` and `npm run build` in the workspace root). Run `node ./bin/cli.js --help` to see options for the CLI; `node ./bin/cli.js generate <source> <destination>` writes the ELK JSON of a DSL file, or of every DSL file in a folder, to `<destination>`.
