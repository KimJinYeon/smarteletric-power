package com.kapple.smarteletric.scheduler;

import com.kapple.smarteletric.kepcoAPI.CustomerJoinInfoDataAPI;
import com.kapple.smarteletric.kepcoAPI.CustomerPowerUsageAPI;
import com.kapple.smarteletric.repository.*;
import com.kapple.smarteletric.service.CustomerBillService;
import com.kapple.smarteletric.service.CustomerNoService;
import com.kapple.smarteletric.service.CustomerPowerUsageService;
import com.google.gson.Gson;
import com.kapple.smarteletric.domain.*;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Component
public class PowerUsageScheduler {

    private final CustomerPowerUsageService customerPowerUsageService;
    private final CustomerNoRepository customerNoRepository;
    private final CustomerPowerUsageRepository customerPowerUsageRepository;
    private final CustomerDayPowerUsageRepository customerDayPowerUsageRepository;
    private final CustomerMonthPowerUsageRepository customerMonthPowerUsageRepository;
    private final CustomerYearPowerUsageRepository customerYearPowerUsageRepository;
    private final CustomerNoService customerNoService;
    private final CustomerBillService customerBillService;
    CustomerPowerUsageAPI customerPowerUsageAPI = new CustomerPowerUsageAPI();


    @Scheduled(cron = "0 0 2 * * *") // 매일 2시에 실행
    public void scheduledGetPowerUsageFromKepco() {
        String yesterday = getDateOnYesterday();
        String rawData = customerPowerUsageAPI.getRawData(yesterday);
        customerPowerUsageService.saveCustomerPowerUsageData(rawData);
    }

    @Scheduled(cron = "0 0 3 * * *") // 매일 3시에 실행
    public void scheduledBuildDayPowerUsageData() {
        getAllCustNoDataFromKepco(); // 전체 고객번호 목록을 한전에서 불러와서 저장
        getAllCustBillDataFromKepco(); // 전체 청구정보 목록을 한전에서 불러와서 저장
        String dateOnYesterday = getDateOnYesterday();
        LocalDateTime targetDayLocalDateTime = LocalDateTime.parse(dateOnYesterday+"00", DateTimeFormatter.ofPattern("yyyyMMddHH"));
        List<CustomerNo> customerNoList = customerNoRepository.findAll();
        for (CustomerNo customerNo : customerNoList) {

            String custNo = customerNo.custNo;
            List<CustomerPowerUsage> yesterdayPowerUsageList =
                    customerPowerUsageRepository
                            .findAllByCustNoEqualsAndDateTimeKrBetween(custNo,
                                    targetDayLocalDateTime,
                                    targetDayLocalDateTime.plusDays(1).plusHours(1));
            Double pwr_qty = 0.0;

            for (CustomerPowerUsage customerPowerUsage : yesterdayPowerUsageList){
                pwr_qty += customerPowerUsage.powerUsageQuantity;
            }

            CustomerDayPowerUsage customerDayPowerUsage = new CustomerDayPowerUsage();
            customerDayPowerUsage.custNo = custNo;
            customerDayPowerUsage.dateTimeKr = LocalDate.parse(dateOnYesterday, DateTimeFormatter.ofPattern("yyyyMMdd"));
            customerDayPowerUsage.powerUsageQuantity = pwr_qty;
            customerDayPowerUsage.ID = "PowerUsageByDay" + "-" + custNo + "-" + customerDayPowerUsage.dateTimeKr.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            customerDayPowerUsageRepository.save(customerDayPowerUsage);
        }
    }

    @Scheduled(cron = "0 0 4 * * *") // 매일 4시에 실행
    public void scheduledBuildMonthPowerUsageData() {
        List<CustomerNo> customerNoList = customerNoService.findAllCustomerNoData();
        for (CustomerNo customerNo : customerNoList) {
            String custNo = customerNo.custNo;
            Boolean billDataExistence = customerBillService.existByCustNo(custNo);
            CustomerJoinInfoDataAPI customerJoinInfoDataAPI = new CustomerJoinInfoDataAPI(custNo);
            LocalDate agreementDate = LocalDate.parse(customerJoinInfoDataAPI.agreementSignedDate(custNo), DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate lastBillDate;
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
            if (billDataExistence){
                List<CustomerBill> customerAllBillData = customerBillService.findAllByCustNo(custNo);
                String mr_ymd = customerAllBillData.get(0).mr_ymd;
                LocalDate firstBillDate;
                if (agreementDate.getDayOfMonth() < Integer.parseInt(mr_ymd)){
                    firstBillDate = agreementDate.withDayOfMonth(Integer.parseInt(mr_ymd));
                } else {
                    firstBillDate = agreementDate.plusMonths(1).withDayOfMonth(Integer.parseInt(mr_ymd));
                }
                while (firstBillDate.isBefore(today.plusDays(28))){
                    System.out.println("firstBillDate = " + firstBillDate);
                    CustomerMonthPowerUsage monthlyPowerUsage = new CustomerMonthPowerUsage();
                    List<CustomerDayPowerUsage> daysBetweenUpdate = customerDayPowerUsageRepository.findAllByCustNoEqualsAndDateTimeKrBetween(custNo,
                            firstBillDate.minusMonths(1).minusDays(1), firstBillDate);
                    Double pwr_qty = 0.0;
                    for(CustomerDayPowerUsage customerDayPowerUsage : daysBetweenUpdate){
                        pwr_qty += customerDayPowerUsage.powerUsageQuantity;
                    }
                    monthlyPowerUsage.endDateKr = daysBetweenUpdate.get(daysBetweenUpdate.size()-1).dateTimeKr;
                    monthlyPowerUsage.ID = "PowerUsageByMonth" + "-" + custNo + "-" + firstBillDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
                    monthlyPowerUsage.custNo = custNo;
                    monthlyPowerUsage.startDateKr = daysBetweenUpdate.get(0).dateTimeKr;
                    monthlyPowerUsage.dateTimeKr = firstBillDate;
                    monthlyPowerUsage.powerUsageQuantity = pwr_qty;
                    customerMonthPowerUsageRepository.save(monthlyPowerUsage);
                    firstBillDate = firstBillDate.plusMonths(1);
                }
            }else{
                CustomerMonthPowerUsage monthlyPowerUsage = new CustomerMonthPowerUsage();
                List<CustomerDayPowerUsage> daysBetweenUpdate = customerDayPowerUsageRepository.findAllByCustNoEqualsAndDateTimeKrBetween(custNo,
                        agreementDate, today);
                Double pwr_qty = 0.0;
                for(CustomerDayPowerUsage customerDayPowerUsage : daysBetweenUpdate){
                    pwr_qty += customerDayPowerUsage.powerUsageQuantity;
                }
                monthlyPowerUsage.ID = "PowerUsageByMonth" + "-" + custNo + "-" + today.format(DateTimeFormatter.ofPattern("yyyyMM"));
                monthlyPowerUsage.custNo = custNo;
                monthlyPowerUsage.startDateKr = daysBetweenUpdate.get(0).dateTimeKr;
                monthlyPowerUsage.endDateKr = daysBetweenUpdate.get(daysBetweenUpdate.size()-1).dateTimeKr;
                monthlyPowerUsage.dateTimeKr = today;
                monthlyPowerUsage.powerUsageQuantity = pwr_qty;
                customerMonthPowerUsageRepository.save(monthlyPowerUsage);
            }
        }
    }

    @Scheduled(cron = "0 0 5 * * *") // 매일 5시에 실행
    public void scheduledBuildYearPowerUsageData() {
        String dateOnYesterday = getDateOnYesterday();
        Integer year = Integer.parseInt(dateOnYesterday.substring(0,4));
        List<CustomerNo> customerNoList = customerNoRepository.findAll();
        for (CustomerNo customerNo : customerNoList) {
            String custNo = customerNo.custNo;
            String expectedID = "PowerUsageByYear" + "-" + custNo + "-" + year;
            Boolean dataExistence = customerYearPowerUsageRepository.existsByID(expectedID);

            LocalDate yearLocalDate = LocalDate.of(year, 1,1);
            List<CustomerMonthPowerUsage> monthBetweenUpdate =
                    customerMonthPowerUsageRepository
                            .findAllByCustNoEqualsAndDateTimeKrBetween(
                                    custNo,
                                    yearLocalDate,
                                    yearLocalDate.withMonth(12).withDayOfMonth(31)
                            );

            Double pwr_qty = 0.0;
            for(CustomerMonthPowerUsage customerMonthPowerUsage : monthBetweenUpdate){
                pwr_qty += customerMonthPowerUsage.powerUsageQuantity;
            }

            if (dataExistence){
                CustomerYearPowerUsage annualPowerUsage = customerYearPowerUsageRepository.findByID(expectedID);
                annualPowerUsage.powerUsageQuantity = pwr_qty;
                annualPowerUsage.dateTimeKr = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
                customerYearPowerUsageRepository.save(annualPowerUsage);
            }else{
                CustomerYearPowerUsage annualPowerUsage = new CustomerYearPowerUsage();
                annualPowerUsage.custNo = custNo;
                annualPowerUsage.dateTimeKr = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
                annualPowerUsage.powerUsageQuantity = pwr_qty;
                annualPowerUsage.ID = "PowerUsageByYear" + "-" + custNo + "-" + year;
                customerYearPowerUsageRepository.save(annualPowerUsage);
            }
        }
    }
//    @Scheduled(cron = "0 0 9 * * *") // 매일 5시에 실행
//    public void scheduledAccumulatedPowerUsage(){
//        fcmWebHook();
//    }

    private void fcmWebHook(){
        String url = "https://api.smartelectric.kr/firebase/message/specific-user";
        List<String> targetCustomers = customerPowerUsageService.checkPowerUsageLimitApproach();
        Map<String, List<String>> targetCustomerJson = new HashMap<>();
        targetCustomerJson.put("customerNumberList", targetCustomers);
        try{
            HttpClient httpClient = HttpClientBuilder.create().build();
            Gson gson = new Gson();
            HttpPost request = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(gson.toJson(targetCustomerJson));
            request.setEntity(stringEntity);
            request.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getDateOnYesterday(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        calendar.add(Calendar.DATE, -1);
        String dateOnYesterday = simpleDateFormat.format(calendar.getTime());
        return dateOnYesterday;
    }

    public void getAllCustNoDataFromKepco(){
        customerNoService.deleteAllCustomerNoData();
        customerNoService.saveAllCustomerNoData();
    }

    public void getAllCustBillDataFromKepco() {
        List<CustomerNo> customerNoList = customerNoService.findAllCustomerNoData();

        for (CustomerNo customerNo : customerNoList) {
            String custNo = customerNo.custNo;
            CustomerJoinInfoDataAPI customerJoinInfoDataAPI = new CustomerJoinInfoDataAPI(custNo);
            LocalDate agreementDate = LocalDate.parse(customerJoinInfoDataAPI.agreementSignedDate(custNo), DateTimeFormatter.ofPattern("yyyyMMdd")).withDayOfMonth(1);
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul")).withDayOfMonth(2);
            while(agreementDate.isBefore(today)){
                if(!customerBillService.dataCheck(custNo, agreementDate.format(DateTimeFormatter.ofPattern("yyyyMM")))){
                    customerBillService.saveCustomerBillData(custNo, agreementDate.format(DateTimeFormatter.ofPattern("yyyyMM")));
                }
                agreementDate = agreementDate.plusMonths(1);
            }
        }
    }

}
