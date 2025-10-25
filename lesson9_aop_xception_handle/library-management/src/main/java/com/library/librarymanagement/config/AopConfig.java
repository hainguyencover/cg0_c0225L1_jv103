package com.library.librarymanagement.config;

import com.library.librarymanagement.aspect.BookActionLogAspect;
import com.library.librarymanagement.aspect.ErrorLogAspect;
import com.library.librarymanagement.aspect.VisitLogAspect;
import org.aspectj.lang.Aspects;

public class AopConfig {
    private static boolean initialized = false;

    public static void initialize() {
        if (!initialized) {
            // Note: AOP aspects are optional in this setup
            // They will work if AspectJ LTW is configured, but app will run without them

            initialized = true;
            System.out.println("[AOP] AOP configuration initialized (aspects are optional)");
            System.out.println("[AOP] To enable full AOP logging, configure AspectJ Load-Time Weaving");
        }
    }
}
