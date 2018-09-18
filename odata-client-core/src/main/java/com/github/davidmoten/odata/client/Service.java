package com.github.davidmoten.odata.client;

import java.util.Map;

public interface Service {

    public ResponseGet GET(String url, Map<String, String> requestHeaders);

    public Path getBasePath();

}
