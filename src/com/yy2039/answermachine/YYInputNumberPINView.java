package com.yy2039.answermachine_1;

import java.util.ArrayList;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;
import android.content.Intent;
import android.widget.Toast;

public class YYInputNumberPINView extends YYViewBase {
    private YYInputNumberPINView yy_input_number;

    public interface onYYInputNumberPINHandler {
        void onSuccessful( String number );
    }

    private String in_title;
    private ArrayList<Character> input_numbers = new ArrayList<Character>();
    private onYYInputNumberPINHandler in_pin_handler;

    private String yy_pin_type;         // "first", "confirm", "enter"
    private String origin_pin;
    private String first_pin;
    private String confirm_pin;
    private boolean checked_flag;

    private ArrayList<Integer> input_text_res_ids = new ArrayList<Integer>();
    private ImageButton btn_next_1;
    private ImageButton btn_next_2;

    public YYInputNumberPINView() {
        yy_input_number = this;

        view_layout_res_id = R.layout.input_number_pin;

        input_text_res_ids.add( R.drawable.default_icon );
        input_text_res_ids.add( R.drawable.pin_number_t_1 );
        input_text_res_ids.add( R.drawable.pin_number_t_2 );
        input_text_res_ids.add( R.drawable.pin_number_t_3 );
        input_text_res_ids.add( R.drawable.pin_number_t_4 );
    }

    public void setView( boolean bIsPush, onViewBackHandler handler ) {
        super.setView( bIsPush, handler );

        btn_next_1 = (ImageButton)main_activity.findViewById( R.id.btn_number_next );
        btn_next_2 = (ImageButton)main_activity.findViewById( R.id.btn_number_n );

        ( (ImageButton)main_activity.findViewById( R.id.btn_number_1 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '1' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_2 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '2' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_3 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '3' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_4 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '4' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_5 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '5' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_6 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '6' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_7 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '7' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_8 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '8' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_9 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '9' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_0 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '0' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_next ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ onNextClick(); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_n ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ onNextClick(); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_cancel ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ) { yy_input_number.onBackClick(); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.input_number_return_button ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ) { yy_input_number.onBackClick(); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_d ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ) {
                if( yy_input_number.input_numbers.size() > 0 )
                {
                    yy_input_number.input_numbers.remove( yy_input_number.input_numbers.size() - 1 );
                    yy_input_number.updateInputNumberText();
                }
            }
        });
    }

    public String getViewTitle() { return in_title; }

    public void showInputNumberView( String title, String ori_pin, onViewBackHandler handler, String pin_type, final onYYInputNumberPINHandler in_handler )
    {
        yy_pin_type = pin_type;
        origin_pin = ori_pin;

        in_title = title;

        // 
        first_pin = "";
        confirm_pin = "";
        checked_flag = false;

        // 
        setView( true, handler );

        in_pin_handler = in_handler;

        // 
        input_numbers.clear();
        updateInputNumberText();
    }

    public void onNextClick() {
        final ImageView iv = (ImageView)main_activity.findViewById( R.id.input_text );
        final TextView tv_tips = (TextView)main_activity.findViewById( R.id.tips_text );

        if( yy_pin_type == "first" ) {
            yy_pin_type = "confirm";

            first_pin = "";
            for( int i=0; i < input_numbers.size(); ++i )
                first_pin = first_pin + input_numbers.get( i );

            input_numbers.clear();

            tv_tips.setText( "Confirm your PIN" );
            iv.setImageResource( R.drawable.default_icon );

            btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_ok_gray ) );

            btn_next_1.setClickable( false );
            btn_next_2.setClickable( false );

            return;
        }

        if( yy_pin_type == "confirm" ) {
            confirm_pin = "";
            for( int i=0; i < input_numbers.size(); ++i )
                confirm_pin = confirm_pin + input_numbers.get( i );

            if( confirm_pin.equals( first_pin ) ) {
                main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_SCCP_RESULT, new YYCommand.onCommandListener() {
                    public void onSend() {
                        Intent banbIntent = new Intent( YYCommand.CALL_GUARDIAN_SCCP );
                        banbIntent.putExtra( "data", "0000" + first_pin );
                        main_activity.sendBroadcast( banbIntent );
                    }
                    public void onRecv( String data, String data2 ) {
                        if( data == null ) {
                            String text = String.format( "%s recv : null", YYCommand.CALL_GUARDIAN_SCCP_RESULT );
                            Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                        }
                        else {
                            // 
                            if( data.equals( "SUCCESS" ) ) {
                                in_pin_handler.onSuccessful( first_pin );
                            }
                            else {
                                input_numbers.clear();

                                tv_tips.setText( "Incorrect PIN entered" );
                                iv.setImageResource( R.drawable.pin_number_t_5 );

                                btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_next_gray ) );

                                btn_next_1.setClickable( false );
                                btn_next_2.setClickable( false );
                            }
                        }
                    }
                    public void onFailure() {
                        Toast.makeText( main_activity, "change pin number failed", Toast.LENGTH_LONG ).show();
                    }
                });
            }
            else {
                yy_pin_type = "first";

                input_numbers.clear();

                tv_tips.setText( "PIN don't match" );

                iv.setImageResource( R.drawable.pin_number_t_5 );

                btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_continue_gray ) );

                btn_next_1.setClickable( false );
                btn_next_2.setClickable( false );
            }

            return;
        }

        if( yy_pin_type == "enter" ) {
            first_pin = "";
            for( int i=0; i < input_numbers.size(); ++i )
                first_pin = first_pin + input_numbers.get( i );

            main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_CMPC_RESULT, new YYCommand.onCommandListener() {
                public void onSend() {
                    Intent banbIntent = new Intent( YYCommand.CALL_GUARDIAN_CMPC );
                    banbIntent.putExtra( "data", first_pin );
                    main_activity.sendBroadcast( banbIntent );
                }
                public void onRecv( String data, String data2 ) {
                    if( data == null ) {
                        String text = String.format( "%s recv : null", YYCommand.CALL_GUARDIAN_CMPC_RESULT );
                        Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                    }
                    else {
                        // 
                        if( data.equals( "EQUAL" ) ) {
                            in_pin_handler.onSuccessful( first_pin );
                        }
                        else {
                            input_numbers.clear();

                            tv_tips.setText( "Incorrect PIN entered" );
                            iv.setImageResource( R.drawable.pin_number_t_5 );

                            btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_next_gray ) );

                            btn_next_1.setClickable( false );
                            btn_next_2.setClickable( false );
                        }
                    }
                }
                public void onFailure() {
                    Toast.makeText( main_activity, "get pin number failed", Toast.LENGTH_LONG ).show();
                }
            });

            return;
        }
    }

    public void updateInputNumberText()
    {
        ImageView iv = (ImageView)main_activity.findViewById( R.id.input_text );
        iv.setImageResource( input_text_res_ids.get( input_numbers.size() ) );

        TextView tv_tips = (TextView)main_activity.findViewById( R.id.tips_text );

        if( yy_pin_type == "first" ) {
            if( input_numbers.size() == 0 ) {
                tv_tips.setText( "Choose your PIN" );

                btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_continue_gray ) );

                btn_next_1.setClickable( false );
                btn_next_2.setClickable( false );
            }
            else if( input_numbers.size() < 4 ) {
                tv_tips.setText( "PIN must be 4 digits" );

                btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_continue_gray ) );

                btn_next_1.setClickable( false );
                btn_next_2.setClickable( false );
            }
            else {
                tv_tips.setText( "Tap \"Continue\" to finish" );

                btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_continue_black ) );

                btn_next_1.setClickable( true );
                btn_next_2.setClickable( true );
            }
        }

        if( yy_pin_type == "confirm" ) {
            if( input_numbers.size() < 4 ) {
                tv_tips.setText( "Confirm your PIN" );

                btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_ok_gray ) );

                btn_next_1.setClickable( false );
                btn_next_2.setClickable( false );
            }
            else {
                btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_ok_black ) );

                btn_next_1.setClickable( true );
                btn_next_2.setClickable( true );
            }
        }

        if( yy_pin_type == "enter" ) {
            if( input_numbers.size() < 4 ) {
                tv_tips.setText( "Confirm your PIN" );

                btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_next_gray ) );

                btn_next_1.setClickable( false );
                btn_next_2.setClickable( false );
            }
            else {
                btn_next_1.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.pin_number_next_black ) );

                btn_next_1.setClickable( true );
                btn_next_2.setClickable( true );
            }
        }
    }

    public void appendNumber( Character ch )
    {
        if( input_numbers.size() < 4 )
            input_numbers.add( ch );

        updateInputNumberText();
    }
}
