package validation;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.OCL;

public class MediaflowOclValidationRunner {

    private static final Pattern INVARIANT_PATTERN = Pattern.compile(
            "(?ms)^context\\s+(\\w+)\\s*\\R\\s*inv\\s+(\\w+)\\s*:\\s*(.*?)"
                    + "(?=^context\\s+\\w+\\s*\\R\\s*inv\\s+\\w+\\s*:|^endpackage\\b)");
    private static final Set<String> WARNING_INVARIANTS = Set.of("TooManyWorkers");

    public static void main(String[] args) throws Exception {
        String inputPath = args.length > 0 ? args[0] : "input";
        String oclPath = args.length > 1 ? args[1] : "validator/mediaflow.ocl";
        String metamodelPath = args.length > 2 ? args[2] : "../mediaflow/metamodels/mediaflow.ecore";

        registerFactories();

        List<Path> inputs = collectInputs(Paths.get(inputPath));
        if (inputs.isEmpty()) {
            System.out.println("No .xmi inputs found at " + inputPath);
            return;
        }

        List<OclInvariant> invariants = parseInvariants(Paths.get(oclPath));
        if (invariants.isEmpty()) {
            throw new IllegalStateException("No OCL invariants found in " + oclPath);
        }

        int errorCount = 0;
        int warningCount = 0;
        for (Path input : inputs) {
            ValidationSummary summary = validate(input, Paths.get(metamodelPath), invariants);
            errorCount += summary.errors;
            warningCount += summary.warnings;
        }

        System.out.println();
        System.out.println("OCL validation summary: " + errorCount + " error(s), "
                + warningCount + " warning(s).");

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

    private static List<Path> collectInputs(Path inputPath) throws IOException {
        List<Path> inputs = new ArrayList<>();
        if (Files.isRegularFile(inputPath) && inputPath.toString().endsWith(".xmi")) {
            inputs.add(inputPath);
        } else if (Files.isDirectory(inputPath)) {
            collectXmiFiles(inputPath, inputs);
        }
        Collections.sort(inputs);
        return inputs;
    }

    private static void collectXmiFiles(Path directory, List<Path> inputs) throws IOException {
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

    private static List<OclInvariant> parseInvariants(Path oclPath) throws IOException {
        String text = Files.readString(oclPath);
        Matcher matcher = INVARIANT_PATTERN.matcher(text);
        List<OclInvariant> invariants = new ArrayList<>();

        while (matcher.find()) {
            invariants.add(new OclInvariant(
                    matcher.group(1),
                    matcher.group(2),
                    stripLineComments(matcher.group(3)).trim()));
        }

        return invariants;
    }

    private static String stripLineComments(String expression) {
        StringBuilder cleaned = new StringBuilder();
        for (String line : expression.split("\\R")) {
            int commentStart = line.indexOf("--");
            cleaned.append(commentStart >= 0 ? line.substring(0, commentStart) : line)
                    .append(System.lineSeparator());
        }
        return cleaned.toString();
    }

    private static ValidationSummary validate(Path input, Path metamodelPath, List<OclInvariant> invariants)
            throws Exception {
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource metamodel = resourceSet.getResource(URI.createFileURI(metamodelPath.toAbsolutePath().toString()), true);
        EPackage mediaflowPackage = (EPackage) metamodel.getContents().get(0);
        resourceSet.getPackageRegistry().put(mediaflowPackage.getNsURI(), mediaflowPackage);
        EPackage.Registry.INSTANCE.put(mediaflowPackage.getNsURI(), mediaflowPackage);

        Resource model = resourceSet.getResource(URI.createFileURI(input.toAbsolutePath().toString()), true);
        Map<EClass, Set<EObject>> extentMap = buildExtentMap(mediaflowPackage, model);

        OCL ocl = OCL.newInstance(new EcoreEnvironmentFactory(resourceSet.getPackageRegistry()));
        ocl.setExtentMap(extentMap);

        int errors = 0;
        int warnings = 0;
        System.out.println();
        System.out.println("== " + input + " ==");

        try {
            for (OclInvariant invariant : invariants) {
                EClass context = findContext(mediaflowPackage, invariant.contextName);
                Constraint constraint = compileInvariant(ocl, context, invariant);

                for (EObject instance : extentMap.getOrDefault(context, Set.of())) {
                    if (!ocl.check(instance, constraint)) {
                        boolean warning = WARNING_INVARIANTS.contains(invariant.name);
                        if (warning) {
                            warnings++;
                        } else {
                            errors++;
                        }
                        String severity = warning ? "WARN" : "ERROR";
                        String counter = warning ? "W" + warnings : String.valueOf(errors);
                        System.out.println(counter + ". " + severity + " " + invariant.name + " on "
                                + describe(instance) + ": " + invariant.expression);
                    }
                }
            }
        } finally {
            ocl.dispose();
        }

        if (errors == 0 && warnings == 0) {
            System.out.println("OK");
        } else {
            System.out.println("Displayed " + errors + " error(s), " + warnings
                    + " warning(s), " + (errors + warnings) + " total issue(s).");
        }

        return new ValidationSummary(errors, warnings);
    }

    private static Map<EClass, Set<EObject>> buildExtentMap(EPackage ePackage, Resource model) {
        Map<EClass, Set<EObject>> extentMap = new HashMap<>();
        List<EClass> classes = new ArrayList<>();

        for (EClassifier classifier : ePackage.getEClassifiers()) {
            if (classifier instanceof EClass eClass) {
                classes.add(eClass);
                extentMap.put(eClass, new LinkedHashSet<>());
            }
        }

        for (EObject root : model.getContents()) {
            addToExtentMap(root, classes, extentMap);
            TreeIterator<EObject> contents = root.eAllContents();
            while (contents.hasNext()) {
                addToExtentMap(contents.next(), classes, extentMap);
            }
        }

        return extentMap;
    }

    private static void addToExtentMap(EObject object, Collection<EClass> classes,
            Map<EClass, Set<EObject>> extentMap) {
        for (EClass eClass : classes) {
            if (eClass.isSuperTypeOf(object.eClass())) {
                extentMap.get(eClass).add(object);
            }
        }
    }

    private static EClass findContext(EPackage ePackage, String contextName) {
        EClassifier classifier = ePackage.getEClassifier(contextName);
        if (classifier instanceof EClass eClass) {
            return eClass;
        }
        throw new IllegalArgumentException("Unknown OCL context: " + contextName);
    }

    private static Constraint compileInvariant(OCL ocl, EClass context, OclInvariant invariant)
            throws ParserException {
        OCL.Helper helper = ocl.createOCLHelper();
        helper.setContext(context);
        try {
            return helper.createInvariant(invariant.expression);
        } catch (ParserException ex) {
            throw new ParserException("Could not parse invariant " + invariant.name
                    + " in context " + invariant.contextName + ": " + ex.getMessage(), ex);
        }
    }

    private static String describe(EObject eObject) {
        EStructuralFeature name = eObject.eClass().getEStructuralFeature("name");
        Object value = name == null ? null : eObject.eGet(name);
        if (value != null && !value.toString().isBlank()) {
            return eObject.eClass().getName() + " '" + value + "'";
        }
        return eObject.eClass().getName();
    }

    private record OclInvariant(String contextName, String name, String expression) {
    }

    private record ValidationSummary(int errors, int warnings) {
    }
}
