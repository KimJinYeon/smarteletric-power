package com.kapple.smarteletric.domain;

import lombok.ToString;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ToString
@Document("CustomerPowerUsage")
public class CustomerPowerUsage {

    @Id
    public String ID; // Document _id

    @Field
    public String custNo; // 고객번호
    public String meterNo; // 계기번호
    public LocalDateTime dateTimeKr; // 시간 정보
    public Double powerUsageQuantity; // 전력소비데이터

    public void setParameters(JSONObject jsonObject){
        // field 주입
        this.custNo = jsonObject.getString("custNo");
        this.meterNo = jsonObject.getString("meterNo");
        String time = jsonObject.getString("mr_ymd") + jsonObject.getString("mr_hhmi").substring(0,2);
        this.dateTimeKr = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyyMMddHH")).minusHours(1);
        this.powerUsageQuantity = jsonObject.getDouble("pwr_qty");
        this.ID = "PowerUsage" + "-" + this.custNo + "-" + this.dateTimeKr.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    }
}
