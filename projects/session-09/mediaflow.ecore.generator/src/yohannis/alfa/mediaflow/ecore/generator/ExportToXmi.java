package yohannis.alfa.mediaflow.ecore.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.google.inject.Injector;

import mediaflow.MediaflowPackage;
import yohannis.alfa.mediaflow.MediaFlowStandaloneSetup;

public class ExportToXmi {
    public static void main(String[] args) throws Exception {
        // Explicitly register the imported metamodel package
        MediaflowPackage.eINSTANCE.eClass();
        EPackage.Registry.INSTANCE.put(
            MediaflowPackage.eNS_URI,
            MediaflowPackage.eINSTANCE
        );

        Injector injector = new MediaFlowStandaloneSetup().createInjectorAndDoEMFRegistration();
        ResourceSet resourceSet = injector.getInstance(ResourceSet.class);

        // Register XMI output
        resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put("xmi", new XMIResourceFactoryImpl());

        File inputFile = new File("input/test.mediaflow");
        File outputFile = new File("output/test.xmi");
        outputFile.getParentFile().mkdirs();

        Resource textResource = resourceSet.getResource(
            URI.createFileURI(inputFile.getAbsolutePath()), true
        );

        Resource xmiResource = resourceSet.createResource(
            URI.createFileURI(outputFile.getAbsolutePath())
        );
        xmiResource.getContents().add(textResource.getContents().get(0));

        Map<String, Object> options = new HashMap<>();
        xmiResource.save(options);

        System.out.println("Saved to " + outputFile.getAbsolutePath());
    }
}