package ir.cherikcoders.springautoui.util.propertiesConfig;

import ir.cherikcoders.springautoui.util.config.PropertiesConfiguration;
import org.springframework.stereotype.Component;

@Component
public class PropertiesService {


    private final PropertiesConfiguration propertiesConfiguration;

    public PropertiesService(PropertiesConfiguration propertiesConfiguration) {
        this.propertiesConfiguration = propertiesConfiguration;
    }

    public DetectionTypeEnum getDetectionType() {
        return propertiesConfiguration.getDetectionType();
    }

    public String getPackageToScan(){
        return propertiesConfiguration.getPackageToScan();
    }

}
