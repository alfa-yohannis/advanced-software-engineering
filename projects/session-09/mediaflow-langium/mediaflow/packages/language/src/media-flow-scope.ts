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

            for (const graph of graphs) {
                for (const element of graph.elements) {
                    if (isNode(element)) {
                        for (const port of element.ports) {
                            descriptions.push(
                                this.descriptions.createDescription(
                                    port,
                                    qualifiedPortName(element.name, port.name),
                                    document
                                )
                            );
                        }
                    }
                }
            }

            return this.createScope(descriptions);
        }

        return super.getGlobalScope(referenceType, context);
    }
}
