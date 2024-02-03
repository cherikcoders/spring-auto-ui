package ir.cherikcoders.springautoui.util.detection;

import ir.cherikcoders.springautoui.util.annotaions.ExcludeFromUI;
import ir.cherikcoders.springautoui.util.annotaions.IncludeInUI;
import ir.cherikcoders.springautoui.util.detection.model.*;
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
import java.lang.reflect.*;
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


    public HashMap<Class<?>, List<Method>> getDetectedMethods() throws ClassNotFoundException {

        HashMap<Class<?>, List<Method>> classMethodsHashMap = new HashMap<>();

//        List<Method> detectedMethods = new ArrayList<>();
//        List<Method> excludeMetods = new ArrayList<>();
//        Map<String, Object> controllers = listableBeanFactory.getBeansWithAnnotation(Controller.class);

        Set<Class<?>> controller2 = scanControllers();

        switch (propertiesService.getDetectionType()) {

            case INCLUDE -> controller2.forEach(aClass -> {
                List<Method> classMethods = new ArrayList<>();

                if (!aClass.isAnnotationPresent(ExcludeFromUI.class)) {
                    for (Method method : aClass.getDeclaredMethods()) {
                        if (!method.isAnnotationPresent(ExcludeFromUI.class)) {
//                            detectedMethods.add(method);
                            classMethods.add(method);
                        }
                    }
                }

                classMethodsHashMap.put(aClass, classMethods);
            });

            case EXCLUDE -> {
                controller2.forEach(aClass -> {

                    List<Method> classMethods = new ArrayList<>();

                    if (aClass.isAnnotationPresent(IncludeInUI.class)) {
                        for (Method method : aClass.getDeclaredMethods()) {
                            if (!method.isAnnotationPresent(ExcludeFromUI.class)) {
//                                detectedMethods.add(method);
                                classMethods.add(method);
                            }
                        }

                    } else {
                        for (Method method : aClass.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(IncludeInUI.class)) {
//                                detectedMethods.add(method);
                                classMethods.add(method);
                            }
                        }
                    }

                    classMethodsHashMap.put(aClass, classMethods);
                });

//                detectedMethods.retainAll(excludeMetods);
            }

        }
        return classMethodsHashMap;
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

    public HashMap<Class<?>, List<DetectedMethodModel>> AllEndPointsDetail() throws ClassNotFoundException {

        List<DetectedMethodModel> detectedMethodModels = new ArrayList<>();
        HashMap<Class<?>, List<Method>> detectedMethodsMap = this.getDetectedMethods();
        HashMap<Class<?>, List<DetectedMethodModel>> methodsModelMap = new HashMap<>();


        detectedMethodsMap.forEach((aClass, methods) -> {
            for (Method method : methods) {

                DetectedMethodModel methodModel = this.getMethodDetails(method);
                detectedMethodModels.add(methodModel);
            }
            methodsModelMap.put(aClass, detectedMethodModels);
        });


        return methodsModelMap;
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
        methodModel.setMethodOutputModel(this.getOutputMethodModel(method));

        return methodModel;
    }

    private List<MethodInputModel> getInputParameters(Method method) {

        List<MethodInputModel> inputParameters = new ArrayList<>();
        Parameter[] parameters = method.getParameters();

        for (Parameter parameter : parameters) {

            MethodInputModel parameterModel = new MethodInputModel();
            parameterModel.setName(parameter.getName());
            parameterModel.setType(parameter.getType());

            Class<?> parameterType = parameter.getType();
            if (parameterType.getDeclaredFields().length > 0) {
                parameterModel.setFieldsModel(this.getFields(parameterType));
            }


            Annotation[] annotations = parameter.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof PathVariable) {
                    parameterModel.setInputSourceEnum(InputSourceEnum.PATH_VARIABLE);
                } else if (annotation instanceof RequestParam) {
                    parameterModel.setInputSourceEnum(InputSourceEnum.REQUEST_PARAM);
                } else if (annotation instanceof RequestHeader) {
                    parameterModel.setInputSourceEnum(InputSourceEnum.REQUEST_HEADER);
                } else if (annotation instanceof RequestBody) {
                    parameterModel.setInputSourceEnum(InputSourceEnum.REQUEST_BODY);
                }
            }

            inputParameters.add(parameterModel);
        }

        return inputParameters;
    }

    private MethodOutputModel getOutputMethodModel(Method method) {

        MethodOutputModel methodOutputModel = new MethodOutputModel();

        Class<?> returnType = method.getReturnType();


        methodOutputModel.setReturnClass(returnType);
        methodOutputModel.setName(returnType.getSimpleName());

//        if (returnType.getDeclaredFields().length > 0) {
        methodOutputModel.setFieldsModel(this.getFields(returnType));
//        }

        return methodOutputModel;
    }


    private ClassFieldsModel getFields(Class<?> aClass) {

        ClassFieldsModel classFieldsModel = new ClassFieldsModel();

        if (Collection.class.isAssignableFrom(aClass)) {

            Type genericSuperclass = aClass.getGenericSuperclass();

            if (genericSuperclass instanceof ParameterizedType) {

                Type genericType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
                if (genericType instanceof Class<?> collectionType) {
                    classFieldsModel.setInClass(genericType.getClass());
                    if (Collection.class.isAssignableFrom(collectionType)) {
                        // If the element type is also a Collection, recursively get its fields
                        this.getFields(collectionType);
                    } else {
                        // If the element type is a custom model, get its fields
                        classFieldsModel.setFields(List.of(collectionType.getDeclaredFields()));
                    }
                }

//                if (genericType instanceof Class<?> collectionType) {
//                    classFieldsModel.setInClass(genericType.getClass());
//                    this.getFields(collectionType);
//                }
//                classFieldsModel.setInClass(genericType.getClass());
            }else if (aClass.getTypeParameters().length > 0) {
                // Check if it's a parameterized type
                TypeVariable<?> typeVariable = aClass.getTypeParameters()[0];
                Type[] bounds = typeVariable.getBounds();

                if (bounds.length > 0 && bounds[0] instanceof Class<?> parameterizedType) {
                    classFieldsModel.setInClass(parameterizedType);
                    // Handle parameterized type as needed
                }
            }
        } else if (!aClass.isPrimitive() && !aClass.isArray() && !isWrapperType(aClass) && !String.class.isAssignableFrom(aClass)) {
            if (aClass.getDeclaredFields().length > 0) {

                classFieldsModel.setFields(List.of(aClass.getDeclaredFields()));
            }
        }


        return classFieldsModel;

    }

    private boolean isWrapperType(Class<?> clazz) {
        return clazz.equals(Integer.class)
                || clazz.equals(Long.class)
                || clazz.equals(Short.class)
                || clazz.equals(Float.class)
                || clazz.equals(Double.class)
                || clazz.equals(Byte.class)
                || clazz.equals(Character.class)
                || clazz.equals(Boolean.class);
    }

}
