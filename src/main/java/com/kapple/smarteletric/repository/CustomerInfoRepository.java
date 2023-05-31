package com.kapple.smarteletric.repository;

import com.kapple.smarteletric.domain.CustomerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerInfoRepository extends MongoRepository<CustomerInfo, String> {
    CustomerInfo findCustomerInfoByID(String ID);
    Boolean existsByID(String ID);
}
