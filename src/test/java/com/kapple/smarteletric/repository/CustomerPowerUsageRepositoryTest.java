package com.kapple.smarteletric.repository;

import com.kapple.smarteletric.kepcoAPI.CustomerPowerUsageAPI;
import com.kapple.smarteletric.service.CustomerPowerUsageService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class CustomerPowerUsageRepositoryTest {

    @Autowired
    CustomerPowerUsageService customerPowerUsageService;
    @Autowired
    CustomerPowerUsageRepository customerPowerUsageRepository;

    CustomerPowerUsageAPI customerPowerUsageAPI = new CustomerPowerUsageAPI();

    @BeforeEach
    void deleteAll() {
        customerPowerUsageRepository.deleteAll();
    }

    @Test
    @DisplayName("전력 사용량 저장 테스트")
    void saveCustomerPowerUsageTest() {
        // given
        String rawData = "{\"allCustPeriodDataInfoList\":[{\"custNo\":\"0130392270\",\"meterNo\":\"26170541209\",\"mr_ymd\":\"20220802\",\"mr_hhmi\":\"0100\",\"pwr_qty\":1.003}]}";
        // rawData로 객체 만들어서 저장 -> repository에서 id로 find -> 둘이 같은지 검증
        customerPowerUsageService.saveCustomerPowerUsageData(rawData);

        // when



        // then
    }
}