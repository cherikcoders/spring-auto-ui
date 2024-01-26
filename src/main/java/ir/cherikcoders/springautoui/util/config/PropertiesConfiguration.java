package ir.cherikcoders.springautoui.util.config;

import ir.cherikcoders.springautoui.util.annotaions.ExcludeFromUI;
import ir.cherikcoders.springautoui.util.propertiesConfig.DetectionTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring-auto-ui")
@ExcludeFromUI
public class PropertiesConfiguration {

    private DetectionTypeEnum detectionType = DetectionTypeEnum.INCLUDE;

    public DetectionTypeEnum getDetectionType() {
        return detectionType;
    }

    public void setDetectionType(DetectionTypeEnum detectionType) {
        this.detectionType = detectionType;
    }
}
