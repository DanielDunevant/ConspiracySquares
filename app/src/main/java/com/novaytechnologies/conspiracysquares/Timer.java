package com.novaytechnologies.conspiracysquares;

public class Timer {
    public static long timerLength;
    public static boolean timerStarted=false;
    public static boolean timerComplete=false;
    public static long countDown=1;
    public static long startTimer;


    static void setTimer(long timeBeforeAction)
    {
        if(!timerComplete) {
            if (!timerStarted) {
                timerComplete = false;
                timerStarted = true;
                timerLength = timeBeforeAction;
                startTimer = System.currentTimeMillis();
            } else {
                countDown = startTimer + timerLength - System.currentTimeMillis();
                if (countDown <= 0) {
                    timerComplete = true;
                    timerStarted = false;
                }
            }
        }
    }
}
