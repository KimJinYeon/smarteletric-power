package com.kapple.smarteletric.repository;

import com.kapple.smarteletric.domain.CustomerPowerUsage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface CustomerPowerUsageRepository extends MongoRepository<CustomerPowerUsage, String> {
    List<CustomerPowerUsage> findAllByCustNoEquals(String custNo);
    List<CustomerPowerUsage> findAllByCustNoEqualsAndDateTimeKrBetween(String custNo, LocalDateTime startDateTimeKr, LocalDateTime endDateTimeKr);
    Boolean existsByCustNoEqualsAndDateTimeKrBetween(String custNo, LocalDateTime startDateTimeKr, LocalDateTime endDateTimeKr);
    List<CustomerPowerUsage> findTop24ByCustNoEqualsOrderByDateTimeKrDesc(String custNo);
}
