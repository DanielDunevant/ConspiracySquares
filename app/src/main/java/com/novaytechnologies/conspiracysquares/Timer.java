package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;

public class Timer {
    public long timerLength;
    public boolean timerStarted=false;
    public boolean timerComplete=false;
    public long countDown=0;
    public long startTimer;


    void setTimer(long timeBeforeAction)
    {
        if(!timerComplete) {
            if (!timerStarted) {
                timerComplete     = false;
                timerStarted      = true;
                timerLength       = timeBeforeAction;
                startTimer        = System.currentTimeMillis();

            } else {
                countDown         = startTimer + timerLength - System.currentTimeMillis();
                if (countDown    <= 0) {
                    timerComplete = true;
                    timerStarted  = false;
                }
            }
        }
    }
}
