package ir.cherikcoders.springautoui.util.detection.model;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class DetectedMethodModel {

    private HttpMethod httpMethod;

    private List<MethodInputModel> methodInputModelList;

    private MethodOutputModel methodOutputModel;

    private String url;

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<MethodInputModel> getMethodInputModelList() {
        return methodInputModelList;
    }

    public void setMethodInputModelList(List<MethodInputModel> methodInputModelList) {
        this.methodInputModelList = methodInputModelList;
    }

    public MethodOutputModel getMethodOutputModel() {
        return methodOutputModel;
    }

    public void setMethodOutputModel(MethodOutputModel methodOutputModel) {
        this.methodOutputModel = methodOutputModel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
