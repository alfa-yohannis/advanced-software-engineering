import { readFileSync } from "node:fs";
import { afterEach, beforeAll, describe, expect, test } from "vitest";
import { EmptyFileSystem, type LangiumDocument } from "langium";
import { clearDocuments, parseHelper } from "langium/test";
import { createMediaFlowServices } from "../src/media-flow-module.js";
import { isEdge, type Edge, type Model, type Port } from "../src/generated/ast.js";

let services: ReturnType<typeof createMediaFlowServices>;
let parse:    ReturnType<typeof parseHelper<Model>>;
let document: LangiumDocument<Model> | undefined;

// The sample that the CLI reads, input/test.mediaflow in the workspace folder.
const sample = readFileSync(new URL('../../../input/test.mediaflow', import.meta.url), 'utf-8');

beforeAll(async () => {
    services = createMediaFlowServices(EmptyFileSystem);
    parse = parseHelper<Model>(services.MediaFlow);
});

afterEach(async () => {
    document && clearDocuments(services.shared, [ document ]);
});

describe('Linking tests', () => {

    test('edges of the sample link to the ports named as node.port', async () => {
        document = await parse(sample);

        expect(edges(document).map(edge => `${edge.name}: ${portPath(edge.source.ref)} -> ${portPath(edge.target.ref)}`)).toEqual([
            'edge1: input1.inputOut -> resize720.resizeIn',
            'edge2: resize720.resizeOut -> output1.outputIn'
        ]);
    });

    test('a port name that is unique in the graph can be used without its node', async () => {
        document = await parse(`
            graph g {
                resource source uri="file:///tmp/in.mp4" {
                    port sourceOut OUT
                }
                resource sink uri="file:///tmp/out.mp4" {
                    port sinkIn IN
                }
                edge e from sourceOut to sinkIn
            }
        `);

        const [edge] = edges(document);
        expect(portPath(edge.source.ref)).toBe('source.sourceOut');
        expect(portPath(edge.target.ref)).toBe('sink.sinkIn');
    });

    test('a port name that two nodes use must be written with its node', async () => {
        document = await parse(`
            graph g {
                resource a uri="file:///tmp/a.mp4" {
                    port out OUT
                }
                resource b uri="file:///tmp/b.mp4" {
                    port out OUT
                }
                resource sink uri="file:///tmp/out.mp4" {
                    port sinkIn IN
                }
                edge e1 from out to sinkIn
                edge e2 from b.out to sinkIn
            }
        `);

        const [e1, e2] = edges(document);
        expect(e1.source.ref).toBeUndefined();
        expect(e1.source.error?.message).toBe("Could not resolve reference to Port named 'out'.");
        expect(portPath(e2.source.ref)).toBe('b.out');
    });

    test('an unknown port is not linked', async () => {
        document = await parse(sample.replace('to resize720.resizeIn', 'to resize720.resizeInn'));

        const [edge1] = edges(document);
        expect(edge1.target.ref).toBeUndefined();
        expect(edge1.target.error?.message).toBe("Could not resolve reference to Port named 'resize720.resizeInn'.");
    });
});

function edges(document: LangiumDocument<Model>): Edge[] {
    return document.parseResult.value.elements.flatMap(graph => graph.elements.filter(isEdge));
}

// "node.port" for a linked port, so a test can see which node the port belongs to.
function portPath(port: Port | undefined): string | undefined {
    return port && `${port.$container.name}.${port.name}`;
}
