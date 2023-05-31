package com.kapple.smarteletric.repository;

import com.kapple.smarteletric.domain.CustomerInfo;
import com.kapple.smarteletric.kepcoAPI.CustomerInfoDataAPI;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class CustomerInfoRepositoryTest {
    @Autowired
    CustomerInfoRepository customerInfoRepository;
    CustomerInfoDataAPI customerInfoDataAPI = new CustomerInfoDataAPI();

    @BeforeEach
    void deleteAll() {
        customerInfoRepository.deleteAll();
    }

    @Test
    @DisplayName("고객정보 저장 테스트")
    void saveCustomerInfoTest() {
        // given
        String rawData = "{\"custInfoDataInfoList\":[{\"custNo\":\"0103288853\",\"meterNo\":\"26190425837\",\"etong_cd\":\"38\",\"meter_mval\":1,\"pwrfact\":90,\"cntr_pwr\":3,\"sel_cost\":\"0\",\"hshcnt\":1,\"tv_cnt\":1,\"amr_yn\":\"N\",\"nm_chg_date\":\"20180703\",\"onsvc_ymd\":\"19930925\",\"pchild_clcd\":\"\",\"lv_hv_val\":\"저압\",\"ictg\":\"주택용\",\"mr_ymd\":\"12\"}]}";
        JSONObject jsonObject = customerInfoDataAPI.getJsonObject(rawData);
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setParameters(jsonObject);

        // when
        customerInfoRepository.save(customerInfo);
        CustomerInfo customerInfoById = customerInfoRepository.findCustomerInfoByID(customerInfo.ID);

        // then
        Assertions.assertEquals(customerInfo.ID, customerInfoById.ID);
        Assertions.assertEquals(customerInfo.toString(), customerInfoById.toString());
    }
}