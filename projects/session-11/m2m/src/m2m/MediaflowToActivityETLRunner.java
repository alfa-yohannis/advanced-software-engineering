package m2m;

import java.io.File;

import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

public class MediaflowToActivityETLRunner {

    public static void main(String[] args) throws Exception {
        String inputPath = args.length > 0 ? args[0] : "input/flow2.xmi";
        String outputPath = args.length > 1 ? args[1] : "output/activity2etl.uml";
        String transformationPath = args.length > 2 ? args[2] : "transformer/mediaflow2activity.etl";
        String metamodelPath = args.length > 3 ? args[3] : "../mediaflow/metamodels/mediaflow.ecore";

        UMLPackage.eINSTANCE.eClass();
        UMLResourcesUtil.initGlobalRegistries();

        EmfModel inputModel = new EmfModel();
        inputModel.setName("In");
        inputModel.setMetamodelFile(new File(metamodelPath).getAbsolutePath());
        inputModel.setModelFile(new File(inputPath).getAbsolutePath());
        inputModel.setReadOnLoad(true);
        inputModel.setStoredOnDisposal(false);
        inputModel.load();

        EmfModel outputModel = new EmfModel();
        outputModel.setName("Out");
        outputModel.setMetamodelUri(UMLPackage.eNS_URI);
        outputModel.setModelFile(new File(outputPath).getAbsolutePath());
        outputModel.setReadOnLoad(false);
        outputModel.setStoredOnDisposal(true);
        outputModel.load();

        EtlModule module = new EtlModule();

        try {
            module.parse(new File(transformationPath));
            if (!module.getParseProblems().isEmpty()) {
                StringBuilder message = new StringBuilder("ETL parse failed:");
                for (ParseProblem problem : module.getParseProblems()) {
                    message.append(System.lineSeparator()).append(problem);
                }
                throw new IllegalStateException(message.toString());
            }

            module.getContext().getModelRepository().addModel(inputModel);
            module.getContext().getModelRepository().addModel(outputModel);
            module.execute();
            outputModel.store();
        } finally {
            module.getContext().getModelRepository().dispose();
            module.getContext().dispose();
        }
    }
}
