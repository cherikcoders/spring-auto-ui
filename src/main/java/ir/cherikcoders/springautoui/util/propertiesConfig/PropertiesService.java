package ir.cherikcoders.springautoui.util.propertiesConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesService {


    private final DetectionTypeEnum detectionTypeEnum;

    public PropertiesService(@Value("${spring-auto-ui.detection-type}") DetectionTypeEnum detectionTypeEnum) {
        this.detectionTypeEnum = detectionTypeEnum;
    }


    public DetectionTypeEnum getDetectionType() {
        return detectionTypeEnum;
    }

}
