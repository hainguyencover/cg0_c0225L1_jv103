package com.library.librarymanagement.config;

import com.library.librarymanagement.aspect.BookActionLogAspect;
import com.library.librarymanagement.aspect.ErrorLogAspect;
import com.library.librarymanagement.aspect.VisitLogAspect;
import org.aspectj.lang.Aspects;

public class AopConfig {
    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            BookActionLogAspect bookActionLogAspect = Aspects.aspectOf(BookActionLogAspect.class);
            VisitLogAspect visitLogAspect = Aspects.aspectOf(VisitLogAspect.class);
            ErrorLogAspect errorLogAspect = Aspects.aspectOf(ErrorLogAspect.class);

            initialized = true;
            System.out.println("AOP aspects initialized successfully");
        }
    }
}
