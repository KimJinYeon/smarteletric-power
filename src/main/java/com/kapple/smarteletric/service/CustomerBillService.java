package com.kapple.smarteletric.service;

import com.kapple.smarteletric.domain.CustomerBill;
import com.kapple.smarteletric.repository.CustomerBillRepository;
import com.kapple.smarteletric.kepcoAPI.CustomerBillDataAPI;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerBillService {

    private final CustomerBillRepository customerBillRepository;

    public void saveCustomerBillData(String custNo, String dataMonth) {
        CustomerBillDataAPI customerBillDataAPI = new CustomerBillDataAPI(custNo, dataMonth);
        //String rawData = "{\"custBillDataInfoList\":[{\"custNo\":\"0103288853\",\"bill_ym\":\"202207\",\"mr_ymd\":\"12\",\"bill_aply_pwr\":3,\"move_ymd\":\"\",\"base_bill\":910,\"kwh_bill\":15005,\"dc_bill\":-1531,\"req_bill\":16364,\"req_amt\":18600,\"lload_usekwh\":0,\"mload_usekwh\":161,\"maxload_usekwh\":0,\"lload_needle\":0,\"mload_needle\":1825,\"maxload_needle\":0,\"jn_pwrfact\":0,\"ji_pwrfact\":0}]}";
        String rawData = customerBillDataAPI.getRawData();
        JSONObject jsonObject = customerBillDataAPI.getJsonObject(rawData);
        CustomerBill customerBill = new CustomerBill();
        try {
            customerBill.setParameters(jsonObject);
            customerBillRepository.save(customerBill);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Boolean dataCheck(String custNo, String dataMonth) {
        String documentID = "CustomerBill" + "-" + custNo + "-" + dataMonth;
        return customerBillRepository.existsByID(documentID);
    }

    public CustomerBill getCustomerBill(String custNo, String dataMonth){
        String documentID = "CustomerBill" + "-" + custNo + "-" + dataMonth;
        return customerBillRepository.findCustomerBillByID(documentID);
    }

    public Boolean existByCustNo(String custNo){
        return customerBillRepository.existsByCustNo(custNo);
    }

    public List<CustomerBill> findAllByCustNo(String custNo){
        return customerBillRepository.findAllByCustNoEquals(custNo);
    }

    public String findBillDate(String custNo){
        List<CustomerBill> allBillInfo = customerBillRepository.findAllByCustNoEquals(custNo);
        if(allBillInfo.size() == 0) {
            return "0";
        }else{
            return allBillInfo.get(0).mr_ymd;
        }
    }
}
