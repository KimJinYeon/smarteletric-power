package com.kapple.smarteletric.repository;

import com.kapple.smarteletric.domain.CustomerBill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerBillRepository extends MongoRepository<CustomerBill, String> {

    CustomerBill findCustomerBillByID(String ID);
    Boolean existsByID(String ID);
    Boolean existsByCustNo(String custNo); // 고객번호로 데이터 있는지 확인
    List<CustomerBill> findAllByCustNoEquals(String custNo);
}