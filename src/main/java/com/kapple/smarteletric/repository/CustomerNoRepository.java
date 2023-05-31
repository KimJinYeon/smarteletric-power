package com.kapple.smarteletric.repository;

import com.kapple.smarteletric.domain.CustomerNo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CustomerNoRepository extends MongoRepository<CustomerNo, String> {
    List<CustomerNo> findAll(); //전체 고객번호 조회
}