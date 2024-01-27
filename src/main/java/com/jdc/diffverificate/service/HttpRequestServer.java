package com.jdc.diffverificate.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.io.HttpRequests;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpRequestServer {

    private static final Cache<String, AntnHttpResponse> httpResponseCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    private static final CookieManager cookieManager = new CookieManager(null, (uri, cookie) -> {
        if (uri == null || cookie == null || uri.getHost().equals(URLUtils.getAntnUrl())) {
            return false;
        }
        return HttpCookie.domainMatches(cookie.getDomain(), uri.getHost());
    });

    static {
        CookieHandler.setDefault(cookieManager);
    }

    @NotNull
    public static AntnHttpResponse executeGet(AntnHttpRequest httpRequest) {
        return CacheProcessor.processor(httpRequest, request -> {
            AntnHttpResponse httpResponse = new AntnHttpResponse();
            try {
                HttpRequests.request(request.getUrl()).
                        throwStatusCodeException(false).
                        tuner(new HttpRequestTuner(request)).
                        connect(new HttpResponseProcessor(request, httpResponse));

            } catch (IOException e) {
                httpResponse.setStatusCode(-1);
            }
            return httpResponse;
        });
    }

    @NotNull
    public static AntnHttpResponse executePost(AntnHttpRequest httpRequest) {
        return CacheProcessor.processor(httpRequest, request -> {
            AntnHttpResponse httpResponse = new AntnHttpResponse();
            try {
                HttpRequests.post(request.getUrl(), request.getContentType())
                        .throwStatusCodeException(false)
                        .tuner(new HttpRequestTuner(request))
                        .connect(new HttpResponseProcessor(request, httpResponse));
            } catch (IOException e) {
                httpResponse.setStatusCode(-1);
            }
            return httpResponse;
        });
    }

    public static AntnHttpResponse executePut(AntnHttpRequest httpRequest) {
        return CacheProcessor.processor(httpRequest, request -> {
            AntnHttpResponse httpResponse = new AntnHttpResponse();
            try {
                HttpRequests.put(request.getUrl(), request.getContentType())
                        .throwStatusCodeException(false)
                        .tuner(new HttpRequestTuner(request))
                        .connect(new HttpResponseProcessor(request, httpResponse));
            } catch (IOException e) {
                httpResponse.setStatusCode(-1);
            }
            return httpResponse;
        });
    }

    public static String getToken() {
        if (cookieManager.getCookieStore().getCookies() == null) {
            return null;
        }
        for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            if (StringUtils.isNotBlank(cookie.getDomain()) &&
                    cookie.getDomain().toLowerCase().contains(URLUtils.getAntnUrl()) && "token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static boolean isLogin(Project project) {
        AntnHttpResponse response = AntnHttpRequest.builderGet(URLUtils.getAntnPoints()).request();
        if (response.getStatusCode() == 200) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static void setCookie(List<HttpCookie> cookieList) {
        cookieManager.getCookieStore().removeAll();
        for (HttpCookie cookie : cookieList) {
            cookie.setVersion(0);
            cookieManager.getCookieStore().add(null, cookie);
        }
    }

    public static void resetHttpclient() {
        cookieManager.getCookieStore().removeAll();
    }


    private static void defaultHeader(AntnHttpRequest httpRequest) {
        Map<String, String> header = httpRequest.getHeader();
        header.putIfAbsent(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36");
        header.putIfAbsent(HttpHeaders.ACCEPT, "*/*");
        //header.putIfAbsent(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br");
        header.putIfAbsent(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9");
        header.putIfAbsent("origin", URLUtils.getAntnUrl());
//        header.putIfAbsent("Authorization", URLUtils.getAntnPersistent().getAntnOauthToken());
        //header.putIfAbsent(":scheme", "https");
    }

    private static class HttpRequestTuner implements HttpRequests.ConnectionTuner {
        private final AntnHttpRequest httpRequest;
        public HttpRequestTuner(AntnHttpRequest httpRequest) {
            this.httpRequest = httpRequest;
        }

        @Override
        public void tune(@NotNull URLConnection urlConnection) throws IOException {
            if (StringUtils.isNotBlank(getToken())) {
                urlConnection.addRequestProperty("Authorization", getToken());
            }
            urlConnection.addRequestProperty("referer", urlConnection.getURL().toString());
            //urlConnection.addRequestProperty(":path", urlConnection.getURL().getPath());

            defaultHeader(httpRequest);
            httpRequest.getHeader().forEach(urlConnection::addRequestProperty);
        }
    }


    private static class HttpResponseProcessor implements HttpRequests.RequestProcessor<AntnHttpResponse> {
        private final AntnHttpRequest httpRequest;
        private final AntnHttpResponse httpResponse;

        public HttpResponseProcessor(AntnHttpRequest httpRequest, AntnHttpResponse httpResponse) {
            System.out.println("***  Requet Heads: "+httpRequest.getHeader().toString());
            this.httpRequest = httpRequest;
            this.httpResponse = httpResponse;
        }

        @Override
        public AntnHttpResponse process(@NotNull HttpRequests.Request request) throws IOException {
            if (StringUtils.isNoneBlank(httpRequest.getBody())) {
                request.write(httpRequest.getBody());
            }

            URLConnection urlConnection = request.getConnection();
            if (!(urlConnection instanceof HttpURLConnection)) {
                httpResponse.setStatusCode(-1);
                return httpResponse;
            } else {
                httpResponse.setStatusCode(((HttpURLConnection) urlConnection).getResponseCode());
            }
            httpResponse.setUrl(urlConnection.getURL().toString());
            try {
                httpResponse.setBody(request.readString());
            } catch (IOException ignore) {
            }
            return httpResponse;
        }
    }

    private static class CacheProcessor {
        public static AntnHttpResponse processor(AntnHttpRequest httpRequest, HttpRequestServer.Callable<AntnHttpResponse> callable) {
            String key = httpRequest.hashCode() + "";
            if (httpRequest.isCache() && httpResponseCache.getIfPresent(key) != null) {
                return httpResponseCache.getIfPresent(key);
            }
            if (httpRequest.isCache()) {
                synchronized (key.intern()) {
                    if (httpResponseCache.getIfPresent(key) != null) {
                        return httpResponseCache.getIfPresent(key);
                    } else {
                        AntnHttpResponse httpResponse = callable.call(httpRequest);
                        if (httpResponse.getStatusCode() == 200) {
                            httpResponseCache.put(key, httpResponse);
                        }
                        return httpResponse;
                    }
                }
            } else {
                return callable.call(httpRequest);
            }
        }
    }

    @FunctionalInterface
    private interface Callable<V> {
        V call(AntnHttpRequest request);
    }


}
