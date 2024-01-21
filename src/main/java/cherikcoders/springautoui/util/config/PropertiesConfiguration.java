package cherikcoders.springautoui.util.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "cherikcoders.springAutoUI")
public class PropertiesConfiguration {

    private String detectionType;
}
