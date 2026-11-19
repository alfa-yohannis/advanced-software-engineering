package m2m;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.m2m.atl.emftvm.compiler.AtlToEmftvmCompiler;
import org.eclipse.m2m.atl.engine.compiler.CompileTimeError;

/**
 * Compiles an ATL module into EMFTVM bytecode outside Eclipse. Inside Eclipse,
 * the ATL builder does the same each time the .atl file is saved.
 * Used by run-m2m.sh; it is not in src/, so Eclipse does not build it.
 */
public class CompileAtl {

    public static void main(String[] args) throws Exception {
        String atlPath = args.length > 0 ? args[0] : "transformer/mediaflow2activity.atl";
        String emftvmPath = args.length > 1 ? args[1] : atlPath.replaceFirst("\\.atl$", ".emftvm");

        CompileTimeError[] problems;
        try (InputStream in = new FileInputStream(atlPath)) {
            problems = new AtlToEmftvmCompiler().compile(in, emftvmPath);
        }

        boolean failed = false;
        for (CompileTimeError problem : problems) {
            System.err.println(atlPath + ":" + problem.getLocation() + ": "
                    + problem.getSeverity() + ": " + problem.getDescription());
            failed |= "error".equalsIgnoreCase(problem.getSeverity());
        }
        if (failed) {
            // The compiler leaves an empty file behind, which the runner cannot load.
            Files.deleteIfExists(Paths.get(emftvmPath));
            System.exit(1);
        }
        System.out.println("Compiled " + atlPath + " to " + emftvmPath);
    }
}
