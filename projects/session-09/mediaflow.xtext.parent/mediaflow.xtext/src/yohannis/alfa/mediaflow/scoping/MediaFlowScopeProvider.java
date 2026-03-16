package yohannis.alfa.mediaflow.scoping;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;

import mediaflow.Edge;
import mediaflow.Graph;
import mediaflow.MediaflowPackage;
import mediaflow.Node;
import mediaflow.Port;

public class MediaFlowScopeProvider extends AbstractMediaFlowScopeProvider {

    @Override
    public IScope getScope(EObject context, EReference reference) {
        if (context instanceof Edge
                && (reference == MediaflowPackage.Literals.EDGE__SOURCE
                 || reference == MediaflowPackage.Literals.EDGE__TARGET)) {

            Graph graph = EcoreUtil2.getContainerOfType(context, Graph.class);
            if (graph == null) {
                return IScope.NULLSCOPE;
            }

            List<Port> ports = new ArrayList<>();
            for (Node node : graph.getNodes()) {
                ports.addAll(node.getPorts());
            }

            return Scopes.scopeFor(ports);
        }

        return super.getScope(context, reference);
    }
}