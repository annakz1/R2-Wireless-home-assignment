package com.r2.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "daily_wins",
    uniqueConstraints = @UniqueConstraint(columnNames = "win_date")
)
public class DailyWin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "win_date", nullable = false)
    private LocalDate winDate;

    @Column(name = "wins_count", nullable = false)
    private int winsCount;

    protected DailyWin() {}

    public DailyWin(LocalDate winDate, int winsCount) {
        this.winDate = winDate;
        this.winsCount = winsCount;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getWinDate() {
        return winDate;
    }

    public int getWinsCount() {
        return winsCount;
    }

    public void increment() {
        this.winsCount++;
    }
}

