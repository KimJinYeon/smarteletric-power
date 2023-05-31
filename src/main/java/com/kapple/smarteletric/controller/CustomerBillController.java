package com.kapple.smarteletric.controller;

import com.kapple.smarteletric.domain.CustomerBill;
import com.kapple.smarteletric.kepcoAPI.CustomerJoinInfoDataAPI;
import com.kapple.smarteletric.scheduler.PowerUsageDataInitializer;
import com.kapple.smarteletric.service.CustomerBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 컨트롤러는 어플리케이션의 사용자 또는 클라이언트가 입력한 값에 대한 응답을 수행한다.
// 데이터를 다루거나 별도의 로직을 처리해야하는 경우에는 서비스 또는 데이터 액세스 레이어까지 요청을 전달하는 것이 일반적이다.

//HtttpServletRequest 들어옴 -> 핸들러(Controller) 매핑 -> @RestController -> MessageConverer -> HTTP응답
// : 뷰가 없는 REST 형식의 @ReponseBody를 사용할 예정이라 뷰리졸버를 호출하지 않고 MessageConverter를 거쳐서 JSON 형식으로 반환하게 된다.

// 클래스 수준에서 @RequestMapping을 추가하면 내부에 선언한 메서드의 URL 리소스 앞에 @RequestMapping의 값이 공통 값으로 추가된다.
@RequiredArgsConstructor
@RequestMapping("/bill")
@RestController
public class CustomerBillController {

    private final CustomerBillService customerBillService;
    private final PowerUsageDataInitializer powerUsageDataInitializer;
    @GetMapping("/special/initialize")
    public ResponseEntity initializeBillData(){
        powerUsageDataInitializer.initializeBillData();
        return ResponseEntity.ok("bill data initialized");
    }

    @GetMapping("/specific-user/all")
    public ResponseEntity<List<CustomerBill>> getAllCustomerBill(@RequestParam("custNo") String custNo) {
        return ResponseEntity.ok(customerBillService.findAllByCustNo(custNo));
    }
    public ResponseEntity<CustomerBill> getCustomerBill(@RequestParam("custNo") String custNo,
                                                        @RequestParam("dataMonth") String dataMonth) {

        Boolean dataExistence = customerBillService.dataCheck(custNo, dataMonth);

        if (dataExistence) {
            CustomerBill customerBill = customerBillService.getCustomerBill(custNo, dataMonth);
            return ResponseEntity.ok(customerBill); // return target customerBill object
        } else {
            // 데이터가 DB내에 없다면 한전에 요청해서 받아오기.
            saveCustomerBill(custNo, dataMonth);
            dataExistence = customerBillService.dataCheck(custNo, dataMonth);
            if(dataExistence) {
                CustomerBill customerBill = customerBillService.getCustomerBill(custNo, dataMonth);
                return ResponseEntity.ok(customerBill); // return target customerBill object
            }
            return ResponseEntity.notFound().build(); // return HTTP Status Code 404 Not Found
        }
    }

    @GetMapping("/specific-user/bill-date")
    public ResponseEntity<Map<String, String>> getCustomerBillDate(@RequestParam("custNo") String custNo){
        String billDate = customerBillService.findBillDate(custNo);
        Map<String, String> result = new HashMap<>();
        result.put("result", billDate);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/specific-user")
    public ResponseEntity saveCustomerBill(@RequestParam(value = "custNo") String custNo,
                                                       @RequestParam(value = "dataMonth") String dataMonth){

        CustomerJoinInfoDataAPI customerJoinInfoDataAPI = new CustomerJoinInfoDataAPI(custNo);
        Boolean userValidation = customerJoinInfoDataAPI.isJoinedCustomer(custNo);
        if (userValidation) {
            Boolean dataExistence = customerBillService.dataCheck(custNo, dataMonth);
            if (dataExistence) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }else {
                customerBillService.saveCustomerBillData(custNo, dataMonth);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
