package top.flagshen.myqq.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import top.flagshen.myqq.common.HttpMethodConstants;
import top.flagshen.myqq.entity.DataResult;
import top.flagshen.myqq.entity.PushMessage;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class HttpApiUtil {
    private static CloseableHttpClient httpClient;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static DataResult sendGet(String host, String port, String token, String function,Map<String, Object> params) {
        try {
            DataResult dataResult = null;
            httpClient = HttpClients.createDefault();
            StringBuilder sb = new StringBuilder();
            // 定义请求的参数http://localhost:7788/MyQQHTTPAPI
            sb.append("http://").append(host).append(":")
                    .append(port).append("/MyQQHTTPAPI");
            List<NameValuePair> nvps = new ArrayList<>();
            NameValuePair nvp = null;

            nvp = new BasicNameValuePair("function",function);
            nvps.add(nvp);
            nvp = new BasicNameValuePair("token",token);
            nvps.add(nvp);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                nvp = new BasicNameValuePair(entry.getKey(),value==null?null:value.toString());
                nvps.add(nvp);
            }
            URI uri = new URIBuilder(sb.toString()).setParameters(nvps).build();

            HttpGet getRequest = new HttpGet(uri);
            CloseableHttpResponse resp = httpClient.execute(getRequest);
            // 判断返回状态是否为200
            if (resp.getStatusLine().getStatusCode() == 200) {
                String rs = EntityUtils.toString(resp.getEntity(), "UTF-8");
                dataResult = OBJECT_MAPPER.readValue(rs,DataResult.class);
                return dataResult;
            } else {
                throw new RuntimeException("失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("向插件发送请求失败！");
        }finally {
            try {
                if(httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public static Map<String,Object> sendPost(String host, String port, PushMessage message) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(message);
            return sendPost(host, port, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("解析json出错");
        }

    }
    public static Map<String,Object> sendPost(String host, String port, String json) {
        try {
            Map<String,Object> dataResult = null;
            httpClient = HttpClients.createDefault();
            StringBuilder sb = new StringBuilder();
            // 定义请求的参数http://localhost:7788/MyQQHTTPAPI
            sb.append("http://").append(host).append(":")
                    .append(port).append("/MyQQHTTPAPI");
            HttpPost postRequest = new HttpPost(sb.toString());
            StringEntity entity = new StringEntity(json, Charset.forName("UTF-8"));
            // 发送Json格式的数据请求
            entity.setContentType("application/json");
            postRequest.setEntity(entity);
            String resp = httpClient.execute(postRequest, new BasicResponseHandler());
            dataResult = OBJECT_MAPPER.readValue(resp,Map.class);
            return dataResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("向插件发送请求失败！");
        }finally {
            try {
                if(httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static JSONObject httpClientCommon(String postType, String url, String jsonStr) {
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
                JSONObject resultObject = JSON.parseObject(result);
                return resultObject;
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
