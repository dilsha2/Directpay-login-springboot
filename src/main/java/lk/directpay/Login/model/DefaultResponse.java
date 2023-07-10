package lk.directpay.Login.model;

import java.util.HashMap;
import java.util.Map;

public class DefaultResponse {
    private int status = 400;
    private String title;
    private String message;
    private Map<String, Object> data;

    public DefaultResponse() {
    }

    public DefaultResponse(int status, String title, String message, Map<String, Object> data) {
        this.status = status;
        this.title = title;
        this.message = message;
        this.data = data;
    }
    public DefaultResponse(int status, String title, String message) {
        this.status = status;
        this.title = title;
        this.message = message;
        this.data = new HashMap<String, Object>();
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public DefaultResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
