package com.r2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WinService {

    private static final Logger logger = LoggerFactory.getLogger(WinService.class);
    private static final double WIN_CHANCE_NORMAL = 0.7;
    private static final double WIN_CHANCE_REDUCED = 0.4;
    private static final int WIN_THRESHOLD = 30;
    
    private final Map<LocalDate, AtomicInteger> dailyWins = new ConcurrentHashMap<>();
    
    /**
     * Determines whether the current attempt is a win. Uses {@value #WIN_CHANCE_NORMAL} as the
     * base win probability, reduced to {@value #WIN_CHANCE_REDUCED} after {@value #WIN_THRESHOLD}
     * wins have occurred on the same calendar day.
     */
    public boolean calculateWin() {
        LocalDate today = LocalDate.now();
        AtomicInteger winsToday = dailyWins.computeIfAbsent(today, k -> new AtomicInteger(0));
        int currentWins = winsToday.get();
        logger.debug("Daily wins for {}: {}", today, currentWins);

        double winChance = currentWins >= WIN_THRESHOLD ? WIN_CHANCE_REDUCED : WIN_CHANCE_NORMAL;
        // Math.random() is uniform in [0.0, 1.0), so P(random < p) = p — thus win probability is exactly winChance.
        boolean win = Math.random() < winChance;

        if (win) {
            int newCount = winsToday.incrementAndGet();
            if (newCount == WIN_THRESHOLD) {
                logger.debug("Reached WIN_THRESHOLD ({}) for {}; win chance now reduced to {}", WIN_THRESHOLD, today, WIN_CHANCE_REDUCED);
            }
        }

        return win;
    }
}
