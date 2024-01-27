package ir.cherikcoders.springautoui.util.detection;

import ir.cherikcoders.springautoui.util.annotaions.ExcludeFromUI;
import ir.cherikcoders.springautoui.util.annotaions.IncludeInUI;
import ir.cherikcoders.springautoui.util.detection.model.DetectedMethodModel;
import ir.cherikcoders.springautoui.util.propertiesConfig.PropertiesService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.*;

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
//        Map<String, Object> controllers = listableBeanFactory.getBeansWithAnnotation(Controller.class);

        Set<Class<?>> controller2 = scanControllers();

        switch (propertiesService.getDetectionType()) {

            case INCLUDE -> controller2.forEach(aClass -> {

                if (!aClass.isAnnotationPresent(ExcludeFromUI.class)) {
                    for (Method method : aClass.getDeclaredMethods()) {
                        if (!method.isAnnotationPresent(ExcludeFromUI.class)) {
                            detectedMethods.add(method);
                        }
                    }
                }
            });

            case EXCLUDE -> {
                controller2.forEach(aClass -> {

                    if (aClass.isAnnotationPresent(IncludeInUI.class)) {
                        for (Method method : aClass.getDeclaredMethods()) {
                            if (!method.isAnnotationPresent(ExcludeFromUI.class)) {
                                detectedMethods.add(method);
                            }
                        }

                    } else {
                        for (Method method : aClass.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(IncludeInUI.class)) {
                                detectedMethods.add(method);
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

    public List<DetectedMethodModel> AllEndPointsDetail() throws ClassNotFoundException {

        List<DetectedMethodModel> detectedMethodModels = new ArrayList<>();
        List<Method> detectedMethods = this.getDetectedMethods();

        for (Method detectedMethod : detectedMethods) {
            DetectedMethodModel methodModel = new DetectedMethodModel();
            methodModel.setHttpMethod(this.getHttpMethod(detectedMethod));


            detectedMethodModels.add(methodModel);
        }
        return detectedMethodModels;
    }

    private HttpMethod getHttpMethod(Method method) {

        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return HttpMethod.valueOf(requestMapping.method()[0].name());
        } else {
            if (method.getAnnotation(GetMapping.class) != null) {
                return HttpMethod.GET;
            } else if (method.getAnnotation(PostMapping.class) != null) {
                return HttpMethod.POST;
            } else if (method.getAnnotation(PutMapping.class) != null) {
                return HttpMethod.PUT;
            } else {
                return HttpMethod.DELETE;
            }

        }
    }


}
