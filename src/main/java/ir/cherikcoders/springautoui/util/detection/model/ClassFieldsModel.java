package ir.cherikcoders.springautoui.util.detection.model;


import jakarta.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class ClassFieldsModel {

    @Nullable
    private Class<?> inClass;

    List<Field> fields;

    ClassFieldsModel classFieldsModels;

    @Nullable
    public Class<?> getInClass() {
        return inClass;
    }

    public void setInClass(@Nullable Class<?> inClass) {
        this.inClass = inClass;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public ClassFieldsModel getClassFieldsModels() {
        return classFieldsModels;
    }

    public void setClassFieldsModels(ClassFieldsModel classFieldsModels) {
        this.classFieldsModels = classFieldsModels;
    }
}
