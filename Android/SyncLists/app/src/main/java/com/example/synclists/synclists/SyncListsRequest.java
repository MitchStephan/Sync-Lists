package com.example.synclists.synclists;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SirChickenHair on 10/26/14.
 */
public class SyncListsRequest {
    private final String USER_AGENT = "SyncLists-AndroidClient/1.0";
    private final String CONTENT_TYPE = "application/json";
    private final String API_BASE = "http://10.0.2.2:8000/api/";

    private String mPath;

    protected enum SyncListsRequestMethod {
        GET,
        POST,
        PUT,
        DELETE
    };

    private SyncListsRequestMethod mMethod;
    private List<NameValuePair> mUrlParameters;
    private Map<String, Object> mJsonMap;

    public SyncListsRequest(SyncListsRequestMethod method, String path) {
        mMethod = method;
        mPath = path;
    }

    public SyncListsRequest(SyncListsRequestMethod method, String path, List<NameValuePair> urlParameters) {
        mMethod = method;
        mPath = path;
        mUrlParameters = urlParameters;
    }

    public SyncListsRequest(SyncListsRequestMethod method, String path, Map<String, Object> jsonMap) {
        mMethod = method;
        mPath = path;
        mJsonMap = jsonMap;
    }

    public SyncListsRequest(SyncListsRequestMethod method, String path, List<NameValuePair> urlParameters, Map<String, Object> jsonMap) {
        mMethod = method;
        mPath = path;
        mUrlParameters = urlParameters;
        mJsonMap = jsonMap;
    }

    public HttpRequest getHttpRequest() throws Exception {
        HttpRequest request;

        String url = getRequestUrl();

        switch(getMethod()) {
            case GET:
                request = new HttpGet(url);
                break;
            case POST:
                request = new HttpPost(url);
                break;
            case PUT:
                request = new HttpPut(url);
                break;
            case DELETE:
                request = new HttpDelete(url);
                break;
            default:
                throw new SyncListsRequestException("Method for SyncListRequest must be GET, POST, PUT, or DELETE");
        }

        // add header
        request.setHeader("User-Agent", USER_AGENT);
        request.setHeader("Content-Type", CONTENT_TYPE);

        if(mMethod == SyncListsRequestMethod.POST && mUrlParameters != null) {
            ((HttpPost) request).setEntity(new UrlEncodedFormEntity(mUrlParameters));
        }

        if(mJsonMap != null) {
            JSONObject json = new JSONObject(mJsonMap);
            StringEntity body = new StringEntity(json.toString());

            switch(getMethod()) {
                case POST:
                    ((HttpPost) request).setEntity(body);
                    break;
                case PUT:
                    ((HttpPut) request).setEntity(body);
                    break;
                default:
                    throw new SyncListsRequestException("Method for body entity must be POST or PUT");
            }
        }

        return request;
    }

    public SyncListsRequestMethod getMethod() {
        return mMethod;
    }

    protected String getRequestUrl() {
        return API_BASE + mPath;
    }
}
