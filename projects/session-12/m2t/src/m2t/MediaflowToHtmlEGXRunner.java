package m2t;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.emc.emf.EmfModel;

public class MediaflowToHtmlEGXRunner {

    public static void main(String[] args) throws Exception {
        String inputDir      = args.length > 0 ? args[0] : "input";
        String outputDir     = args.length > 1 ? args[1] : "output";
        String egxPath       = args.length > 2 ? args[2] : "transformer/m2t.egx";
        String metamodelPath = args.length > 3 ? args[3] : "../mediaflow/metamodels/mediaflow.ecore";

        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put("ecore", new EcoreResourceFactoryImpl());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put("xmi", new XMIResourceFactoryImpl());

        Files.createDirectories(Paths.get(outputDir));

        List<Path> inputs = new ArrayList<>();
        try (DirectoryStream<Path> stream =
                 Files.newDirectoryStream(Paths.get(inputDir), "*.xmi")) {
            for (Path p : stream) inputs.add(p);
        }
        Collections.sort(inputs);

        if (inputs.isEmpty()) {
            System.out.println("No .xmi inputs found in " + inputDir);
            return;
        }

        for (Path input : inputs) {
            runOne(input, outputDir, egxPath, metamodelPath);
        }
    }

    private static void runOne(Path input, String outputDir, String egxPath,
                                 String metamodelPath) throws Exception {

        EmfModel model = new EmfModel();
        model.setName("Mediaflow");
        model.setMetamodelFile(new File(metamodelPath).getAbsolutePath());
        model.setModelFile(input.toAbsolutePath().toString());
        model.setReadOnLoad(true);
        model.setStoredOnDisposal(false);
        model.load();

        // Extract graph.name from the model root for logging
        org.eclipse.emf.ecore.EObject root = model.getResource().getContents().get(0);
        org.eclipse.emf.ecore.EStructuralFeature nameFeature = root.eClass().getEStructuralFeature("name");
        String graphName = (String) root.eGet(nameFeature);

        // Set output root to the EGX file's parent directory (transformer/) so that
        // EGX target paths like "../output/<name>.json" resolve correctly, matching
        // the same behaviour as running the EGX from Eclipse as a plugin.
        EglFileGeneratingTemplateFactory factory = new EglFileGeneratingTemplateFactory();
        factory.setOutputRoot(new File(egxPath).getAbsoluteFile().getParent());

        EgxModule module = new EgxModule(factory);

        try {
            module.parse(new File(egxPath));
            if (!module.getParseProblems().isEmpty()) {
                StringBuilder message = new StringBuilder("EGX parse failed:");
                for (ParseProblem problem : module.getParseProblems()) {
                    message.append(System.lineSeparator()).append(problem);
                }
                throw new IllegalStateException(message.toString());
            }

            module.getContext().getModelRepository().addModel(model);

            module.execute();

            System.out.println("Generated " + outputDir + "/elk/" + graphName + ".json"
                             + " and "      + outputDir + "/tables/" + graphName + ".tables.html");
        } finally {
            module.getContext().getModelRepository().dispose();
            module.getContext().dispose();
        }
    }
}
