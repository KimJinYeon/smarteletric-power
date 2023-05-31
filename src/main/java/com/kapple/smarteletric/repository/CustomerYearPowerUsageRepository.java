package com.kapple.smarteletric.repository;

import com.kapple.smarteletric.domain.CustomerYearPowerUsage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerYearPowerUsageRepository extends MongoRepository<CustomerYearPowerUsage, String> {
    CustomerYearPowerUsage findByID(String ID);
    List<CustomerYearPowerUsage> findAllByCustNoEquals(String custNo);
    List<CustomerYearPowerUsage> findAllByCustNoEqualsAndDateTimeKrBetween(String custNo,
                                                                           LocalDate startDateTimeKr,
                                                                           LocalDate endDateTimeKr);
    List<CustomerYearPowerUsage> findAllByDateTimeKrBetween(LocalDate startDateTimeKr,
                                                            LocalDate endDateTimeKr);
    Boolean existsByID(String ID);
}
