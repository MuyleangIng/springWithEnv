package co.cstad.sen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RescueConfigurationHandler implements WebMvcConfigurer {
    @Value("${file.serverPath}")
    private String fileServerPath;
    @Value("${file.clientPath}")
    private String fileClientPath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(fileClientPath).addResourceLocations("file:"+fileServerPath);
    }
}
