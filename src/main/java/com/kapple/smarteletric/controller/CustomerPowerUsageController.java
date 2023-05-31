package com.kapple.smarteletric.controller;


import com.kapple.smarteletric.domain.CustomerDayPowerUsage;
import com.kapple.smarteletric.domain.CustomerMonthPowerUsage;
import com.kapple.smarteletric.domain.CustomerPowerUsage;
import com.kapple.smarteletric.domain.CustomerYearPowerUsage;
import com.kapple.smarteletric.kepcoAPI.CustomerJoinInfoDataAPI;
import com.kapple.smarteletric.scheduler.PowerUsageDataInitializer;
import com.kapple.smarteletric.scheduler.PowerUsageScheduler;
import com.kapple.smarteletric.service.CustomerPowerUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/power-usage")
@RestController
public class CustomerPowerUsageController {

    private final CustomerPowerUsageService customerPowerUsageService;
    private final PowerUsageScheduler powerUsageScheduler;
    private final PowerUsageDataInitializer powerUsageDataInitializer;

    @GetMapping("/special/initialize")
    public ResponseEntity initializeAllData(){
        powerUsageDataInitializer.initializer();
        System.out.println("initialized");
        return ResponseEntity.ok("initialized");
    }

    @GetMapping("/special/initialize/day")
    public ResponseEntity initializeDayData(){
        powerUsageDataInitializer.initializeDayData();
        System.out.println("day data initialized");
        return ResponseEntity.ok("day data initialized");
    }

    @GetMapping("/special/initialize/month")
    public ResponseEntity initializeMonthData(){
        powerUsageDataInitializer.initializeMonthDataAndAfter();
        System.out.println("month initialized");
        return ResponseEntity.ok("month initialized");
    }

    @GetMapping("/specific-user/period/hour")
    public ResponseEntity<List<CustomerPowerUsage>> getSpecificUserDataBetweenGivenHours(@RequestParam("custNo") String custNo,
                                                                                         @RequestParam("startDate") String startDate,
                                                                                         @RequestParam("endDate") String endDate){
        List<CustomerPowerUsage> specificUserPowerUsageBetweenGivenHours = customerPowerUsageService.getSpecificUserPowerUsageBetweenGivenHours(custNo, startDate, endDate);
        return ResponseEntity.ok(specificUserPowerUsageBetweenGivenHours);
    }

    @GetMapping("/specific-user/period/day")
    public ResponseEntity<List<CustomerDayPowerUsage>> getSpecificUserDataBetweenGivenDays(@RequestParam("custNo") String custNo,
                                                                                           @RequestParam("startDate") String startDate,
                                                                                           @RequestParam("endDate") String endDate) {
        List<CustomerDayPowerUsage> specificUserPowerUsageBetweenGivenDays = customerPowerUsageService.getSpecificUserPowerUsageBetweenGivenDays(custNo, startDate, endDate);
        return ResponseEntity.ok(specificUserPowerUsageBetweenGivenDays);
    }

    @GetMapping("/specific-user/period/month")
    public ResponseEntity<List<CustomerMonthPowerUsage>> getSpecificUserDataBetweenGivenMonths(@RequestParam("custNo") String custNo,
                                                                                               @RequestParam("startDate") String startDate,
                                                                                               @RequestParam("endDate") String endDate){
        List<CustomerMonthPowerUsage> specificUserPowerUsageBetweenGivenMonths = customerPowerUsageService.getSpecificUserPowerUsageBetweenGivenMonths(custNo, startDate, endDate);
        return ResponseEntity.ok(specificUserPowerUsageBetweenGivenMonths);
    }

    @GetMapping("/specific-user/period/month/most-recent")
    public ResponseEntity<Object> getSpecificUserDataBetweenGivenMonths(@RequestParam("custNo") String custNo){
        try{
            CustomerMonthPowerUsage recentMonthPowerUsage = customerPowerUsageService.getRecentMonthPowerUsage(custNo);
            return ResponseEntity.ok(recentMonthPowerUsage);
        }catch (Exception e) {
            e.printStackTrace();
            RequestFailHandler requestFailHandler = new RequestFailHandler();
            requestFailHandler.setExceptionLog(e.toString());
            return ResponseEntity.ok(requestFailHandler);
        }
    }

    @GetMapping("/specific-user/period/day/most-recent")
    public ResponseEntity<Object> getSpecificUserDataLastAndSecondLastMonth(@RequestParam("custNo") String custNo){
        try{
            List<CustomerDayPowerUsage> lastMonthPowerUsage = customerPowerUsageService.getLastMonthDaysPowerUsage(custNo);
            List<CustomerDayPowerUsage> secondLastMonthPowerUsage = customerPowerUsageService.getSecondLastMonthDaysPowerUsage(custNo);
            Map<String, List<CustomerDayPowerUsage>> graphData = new HashMap<>();
            graphData.put("lastMonth", lastMonthPowerUsage);
            graphData.put("secondLastMonth", secondLastMonthPowerUsage);
            return ResponseEntity.ok(graphData);
        }catch (Exception e){
            e.printStackTrace();
            RequestFailHandler requestFailHandler = new RequestFailHandler();
            requestFailHandler.setExceptionLog(e.toString());
            return ResponseEntity.ok(requestFailHandler);
        }
    }

    @GetMapping("/specific-user/period/hour/most-recent")
    public ResponseEntity<List<CustomerPowerUsage>> getMostRecentHourData(@RequestParam("custNo") String custNo){
        List<CustomerPowerUsage> recentPowerUsages = customerPowerUsageService.getRecentPowerUsages(custNo);
        return ResponseEntity.ok(recentPowerUsages);
    }

    @GetMapping("/specific-user/period/year")
    public ResponseEntity<List<CustomerYearPowerUsage>> getSpecificUserDataBetweenGivenYears(@RequestParam("custNo") String custNo,
                                                                                             @RequestParam("startDate") String startDate,
                                                                                             @RequestParam("endDate") String endDate){
        List<CustomerYearPowerUsage> specificUserPowerUsageBetweenGivenYears = customerPowerUsageService.getSpecificUserPowerUsageBetweenGivenYears(custNo, startDate, endDate);
        return ResponseEntity.ok(specificUserPowerUsageBetweenGivenYears);
    }

    @GetMapping("/specific-user/whole/hour")
    public ResponseEntity<List<CustomerPowerUsage>> getSpecificUserWholeDataByHour(@RequestParam("custNo") String custNo) {
        List<CustomerPowerUsage> specificUserWholeDataByHour = customerPowerUsageService.getSpecificUserWholeDataByHour(custNo);
        return ResponseEntity.ok(specificUserWholeDataByHour);
    }

    @GetMapping("/specific-user/whole/day")
    public ResponseEntity<List<CustomerDayPowerUsage>> getSpecificUserWholeDataByDay(@RequestParam("custNo") String custNo) {
        List<CustomerDayPowerUsage> specificUserWholeDataByDay = customerPowerUsageService.getSpecificUserWholeDataByDay(custNo);
        return ResponseEntity.ok(specificUserWholeDataByDay);
    }

    @GetMapping("/specific-user/whole/month")
    public ResponseEntity<List<CustomerMonthPowerUsage>> getSpecificUserWholeDataByMonth(@RequestParam("custNo") String custNo) {
        List<CustomerMonthPowerUsage> specificUserWholeDataByMonth = customerPowerUsageService.getSpecificUserWholeDataByMonth(custNo);
        return ResponseEntity.ok(specificUserWholeDataByMonth);
    }

    @GetMapping("/specific-user/whole/year")
    public ResponseEntity<List<CustomerYearPowerUsage>> getSpecificUserWholeDataByYear(@RequestParam("custNo") String custNo) {
        List<CustomerYearPowerUsage> specificUserWholeDataByYear = customerPowerUsageService.getSpecificUserWholeDataByYear(custNo);
        return ResponseEntity.ok(specificUserWholeDataByYear);
    }

    @GetMapping("/test/daily-schedule")
    public ResponseEntity<List<CustomerDayPowerUsage>> buildDailyPowerUsageData(@RequestParam("date") String date) {
        powerUsageScheduler.scheduledBuildDayPowerUsageData();
        List<CustomerDayPowerUsage> allUserPowerUsagePerMonth = customerPowerUsageService.getAllUserPowerUsagePerMonth(date);
        return ResponseEntity.ok(allUserPowerUsagePerMonth);
    }

    @GetMapping("/test/monthly-schedule")
    public ResponseEntity<List<CustomerMonthPowerUsage>> buildMonthPowerUsageData(@RequestParam("date") String date) {
        powerUsageScheduler.scheduledBuildMonthPowerUsageData();
        List<CustomerMonthPowerUsage> allUserPowerUsagePerYear = customerPowerUsageService.getAllUserPowerUsagePerYear(date);
        return ResponseEntity.ok(allUserPowerUsagePerYear);
    }

    @GetMapping("/test/annual-schedule")
    public ResponseEntity<List<CustomerYearPowerUsage>> buildAnnualPowerUsageData(@RequestParam("date") String date) {
        powerUsageScheduler.scheduledBuildYearPowerUsageData();
        List<CustomerYearPowerUsage> allUserPowerUsage = customerPowerUsageService.getAllUserPowerUsage(date);
        return ResponseEntity.ok(allUserPowerUsage);
    }

    @GetMapping("/specific-user")
    public ResponseEntity<List<CustomerPowerUsage>> getSpecificUserPowerUsagePerDay(@RequestParam("custNo") String custNo,
                                                                          @RequestParam("date") String date) {

        Boolean dataExistence = customerPowerUsageService.dataCheck(custNo, date);

        if (dataExistence) {
            List<CustomerPowerUsage> targetPowerUsageData = customerPowerUsageService.getSpecificUserPowerUsagePerDay(custNo, date);
            System.out.println("targetPowerUsageData = " + targetPowerUsageData.toString());
            return ResponseEntity.ok(targetPowerUsageData);
        }else {
            // 데이터가 DB내에 없다면 한전에 요청해서 모든 유저의 해당 날짜 데이터 받아오기.
            saveAllUserPowerUsagePerDay(date);
            dataExistence = customerPowerUsageService.dataCheck(custNo, date );
            if(dataExistence){
                System.out.println("데이터 있음");
                List<CustomerPowerUsage> targetPowerUsageData = customerPowerUsageService.getSpecificUserPowerUsagePerDay(custNo, date);
                return ResponseEntity.ok(targetPowerUsageData);
            }
            System.out.println("데이터 없음");
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/specific-user")
    public ResponseEntity saveSpecificUserPowerUsagePerDay(@RequestParam(value = "custNo") String custNo,
                                                       @RequestParam(value = "date") String date) {
        CustomerJoinInfoDataAPI customerJoinInfoDataAPI = new CustomerJoinInfoDataAPI(custNo);
        Boolean userValidation = customerJoinInfoDataAPI.isJoinedCustomer(custNo);
        if (userValidation) {
            Boolean dataExistence = customerPowerUsageService.dataCheck(custNo, date);
            if (dataExistence) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }else {
                //해당 고객의 일단위 전력소비 데이터가 없다면 한전에 요청해서 받아오기. 이때 해당날짜의 전체 고객에 대한 데이터를 받아와서 DB에 저장한다.
                saveAllUserPowerUsagePerDay(date);
                List<CustomerPowerUsage> specificUserPowerUsagePerDay = customerPowerUsageService.getSpecificUserPowerUsagePerDay(custNo, date);
                return ResponseEntity.ok(specificUserPowerUsagePerDay);
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/all-user")
    public ResponseEntity saveAllUserPowerUsagePerDay(@RequestParam(value = "date") String date){
        String rawData = customerPowerUsageService.getPowerUsageDataFromKepcoAPI(date);
        System.out.println("rawData = " + rawData);
        customerPowerUsageService.saveCustomerPowerUsageData(rawData);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/special/specific-user/delete")
    public ResponseEntity deleteSpecificUserPowerUsagePerHour(@RequestParam("custNo") String custNo){
        customerPowerUsageService.deleteRecentPowerUsage(custNo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
