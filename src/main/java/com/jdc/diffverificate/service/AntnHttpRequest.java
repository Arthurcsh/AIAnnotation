package com.jdc.diffverificate.service;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AntnHttpRequest {

    private String url;
    private String body;
    /**
     * POST
     */
    private String contentType;
    private Map<String, String> header;
    private boolean cache;
    private String cacheParam;

    private AntnHttpRequest(String url, String body, String contentType, Map<String, String> header, boolean cache, String cacheParam) {
        this.url = url;
        this.body = body;
        this.contentType = contentType;
        this.header = header;
        this.cache = cache;
        this.cacheParam = cacheParam;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public boolean isCache() {
        return cache;
    }

    public String getCacheParam() {
        return cacheParam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AntnHttpRequest that = (AntnHttpRequest) o;

        return new EqualsBuilder().append(url, that.url).append(body, that.body).append(contentType, that.contentType).append(header, that.header).append(cacheParam, that.cacheParam).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(url).append(body).append(contentType).append(header).append(cacheParam).toHashCode();
    }

    public static AntnHttpRequest.HttpRequestBuilder builderGet(String url) {
        return new AntnHttpRequest.HttpRequestBuilder().get(url);
    }

    public static AntnHttpRequest.HttpRequestBuilder builderPost(String url, String contentType) {
        return new AntnHttpRequest.HttpRequestBuilder().post(url, contentType);
    }

    public static AntnHttpRequest.HttpRequestBuilder builderPut(String url, String contentType) {
        return new AntnHttpRequest.HttpRequestBuilder().put(url, contentType);
    }

    public static class HttpRequestBuilder {
        private String url;

        private String body;
        /**
         * POST
         */
        private String contentType;

        private Type type;

        private Map<String, String> header = new HashMap<>();

        private boolean cache = false;

        private String cacheParam;

        private HttpRequestBuilder() {

        }

        private HttpRequestBuilder get(String url) {
            this.url = url;
            this.type = Type.GET;
            return this;
        }

        private HttpRequestBuilder post(String url, String contentType) {
            this.url = url;
            this.contentType = contentType;
            this.type = Type.POST;
            return this;
        }

        private HttpRequestBuilder put(String url, String contentType) {
            this.url = url;
            this.contentType = contentType;
            this.type = Type.PUT;
            return this;
        }

        public HttpRequestBuilder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequestBuilder addHeader(String name, String value) {
            this.header.put(name, value);
            return this;
        }

        public HttpRequestBuilder cache(boolean cache) {
            this.cache = cache;
            return this;
        }

        public HttpRequestBuilder cacheParam(String cacheParam) {
            this.cacheParam = cacheParam;
            this.cache = true;
            return this;
        }

        public AntnHttpRequest build() {
            return new AntnHttpRequest(url, body, contentType, header, cache, cacheParam);
        }

        @NotNull
        public AntnHttpResponse request() {
            AntnHttpRequest httpRequest = build();
            switch (type) {
                case GET:
                    return HttpRequestServer.executeGet(httpRequest);
                case POST:
                    return HttpRequestServer.executePost(httpRequest);
                case PUT:
                    return HttpRequestServer.executePut(httpRequest);
                default:
                    throw new RuntimeException("Type not supported");
            }
        }

    }

    private enum Type {
        GET, POST, PUT;
    }

}
