package com.drinking.repository;

import com.drinking.domain.DrinkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrinkRecordRepository extends JpaRepository<DrinkRecord, Long> {
}