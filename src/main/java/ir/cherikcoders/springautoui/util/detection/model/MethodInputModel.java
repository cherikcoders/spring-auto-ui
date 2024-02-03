package ir.cherikcoders.springautoui.util.detection.model;

import java.lang.reflect.Field;
import java.util.List;

public class MethodInputModel {

    private InputSourceEnum inputSourceEnum;

    private String name;

    private Class<?> Type;

    private ClassFieldsModel fieldsModel;



    public InputSourceEnum getInputSourceEnum() {
        return inputSourceEnum;
    }

    public void setInputSourceEnum(InputSourceEnum inputSourceEnum) {
        this.inputSourceEnum = inputSourceEnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return Type;
    }

    public void setType(Class<?> type) {
        Type = type;
    }

    public ClassFieldsModel getFieldsModel() {
        return fieldsModel;
    }

    public void setFieldsModel(ClassFieldsModel fieldsModel) {
        this.fieldsModel = fieldsModel;
    }
}
