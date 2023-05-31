package com.kapple.smarteletric.repository;

import com.kapple.smarteletric.domain.CustomerMonthPowerUsage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerMonthPowerUsageRepository extends MongoRepository<CustomerMonthPowerUsage, String> {
    List<CustomerMonthPowerUsage> findAllByCustNoEquals(String custNo);
    List<CustomerMonthPowerUsage> findAllByCustNoEqualsAndDateTimeKrBetween(String custNo, LocalDate startDateTimeKr, LocalDate endDateTimeKr);
    List<CustomerMonthPowerUsage> findAllByDateTimeKrBetween(LocalDate startDateTimeKr, LocalDate endDateTimeKr);
    List<CustomerMonthPowerUsage> findAllByCustNoEqualsOrderByDateTimeKrDesc(String custNo);
}
