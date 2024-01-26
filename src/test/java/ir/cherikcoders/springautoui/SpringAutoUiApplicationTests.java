package ir.cherikcoders.springautoui;

import ir.cherikcoders.springautoui.util.detection.ControllerMethodDetector;
import ir.cherikcoders.springautoui.util.propertiesConfig.PropertiesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.List;

@SpringBootTest
class SpringAutoUiApplicationTests {

    @Autowired
    private ControllerMethodDetector controllerMethodDetector;

    @Test
    void contextLoads() {
    }

    @Test
    void getDetectedMethods() throws ClassNotFoundException {
        List<Method> detectedMethods = controllerMethodDetector.getDetectedMethods();
    }

}
