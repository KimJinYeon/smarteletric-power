package com.kapple.smarteletric.service;


import com.kapple.smarteletric.domain.CustomerNo;
import com.kapple.smarteletric.kepcoAPI.CustomerNoListDataAPI;
import com.kapple.smarteletric.repository.CustomerNoRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor //lombok에서 생성자를 만들어준다. (필수 argument를 요구한다.)
@Service
public class CustomerNoService {

    private final CustomerNoRepository customerNoRepository;
    CustomerNoListDataAPI customerNoListDataAPI = new CustomerNoListDataAPI();

    public void saveAllCustomerNoData(){
        String rawData = customerNoListDataAPI.getRawData();
        JSONObject jsonObject = customerNoListDataAPI.getJsonObject(rawData);
        List<JSONObject> innerJsonObjectList = customerNoListDataAPI.getInnerJsonObjectList(jsonObject);

        for (JSONObject tempJsonObject : innerJsonObjectList) {
            CustomerNo customerNo = new CustomerNo();
            customerNo.setParameters(tempJsonObject);

            customerNoRepository.save(customerNo);
        }

        List<CustomerNo> allCustomerNo = customerNoRepository.findAll();
    }

    public void deleteAllCustomerNoData(){
        customerNoRepository.deleteAll();
    }

    public List<CustomerNo> findAllCustomerNoData() {
        return customerNoRepository.findAll();
    }
}
