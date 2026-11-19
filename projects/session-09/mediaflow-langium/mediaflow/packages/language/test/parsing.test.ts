import { readFileSync } from "node:fs";
import { beforeAll, describe, expect, test } from "vitest";
import { EmptyFileSystem, type LangiumDocument } from "langium";
import { parseHelper } from "langium/test";
import { createMediaFlowServices } from "../src/media-flow-module.js";
import { isEdge, isNode, isResource, isScaler, type Model } from "../src/generated/ast.js";

let services: ReturnType<typeof createMediaFlowServices>;
let parse:    ReturnType<typeof parseHelper<Model>>;

// The sample that the CLI reads, input/test.mediaflow in the workspace folder.
const sample = readFileSync(new URL('../../../input/test.mediaflow', import.meta.url), 'utf-8');

beforeAll(async () => {
    services = createMediaFlowServices(EmptyFileSystem);
    parse = parseHelper<Model>(services.MediaFlow);
});

describe('Parsing tests', () => {

    test('the sample input parses without syntax errors', async () => {
        const document = await parse(sample);

        expect(syntaxErrors(document)).toEqual([]);
        const [graph] = document.parseResult.value.elements;
        expect(graph.name).toBe('demo');
        expect(graph.elements.filter(isNode).map(node => node.name)).toEqual(['input1', 'resize720', 'output1']);
        expect(graph.elements.filter(isEdge).map(edge => edge.name)).toEqual(['edge1', 'edge2']);
    });

    test('attribute values end up in the AST', async () => {
        const document = await parse(sample);

        const nodes = document.parseResult.value.elements[0].elements.filter(isNode);
        const input = nodes.find(isResource);
        expect(input?.mediaType).toBe('VIDEO');
        expect(input?.uri).toBe('file:///home/alfa/videos/input.mp4');
        expect(input?.external).toBe(true);

        const scaler = nodes.find(isScaler);
        expect(scaler?.backend).toBe('FFMPEG');
        expect(scaler?.replicas).toBe(1);
        expect([scaler?.width, scaler?.height]).toEqual([1280, 720]);
        expect(scaler?.ports.map(port => `${port.name} ${port.direction}`)).toEqual(['resizeIn IN', 'resizeOut OUT']);
    });

    test('mediaType and external are optional for a resource', async () => {
        const document = await parse(`
            graph g {
                resource source uri="file:///tmp/in.mp4" {
                    port out OUT
                }
            }
        `);

        expect(syntaxErrors(document)).toEqual([]);
        const resource = document.parseResult.value.elements[0].elements.find(isResource);
        expect(resource?.mediaType).toBeUndefined();
        expect(resource?.external).toBeUndefined();
    });

    test('a misspelled keyword is a syntax error', async () => {
        const document = await parse(`
            graph g {
                scaler s backend=FFMPEG command="ffmpeg" replicas=1 widht=1280 height=720 {
                }
            }
        `);

        expect(document.parseResult.parserErrors).not.toHaveLength(0);
        expect(document.parseResult.parserErrors[0].message).toBe("Expecting token of type 'width' but found `widht`.");
    });

    test('a value outside an enumeration is a syntax error', async () => {
        const document = await parse(`
            graph g {
                scaler s backend=VLC command="vlc" replicas=1 width=1280 height=720 {
                }
            }
        `);

        expect(document.parseResult.parserErrors).not.toHaveLength(0);
        expect(document.parseResult.parserErrors[0].message).toContain("but found: 'VLC'");
    });
});

function syntaxErrors(document: LangiumDocument): string[] {
    return [
        ...document.parseResult.lexerErrors.map(error => error.message),
        ...document.parseResult.parserErrors.map(error => error.message)
    ];
}
