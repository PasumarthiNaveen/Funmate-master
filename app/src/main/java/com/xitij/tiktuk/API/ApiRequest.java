package com.xitij.tiktuk.API;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiRequest {
    private JSONObject jsonService;
    private JSONObject jsonRequest;
    private JSONObject jsonData;

    public ApiRequest() {

    }

    public void setJsonData(JSONObject jsonData) {
        this.jsonData = jsonData;
    }

    public void setJsonService(JSONObject jsonService) {
        this.jsonService = jsonService;
    }

    public void setJsonRequest(JSONObject jsonRequest) {
        this.jsonRequest = new JSONObject();
    }

    public void setServiceName(String serviceName) throws JSONException{
        jsonService = new JSONObject();
        jsonService.put("service",serviceName);
    }

}
