package fr.usubelli.accounting.backend.adapter.rest;

public class RestResponse {

    private final int code;
    private final String payload;

    public RestResponse(int code, String payload) {
        this.code = code;
        this.payload = payload;
    }

    public int code() {
        return code;
    }

    public String payload() {
        return payload;
    }

}
