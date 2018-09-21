package com.novaytechnologies.conspiracysquares;

import java.util.ArrayList;

import static com.novaytechnologies.conspiracysquares.Server_Sync.ResolveEncryption;

/**
 * A timer class to determine when the round should start.
 * @author Daniel Dunevant
 */
public class Game_Timer {
    /**
     * Time helper class
     * @author Daniel Dunevant
     */
    static class Timer {
        long timerLength;
        long startTime;
        long countDown=1;
        boolean timerStarted=false;
        boolean timerComplete=false;

        void setTimer(long timeBeforeAction)
        {
            if(!timerComplete) {
                if (!timerStarted) {
                    timerComplete     = false;
                    timerStarted      = true;
                    timerLength       = timeBeforeAction;
                    startTime        = System.currentTimeMillis();

                } else {
                    countDown         = startTime + timerLength - System.currentTimeMillis();
                    if (countDown    <= 0) {
                        timerComplete = true;
                        timerStarted  = false;
                    }
                }
            }
        }
    }

    // Timers
    static Timer startRoundTimer;
    static Timer notificationTimer;

    //Round start Time variable
    static long roundStartTime= 0;

    //Whether round is starting due to timer running out of time
    static boolean sm_brRoundStarting= false;

    /**
     * Timer Initializer
     * @author Daniel Dunevant
     */
    static void init()
    {
        Game_Timer.roundStartTime = System.currentTimeMillis();
        startRoundTimer = new Timer();
        notificationTimer = new Timer();
    }

    /**
     * Timer loop
     * @author Daniel Dunevant
     */
    static void runTimer()
    {
        if (startRoundTimer != null) {
            startRoundTimer.setTimer(3000);
            if (startRoundTimer.timerComplete && !sm_brRoundStarting) {
                if (Game_Main.sm_PlayersArray.size() >= 3) {
                    Utility_Post gameStartPost = new Utility_Post();
                    ArrayList<String> params = new ArrayList<>();
                    params.add("ReqPass");
                    params.add(ResolveEncryption());
                    params.add("ServerName");
                    params.add(Game_Main.sm_strServerName);
                    params.add("ServerPassword");
                    params.add(Game_Main.sm_strServerPass);
                    String ParamsString = Utility_Post.GetParamsString(params);
                    sm_brRoundStarting = true;
                    gameStartPost.execute("https://conspiracy-squares.appspot.com/Servlet_StartRound", ParamsString);
                }
            }
        }
    }
}
