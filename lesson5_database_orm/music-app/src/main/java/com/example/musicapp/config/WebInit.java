package com.example.musicapp.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

public class WebInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{}; // có thể add HibernateConfig nếu tách riêng
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class}; // load cấu hình Spring MVC
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"}; // map DispatcherServlet
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        MultipartConfigElement multipartConfig =
                new MultipartConfigElement("/uploads/", // thư mục tạm
                        50 * 1024 * 1024,             // max file size 50MB
                        100 * 1024 * 1024,            // max request size
                        0);                           // file size threshold
        registration.setMultipartConfig(multipartConfig);
    }
}
