package com.kapple.smarteletric.domain;

import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@ToString
@Document("CustomerYearPowerUsage")
public class CustomerYearPowerUsage {

    @Id
    public String ID; // Document _id, PowerUsageByYear-custNo-date 형식

    @Field
    public String custNo; // 고객번호
    public LocalDate dateTimeKr;
    public Double powerUsageQuantity; // 전력소비데이터
}
