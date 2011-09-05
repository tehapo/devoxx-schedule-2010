package org.vaadin.devoxx2k10.data;

public class RestApiException extends Exception {

    private static final long serialVersionUID = 6513379930446993697L;

    public RestApiException() {
        super();
    }

    public RestApiException(final String message) {
        super(message);
    }

    public RestApiException(final Throwable cause) {
        super(cause);
    }
    
    public RestApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
