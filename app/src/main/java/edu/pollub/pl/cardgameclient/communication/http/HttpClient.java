package edu.pollub.pl.cardgameclient.communication.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import command.CardGameCommand;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;
import response.CardGameResponse;

import static java.util.Objects.nonNull;

@Singleton
public class HttpClient {

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";

    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private String token;
    private String sessionId;

    public HttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        CookieHandler cookieHandler = new CookieManager();

        client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieHandler))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        mapper = new ObjectMapper();
    }

    public OkHttpClient getClient() {
        return client;
    }

    private static final String REQUEST_MEDIA_TYPE_JSON = "application/json";

    public void get(String url) throws Exception {
        get(url, new HashMap<>());
    }

    public void get(String uri, Map<String, String> headers) throws Exception {
        Request request = createRequest(uri, headers, GET);
        Response response = sendRequest(request);
        parseResponse(response);
    }

    public <T extends CardGameResponse> T get(String url, TypeReference<T> responseClass) throws Exception {
        return get(url, new HashMap<>(), responseClass);
    }

    public <T extends CardGameResponse> T get(String uri, Map<String, String> headers, TypeReference<T>  responseClass) throws Exception {
        Request request = createRequest(uri, headers, GET);
        Response response = sendRequest(request);
        return parseResponse(response, responseClass);
    }

    public void post(String url) throws Exception {
        Request request = createRequest(url, new HashMap<>(), POST);
        Response response = sendRequest(request);
        parseResponse(response);
    }

    public void post(String url, CardGameCommand body) throws Exception {
        post(url, body, new HashMap<>());
    }

    public void post(String url, CardGameCommand body, Map<String, String> headers) throws Exception {
        Request request = createRequest(url, body, headers, POST);
        Response response = sendRequest(request);
        parseResponse(response);
    }

    public <T extends CardGameResponse> T post(String url, CardGameCommand body, TypeReference<T> responseClass) throws Exception {
        return post(url, body, new HashMap<>(), responseClass);
    }

    public <T extends CardGameResponse> T post(String url, CardGameCommand body, Map<String, String> headers, TypeReference<T>responseClass) throws Exception {
        Request request = createRequest(url, body, headers, POST);
        Response response = sendRequest(request);
        return parseResponse(response, responseClass);
    }

    public void put(String url) throws Exception {
        Request request = createRequest(url, new HashMap<>(), PUT);
        Response response = sendRequest(request);
        parseResponse(response);
    }

    public void put(String url, CardGameCommand body) throws Exception {
        put(url, body, new HashMap<>());
    }

    public void put(String url, CardGameCommand body, Map<String, String> headers) throws Exception {
        Request request = createRequest(url, body, headers, PUT);
        Response response = sendRequest(request);
        parseResponse(response);
    }

    public <T extends CardGameResponse> T put(String url, CardGameCommand body, TypeReference<T> responseClass) throws Exception {
        return post(url, body, new HashMap<>(), responseClass);
    }

    public <T extends CardGameResponse> T put(String url, CardGameCommand body, Map<String, String> headers, TypeReference<T> responseClass) throws Exception {
        Request request = createRequest(url, body, headers, PUT);
        Response response = sendRequest(request);
        return parseResponse(response, responseClass);
    }

    public void delete(String url) throws Exception {
        Request request = createRequest(url, new HashMap<>(), DELETE);
        Response response = sendRequest(request);
        parseResponse(response);
    }

    public void delete(String url, CardGameCommand body) throws Exception {
        post(url, body, new HashMap<>());
    }

    public void delete(String url, CardGameCommand body, Map<String, String> headers) throws Exception {
        Request request = createRequest(url, body, headers, DELETE);
        Response response = sendRequest(request);
        parseResponse(response);
    }

    public <T extends CardGameResponse> T delete(String url, CardGameCommand body, TypeReference<T> responseClass) throws Exception {
        return post(url, body, new HashMap<>(), responseClass);
    }

    public <T extends CardGameResponse> T delete(String url, CardGameCommand body, Map<String, String> headers, TypeReference<T> responseClass) throws Exception {
        Request request = createRequest(url, body, headers, DELETE);
        Response response = sendRequest(request);
        return parseResponse(response, responseClass);
    }

    public <T extends CardGameResponse> T delete(String url, Map<String, String> headers, TypeReference<T> responseClass) throws Exception {
        Request request = createRequest(url, headers, DELETE);
        Response response = sendRequest(request);
        return parseResponse(response, responseClass);
    }

    public String getToken() {
        return token;
    }

    public String getSessionId() {
        return sessionId;
    }

    private Request createRequest(String uri, Map<String, String> headers, String method) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(uri).method(method, !method.equals(GET) ? Util.EMPTY_REQUEST : null);

        requestBuilder = putHeaders(headers, requestBuilder);

        return requestBuilder.build();
    }

    private Request createRequest(String url, CardGameCommand body, Map<String, String> headers, String method) throws JsonProcessingException {
        headers.put("Content-Type", "application/json");
        MediaType textPlainMT = MediaType.parse(REQUEST_MEDIA_TYPE_JSON);

        String parsedBody = mapper.writeValueAsString(body);
        Request.Builder requestBuilder = new Request.Builder().url(url)
                .method(method, RequestBody.create(textPlainMT, parsedBody));

        requestBuilder = putHeaders(headers, requestBuilder);

        return requestBuilder.build();
    }

    private Request.Builder putHeaders(Map<String, String> headers, Request.Builder requestBuilder) {
        for(Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder = requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        return requestBuilder;
    }


    private Response sendRequest(Request request) throws Exception {
        return client.newCall(request).execute();
    }

    private <T extends CardGameResponse> T parseResponse(Response response, TypeReference<T> responseClass) throws Exception {
        ResponseBody b = response.body();
        String resp = null;
        if(nonNull(b)) {
            resp = b.string();
        }
        response.close();

        if(response.isSuccessful()) {
            parseHeaders(response);
        }
        else {
            throw new RequestFail(resp);
        }

        return mapper.readValue(resp, responseClass);
    }

    private void parseResponse(Response response) throws Exception {
        ResponseBody b = response.body();
        String resp = null;
        if(nonNull(b)) {
            resp = b.string();
        }

        if(!response.isSuccessful()) {
            throw new RequestFail(resp);
        }
    }

    private void parseHeaders(Response response) {
        String header = response.header("Set-Cookie");
        if(nonNull(header)) {
            String[] parts = header.split(";");
            for(String part : parts) {
                String[] pair = part.split("=");
                String pair_name = pair[0];
                String pair_value = "";
                if(pair.length > 1) {
                    pair_value = pair[1];
                }
                if(pair_name.equals("XSRF-TOKEN")) {
                    token = pair_value;
                    break;
                } else if(pair_name.equals("JSESSIONID")) {
                    sessionId = pair_value;
                }
            }
        }
    }

}
