package com.kapple.smarteletric.repository;

import com.kapple.smarteletric.domain.CustomerBill;
import com.kapple.smarteletric.kepcoAPI.CustomerBillDataAPI;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class CustomerBillRepositoryTest {
    @Autowired
    CustomerBillRepository customerBillRepository;
    CustomerBillDataAPI customerBillDataAPI = new CustomerBillDataAPI();

    @BeforeEach
    void deleteAll() {
        customerBillRepository.deleteAll();
    }

    @Test
    @DisplayName("청구정보 저장 테스트")
    void saveCustomerBillTest(){

        // Given
        String rawData = "{\"custBillDataInfoList\":[{\"custNo\":\"0103288853\",\"bill_ym\":\"202207\",\"mr_ymd\":\"12\",\"bill_aply_pwr\":3,\"move_ymd\":\"\",\"base_bill\":910,\"kwh_bill\":15005,\"dc_bill\":-1531,\"req_bill\":16364,\"req_amt\":18600,\"lload_usekwh\":0,\"mload_usekwh\":161,\"maxload_usekwh\":0,\"lload_needle\":0,\"mload_needle\":1825,\"maxload_needle\":0,\"jn_pwrfact\":0,\"ji_pwrfact\":0}]}";
        JSONObject jsonObject = customerBillDataAPI.getJsonObject(rawData);
        CustomerBill customerBill = new CustomerBill();
        customerBill.setParameters(jsonObject);
        String customerBillId = customerBill.ID;

        // when
        customerBillRepository.save(customerBill);
        CustomerBill customerBillByID = customerBillRepository.findCustomerBillByID(customerBillId);

        // then
        assertEquals(customerBillId, customerBillRepository.findCustomerBillByID(customerBillId).ID);
        assertEquals(customerBill.toString(), customerBillByID.toString());
    }
}