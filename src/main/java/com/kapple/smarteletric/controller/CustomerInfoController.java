package com.kapple.smarteletric.controller;

import com.kapple.smarteletric.domain.CustomerInfo;
import com.kapple.smarteletric.kepcoAPI.CustomerJoinInfoDataAPI;
import com.kapple.smarteletric.service.CustomerInfoService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/info")
@RestController
public class CustomerInfoController {

    // @RequiredArgsConstructor 어노테이션은 lombok을 사용하여
    // 생성자를 통한 의존성 주입 방식을 다음과 같이 final이 붙거나 @NotNull이 붙은 필드의 생성자를 자동으로 생성해준다.
    // lombok은 getter setter, toString등의 메서드 작성 코드를 줄여주는 코드 라이브러리이다.
    // (참고로 스프링 공식 문서에서는 생성자를 통한 의존성을 주입하는 방식을 권장한다. 이유는 래퍼런스 객체 없이 객체를 초기화 할 수 없게 설계할 수 있기 때문이다.)
    private final CustomerInfoService customerInfoService;

    @GetMapping("/specific-user/validation")
    public ResponseEntity<Map<String, Object>> getCustomerValidation(@RequestParam("custNo") String custNo){
        CustomerJoinInfoDataAPI customerJoinInfoDataAPI = new CustomerJoinInfoDataAPI(custNo);
        JSONObject specificCustomerJoinInfoJsonObject = customerJoinInfoDataAPI.getSpecificCustomerJoinInfo(custNo);
        Map<String, Object> stringObjectMap = specificCustomerJoinInfoJsonObject.toMap();
        String specificCustomerJoinInfoStringData = specificCustomerJoinInfoJsonObject.toString();
        return ResponseEntity.ok(stringObjectMap);
    }
    //
    @GetMapping("/specific-user")
    public ResponseEntity<CustomerInfo> getCustomerInfo(@RequestParam("custNo") String custNo){
        Boolean dataExistence = customerInfoService.dataCheck(custNo);
        if (dataExistence) {
            CustomerInfo targetInfoData = customerInfoService.getCustomerInfo(custNo);
            return ResponseEntity.ok(targetInfoData);
        }else {
            // 데이터가 DB내에 없다면 한전에 요청해서 받아오기.
            saveCustomerInfo(custNo);
            dataExistence = customerInfoService.dataCheck(custNo);
            if(dataExistence){
                CustomerInfo targetInfoData = customerInfoService.getCustomerInfo(custNo);
                return ResponseEntity.ok(targetInfoData);
            }
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/specific-user")
    public ResponseEntity saveCustomerInfo(@RequestParam("custNo") String custNo) {

        CustomerJoinInfoDataAPI customerJoinInfoDataAPI = new CustomerJoinInfoDataAPI(custNo);
        Boolean userValidation = customerJoinInfoDataAPI.isJoinedCustomer(custNo);
        if (userValidation) {
            Boolean dataExistence = customerInfoService.dataCheck(custNo);
            if (dataExistence) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }else {
                customerInfoService.saveCustomerInfoData(custNo);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}
