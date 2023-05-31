package com.kapple.smarteletric.service;

import com.kapple.smarteletric.domain.CustomerDayPowerUsage;
import com.kapple.smarteletric.domain.CustomerMonthPowerUsage;
import com.kapple.smarteletric.domain.CustomerPowerUsage;
import com.kapple.smarteletric.domain.CustomerYearPowerUsage;
import com.kapple.smarteletric.repository.CustomerDayPowerUsageRepository;
import com.kapple.smarteletric.repository.CustomerMonthPowerUsageRepository;
import com.kapple.smarteletric.repository.CustomerPowerUsageRepository;
import com.kapple.smarteletric.kepcoAPI.CustomerPowerUsageAPI;
import com.kapple.smarteletric.repository.CustomerYearPowerUsageRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
public class CustomerPowerUsageService {

    private final CustomerPowerUsageRepository customerPowerUsageRepository;
    private final CustomerDayPowerUsageRepository customerDayPowerUsageRepository;
    private final CustomerMonthPowerUsageRepository customerMonthPowerUsageRepository;
    private final CustomerYearPowerUsageRepository customerYearPowerUsageRepository;


    public String getPowerUsageDataFromKepcoAPI(String date){
        CustomerPowerUsageAPI customerPowerUsageAPI = new CustomerPowerUsageAPI(date);
        return customerPowerUsageAPI.getRawData(date);
    }

    public void saveCustomerPowerUsageData(String rawPowerUsageData) {
        CustomerPowerUsageAPI customerPowerUsageAPI = new CustomerPowerUsageAPI();
        //String rawPowerUsageData = customerPowerUsageAPI.getRawData();
        List<JSONObject> jsonObjectList = customerPowerUsageAPI.getInnerJsonObjectList(customerPowerUsageAPI.getJsonObject(rawPowerUsageData));

        for (JSONObject jsonObject : jsonObjectList) {
            System.out.println("jsonObject = " + jsonObject);
            CustomerPowerUsage customerPowerUsage = new CustomerPowerUsage();
            customerPowerUsage.setParameters(jsonObject);

            String ID = customerPowerUsage.ID;
            System.out.println("ID = " + ID);
            System.out.println("customerPowerUsage = " + customerPowerUsage);
            customerPowerUsageRepository.save(customerPowerUsage);
        }
    }

    public Boolean dataCheck(String custNo, String date) {
        LocalDateTime targetDayLocalDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        return customerPowerUsageRepository
                .existsByCustNoEqualsAndDateTimeKrBetween(
                        custNo,
                        targetDayLocalDateTime.withHour(0),
                        targetDayLocalDateTime.withHour(23));
    }


    public List<CustomerPowerUsage> getSpecificUserPowerUsagePerDay(String custNo, String date) {
        LocalDateTime targetDayLocalDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        return customerPowerUsageRepository
                .findAllByCustNoEqualsAndDateTimeKrBetween(
                        custNo,
                targetDayLocalDateTime.withHour(0),
                targetDayLocalDateTime.withHour(23));
    }
    public List<CustomerDayPowerUsage> getAllUserPowerUsagePerMonth(String date) {
        LocalDate targetDayLocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMM")).withDayOfMonth(1);
        List<CustomerDayPowerUsage> allByYearAndMonth = customerDayPowerUsageRepository
                .findAllByDateTimeKrBetween(
                        targetDayLocalDate,
                        targetDayLocalDate.plusMonths(1).minusDays(1));
        return allByYearAndMonth;
    }

    public List<CustomerMonthPowerUsage> getAllUserPowerUsagePerYear(String date){
        LocalDate yearLocalDate = LocalDate.of(Integer.parseInt(date), 1,1);
        return customerMonthPowerUsageRepository
                .findAllByDateTimeKrBetween(
                        yearLocalDate,
                        yearLocalDate.withMonth(12).withDayOfMonth(31));
    }

    public List<CustomerYearPowerUsage> getAllUserPowerUsage(String date){
        LocalDate yearLocalDate = LocalDate.of(Integer.parseInt(date), 1,1);
        return customerYearPowerUsageRepository
                .findAllByDateTimeKrBetween(yearLocalDate,
                        yearLocalDate.withMonth(12).withDayOfMonth(31));
    }

    public List<CustomerPowerUsage> getSpecificUserPowerUsageBetweenGivenHours(String custNo, String startDate, String endDate){
        LocalDateTime startDateInLocalDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMddHH")).minusHours(1);
        LocalDateTime endDateInLocalDateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyyMMddHH")).plusHours(1);
        return customerPowerUsageRepository.findAllByCustNoEqualsAndDateTimeKrBetween(custNo, startDateInLocalDateTime, endDateInLocalDateTime);
    }

    public List<CustomerDayPowerUsage> getSpecificUserPowerUsageBetweenGivenDays(String custNo, String startDate, String endDate) {
        LocalDate startDateInLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd")).minusDays(1);
        LocalDate endDateInLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyyMMdd")).plusDays(1);

        return customerDayPowerUsageRepository.findAllByCustNoEqualsAndDateTimeKrBetween(custNo, startDateInLocalDate, endDateInLocalDate);
    }

    public List<CustomerMonthPowerUsage> getSpecificUserPowerUsageBetweenGivenMonths(String custNo, String startDate, String endDate) {

        LocalDate startDateInLocalDate = LocalDate.parse(startDate+"01", DateTimeFormatter.ofPattern("yyyyMMdd")).minusDays(1);
        LocalDate endDateInLocalDate = LocalDate.parse(endDate+"01", DateTimeFormatter.ofPattern("yyyyMMdd")).plusMonths(1);
        return customerMonthPowerUsageRepository.findAllByCustNoEqualsAndDateTimeKrBetween(custNo, startDateInLocalDate, endDateInLocalDate);
    }

    public List<CustomerYearPowerUsage> getSpecificUserPowerUsageBetweenGivenYears(String custNo, String startDate, String endDate) {
        LocalDate startYear = LocalDate.of(Integer.parseInt(startDate), 1,1);
        LocalDate endYear = LocalDate.of(Integer.parseInt(endDate), 12,31);
        return customerYearPowerUsageRepository.findAllByCustNoEqualsAndDateTimeKrBetween(custNo, startYear, endYear);
    }

    public List<CustomerPowerUsage> getSpecificUserWholeDataByHour(String custNo) {
        return customerPowerUsageRepository.findAllByCustNoEquals(custNo);
    }

    public List<CustomerDayPowerUsage> getSpecificUserWholeDataByDay(String custNo) {
        return customerDayPowerUsageRepository.findAllByCustNoEquals(custNo);
    }

    public List<CustomerMonthPowerUsage> getSpecificUserWholeDataByMonth(String custNo) {
        return customerMonthPowerUsageRepository.findAllByCustNoEquals(custNo);
    }

    public List<CustomerYearPowerUsage> getSpecificUserWholeDataByYear(String custNo) {
        return customerYearPowerUsageRepository.findAllByCustNoEquals(custNo);
    }

    public List<String> checkPowerUsageLimitApproach(){
        ZoneId zoneIdKr = ZoneId.of("Asia/Seoul");
        ZonedDateTime dateTimeKr = ZonedDateTime.now(zoneIdKr);
        LocalDate startDate = dateTimeKr.withDayOfMonth(1).minusDays(1).toLocalDate();
        LocalDate endDate = dateTimeKr.withDayOfMonth(1).plusMonths(1).minusDays(1).toLocalDate();

        List<CustomerMonthPowerUsage> allByDateTimeKrBetween =
                customerMonthPowerUsageRepository.findAllByDateTimeKrBetween(startDate, endDate);
        List<String> messageTargetCustomers = new ArrayList<>();
        for (CustomerMonthPowerUsage customerMonthPowerUsage: allByDateTimeKrBetween){
            if (checkAccumulatedPowerUsage(customerMonthPowerUsage.powerUsageQuantity)){
                messageTargetCustomers.add(customerMonthPowerUsage.custNo);
            }
        }
        Set<String> messageTargetSet = new HashSet<>(messageTargetCustomers);
        return new ArrayList<>(messageTargetSet);
    }

    public CustomerMonthPowerUsage getRecentMonthPowerUsage(String custNo) throws Exception{
        List<CustomerMonthPowerUsage> mostRecentPowerUsage
                = customerMonthPowerUsageRepository.findAllByCustNoEqualsOrderByDateTimeKrDesc(custNo);
        return mostRecentPowerUsage.get(0);
    }

    public List<CustomerDayPowerUsage> getLastMonthDaysPowerUsage(String custNo) throws Exception{
        CustomerMonthPowerUsage recentMonthPowerUsage = getRecentMonthPowerUsage(custNo);
        LocalDate startDateKr = recentMonthPowerUsage.startDateKr.minusDays(1);
        LocalDate endDateKr = recentMonthPowerUsage.endDateKr.plusDays(1);
        return customerDayPowerUsageRepository.findAllByCustNoEqualsAndDateTimeKrBetween(custNo, startDateKr, endDateKr);
    }

    public List<CustomerDayPowerUsage> getSecondLastMonthDaysPowerUsage(String custNo) throws Exception{
        CustomerMonthPowerUsage recentMonthPowerUsage = getRecentMonthPowerUsage(custNo);
        LocalDate startDateKr = recentMonthPowerUsage.startDateKr.minusMonths(1).minusDays(1);
        LocalDate endDateKr = recentMonthPowerUsage.dateTimeKr.minusMonths(1);
        return customerDayPowerUsageRepository.findAllByCustNoEqualsAndDateTimeKrBetween(custNo, startDateKr, endDateKr);
    }

    public List<CustomerPowerUsage> getRecentPowerUsages(String custNo){
        List<CustomerPowerUsage> recentPowerUsages = customerPowerUsageRepository.findTop24ByCustNoEqualsOrderByDateTimeKrDesc(custNo);
        Collections.reverse(recentPowerUsages);
        return recentPowerUsages;
    }

    private Boolean checkAccumulatedPowerUsage(Double powerUsage){
        if ((190.0 <= powerUsage) && (powerUsage <= 200)){
            return true;
        } else if ((390.0 <= powerUsage) && (powerUsage <= 400)) {
            return true;
        } else if ((590.0 <= powerUsage) && (powerUsage <= 600)) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteRecentPowerUsage(String custNo){
        List<CustomerPowerUsage> recentPowerUsages = getRecentPowerUsages(custNo);
        customerPowerUsageRepository.deleteAll(recentPowerUsages);
    }
}
