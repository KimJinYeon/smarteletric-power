package com.kapple.smarteletric.kepcoAPI;

import org.json.JSONObject;

public interface KepcoAPI {

    /*
    kepco api data 가져오는 모듈의 공통 interface
     */
    public String getRawData(); // Data 요청하고 String 형태로 변경하여 반환

    JSONObject getJsonObject(String jsonData); // String 형태의 데이터를 JsonObject로 변환하여 반환
}
