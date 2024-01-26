package ir.cherikcoders.springautoui.util.detection;

import ir.cherikcoders.springautoui.util.annotaions.ExcludeFromUI;
import ir.cherikcoders.springautoui.util.annotaions.IncludeInUI;
import ir.cherikcoders.springautoui.util.propertiesConfig.PropertiesService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class ControllerMethodDetector {

    private final ListableBeanFactory listableBeanFactory;
    private final PropertiesService propertiesService;

    @Autowired
    public ControllerMethodDetector(ListableBeanFactory listableBeanFactory, PropertiesService propertiesService) {
        this.listableBeanFactory = listableBeanFactory;
        this.propertiesService = propertiesService;
    }


    public List<Method> getDetectedMethods() throws ClassNotFoundException {

        List<Method> detectedMethods = new ArrayList<>();
        List<Method> excludeMetods = new ArrayList<>();
        Map<String, Object> controllers = listableBeanFactory.getBeansWithAnnotation(Controller.class);

        Set<Class<?>> controller2=scanControllers();

        switch (propertiesService.getDetectionType()) {
            case INCLUDE -> controllers.forEach((s, o) -> {

                if (!o.getClass().isAnnotationPresent(ExcludeFromUI.class)) {
                    detectedMethods.addAll(Arrays.asList(o.getClass().getDeclaredMethods()));
                } else {

                    for (Method method : o.getClass().getDeclaredMethods()) {
                        if (!method.isAnnotationPresent(ExcludeFromUI.class)) {
                            detectedMethods.add(method);
                        }
                    }
                }
            });
            case EXCLUDE -> {
                controllers.forEach((s, o) -> {

                    detectedMethods.addAll(Arrays.asList(o.getClass().getDeclaredMethods()));
                    if (o.getClass().isAnnotationPresent(IncludeInUI.class)) {
                        excludeMetods.addAll(Arrays.asList(o.getClass().getDeclaredMethods()));
                    } else {
                        for (Method method : o.getClass().getDeclaredMethods()) {
                            if (method.isAnnotationPresent(IncludeInUI.class)) {
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

    public Set<Class<?>> scanControllers() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        //provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Test")));

        Set<Class<?>> controllerClasses = new HashSet<>();
        for (BeanDefinition beanDefinition : provider.findCandidateComponents(propertiesService.getPackageToScan())) {
            try {
                Class<?> controllerClass = Class.forName(beanDefinition.getBeanClassName());
                controllerClasses.add(controllerClass);
            } catch (ClassNotFoundException e) {
                // Handle exception if needed
            }
        }

        return controllerClasses;
    }


}
