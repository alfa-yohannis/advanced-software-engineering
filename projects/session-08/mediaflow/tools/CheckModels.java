import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import mediaflow.Edge;
import mediaflow.Graph;
import mediaflow.MediaflowPackage;
import mediaflow.Node;
import mediaflow.Port;

/**
 * Loads MediaFlow models with the Java code that EMF generated from
 * mediaflow.ecore, prints what each model contains and validates it with EMF.
 * Used by check-models.sh; it is not in src/, so Eclipse does not build it.
 */
public class CheckModels {

    public static void main(String[] args) {
        // Touching the generated package registers it under its namespace URI,
        // "mediaflow", so EMF knows the root element mediaflow:Graph.
        MediaflowPackage.eINSTANCE.eClass();

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("*", new XMIResourceFactoryImpl());

        String[] paths = args.length > 0 ? args : new String[] { "models/flow1.model", "models/flow2.model" };
        int failed = 0;

        for (String path : paths) {
            Graph graph;
            try {
                graph = (Graph) resourceSet
                        .getResource(URI.createFileURI(new File(path).getAbsolutePath()), true)
                        .getContents().get(0);
            } catch (RuntimeException e) {
                System.out.println(path + ": cannot be loaded: " + e.getMessage());
                failed++;
                continue;
            }

            Map<String, Integer> nodeTypes = new TreeMap<>();
            int ports = 0;
            for (Node node : graph.getNodes()) {
                nodeTypes.merge(node.eClass().getName(), 1, Integer::sum);
                ports += node.getPorts().size();
            }
            System.out.println(path + ": graph " + graph.getName() + ", " + graph.getNodes().size() + " nodes "
                    + nodeTypes + ", " + ports + " ports, " + graph.getEdges().size() + " edges");
            for (Edge edge : graph.getEdges()) {
                System.out.println("  " + edge.getName() + ": " + describe(edge.getSource()) + " -> "
                        + describe(edge.getTarget()));
            }

            Diagnostic diagnostic = Diagnostician.INSTANCE.validate(graph);
            if (diagnostic.getSeverity() == Diagnostic.OK) {
                System.out.println("  EMF validation: OK");
            } else {
                for (Diagnostic child : diagnostic.getChildren()) {
                    System.out.println("  EMF validation: " + child.getMessage());
                }
                if (diagnostic.getSeverity() >= Diagnostic.ERROR) {
                    failed++;
                }
            }
        }

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static String describe(Port port) {
        if (port == null) {
            return "(none)";
        }
        Node node = (Node) port.eContainer();
        return node.getName() + "." + port.getName() + " (" + port.getDirection() + ")";
    }
}
