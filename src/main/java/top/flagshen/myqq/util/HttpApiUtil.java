package top.flagshen.myqq.util;


import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import top.flagshen.myqq.common.HttpMethodConstants;

import java.io.IOException;
import java.util.Objects;

public final class HttpApiUtil {
    private static CloseableHttpClient httpClient;

    public static String httpClientCommon(String postType, String url, String jsonStr) {
        httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        try {
            if (postType.equals(HttpMethodConstants.HTTP_GET)) {
                // ===== get =====//
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("Content-type", "application/json");
                httpGet.setHeader("DataEncoding", "UTF-8");
                httpResponse = httpClient.execute(httpGet);
            } else if (postType.equals(HttpMethodConstants.HTTP_POST)) {
                // ===== post =====//
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setHeader("DataEncoding", "UTF-8");
                if (StringUtils.isNotEmpty(jsonStr)) {
                    httpPost.setEntity(new StringEntity(jsonStr, "UTF-8"));
                }
                httpResponse = httpClient.execute(httpPost);
            } else if (postType.equals(HttpMethodConstants.HTTP_PUT)) {
                // ===== put =====//
                HttpPut httpPut = new HttpPut(url);
                httpPut.setHeader("Content-type", "application/json");
                httpPut.setHeader("DataEncoding", "UTF-8");
                if (StringUtils.isNotEmpty(jsonStr)) {
                    httpPut.setEntity(new StringEntity(jsonStr, "UTF-8"));
                }
                httpResponse = httpClient.execute(httpPut);
            } else if (postType.equals(HttpMethodConstants.HTTP_DELETE)) {
                // ===== delete =====//
                HttpDelete httpDelete = new HttpDelete(url);
                httpDelete.setHeader("Content-type", "application/json");
                httpDelete.setHeader("DataEncoding", "UTF-8");
                httpResponse = httpClient.execute(httpDelete);
            }
            int statusCode = Objects.requireNonNull(httpResponse).getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                return result;
            }
        } catch (IOException e) {
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
