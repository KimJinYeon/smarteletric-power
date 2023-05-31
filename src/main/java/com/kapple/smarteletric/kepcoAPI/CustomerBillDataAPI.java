package com.kapple.smarteletric.kepcoAPI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;

public class CustomerBillDataAPI implements KepcoAPI {

    private String custNo;
    private String dataMonth;
    private final String serviceKey = "";
    private final String returnType = "02";
    private final String url = "https://opm.kepco.co.kr:11080/OpenAPI/getCustBillData.do";

    public CustomerBillDataAPI(String custNo, String dataMonth) {
        this.custNo = custNo;
        this.dataMonth = dataMonth;
    }

    public CustomerBillDataAPI() {
    }

    @Override
    public String getRawData(){
        URI uri = null;
        URI uriFull = null;
        try {
            uri = new URI(url);
            uriFull = new URIBuilder(uri)
                    .addParameter("custNo", custNo)
                    .addParameter("dataMonth", dataMonth)
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

}
