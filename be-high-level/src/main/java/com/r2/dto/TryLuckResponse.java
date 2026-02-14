package com.r2.dto;

public class TryLuckResponse {
    private boolean win;
    
    public TryLuckResponse(boolean win) {
        this.win = win;
    }
    
    public boolean isWin() {
        return win;
    }
    
    public void setWin(boolean win) {
        this.win = win;
    }
}
