package com.ff.hermes.response;

/**
 * description:
 * author: FF
 * time: 2019-07-07 12:01
 */
public class ResponseBean {

    private Object data;

    public ResponseBean(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
