package com.drinking.repository;

import com.drinking.domain.ReactionTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionTestRepository extends JpaRepository<ReactionTest, Long> {
}