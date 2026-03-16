import {
    AstUtils,
    DefaultScopeProvider,
    type AstNodeDescription,
    type ReferenceInfo
} from 'langium';
import { isGraph, isModel, isNode } from './generated/ast.js';

function qualifiedPortName(nodeName: string, portName: string): string {
    return `${nodeName}.${portName}`;
}

export class MediaFlowScopeProvider extends DefaultScopeProvider {
    protected override getGlobalScope(
        referenceType: string,
        context: ReferenceInfo
    ) {
        const document = AstUtils.getDocument(context.container);
        const root = document.parseResult.value;

        if (referenceType === 'Port' && (isModel(root) || isGraph(root))) {
            const graphs = isModel(root) ? root.elements : [root];
            const descriptions: AstNodeDescription[] = [];
            const portsBySimpleName = new Map<string, AstNodeDescription[]>();

            for (const graph of graphs) {
                for (const element of graph.elements) {
                    if (isNode(element)) {
                        for (const port of element.ports) {
                            const qualifiedDescription = this.descriptions.createDescription(
                                port,
                                qualifiedPortName(element.name, port.name),
                                document
                            );
                            descriptions.push(qualifiedDescription);

                            const simpleDescription = this.descriptions.createDescription(
                                port,
                                port.name,
                                document
                            );
                            const existing = portsBySimpleName.get(port.name);
                            if (existing) {
                                existing.push(simpleDescription);
                            } else {
                                portsBySimpleName.set(port.name, [simpleDescription]);
                            }
                        }
                    }
                }
            }

            // Expose unqualified names only when unique, to avoid ambiguous references.
            for (const simpleDescriptions of portsBySimpleName.values()) {
                if (simpleDescriptions.length === 1) {
                    descriptions.push(simpleDescriptions[0]);
                }
            }

            return this.createScope(descriptions);
        }

        return super.getGlobalScope(referenceType, context);
    }
}
