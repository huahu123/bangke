package com.neuq.info.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neuq.info.enums.ResultStatus;
import lombok.Data;

/**
 * Created by lihang on 2017/4/4.
 */
@Data
public class ResultResponse {
    private int code;

    private boolean success;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object content;

    public ResultResponse(int code, boolean success, Object content) {
       this.code = code;
       this.success = success;
       this.content = content;
    }

    public ResultResponse(int code, boolean success, String message,  Object content) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.content = content;
    }

    public ResultResponse(int code, boolean success, String message) {
        this.code = code;
        this.success = success;
        this.message = message;
    }

    public ResultResponse() {

    }

}
