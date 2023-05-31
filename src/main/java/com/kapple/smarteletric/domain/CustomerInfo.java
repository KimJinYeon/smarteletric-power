package com.kapple.smarteletric.domain;

import lombok.ToString;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@ToString
@Document("CustomerInfo")
public class CustomerInfo {

    @Id
    public String ID; // Document _id;

    public String custNo; // 고객번호
    public String meterNo; // 계기번호
    public String etong_cd; // 계기유형

    /*
    계기유형별 정보
    11:고압기계식 12:고압전자식-표준형 13:고압전자식-기록형
    14:고압전자식-거래용 21:저압기계식-기계식 22:저압기계식-OMR
    23:저압기계식-RFID 31:저압:전자식-심야용 32:저압전자식-복합
    33:저압전자식-역률용 34:저압전자식-표준형일반 35:저압전자식-E타입
    36:저압전자식-G타입 37:저압전자식-CT일체형 38:저압전자식-AE타입
    41:최대수요전력(DM) 42:저압전자식-RFID 99:기타
    */

    public String meter_mval; // 계기배수
    public String pwrfact; // 계기역률
    public String cntr_pwr; // 계약전력
    public String sel_cost; // 선택요금
    public String hshcnt; // 가구수
    public String tv_cnt; // TV 수
    public String amr_yn; // AMI 구분
    public String nm_chg_date; // 명의변경일
    public String onsvc_ymd; // 송전일자

    public void setParameters(JSONObject jsonObject){
        // field 주입
        this.custNo = jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getString("custNo");
        this.meterNo = jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getString("meterNo");
        this.etong_cd = jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getString("etong_cd");
        this.meter_mval = Long.toString(jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getLong("meter_mval"));
        this.pwrfact = Long.toString(jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getLong("pwrfact"));
        this.cntr_pwr = Long.toString(jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getLong("cntr_pwr"));
        this.sel_cost = jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getString("sel_cost");
        this.hshcnt = Long.toString(jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getLong("hshcnt"));
        this.tv_cnt = Long.toString(jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getLong("tv_cnt"));
        this.amr_yn = jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getString("amr_yn");
        this.nm_chg_date = jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getString("nm_chg_date");
        this.onsvc_ymd = jsonObject.getJSONArray("custInfoDataInfoList").getJSONObject(0).getString("onsvc_ymd");
        this.ID = "CustomerInfo" + "-" + this.custNo;
    }
}
