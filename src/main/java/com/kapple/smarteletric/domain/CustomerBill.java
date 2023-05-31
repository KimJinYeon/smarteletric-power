package com.kapple.smarteletric.domain;
import lombok.ToString;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@ToString
@Document("CustomerBill")
public class CustomerBill {

    @Id
    public String ID; // Document _id

    public String custNo; // 고객번호
    public String bill_ym; // 청구년월
    public String mr_ymd; // 정기검침일
    public String bill_aply_pwr; // 요금적용전력
    public String move_ymd; // 이사정산일
    public String base_bill; // 기본요금
    public String kwh_bill; // 전력량요금
    public String dc_bill; // 할인공제계
    public String req_bill; // 전기요금제
    public String req_amt; // 청구요금
    public String lload_usekwh; // 경부하사용량
    public String mload_usekwh; // 중부하사용량
    public String maxload_usekwh; // 최대부하사용량
    public String lload_needle; // 경부하당월지침
    public String mload_needle; // 중부하당월지침
    public String maxload_needle; // 최대부하당월지침
    public String jn_pwrfact; // 진상역률
    public String ji_pwrfact; // 지상역률

    public void setParameters(JSONObject jsonObject){
        // field 주입
        this.custNo = jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getString("custNo");
        this.bill_ym = jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getString("bill_ym");
        this.mr_ymd = jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getString("mr_ymd");
        this.dc_bill = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("dc_bill"));
        this.bill_aply_pwr = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("bill_aply_pwr"));
        this.move_ymd = jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getString("move_ymd");
        this.base_bill = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("base_bill"));
        this.kwh_bill = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("kwh_bill"));
        this.req_bill = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("req_bill"));
        this.req_amt = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("req_amt"));
        this.lload_usekwh = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("lload_usekwh"));
        this.mload_usekwh = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("mload_usekwh"));
        this.maxload_usekwh = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("mload_usekwh"));
        this.lload_needle = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("lload_needle"));
        this.mload_needle = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("mload_needle"));
        this.maxload_needle = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("maxload_needle"));
        this.jn_pwrfact = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("jn_pwrfact"));
        this.ji_pwrfact = Long.toString(jsonObject.getJSONArray("custBillDataInfoList").getJSONObject(0).getLong("ji_pwrfact"));
        this.ID = "CustomerBill" + "-" + this.custNo + "-" + this.bill_ym;
    }
}
