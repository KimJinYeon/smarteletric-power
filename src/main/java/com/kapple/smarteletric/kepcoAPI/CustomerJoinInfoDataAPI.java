package com.kapple.smarteletric.kepcoAPI;

import lombok.NoArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;

@NoArgsConstructor
public class CustomerJoinInfoDataAPI implements KepcoAPI {

    private String custNo;
    private String serviceKey = "";
    private final String returnType = "02";
    private final String url = "https://opm.kepco.co.kr:11080/OpenAPI/getCustJoinInfoData.do";

    public CustomerJoinInfoDataAPI(String custNo) {
        this.custNo = custNo;
    }

    @Override
    public String getRawData() {
        URI uri = null;
        URI uriFull = null;
        try {
            uri = new URI(url);
            uriFull = new URIBuilder(uri)
                    .addParameter("custNo", custNo)
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

    public Boolean isJoinedCustomer(String custNo){
        CustomerJoinInfoDataAPI customerJoinInfoDataAPI = new CustomerJoinInfoDataAPI(custNo);
        String rawData = customerJoinInfoDataAPI.getRawData();
        JSONObject wholeData = customerJoinInfoDataAPI.getJsonObject(rawData);
        JSONObject targetScope = wholeData.getJSONArray("custJoinDataInfoList").getJSONObject(0);
        Boolean joinYN = targetScope.getString("joinYn").equals("Y");   // eds 가입 여부
        Boolean infoYN = targetScope.getString("infoYn").equals("Y");   // 고객번호 등록 여부
        Boolean agreeYN = targetScope.getString("agreeYn").equals("Y"); // 스마트 전기앱 동의 여부
        if (joinYN && infoYN && agreeYN) {
            return true;
        }else {
            return false;
        }
    }

    public String agreementSignedDate(String custNo){
        CustomerJoinInfoDataAPI customerJoinInfoDataAPI = new CustomerJoinInfoDataAPI(custNo);
        String rawData = customerJoinInfoDataAPI.getRawData();
        JSONObject wholeData = customerJoinInfoDataAPI.getJsonObject(rawData);
        JSONObject targetScope = wholeData.getJSONArray("custJoinDataInfoList").getJSONObject(0);
        return targetScope.getString("prvd_agre_dd");
    }

    public JSONObject getSpecificCustomerJoinInfo(String custNo){
        CustomerJoinInfoDataAPI customerJoinInfoDataAPI = new CustomerJoinInfoDataAPI(custNo);
        String customerJoinInfoStringData = customerJoinInfoDataAPI.getRawData();
        JSONObject customerJoinInfoJsonData = customerJoinInfoDataAPI.getJsonObject(customerJoinInfoStringData);
        // customerJoinInfoJsonData은 list의 값을 1개 넣어서 보내준다. 따라서 getJSONObject(0)을 하여 index 0번째 값을 받아온다.
        return customerJoinInfoJsonData.getJSONArray("custJoinDataInfoList").getJSONObject(0);

    }
}
