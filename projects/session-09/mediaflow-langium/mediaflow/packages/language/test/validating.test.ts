import { readFileSync } from "node:fs";
import { beforeAll, describe, expect, test } from "vitest";
import { EmptyFileSystem } from "langium";
import { expectError, expectNoIssues, validationHelper } from "langium/test";
import { createMediaFlowServices } from "../src/media-flow-module.js";
import { isEdge, type Model } from "../src/generated/ast.js";

let services: ReturnType<typeof createMediaFlowServices>;
let validate: ReturnType<typeof validationHelper<Model>>;

// The sample that the CLI reads, input/test.mediaflow in the workspace folder.
const sample = readFileSync(new URL('../../../input/test.mediaflow', import.meta.url), 'utf-8');

beforeAll(async () => {
    services = createMediaFlowServices(EmptyFileSystem);
    validate = validationHelper<Model>(services.MediaFlow);
});

describe('Validating', () => {

    test('the sample input has no diagnostics', async () => {
        const result = await validate(sample);

        expectNoIssues(result);
    });

    test('an unknown port is reported as an error on the edge', async () => {
        const result = await validate(sample.replace('to resize720.resizeIn', 'to resize720.resizeInn'));

        const edge1 = result.document.parseResult.value.elements[0].elements.filter(isEdge)[0];
        expectError(result, "Could not resolve reference to Port named 'resize720.resizeInn'.", {
            node: edge1,
            property: 'target'
        });
        expect(result.diagnostics).toHaveLength(1);
    });

    test('a syntax error is reported as an error', async () => {
        const result = await validate(sample.replace('width=1280', 'widht=1280'));

        expectError(result, "Expecting token of type 'width' but found `widht`.", {
            offset: sample.indexOf('width=1280'),
            length: 'widht'.length
        });
    });
});
