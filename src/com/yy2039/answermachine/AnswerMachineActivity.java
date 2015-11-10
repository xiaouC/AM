package com.yy2039.answermachine_1;

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

public class AnswerMachineActivity extends FragmentActivity
{
    // 
    public static AnswerMachineActivity main_activity;

    public YYCommon yy_common;
    public YYDataSource yy_data_source;
    public YYCommand yy_command;
    public YYShowAlertDialog yy_show_alert_dialog;
    public YYInputNumberView yy_input_number_view;
    public YYInputNumberCallbackView yy_input_number_callback_view;
    public YYInputNumberPINView yy_input_number_pin_view;
    public AnswerMachineView answer_machine_view;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        main_activity = this;

        yy_common = new YYCommon();
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
    }

    public boolean onKeyDown( int keyCode, KeyEvent event )
    {
        if( keyCode == KeyEvent.KEYCODE_BACK )
        {
            YYViewBase.onBackClick();
        }

        return false;
    }

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();

        yy_command.unregisterReceiver();
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
