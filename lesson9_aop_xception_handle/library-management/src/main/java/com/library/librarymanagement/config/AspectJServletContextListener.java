package com.library.librarymanagement.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AspectJServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=================================================");
        System.out.println("Library Management System Starting...");
        System.out.println("=================================================");

        try {
            // Initialize AOP Configuration
            AopConfig.init();

            System.out.println("[INIT] AOP Aspects initialized successfully");
            System.out.println("[INIT] - BookActionLogAspect: ACTIVE");
            System.out.println("[INIT] - VisitLogAspect: ACTIVE");
            System.out.println("[INIT] - ErrorLogAspect: ACTIVE");

            // Test database connection
            testDatabaseConnection();

            System.out.println("=================================================");
            System.out.println("Library Management System Started Successfully!");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to initialize application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=================================================");
        System.out.println("Library Management System Shutting Down...");
        System.out.println("=================================================");

        try {
            // Close database connection pool
            DatabaseConfig.close();
            System.out.println("[SHUTDOWN] Database connection pool closed");

        } catch (Exception e) {
            System.err.println("[ERROR] Error during shutdown: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=================================================");
        System.out.println("Library Management System Stopped");
        System.out.println("=================================================");
    }

    private void testDatabaseConnection() {
        try {
            DatabaseConfig.getConnection().close();
            System.out.println("[INIT] Database connection test: SUCCESS");
        } catch (Exception e) {
            System.err.println("[ERROR] Database connection test: FAILED");
            System.err.println("[ERROR] " + e.getMessage());
        }
    }
}
