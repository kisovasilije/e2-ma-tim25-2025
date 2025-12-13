package com.example.rpg.model.statistics;

public class ProgressUpdateResult {
    public boolean isLevelPassed;

    public int xp;

    public ProgressUpdateResult(boolean isLevelPassed, int xp) {
        this.isLevelPassed = isLevelPassed;
        this.xp = xp;
    }
}
