package org.vaadin.devoxx2k10.data.http;

import java.io.IOException;

public interface HttpClient {

    public HttpResponse get(String urlString) throws IOException;

    public int post(String urlString, String postData) throws IOException;

}
