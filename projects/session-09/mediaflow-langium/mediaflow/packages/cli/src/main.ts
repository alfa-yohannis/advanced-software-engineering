import type { Model } from 'media-flow-language';
import { createMediaFlowServices, MediaFlowLanguageMetaData } from 'media-flow-language';
import chalk from 'chalk';
import { Command } from 'commander';
import { NodeFileSystem } from 'langium/node';
import * as fs from 'node:fs/promises';
import * as path from 'node:path';
import * as url from 'node:url';
import { generateOutput } from './generator.js';
import { extractAstNode } from './util.js';

const __dirname = url.fileURLToPath(new URL('.', import.meta.url));
const packagePath = path.resolve(__dirname, '..', 'package.json');
const packageContent = await fs.readFile(packagePath, 'utf-8');

function destinationFilePath(sourceFile: string, destinationDirectory: string): string {
    const fileName = `${path.basename(sourceFile, path.extname(sourceFile))}.json`;
    return path.join(destinationDirectory, fileName);
}

async function collectSourceFiles(sourcePath: string, extensions: Set<string>): Promise<string[]> {
    const sourceStat = await fs.stat(sourcePath);
    if (sourceStat.isFile()) {
        return [sourcePath];
    }

    const sourceFiles: string[] = [];
    const directories: string[] = [sourcePath];

    while (directories.length > 0) {
        const currentDirectory = directories.shift();
        if (!currentDirectory) {
            continue;
        }

        const entries = await fs.readdir(currentDirectory, { withFileTypes: true });
        for (const entry of entries) {
            const entryPath = path.join(currentDirectory, entry.name);
            if (entry.isDirectory()) {
                directories.push(entryPath);
            } else if (entry.isFile() && extensions.has(path.extname(entry.name))) {
                sourceFiles.push(entryPath);
            }
        }
    }

    return sourceFiles.sort();
}

async function resolveDestination(sourceFile: string, destinationPath: string, sourceIsDirectory: boolean): Promise<string> {
    if (sourceIsDirectory) {
        return destinationFilePath(sourceFile, destinationPath);
    }

    try {
        const destinationStat = await fs.stat(destinationPath);
        if (destinationStat.isDirectory()) {
            return destinationFilePath(sourceFile, destinationPath);
        }
    } catch {
        if (path.extname(destinationPath) === '') {
            return destinationFilePath(sourceFile, destinationPath);
        }
    }

    return destinationPath;
}

export const generateAction = async (source: string, destination: string): Promise<void> => {
    const services = createMediaFlowServices(NodeFileSystem).MediaFlow;
    const sourcePath = path.resolve(source);
    const destinationPath = path.resolve(destination);
    const extensions = new Set(MediaFlowLanguageMetaData.fileExtensions);

    let sourceStat;
    try {
        sourceStat = await fs.stat(sourcePath);
    } catch {
        console.error(chalk.red(`Source path ${sourcePath} does not exist.`));
        process.exit(1);
    }

    const sourceFiles = await collectSourceFiles(sourcePath, extensions);
    if (sourceFiles.length === 0) {
        console.error(chalk.red(`No source files found in ${sourcePath}.`));
        process.exit(1);
    }

    if (sourceStat.isDirectory()) {
        try {
            const destinationStat = await fs.stat(destinationPath);
            if (!destinationStat.isDirectory()) {
                console.error(chalk.red(`Destination ${destinationPath} must be a directory when source is a directory.`));
                process.exit(1);
            }
        } catch {
            await fs.mkdir(destinationPath, { recursive: true });
        }
    }

    for (const sourceFile of sourceFiles) {
        const model = await extractAstNode<Model>(sourceFile, services);
        const resolvedDestination = await resolveDestination(sourceFile, destinationPath, sourceStat.isDirectory());
        const generatedFilePath = generateOutput(model, sourceFile, resolvedDestination);
        console.log(chalk.green(`Code generated succesfully: ${generatedFilePath}`));
    }
};

export default function(): void {
    const program = new Command();

    program.version(JSON.parse(packageContent).version);

    const fileExtensions = MediaFlowLanguageMetaData.fileExtensions.join(', ');
    program
        .command('generate')
        .argument('<source>', `source file or directory (possible file extensions: ${fileExtensions})`)
        .argument('<destination>', 'destination file or directory')
        .description('Generates ELK JSON for a provided source file or source directory.')
        .action(generateAction);

    program.parse(process.argv);
}
