package com.kapple.smarteletric.domain;

import lombok.ToString;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Document("CustomerNo")
public class CustomerNo {
    public String ID;
    public String custNm; //고객이름
    public String custNo; //고객번호

    public void setParameters(JSONObject jsonObject) {
        this.custNm = jsonObject.getString("custNm");
        this.custNo = jsonObject.getString("custNo");
        this.ID = "CustomerNo" + "-" + this.custNo;
    }
}
