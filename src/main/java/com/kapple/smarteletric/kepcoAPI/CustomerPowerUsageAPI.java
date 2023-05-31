package com.kapple.smarteletric.kepcoAPI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class CustomerPowerUsageAPI implements KepcoAPI {
    private String date;
    private String serviceKey = "";
    private final String url = "https://opm.kepco.co.kr:11080/OpenAPI/getAllCustPeriodData.do";
    private final String returnType = "02";

    public CustomerPowerUsageAPI(String date) {
        this.date = date;
    }

    public CustomerPowerUsageAPI() {
    }

    @Override
    public String getRawData() {
        URI uri = null;
        URI uriFull = null;
        try {
            uri = new URI(url);
            uriFull = new URIBuilder(uri)
                    .addParameter("date", date)
                    .addParameter("serviceKey", serviceKey)
                    .addParameter("returnType", returnType)
                    .build();
        }catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        HttpGet request = new HttpGet(uriFull);
        HttpClient httpClient = HttpClientBuilder.create().build();
        try{
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            return content;
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    public String getRawData(String date) {
        URI uri = null;
        URI uriFull = null;
        try {
            uri = new URI(url);
            uriFull = new URIBuilder(uri)
                    .addParameter("date", date)
                    .addParameter("serviceKey", serviceKey)
                    .addParameter("returnType", returnType)
                    .build();
        }catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        HttpGet request = new HttpGet(uriFull);
        HttpClient httpClient = HttpClientBuilder.create().build();
        try{
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            return content;
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    @Override
    public JSONObject getJsonObject(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        return jsonObject;
    }

    public List<JSONObject> getInnerJsonObjectList(JSONObject jsonObject) {
        /*
        전체 고객의 전력 사용 데이터는 inner json 여러 개로 이루어져 있으므로 list로 바꿔서 return
         */

        // define local variables
        List<JSONObject> result = new ArrayList<>();
        JSONArray innerJsonObjectArray = jsonObject.getJSONArray("allCustPeriodDataInfoList");

        // extract inner JsonObjects from JSONArray
        for (int i=0; i<innerJsonObjectArray.length(); i++) {
            result.add(innerJsonObjectArray.getJSONObject(i));
        }

        // return result
        return result;
    }
}
