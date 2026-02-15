package com.r2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import com.r2.model.DailyWin;
import com.r2.repo.DailyWinRepository;

@Service
public class WinService {

    private static final Logger logger = LoggerFactory.getLogger(WinService.class);

    private static final double WIN_CHANCE_NORMAL = 0.7;
    private static final double WIN_CHANCE_REDUCED = 0.4;
    private static final int WIN_THRESHOLD = 30;

    private final DailyWinRepository dailyWinRepository;

    public WinService(DailyWinRepository dailyWinRepository) {
        this.dailyWinRepository = dailyWinRepository;
    }

    /**
     * Determines whether the current attempt is a win. Uses {@value #WIN_CHANCE_NORMAL} as the 
     * base win probability, reduced to {@value #WIN_CHANCE_REDUCED} after {@value #WIN_THRESHOLD}
     * wins have occurred on the same calendar day.
     */
    @Transactional
    public boolean calculateWin() {

        LocalDate today = LocalDate.now();

        // 1️⃣ Fetch or create today's record
        DailyWin dailyWin = dailyWinRepository
                .findByWinDate(today)
                .orElseGet(() -> dailyWinRepository.save(new DailyWin(today, 0)));

        int currentWins = dailyWin.getWinsCount();

        logger.debug("Daily wins for {}: {}", today, currentWins);

        // 2️⃣ Determine win probability
        double winChance = currentWins >= WIN_THRESHOLD
                ? WIN_CHANCE_REDUCED
                : WIN_CHANCE_NORMAL;

        boolean win = Math.random() < winChance;

        // 3️⃣ If win → increment atomically
        if (win) {
            dailyWinRepository.incrementWins(today);
            if (dailyWin.getWinsCount() + 1 == WIN_THRESHOLD) {
                logger.debug("Reached WIN_THRESHOLD ({}) for {}; win chance now reduced to {}", WIN_THRESHOLD, today,
                        WIN_CHANCE_REDUCED);
            }
        }

        return win;
    }
}
