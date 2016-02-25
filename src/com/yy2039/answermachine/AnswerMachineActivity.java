package com.yy2039.answermachine;

import java.util.List;
import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.content.Context;  
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import android.content.Intent;
import android.os.RemoteException;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.app.Notification; 
import android.app.NotificationManager; 
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.app.AlertDialog;

public class AnswerMachineActivity extends FragmentActivity
{
    // 
    public static AnswerMachineActivity main_activity;
    public boolean bIsDestroy = false;

    public YYCommon yy_common;
    public YYSchedule yy_schedule;
    public YYDataSource yy_data_source;
    public YYCommand yy_command;
    public YYShowAlertDialog yy_show_alert_dialog;
    public YYInputNumberView yy_input_number_view;
    public YYInputNumberCallbackView yy_input_number_callback_view;
    public YYInputNumberPINView yy_input_number_pin_view;
    public AnswerMachineView answer_machine_view;

    //private final static int NOTIFICATION_ID_ICON = 0x10000;
	private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( intent.hasExtra( "state" ) ) {
                changeShengDao( false );
            }
        }
    };

    public AlertDialog yy_playing_msg_dlg = null;
    private BroadcastReceiver playingMsgEndReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( yy_playing_msg_dlg != null ) {
                yy_playing_msg_dlg.hide();
                yy_playing_msg_dlg = null;
            }
        }
    };

    public AlertDialog yy_record_auto_save_dlg = null;
    public interface onAutoSaveListener {
        public void onAutoSave();
    }
    public onAutoSaveListener yy_auto_save_listener = null;
    private BroadcastReceiver autoSaveReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( yy_auto_save_listener != null ) {
                yy_auto_save_listener.onAutoSave();
                yy_auto_save_listener = null;
            }
        }
    };

    private BroadcastReceiver incomingCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    public AudioManager localAudioManager = null;
    public int nDefaultStreamType = 0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        main_activity = this;

        yy_common = new YYCommon();
        yy_schedule = new YYSchedule( this );
        yy_command = new YYCommand( this );
        yy_show_alert_dialog = new YYShowAlertDialog( this );
        yy_data_source = new YYDataSource( this );
        yy_input_number_view = new YYInputNumberView();
        yy_input_number_callback_view = new YYInputNumberCallbackView();
        yy_input_number_pin_view = new YYInputNumberPINView();

        // 
        answer_machine_view = new AnswerMachineView();
        answer_machine_view.setView( false, null );

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();  
            winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;  
            win.setAttributes( winParams );

            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager( this );
            // enable status bar tint
            tintManager.setStatusBarTintEnabled( true );
            //// enable navigation bar tint
            //tintManager.setNavigationBarTintEnabled( true );

            // set a custom tint color for all system bars
            tintManager.setTintColor( Color.parseColor( "#B8392D" ) );
            //// set a custom navigation bar resource
            //tintManager.setNavigationBarTintResource(R.drawable.my_tint);
            //// set a custom status bar drawable
            //tintManager.setStatusBarTintDrawable(MyDrawable);
        }

        //NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); 
        //Notification n = new Notification();
        ////常驻状态栏的图标
        //n.icon = R.drawable.announce_message;
        //// 将此通知放到通知栏的"Ongoing"即"正在运行"组中  
        //n.flags |= Notification.FLAG_ONGOING_EVENT; 
        //// 表明在点击了通知栏中的"清除通知"后，此通知不清除， 经常与FLAG_ONGOING_EVENT一起使用  
        //n.flags |= Notification.FLAG_NO_CLEAR;          
        //PendingIntent pi = PendingIntent.getActivity(this, 0, getIntent(), 0); 
        //n.contentIntent = pi; 
        //n.setLatestEventInfo( this, "Answer Machine", "Answer Machine", pi );
        //nm.notify(NOTIFICATION_ID_ICON, n);

        IntentFilter filter = new IntentFilter();
        filter.addAction( "android.intent.action.HEADSET_PLUG" );
        registerReceiver( headsetPlugReceiver, filter );  

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction( "com.action.dect.page.voicemsg.play.over" );
        //filter2.addAction( "com.action.dect.page.voicemsg.delete.play.over" );
        registerReceiver( playingMsgEndReceiver, filter2 );  

        IntentFilter filter3 = new IntentFilter();
        filter3.addAction( "com.action.dect.page.incoming.call" );
        registerReceiver( incomingCallReceiver, filter3 );  

        IntentFilter filter5 = new IntentFilter();
        filter5.addAction( "com.action.dect.page.voicemsg.overtime.autosave" );
        //filter5.addAction( "com.action.dect.page.voicemsg.delete.play.over" );
        registerReceiver( autoSaveReceiver, filter5 );  

        localAudioManager = (AudioManager)getSystemService( Context.AUDIO_SERVICE );  
        nDefaultStreamType = getVolumeControlStream();
    }

    public boolean onKeyDown( int keyCode, KeyEvent event )
    {
        if( keyCode == KeyEvent.KEYCODE_BACK )
        {
            YYViewBase.onBackClick();
        }

        return false;
    }

    public final static String ANSWER_MACHINE_CHANGE_HEADSET = "andorid.intent.action.answer.machine.change.headset";           // 耳机
    public final static String ANSWER_MACHINE_CHANGE_HANDFREE = "andorid.intent.action.answer.machine.change.handfree";         // 免提
    public final static String ANSWER_MACHINE_CHANGE_NORMAL = "andorid.intent.action.answer.machine.change.normal";             // 普通
    public void changeShengDao( boolean bResumeNormal ) {
        if( !bResumeNormal ) {
            if( yy_playing_msg_dlg != null || yy_record_auto_save_dlg != null ) {
                Intent intent = new Intent();  
                if( localAudioManager.isWiredHeadsetOn() ) {
                    intent.setAction( ANSWER_MACHINE_CHANGE_HEADSET );
                } else {
                    intent.setAction( ANSWER_MACHINE_CHANGE_HANDFREE );
                }
                sendBroadcast( intent );

                setVolumeControlStream( AudioManager.STREAM_VOICE_CALL );
            }
        } else {
            Intent intent = new Intent();  
            intent.setAction( ANSWER_MACHINE_CHANGE_NORMAL );
            sendBroadcast( intent );

            setVolumeControlStream( nDefaultStreamType );
        }
    }

	@Override
	protected void onResume() {
        super.onResume();

        if( localAudioManager != null ) {
            changeShengDao( false );
        }
    }

	@Override
	protected void onPause() {
        changeShengDao( true );

        super.onPause();
    }

	@Override
	protected void onDestroy()
	{
        bIsDestroy = true;

		// TODO Auto-generated method stub
        changeShengDao( true );

        yy_schedule.cancelAllSchedule();
        yy_command.unregisterReceiver();
        answer_machine_view.cancelListen();

        unregisterReceiver( headsetPlugReceiver );
        unregisterReceiver( playingMsgEndReceiver );
        unregisterReceiver( incomingCallReceiver );
        unregisterReceiver( autoSaveReceiver );

        //NotificationManager nm = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );
        //nm.cancel( NOTIFICATION_ID_ICON );

		super.onDestroy();
	}

    // 
    public static String PREFER_NAME = "AnswerMachine";
    public static int MODE = Context.MODE_PRIVATE;
    private boolean bIsLoading = false;
    public void loadSharedPreferences() {
        bIsLoading = true;

        SharedPreferences share = main_activity.getSharedPreferences( PREFER_NAME, MODE );

        yy_data_source.setIsFirstTimeUseRemoteAccess( share.getBoolean( "FirstTimeUseRemoteAccess", true ) );

        bIsLoading = false;
    }

    public void saveSharedPreferences() {
        if( bIsLoading )
            return;

        SharedPreferences share = main_activity.getSharedPreferences( PREFER_NAME, MODE );
        SharedPreferences.Editor editor = share.edit();

        editor.putBoolean( "FirstTimeUseRemoteAccess", yy_data_source.getIsFirstTimeUseRemoteAccess() );

        editor.commit();
    }
}
