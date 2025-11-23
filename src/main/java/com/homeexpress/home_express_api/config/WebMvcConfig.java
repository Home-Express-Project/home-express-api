package com.homeexpress.home_express_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir:uploads/avatars}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /uploads/** to the parent directory of the upload directory (which is .../uploads)
        // uploadDir is usually .../uploads/avatars, so we want to serve from .../uploads/
        // This allows accessing /uploads/avatars/filename.png via /uploads/avatars/filename.png URL
        Path parentPath = Paths.get(uploadDir).toAbsolutePath().normalize().getParent();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + parentPath.toString().replace("\\", "/") + "/");
    }
}
