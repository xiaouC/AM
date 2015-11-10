package com.yy2039.answermachine_1;

import java.util.List;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.LayoutInflater;
import android.view.ContextThemeWrapper;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.graphics.Typeface;
import android.view.WindowManager;
import android.graphics.drawable.AnimationDrawable;
import android.content.DialogInterface;
import android.view.KeyEvent;

public class YYShowAlertDialog {
    private AnswerMachineActivity main_activity;

    public interface onAlertDialogClickHandler {
        void onOK();
        void onCancel();
    }

    // 
    public interface onAlertDialogHandler {
        void onInit( AlertDialog ad, View view );
        void onOK();
        void onCancel();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public interface onAlertDialogRadioItemHandler {
        String getRadioText();
        void onRadioClick();
        boolean isRadioChecked();
    }

    // alert dialog
    private AlertDialog cur_show_ad;
    private AlertDialog waiting_ad;

    public YYShowAlertDialog( AnswerMachineActivity activity ) {
        main_activity = activity;
    }

    public AlertDialog showAlertDialog( int nAlertDialogRes, final onAlertDialogHandler handler )
    {
        LayoutInflater li = LayoutInflater.from( main_activity );
        View view = li.inflate( nAlertDialogRes, null );

        AlertDialog.Builder builder = new AlertDialog.Builder( new ContextThemeWrapper( main_activity, R.style.BT_Call_Guardian_Mode ) );
        builder.setView( view );
        builder.setCancelable( true );

        cur_show_ad = builder.create();

        handler.onInit( cur_show_ad, view );

        // 
        ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
        if( btn_ok != null )
        {
            btn_ok.setOnClickListener( new View.OnClickListener () {
                public void onClick( View v ){
                    // 先清理，再回调
                    cur_show_ad.hide();
                    cur_show_ad = null;

                    handler.onOK();
                }
            });
        }

        // 
        ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
        if( btn_cancel != null )
        {
            btn_cancel.setOnClickListener( new View.OnClickListener () {
                public void onClick( View v ){
                    // 先清理，再回调
                    cur_show_ad.hide();
                    cur_show_ad = null;

                    handler.onCancel();
                }
            });
        }

        //cur_show_ad.setCanceledOnTouchOutside( true );   // 设置点击 Dialog 外部任意区域关闭 Dialog
        cur_show_ad.show();

        return cur_show_ad;
    }

    public RadioButton createRadioButton( RadioGroup r_group, String text, View.OnClickListener click_handler ) {
        RadioButton rb = new RadioButton( main_activity );
        rb.setText( text );
        rb.setTextSize( TypedValue.COMPLEX_UNIT_PX, 24 );
        rb.setTextColor( Color.BLACK );
        rb.setTypeface( Typeface.DEFAULT_BOLD, Typeface.BOLD );
        rb.setOnClickListener( click_handler );
        rb.setButtonDrawable( R.drawable.radio_button_normal );
        rb.setPadding( 20, 10, 0, 10 );

        r_group.addView( rb );

        return rb;
    }

    public void showRadioGroupAlertDialog( final String title, final List<onAlertDialogRadioItemHandler> item_list_data, final onAlertDialogClickHandler click_handler ) {
        showAlertDialog( R.layout.alert_radio_group, new YYShowAlertDialog.onAlertDialogHandler() {
            public void onInit( AlertDialog ad, View view ) {
                TextView tv_title = (TextView)view.findViewById( R.id.alert_title );
                tv_title.setText( title );

                // 
                RadioGroup r_group = (RadioGroup)view.findViewById( R.id.alert_radio_group );

                RadioButton rb_checked = null;
                final List<RadioButton> listRadioButton = new ArrayList<RadioButton>();
                for( int i=0; i < item_list_data.size(); ++i ) {
                    final onAlertDialogRadioItemHandler r_handler = item_list_data.get( i );
                    final int item_index = i;

                    RadioButton rb = createRadioButton( r_group, r_handler.getRadioText(), new View.OnClickListener() {
                        public void onClick( View v ) {
                            r_handler.onRadioClick();

                            for( int index=0; index < listRadioButton.size(); ++index ) {
                                RadioButton rb_temp = listRadioButton.get( index );
                                rb_temp.setButtonDrawable( item_index == index ? R.drawable.radio_button_select : R.drawable.radio_button_normal );
                            }
                        }
                    } );
                    listRadioButton.add( rb );

                    if( r_handler.isRadioChecked() ) {
                        rb_checked = rb;
                    }
                }

                if( rb_checked != null ) {
                    rb_checked.setChecked( true );
                    rb_checked.setButtonDrawable( R.drawable.radio_button_select );
                }
            }

            public void onOK() { click_handler.onOK(); }
            public void onCancel() { click_handler.onCancel(); }
        });
    }

    public void showImageTipsAlertDialog( final String title, final int image_id, final String tips, final int btn_ok_image_id, final int btn_cancel_image_id, final onAlertDialogClickHandler click_handler ) {
        showAlertDialog( R.layout.alert_image_tips, new onAlertDialogHandler() {
            public void onInit( AlertDialog ad, View view ) {
                TextView tv_title = (TextView)view.findViewById( R.id.alert_title );
                tv_title.setText( title );

                ImageView iv = (ImageView)view.findViewById( R.id.alert_image );
                iv.setBackgroundResource( image_id );

                TextView tv_tips = (TextView)view.findViewById( R.id.alert_tips );
                tv_tips.setText( tips );

                if( btn_ok_image_id != 0 ) {
                    ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                    if( btn_ok != null )
                        btn_ok.setImageDrawable( main_activity.getResources().getDrawable( btn_ok_image_id ) );
                }

                if( btn_cancel_image_id != 0 ) {
                    ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                    if( btn_cancel != null )
                        btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( btn_cancel_image_id ) );
                }
            }
            public void onOK() { click_handler.onOK(); }
            public void onCancel() { click_handler.onCancel(); }
        });
    }

    public void showSuccessfullImageTipsAlertDialog( final String title, final int image_id, final String tips, final int btn_ok_image_id, final onAlertDialogClickHandler click_handler ) {
        showAlertDialog( R.layout.alert_image_tips_title_center, new onAlertDialogHandler() {
            public void onInit( AlertDialog ad, View view ) {
                TextView tv_title = (TextView)view.findViewById( R.id.alert_title );
                tv_title.setText( title );

                ImageView iv = (ImageView)view.findViewById( R.id.alert_image );
                iv.setBackgroundResource( image_id );

                TextView tv_tips = (TextView)view.findViewById( R.id.alert_tips );
                tv_tips.setText( tips );

                if( btn_ok_image_id != 0 ) {
                    ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                    if( btn_ok != null )
                        btn_ok.setImageDrawable( main_activity.getResources().getDrawable( btn_ok_image_id ) );
                }
            }
            public void onOK() { click_handler.onOK(); }
            public void onCancel() { click_handler.onCancel(); }
        });
    }

    public void showMenuAlertDialog( int nAlertDialogRes, int x, int y, final onAlertDialogHandler handler ) {
        LayoutInflater li = LayoutInflater.from( main_activity );
        View view = li.inflate( nAlertDialogRes, null );

        AlertDialog.Builder builder = new AlertDialog.Builder( new ContextThemeWrapper( main_activity, R.style.calls_and_notifications_arrive ) );
        builder.setView( view );
        builder.setCancelable( true );

        final AlertDialog ad = builder.create();

        WindowManager.LayoutParams lp = ad.getWindow().getAttributes();
        lp.x = x;
        lp.y = y;

        handler.onInit( ad, view );

        // 
        ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
        if( btn_ok != null ) {
            btn_ok.setOnClickListener( new View.OnClickListener () {
                public void onClick( View v ){
                    // 先清理，再回调
                    ad.hide();

                    handler.onOK();
                }
            });
        }

        ad.setCanceledOnTouchOutside( true );   // 设置点击 Dialog 外部任意区域关闭 Dialog
        ad.show();
    }

    public void showWaitingAlertDialog() {
        LayoutInflater li = LayoutInflater.from( main_activity );
        View view = li.inflate( R.layout.alert_waiting, null );

        AlertDialog.Builder builder = new AlertDialog.Builder( new ContextThemeWrapper( main_activity, R.style.alert_waiting ) );
        builder.setView( view );
        //builder.setCancelable( false );
        builder.setCancelable( true );

        builder.setOnKeyListener( new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey( DialogInterface dialog, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    //Log.v( "cconn", "loading =================================================" );
                    //if( main_activity.yy_command.cur_command_info != null ) {
                    //    Log.v( "cconn", "loading =================================================" );
                    //    main_activity.yy_command.unregisterReceiver();
                    //    main_activity.yy_command.cur_command_info = null;
                    //    main_activity.yy_command.request_command_list.clear();
                    //}

                }
                return false;
            }
        });


        waiting_ad = builder.create();

        ////透明
        //Window window = waiting_ad.getWindow(); 
        //WindowManager.LayoutParams lp = window.getAttributes();
        //lp.alpha = 0.0f;
        //window.setAttributes(lp);

        ImageView iv = (ImageView)view.findViewById( R.id.anim_waiting );
        AnimationDrawable anim = (AnimationDrawable)iv.getBackground();
        anim.start();

        waiting_ad.setCanceledOnTouchOutside( false );
        waiting_ad.show();
    }

    public void hideWaitingAlertDialog() {
        if( waiting_ad != null ) {
            waiting_ad.hide();
            waiting_ad = null;
        }
        Log.v( "cconn", "hideWaitingAlertDialog ----------------------------------------------" );
    }
}
