package ir.cherikcoders.springautoui;

import ir.cherikcoders.springautoui.util.config.PropertiesConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PropertiesConfiguration.class)
public class SpringAutoUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAutoUiApplication.class, args);
    }

}
