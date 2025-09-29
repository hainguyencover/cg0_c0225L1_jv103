package com.example.springuploadmusic.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.io.File;

public class AppInit implements WebApplicationInitializer {

    // Cấu hình cho việc upload file
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final long MAX_REQUEST_SIZE = 55 * 1024 * 1024; // 55MB
    private static final int FILE_SIZE_THRESHOLD = 0;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(AppConfig.class);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                "SpringDispatcher", new DispatcherServlet(appContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        // Lấy thư mục tạm của hệ thống một cách an toàn
        String tempDir = System.getProperty("java.io.tmpdir");
        if (tempDir == null) {
            // Fallback an toàn nếu không lấy được property
            tempDir = new File(".").getAbsolutePath();
        }

        // Đăng ký cấu hình multipart cho DispatcherServlet
        // Sử dụng thư mục tạm của hệ thống để đảm bảo tính tương thích
        dispatcher.setMultipartConfig(new MultipartConfigElement(
                tempDir,
                MAX_FILE_SIZE,
                MAX_REQUEST_SIZE,
                FILE_SIZE_THRESHOLD
        ));
    }
}
