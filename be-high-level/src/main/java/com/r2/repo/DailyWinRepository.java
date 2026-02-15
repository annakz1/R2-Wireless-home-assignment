package com.r2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.r2.model.DailyWin;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyWinRepository extends JpaRepository<DailyWin, Long> {

    Optional<DailyWin> findByWinDate(LocalDate winDate);

    boolean existsByWinDate(LocalDate winDate);

    @Modifying
    @Query("""
           UPDATE DailyWin d
           SET d.winsCount = d.winsCount + 1
           WHERE d.winDate = :date
           """)
    int incrementWins(@Param("date") LocalDate date);
}

