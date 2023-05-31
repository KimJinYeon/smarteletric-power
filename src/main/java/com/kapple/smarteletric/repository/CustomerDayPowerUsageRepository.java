package com.kapple.smarteletric.repository;

import com.kapple.smarteletric.domain.CustomerDayPowerUsage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerDayPowerUsageRepository extends MongoRepository<CustomerDayPowerUsage, String> {
    List<CustomerDayPowerUsage> findAllByCustNoEquals(String custNo);
    List<CustomerDayPowerUsage> findAllByDateTimeKrBetween(LocalDate startDateTimeKr, LocalDate endDateTimeKr);
    List<CustomerDayPowerUsage> findAllByCustNoEqualsAndDateTimeKrBetween(String custNo, LocalDate startDateTimeKr, LocalDate endDateTimeKr);
}
