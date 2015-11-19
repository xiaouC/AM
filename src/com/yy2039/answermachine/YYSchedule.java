package com.yy2039.answermachine_1;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.HashMap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class YYSchedule
{
    public interface onScheduleAction {
        void doSomething();
    }

    private Map<Integer,onScheduleAction> all_schedule_actions = new HashMap<Integer,onScheduleAction>();
    private Map<Integer,Timer> all_schedule_timers = new HashMap<Integer,Timer>();
    private int nNextScheduleIndex = 0;

    private Handler handler;
    private AnswerMachineActivity main_activity;
    public YYSchedule( AnswerMachineActivity activity ) {
        main_activity = activity;

        handler = new Handler(){
            public void handleMessage( Message msg ) {
                Log.v( "cconn", "handleMessage schedule_index : " + msg.what );
                onScheduleAction schedule_action = all_schedule_actions.get( msg.what );
                all_schedule_actions.remove( msg.what );
                all_schedule_timers.remove( msg.what );

                if( schedule_action != null ) {
                    schedule_action.doSomething();
                }

                // 
                super.handleMessage( msg );
            }
        };
    }

    public void scheduleOnceTime( long delay, onScheduleAction schedule_action ) {
        final int schedule_index = nNextScheduleIndex++;
        all_schedule_actions.put( schedule_index, schedule_action );

        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = schedule_index;
                handler.sendMessage( message );
            }
        };

        Timer timer = new Timer( true );
        all_schedule_timers.put( schedule_index, timer );

        timer.schedule( task, delay );

        Log.v( "cconn", "scheduleOnceTime : " + schedule_index );
    }

    public void cancelAllSchedule() {
        all_schedule_actions.clear();

        for( Map.Entry<Integer,Timer> item_entry : all_schedule_timers.entrySet() ) {
            Timer timer = item_entry.getValue();
            timer.cancel();
        }
        all_schedule_timers.clear();
    }
}
