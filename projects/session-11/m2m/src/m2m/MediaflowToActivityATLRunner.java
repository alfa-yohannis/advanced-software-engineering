package m2m;

import java.io.File;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.EmftvmPackage;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.TimingData;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

public class MediaflowToActivityATLRunner {

    public static void main(String[] args) throws Exception {
        String inputPath       = args.length > 0 ? args[0] : "input/flow1.xmi";
        String outputPath      = args.length > 1 ? args[1] : "output/activity1atl.uml";
        String transformerDir  = args.length > 2 ? args[2] : "transformer/";
        String metamodelPath   = args.length > 3 ? args[3] : "../mediaflow/metamodels/mediaflow.ecore";

        // Initialise EMF packages and resource factories.
        EcorePackage.eINSTANCE.eClass();
        UMLPackage.eINSTANCE.eClass();
        EmftvmPackage.eINSTANCE.eClass();
        UMLResourcesUtil.initGlobalRegistries();

        // Register resource factories globally for all file types we use.
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put("ecore", new EcoreResourceFactoryImpl());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put("xmi", new XMIResourceFactoryImpl());
        // Use the EMFTVM bundle's own classloader to access its internal factory
        Resource.Factory emftvmFactory = (Resource.Factory)
                EmftvmFactory.class.getClassLoader()
                        .loadClass("org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl")
                        .getDeclaredConstructor().newInstance();
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put("emftvm", emftvmFactory);

        // EMFTVM execution environment
        ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();
        ResourceSet rs = new ResourceSetImpl();

        // ── Metamodels ──────────────────────────────────────────────────────
        // Load Mediaflow .ecore and register its EPackage so that the XMI
        // loader can resolve the 'mediaflow' namespace URI in flow1.xmi.
        Resource ecoreResource = rs.getResource(
                URI.createFileURI(new File(metamodelPath).getAbsolutePath()), true);
        for (EObject obj : ecoreResource.getContents()) {
            if (obj instanceof EPackage) {
                EPackage pkg = (EPackage) obj;
                EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
            }
        }

        Metamodel mediaflowMM = EmftvmFactory.eINSTANCE.createMetamodel();
        mediaflowMM.setResource(ecoreResource);
        env.registerMetaModel("Mediaflow", mediaflowMM);

        // Output metamodel: UML (sourced from the UML2 plug-in's built-in resource)
        Metamodel umlMM = EmftvmFactory.eINSTANCE.createMetamodel();
        umlMM.setResource(UMLPackage.eINSTANCE.eResource());
        env.registerMetaModel("UML", umlMM);

        // ── Models ──────────────────────────────────────────────────────────
        // Input model: the Mediaflow XMI instance
        Model inModel = EmftvmFactory.eINSTANCE.createModel();
        inModel.setResource(rs.getResource(
                URI.createFileURI(new File(inputPath).getAbsolutePath()), true));
        env.registerInputModel("IN", inModel);

        // Output model: empty resource that will be populated and saved
        Model outModel = EmftvmFactory.eINSTANCE.createModel();
        outModel.setResource(rs.createResource(
                URI.createFileURI(new File(outputPath).getAbsolutePath())));
        env.registerOutputModel("OUT", outModel);

        // ── Transformation ───────────────────────────────────────────────────
        // DefaultModuleResolver builds: prefix + moduleName + ".emftvm"
        String transformerURI = new File(transformerDir).getAbsoluteFile().toURI().toString();
        if (!transformerURI.endsWith("/")) {
            transformerURI += "/";
        }

        ModuleResolver mr = new DefaultModuleResolver(transformerURI, new ResourceSetImpl());

        TimingData td = new TimingData();
        env.loadModule(mr, "mediaflow2activity");
        td.finishLoading();
        env.run(td);
        td.finish();

        // Persist the output UML model
        outModel.getResource().save(Collections.emptyMap());

        System.out.println("ATL transformation complete: " + outputPath);
        System.out.println(td);
    }
}
