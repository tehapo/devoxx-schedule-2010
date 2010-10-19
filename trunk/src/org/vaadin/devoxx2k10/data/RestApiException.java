package org.vaadin.devoxx2k10.data;

public class RestApiException extends Exception {

    private static final long serialVersionUID = 6513379930446993697L;

    public RestApiException() {
        super();
    }

    public RestApiException(String message) {
        super(message);
    }

    public RestApiException(Throwable cause) {
        super(cause);
    }
}
