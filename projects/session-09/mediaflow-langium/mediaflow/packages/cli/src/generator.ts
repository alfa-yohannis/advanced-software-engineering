import type { Model, Port } from 'media-flow-language';
import { isEdge, isNode } from 'media-flow-language';
import * as fs from 'node:fs';
import * as path from 'node:path';

interface ElkLabel {
    text: string;
    width: number;
    height: number;
}

interface ElkPort {
    id: string;
    width: number;
    height: number;
    labels: ElkLabel[];
    layoutOptions: {
        'elk.port.side': 'WEST' | 'EAST';
    };
}

interface ElkChild {
    id: string;
    width: number;
    height: number;
    labels: ElkLabel[];
    ports: ElkPort[];
}

interface ElkEdge {
    id: string;
    sources: [string];
    targets: [string];
}

interface ElkRootGraph {
    id: string;
    layoutOptions: {
        'elk.algorithm': 'layered';
        'elk.direction': 'RIGHT';
    };
    children: ElkChild[];
    edges: ElkEdge[];
}

const NODE_WIDTH = 120;
const NODE_HEIGHT = 60;
const PORT_SIZE = 12;
const LABEL_HEIGHT = 16;

function estimateLabelWidth(text: string): number {
    return Math.max(40, Math.ceil(text.length * 7));
}

function toLabel(text: string): ElkLabel {
    return {
        text,
        width: estimateLabelWidth(text),
        height: LABEL_HEIGHT
    };
}

function resolvePortName(reference: { ref?: Port; $refText?: string }): string {
    if (reference.ref) {
        return reference.ref.name;
    }

    if (reference.$refText) {
        const segments = reference.$refText.split('.');
        return segments[segments.length - 1];
    }

    throw new Error('Unable to resolve edge port reference.');
}

function buildElkGraph(model: Model): ElkRootGraph {
    const [graph] = model.elements;
    if (!graph) {
        throw new Error('No graph found in source model.');
    }

    const children: ElkChild[] = graph.elements
        .filter(isNode)
        .map(node => ({
            id: node.name,
            width: NODE_WIDTH,
            height: NODE_HEIGHT,
            labels: [toLabel(node.name)],
            ports: node.ports.map(port => ({
                id: port.name,
                width: PORT_SIZE,
                height: PORT_SIZE,
                labels: [toLabel(port.name)],
                layoutOptions: {
                    'elk.port.side': port.direction === 'IN' ? 'WEST' : 'EAST'
                }
            }))
        }));

    const edges: ElkEdge[] = graph.elements
        .filter(isEdge)
        .map(edge => ({
            id: edge.name,
            sources: [resolvePortName(edge.source)],
            targets: [resolvePortName(edge.target)]
        }));

    return {
        id: graph.name,
        layoutOptions: {
            'elk.algorithm': 'layered',
            'elk.direction': 'RIGHT'
        },
        children,
        edges
    };
}

export function generateOutput(model: Model, _source: string, destination: string): string {
    const directory = path.dirname(destination);
    const elkGraph = buildElkGraph(model);

    if (!fs.existsSync(directory)) {
        fs.mkdirSync(directory, { recursive: true });
    }

    fs.writeFileSync(destination, `${JSON.stringify(elkGraph, null, 2)}\n`, 'utf-8');
    return destination;
}
