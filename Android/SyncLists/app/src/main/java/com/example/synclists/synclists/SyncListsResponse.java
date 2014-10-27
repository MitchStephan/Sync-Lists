package com.example.synclists.synclists;

import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by SirChickenHair on 10/26/14.
 */
public class SyncListsResponse {
    private HttpResponse mHttpResponse;
    private String mBody;

    public SyncListsResponse(HttpResponse httpResponse) {
        mHttpResponse = httpResponse;
        mBody = extractBody(httpResponse);
    }

    public HttpResponse getHttpResponse() {
        return mHttpResponse;
    }

    public String getBody() {
        return mBody;
    }

    private String extractBody(HttpResponse httpResponse) {
        String body = "";

        try {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(httpResponse.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            body = result.toString();
        }
        catch(Exception e) { }

        return body;
    }

    public String toString() {
        return mBody;
    }
}
