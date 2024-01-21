package cherikcoders.springautoui.util.propertiesConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesService {


    private final DetectionTypeEnum detectionTypeEnum;

    public PropertiesService(@Value("${cherikcoders.springAutoUI.detectionType}") String detectionTypeEnum) {
        this.detectionTypeEnum = DetectionTypeEnum.valueOf(detectionTypeEnum);
    }


    public DetectionTypeEnum getDetectionType() {
        return detectionTypeEnum;
    }

}
