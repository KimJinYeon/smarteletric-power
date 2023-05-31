package com.kapple.smarteletric.service;

import com.kapple.smarteletric.domain.CustomerInfo;
import com.kapple.smarteletric.repository.CustomerInfoRepository;
import com.kapple.smarteletric.kepcoAPI.CustomerInfoDataAPI;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CustomerInfoService {

    private final CustomerInfoRepository customerInfoRepository;


    public void saveCustomerInfoData(String custNo) {
        CustomerInfoDataAPI customerInfoDataAPI = new CustomerInfoDataAPI(custNo);
        String rawCustomerInfoData = customerInfoDataAPI.getRawData();
        System.out.println("rawCustomerInfoData = " + rawCustomerInfoData);
        CustomerInfo customerInfo = new CustomerInfo();
        JSONObject jsonObject = customerInfoDataAPI.getJsonObject(rawCustomerInfoData);
        customerInfo.setParameters(jsonObject);
        customerInfoRepository.save(customerInfo);
    }

    public Boolean dataCheck(String custNo) {
        String documentID = "CustomerInfo" + "-" + custNo;
        return customerInfoRepository.existsByID(documentID);
    }

    public CustomerInfo getCustomerInfo(String custNo) {
        String documentID = "CustomerInfo" + "-" + custNo;
        return customerInfoRepository.findCustomerInfoByID(documentID);
    }
}
