package com.yupi.yuoj.config;

import com.yupi.yuoj.utils.UploadPathUtils;
import java.nio.file.Path;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadRoot = UploadPathUtils.getUploadRoot();
        String location = uploadRoot.toUri().toString();
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
