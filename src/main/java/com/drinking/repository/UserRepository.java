package com.drinking.repository;

import com.drinking.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRoomCode(String roomCode);
    List<User> findByRoomCodeOrderByTotalSojuDesc(String roomCode);
    Long countByRoomCode(String roomCode);
    Long countByRoomCodeAndFinishedAtIsNull(String roomCode);
}