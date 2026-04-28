package validation;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;

public class MediaflowEvlValidationRunner {

    public static void main(String[] args) throws Exception {
        String inputPath = args.length > 0 ? args[0] : "input";
        String evlPath = args.length > 1 ? args[1] : "validator/mediaflow.evl";
        String metamodelPath = args.length > 2 ? args[2] : "../mediaflow/metamodels/mediaflow.ecore";

        registerFactories();

        List<Path> inputs = collectInputs(Paths.get(inputPath));
        if (inputs.isEmpty()) {
            System.out.println("No .xmi inputs found at " + inputPath);
            return;
        }

        int errorCount = 0;
        int warningCount = 0;

        for (Path input : inputs) {
            ValidationSummary summary = validate(input, Paths.get(evlPath), Paths.get(metamodelPath));
            errorCount += summary.errors;
            warningCount += summary.warnings;
        }

        System.out.println();
        System.out.println("Validation summary: " + errorCount + " error(s), " + warningCount + " warning(s).");

        if (errorCount > 0) {
            System.exit(1);
        }
    }

    private static void registerFactories() {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put("ecore", new EcoreResourceFactoryImpl());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put("xmi", new XMIResourceFactoryImpl());
    }

    private static List<Path> collectInputs(Path inputPath) throws Exception {
        List<Path> inputs = new ArrayList<>();
        if (Files.isRegularFile(inputPath) && inputPath.toString().endsWith(".xmi")) {
            inputs.add(inputPath);
        } else if (Files.isDirectory(inputPath)) {
            collectXmiFiles(inputPath, inputs);
        }
        Collections.sort(inputs);
        return inputs;
    }

    private static void collectXmiFiles(Path directory, List<Path> inputs) throws Exception {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    collectXmiFiles(path, inputs);
                } else if (path.toString().endsWith(".xmi")) {
                    inputs.add(path);
                }
            }
        }
    }

    private static ValidationSummary validate(Path input, Path evlPath, Path metamodelPath) throws Exception {
        EmfModel model = new EmfModel();
        model.setName("Mediaflow");
        model.setMetamodelFile(metamodelPath.toAbsolutePath().toString());
        model.setModelFile(input.toAbsolutePath().toString());
        model.setReadOnLoad(true);
        model.setStoredOnDisposal(false);
        model.load();

        EvlModule module = new EvlModule();

        try {
            module.parse(evlPath.toFile());
            failOnParseProblems(module.getParseProblems(), "EVL parse failed");

            module.getContext().getModelRepository().addModel(model);
            Set<UnsatisfiedConstraint> unsatisfied = module.execute();

            return printResults(input, unsatisfied);
        } finally {
            module.getContext().getModelRepository().dispose();
            module.getContext().dispose();
        }
    }

    private static void failOnParseProblems(List<ParseProblem> problems, String heading) {
        if (problems.isEmpty()) {
            return;
        }

        StringBuilder message = new StringBuilder(heading + ":");
        for (ParseProblem problem : problems) {
            message.append(System.lineSeparator()).append(problem);
        }
        throw new IllegalStateException(message.toString());
    }

    private static ValidationSummary printResults(Path input, Collection<UnsatisfiedConstraint> unsatisfied) {
        int errors = 0;
        int warnings = 0;

        System.out.println();
        System.out.println("== " + input + " ==");

        if (unsatisfied.isEmpty()) {
            System.out.println("OK");
            return new ValidationSummary(0, 0);
        }

        for (UnsatisfiedConstraint result : unsatisfied) {
            boolean warning = result.getConstraint().isCritique();
            if (warning) {
                warnings++;
            } else {
                errors++;
            }

            String severity = warning ? "WARN" : "ERROR";
            String counter = warning ? "W" + warnings : String.valueOf(errors);
            System.out.println(counter + ". " + severity + " " + result.getConstraint().getName() + " on "
                    + describe(result.getInstance()) + ": " + result.getMessage());
        }

        System.out.println("Displayed " + errors + " error(s), " + warnings
                + " warning(s), " + (errors + warnings) + " total issue(s).");

        return new ValidationSummary(errors, warnings);
    }

    private static String describe(Object instance) {
        if (instance instanceof EObject eObject) {
            EStructuralFeature name = eObject.eClass().getEStructuralFeature("name");
            Object value = name == null ? null : eObject.eGet(name);
            if (value != null && !value.toString().isBlank()) {
                return eObject.eClass().getName() + " '" + value + "'";
            }
            return eObject.eClass().getName();
        }
        return String.valueOf(instance);
    }

    private record ValidationSummary(int errors, int warnings) {
    }
}
