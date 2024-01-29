package ir.cherikcoders.springautoui.util.detection.model;

public class MethodInputModel {

    private InputSourceEnum inputSourceEnum;

    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public InputSourceEnum getInputTypeEnum() {
        return inputSourceEnum;
    }

    public void setInputTypeEnum(InputSourceEnum inputSourceEnum) {
        this.inputSourceEnum = inputSourceEnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
