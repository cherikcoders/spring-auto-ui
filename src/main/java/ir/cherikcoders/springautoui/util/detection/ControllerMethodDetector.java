package ir.cherikcoders.springautoui.util.detection;

import ir.cherikcoders.springautoui.util.annotaions.ExcludeFromUI;
import ir.cherikcoders.springautoui.util.annotaions.IncludeInUI;
import ir.cherikcoders.springautoui.util.detection.model.DetectedMethodModel;
import ir.cherikcoders.springautoui.util.detection.model.InputSourceEnum;
import ir.cherikcoders.springautoui.util.detection.model.MethodInputModel;
import ir.cherikcoders.springautoui.util.propertiesConfig.PropertiesService;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Component
public class ControllerMethodDetector {

    private final ListableBeanFactory listableBeanFactory;
    private final PropertiesService propertiesService;
    private final ServletContext servletContext;


    @Autowired
    public ControllerMethodDetector(ListableBeanFactory listableBeanFactory, PropertiesService propertiesService, ServletContext servletContext) {
        this.listableBeanFactory = listableBeanFactory;
        this.propertiesService = propertiesService;

        this.servletContext = servletContext;
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

            DetectedMethodModel methodModel = this.getMethodDetails(detectedMethod);


            detectedMethodModels.add(methodModel);
        }
        return detectedMethodModels;
    }

    private DetectedMethodModel getMethodDetails(Method method) {

        DetectedMethodModel methodModel = new DetectedMethodModel();
        HttpMethod httpMethod;
        String urlPattern;


        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

        if (requestMapping != null) {

            urlPattern = requestMapping.value()[0];
            httpMethod = HttpMethod.valueOf(requestMapping.method()[0].name());

        } else {

            if (method.getAnnotation(GetMapping.class) != null) {

                urlPattern = method.getAnnotation(GetMapping.class).value()[0];
                httpMethod = HttpMethod.GET;

            } else if (method.getAnnotation(PostMapping.class) != null) {

                urlPattern = method.getAnnotation(PostMapping.class).value()[0];
                httpMethod = HttpMethod.POST;

            } else if (method.getAnnotation(PutMapping.class) != null) {

                urlPattern = method.getAnnotation(PutMapping.class).value()[0];
                httpMethod = HttpMethod.PUT;

            } else {

                urlPattern = method.getAnnotation(DeleteMapping.class).value()[0];
                httpMethod = HttpMethod.DELETE;
            }

        }


        methodModel.setHttpMethod(httpMethod);
        methodModel.setUrl(servletContext.getContextPath() + urlPattern);
        methodModel.setMethodInputModelList(this.getInputParameters(method));
        return methodModel;
    }

    private List<MethodInputModel> getInputParameters(Method method) {
        List<MethodInputModel> inputParameters = new ArrayList<>();
        Parameter[] parameters = method.getParameters();

        for (Parameter parameter : parameters) {
            MethodInputModel parameterModel = new MethodInputModel();
            parameterModel.setName(parameter.getName());
            parameterModel.setType(parameter.getType().getSimpleName());

            Annotation[] annotations = parameter.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof PathVariable) {
                    parameterModel.setInputTypeEnum(InputSourceEnum.PATH_VARIABLE);
                } else if (annotation instanceof RequestParam) {
                    parameterModel.setInputTypeEnum(InputSourceEnum.REQUEST_PARAM);
                } else if (annotation instanceof RequestHeader) {
                    parameterModel.setInputTypeEnum(InputSourceEnum.REQUEST_HEADER);
                } else if (annotation instanceof RequestBody) {
                    parameterModel.setInputTypeEnum(InputSourceEnum.REQUEST_BODY);
                }
            }

            inputParameters.add(parameterModel);
        }

        return inputParameters;
    }



}
