package com.kapple.smarteletric.scheduler;

import com.kapple.smarteletric.kepcoAPI.CustomerJoinInfoDataAPI;
import com.kapple.smarteletric.repository.CustomerDayPowerUsageRepository;
import com.kapple.smarteletric.repository.CustomerMonthPowerUsageRepository;
import com.kapple.smarteletric.repository.CustomerPowerUsageRepository;
import com.kapple.smarteletric.repository.CustomerYearPowerUsageRepository;
import com.kapple.smarteletric.service.CustomerBillService;
import com.kapple.smarteletric.service.CustomerNoService;
import com.kapple.smarteletric.service.CustomerPowerUsageService;
import com.kapple.smarteletric.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PowerUsageDataInitializer {
    // 전력 사용량 데이터가 없는 날짜 고려하여 PowerUsage Repository 초기화하고 다시 요청하는 기능 구현한 클래스


    private final CustomerPowerUsageRepository customerPowerUsageRepository; // 시간별 전력 사용량 Repository DI
    private final CustomerDayPowerUsageRepository customerDayPowerUsageRepository; // 일별 전력 사용량 Repository DI
    private final CustomerMonthPowerUsageRepository customerMonthPowerUsageRepository; // 월별 전력 사용량 Repository DI
    private final CustomerYearPowerUsageRepository customerYearPowerUsageRepository; // 연도별 전력 사용량 Repository DI
    private final CustomerPowerUsageService customerPowerUsageService; // 전력 사용량 Service Component DI
    private final CustomerNoService customerNoService; // 고객번호 service DI
    private final CustomerBillService customerBillService; // Bill service DI

    public void initializer(){
        deleteAllPowerUsageRepository(); // repository 다 지우기
        initializeCustNoDataFromKepco(); // 고객정보 다 지우고 새로 받기
        List<LocalDate> startDateAndEndDateInList = getStartDateAndEndDateInList(); // 시작 날짜와 끝 날짜 지정
        initializeAllPowerUsageRepository(startDateAndEndDateInList); // 시간 단위 initialize
//        initializeAllDayPowerUsageRepository(startDateAndEndDateInList); // 일 단위 initialize
//        initializeAllMonthPowerUsageRepository(startDateAndEndDateInList); // 월 단위 initialize
//        initializeAllYearPowerUsageRepository(2022); // 2022 initialize
    }

    public void initializeDayData(){
        deleteAllPowerUsageByDay();
        List<LocalDate> startDateAndEndDateInList = getStartDateAndEndDateInList();
        initializeAllDayPowerUsageRepository(startDateAndEndDateInList);
    }

    public void initializeMonthDataAndAfter() {
        deleteAllPowerUsageByMonth();
        deleteAllPowerUsageByYear();
        buildMonthPowerUsageData();
        initializeAllYearPowerUsageRepository(2022); // 2022 initialize
    }

    public void initializeBillData() {
        initializeCustomerBillRepository();
    }


    private void deleteAllPowerUsageRepository() { // 모든 PowerUsage Repository 초기화 메서드
        deleteAllPowerUsageByHour(); // 시간별 전력 사용량 데이터 초기화
        deleteAllPowerUsageByDay(); // 일별 누적 전력 사용량 데이터 초기화
        deleteAllPowerUsageByMonth(); // 월별 누적 전력 사용량 데이터 초기화
        deleteAllPowerUsageByYear(); // 연도별 누적 전력 사용량 데이터 초기화
    }

    private void initializeCustomerBillRepository() {
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

    private void initializeAllPowerUsageRepository(List<LocalDate> dateRange) { // 모든 PowerUsage Repository 데이터 축적 메서드
        LocalDate startDate = dateRange.get(0); // 시작 날짜
        LocalDate endDate = dateRange.get(1); // 마지막 날짜(오늘)
        while (!startDate.equals(endDate)){ // 시작 날짜부터 마지막 날짜까지 하루씩 더해가며
            System.out.println("startDate = " + startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            String powerUsageDataFromKepcoAPI = customerPowerUsageService.getPowerUsageDataFromKepcoAPI(startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            customerPowerUsageService.saveCustomerPowerUsageData(powerUsageDataFromKepcoAPI); // 데이터 저장
            startDate = startDate.plusDays(1); // 하루 더하기
        }
    }

    private void initializeAllDayPowerUsageRepository(List<LocalDate> dateRange) {
        LocalDate startDate = dateRange.get(0); // 시작 날짜
        LocalDate endDate = dateRange.get(1); // 마지막 날짜(오늘)
        while (!startDate.equals(endDate)){ // 시작 날짜부터 마지막 날짜까지 하루씩 더해가며
            String startDateInStr = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            buildDayPowerUsageData(startDateInStr);
            startDate = startDate.plusDays(1); // 하루 더하기
        }
    }

    private void initializeAllYearPowerUsageRepository(Integer year){
        buildYearPowerUsageData(year);
    }
    private List<LocalDate> getStartDateAndEndDateInList() { // 시작 날짜와 오늘 날짜를 리스트에 넣어서 반환하는 메서드

        String dateOnYesterday = getDateOnYesterday(); // 어제 날짜
        LocalDate startDate = LocalDate.parse("20220803", DateTimeFormatter.ofPattern("yyyyMMdd")); // 첫 스마트 계량기 정보제공 동의 고객 날짜
        LocalDate endDate = LocalDate.parse(dateOnYesterday, DateTimeFormatter.ofPattern("yyyyMMdd")).plusDays(1); // 오늘 날짜

        List<LocalDate> startDateEndDateInList = new ArrayList<>();

        startDateEndDateInList.add(startDate);
        startDateEndDateInList.add(endDate);

        return startDateEndDateInList;
    }

    private void buildDayPowerUsageData(String targetDate) {
        LocalDateTime targetDayLocalDateTime = LocalDateTime.parse(targetDate+"00", DateTimeFormatter.ofPattern("yyyyMMddHH"));

        List<CustomerNo> customerNoList = customerNoService.findAllCustomerNoData();
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
            customerDayPowerUsage.dateTimeKr = LocalDate.parse(targetDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
            customerDayPowerUsage.powerUsageQuantity = pwr_qty;
            customerDayPowerUsage.ID = "PowerUsageByDay" + "-" + custNo + "-" + customerDayPowerUsage.dateTimeKr.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            customerDayPowerUsageRepository.save(customerDayPowerUsage);
        }
    }

    private void buildMonthPowerUsageData() {
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
                    monthlyPowerUsage.endDateKr = firstBillDate;
                    monthlyPowerUsage.ID = "PowerUsageByMonth" + "-" + custNo + "-" + firstBillDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
                    monthlyPowerUsage.custNo = custNo;
                    monthlyPowerUsage.startDateKr = daysBetweenUpdate.get(0).dateTimeKr;
                    monthlyPowerUsage.dateTimeKr = daysBetweenUpdate.get(daysBetweenUpdate.size()-1).dateTimeKr;
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
                monthlyPowerUsage.endDateKr = today;
                monthlyPowerUsage.dateTimeKr = daysBetweenUpdate.get(daysBetweenUpdate.size()-1).dateTimeKr;
                monthlyPowerUsage.powerUsageQuantity = pwr_qty;
                customerMonthPowerUsageRepository.save(monthlyPowerUsage);
            }
        }
    }

    private void buildYearPowerUsageData(Integer year) {
        List<CustomerNo> customerNoList = customerNoService.findAllCustomerNoData();
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
                annualPowerUsage.ID = "PowerUsageByYear" + "-" + custNo + "-" + annualPowerUsage.dateTimeKr.format(DateTimeFormatter.ofPattern("yyyy"));
                customerYearPowerUsageRepository.save(annualPowerUsage);
            }
        }
    }

    private String getDateOnYesterday(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
    }

    private void initializeCustNoDataFromKepco(){
        customerNoService.deleteAllCustomerNoData();
        customerNoService.saveAllCustomerNoData();
    }

    private void deleteAllPowerUsageByHour() {
        customerPowerUsageRepository.deleteAll(); // 시간별 전력 사용량 데이터 초기화
    }

    private void deleteAllPowerUsageByDay() {
        customerDayPowerUsageRepository.deleteAll(); // 일별 누적 전력 사용량 데이터 초기화
    }

    private void deleteAllPowerUsageByMonth() {
        customerMonthPowerUsageRepository.deleteAll(); // 월별 누적 전력 사용량 데이터 초기화
    }

    private void deleteAllPowerUsageByYear() {
        customerYearPowerUsageRepository.deleteAll(); // 연도별 누적 전력 사용량 데이터 초기화
    }


}
