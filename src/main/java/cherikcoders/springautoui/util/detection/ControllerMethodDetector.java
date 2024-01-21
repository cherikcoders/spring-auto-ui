package cherikcoders.springautoui.util.detection;

import cherikcoders.springautoui.util.annotaions.ExcludeFromUI;
import cherikcoders.springautoui.util.annotaions.IncludeInUI;
import cherikcoders.springautoui.util.propertiesConfig.PropertiesService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ControllerMethodDetector {

    private final ListableBeanFactory listableBeanFactory;
    private final PropertiesService propertiesService;

    @Autowired
    public ControllerMethodDetector(ListableBeanFactory listableBeanFactory, PropertiesService propertiesService) {
        this.listableBeanFactory = listableBeanFactory;
        this.propertiesService = propertiesService;
    }


    public List<Method> getDetectedMethods() {

        List<Method> detectedMethods = new ArrayList<>();
        List<Method> excludeMetods = new ArrayList<>();
        Map<String, Object> controllers = listableBeanFactory.getBeansWithAnnotation(Controller.class);

        switch (propertiesService.getDetectionType()) {
            case INCLUDE -> controllers.forEach((s, o) -> {

                if (o.getClass().isAnnotationPresent(IncludeInUI.class)) {
                    detectedMethods.addAll(Arrays.asList(o.getClass().getDeclaredMethods()));
                } else {

                    for (Method method : o.getClass().getDeclaredMethods()) {
                        if (method.isAnnotationPresent(IncludeInUI.class)) {
                            detectedMethods.add(method);
                        }
                    }
                }
            });
            case EXCLUDE -> {
                controllers.forEach((s, o) -> {

                    detectedMethods.addAll(Arrays.asList(o.getClass().getDeclaredMethods()));
                    if (o.getClass().isAnnotationPresent(ExcludeFromUI.class)) {
                        excludeMetods.addAll(Arrays.asList(o.getClass().getDeclaredMethods()));
                    } else {
                        for (Method method : o.getClass().getDeclaredMethods()) {
                            if (method.isAnnotationPresent(ExcludeFromUI.class)) {
                                excludeMetods.add(method);
                            }
                        }
                    }
                });

                detectedMethods.retainAll(excludeMetods);
            }

        }
        return detectedMethods;
    }


}
