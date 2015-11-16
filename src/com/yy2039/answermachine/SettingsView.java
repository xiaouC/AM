package com.yy2039.answermachine;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import android.view.View;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;

public class SettingsView extends YYViewBackList {
    private RemoteAccessView remote_access_view;
    public SettingsView() {
        remote_access_view = new RemoteAccessView();
    }

    public String getViewTitle() { return "Settings"; }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Remote access
        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                String text_1 = "Remote access";
                String text_2 = "";
                btn_obj.setText( YYViewBase.transferText( text_1, text_2 ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_GDES_RESULT, new YYCommand.onCommandListener() {
                            public void onSend() {
                                main_activity.sendBroadcast( new Intent( YYCommand.CALL_GUARDIAN_GDES ) );
                                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : send" );
                            }
                            public void onRecv( String data, String data2 ) {
                                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : recv data : " + data );
                                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : recv data2 : " + data2 );
                                if( data == null ) {
                                    String text = String.format( "%s recv : null", YYCommand.CALL_GUARDIAN_GDES_RESULT );
                                    Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                                }
                                else {
                                    final String pin_type = data.equals( "01" ) ? "first" : "enter";
                                    main_activity.yy_input_number_pin_view.showInputNumberView( "Confirm your PIN", "", yy_view_self.getViewBackHandler(), pin_type, new YYInputNumberPINView.onYYInputNumberPINHandler() {
                                        public void onSuccessful( String number ) {
                                            YYViewBase.onBackClick();

                                            remote_access_view.setView( true, yy_view_self.getViewBackHandler() );

                                            if( pin_type.equals( "first" ) ) {
                                                main_activity.yy_data_source.setIsFirstTimeUseRemoteAccess( false );

                                                main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention_2, new YYShowAlertDialog.onAlertDialogHandler() {
                                                    public void onInit( AlertDialog ad, View view ) {
                                                        String text1 = "Please remember this Access PIN is used for both remote access and outgoing call control";
                                                        TextView tv = (TextView)view.findViewById( R.id.attention_text );
                                                        tv.setText( text1 );
                                                    }
                                                    public void onOK() { }
                                                    public void onCancel() { }
                                                });
                                            }
                                        }
                                        public boolean onCheckNumber( String number ) {
                                            return true;
                                        }
                                    });
                                }
                            }
                            public void onFailure() {
                                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : failed " );
                                String text = String.format( "%s recv failed", YYCommand.CALL_GUARDIAN_GDES_RESULT );
                                Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                            }
                        });
                    }
                });
            }
        });
        ret_data.add( map );

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Answer delay
        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                String text_1 = "Answer delay";
                String text_2 = String.format( "%d rings", main_activity.yy_data_source.getAnswerDelayType() + YYCommon.ANSWER_DELAY_AMEND );

                btn_obj.setText( YYViewBase.transferText( text_1, text_2 ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "2 rings"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_delay", YYCommon.ANSWER_DELAY_2_RINGS ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerDelayType() == YYCommon.ANSWER_DELAY_2_RINGS; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "3 rings"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_delay", YYCommon.ANSWER_DELAY_3_RINGS ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerDelayType() == YYCommon.ANSWER_DELAY_3_RINGS; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "4 rings"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_delay", YYCommon.ANSWER_DELAY_4_RINGS ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerDelayType() == YYCommon.ANSWER_DELAY_4_RINGS; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "5 rings"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_delay", YYCommon.ANSWER_DELAY_5_RINGS ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerDelayType() == YYCommon.ANSWER_DELAY_5_RINGS; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "6 rings"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_delay", YYCommon.ANSWER_DELAY_6_RINGS ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerDelayType() == YYCommon.ANSWER_DELAY_6_RINGS; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "7 rings"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_delay", YYCommon.ANSWER_DELAY_7_RINGS ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerDelayType() == YYCommon.ANSWER_DELAY_7_RINGS; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "8 rings"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_delay", YYCommon.ANSWER_DELAY_8_RINGS ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerDelayType() == YYCommon.ANSWER_DELAY_8_RINGS; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "9 rings"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_delay", YYCommon.ANSWER_DELAY_9_RINGS ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerDelayType() == YYCommon.ANSWER_DELAY_9_RINGS; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "10 rings"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_delay", YYCommon.ANSWER_DELAY_10_RINGS ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerDelayType() == YYCommon.ANSWER_DELAY_10_RINGS; }
                        });

                        main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Answer delay", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() {
                                Integer nDelayType = (Integer)yy_view_self.yy_temp_data.get( "answer_delay" );
                                if( nDelayType != null ) {
                                    main_activity.yy_data_source.setAnswerDelayType( nDelayType );

                                    YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                    task.execute();
                                }
                            }
                            public void onCancel() { }
                        });
                    }
                });
            }
        });
        ret_data.add( map );

        /*
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Recording quality
        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                String text_1 = "Recording quality";
                String text_2 = ( main_activity.yy_data_source.getRecordingQuality() == YYCommon.RECORDING_QUALITY_HIGH ? "high" : "standard" );

                btn_obj.setText( YYViewBase.transferText( text_1, text_2 ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "High(default)"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "recording_quality", YYCommon.RECORDING_QUALITY_HIGH ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getRecordingQuality() == YYCommon.RECORDING_QUALITY_HIGH; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Standard"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "recording_quality", YYCommon.RECORDING_QUALITY_STANDARD ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getRecordingQuality() == YYCommon.RECORDING_QUALITY_STANDARD; }
                        });

                        main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Recording quality", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() {
                                Integer nRecordingQuality = (Integer)yy_view_self.yy_temp_data.get( "recording_quality" );
                                if( nRecordingQuality != null ) {
                                    main_activity.yy_data_source.setRecordingQuality( nRecordingQuality );

                                    YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                    task.execute();
                                }
                            }
                            public void onCancel() { }
                        });
                    }
                });
            }
        });
        ret_data.add( map );
        */

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Answer mode
        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                String text_1 = "Answer mode";
                String text_2 = "Answer & record";
                if( main_activity.yy_data_source.getAnswerMode() == YYCommon.ANSWER_MODE_ANSWER_ONLY ) {
                    text_2 = "Answer only";
                }
                if( main_activity.yy_data_source.getAnswerMode() == YYCommon.ANSWER_MODE_OFF ) {
                    text_2 = "Off";
                }

                btn_obj.setText( YYViewBase.transferText( text_1, text_2 ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Answer & record"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_mode", YYCommon.ANSWER_MODE_ANSWER_AND_RECORD ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerMode() == YYCommon.ANSWER_MODE_ANSWER_AND_RECORD; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Answer only"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_mode", YYCommon.ANSWER_MODE_ANSWER_ONLY ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerMode() == YYCommon.ANSWER_MODE_ANSWER_ONLY; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Off"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "answer_mode", YYCommon.ANSWER_MODE_OFF ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAnswerMode() == YYCommon.ANSWER_MODE_OFF; }
                        });

                        main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Answer mode", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() {
                                Integer nAnswerMode = (Integer)yy_view_self.yy_temp_data.get( "answer_mode" );
                                if( nAnswerMode != null ) {
                                    main_activity.yy_data_source.setAnswerMode( nAnswerMode );

                                    YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                    task.execute();
                                }
                            }
                            public void onCancel() { }
                        });

                    }
                });
            }
        });
        ret_data.add( map );

        return ret_data;
    }

    public class RemoteAccessView extends YYViewBackList {
        public String getViewTitle() { return "Remote access"; }

        public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
            List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Remote access
            Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                @Override
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    String text_1 = "Remote access";
                    String text_2 = main_activity.yy_data_source.getIsUseRemoteAccess() ? "on" : "off";
                    btn_obj.setText( YYViewBase.transferText( text_1, text_2 ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View v ) {
                            List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "On"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "remote_access", true ); }
                                public boolean isRadioChecked() { return main_activity.yy_data_source.getIsUseRemoteAccess(); }
                            });
                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "Off(default)"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "remote_access", false ); }
                                public boolean isRadioChecked() { return !main_activity.yy_data_source.getIsUseRemoteAccess(); }
                            });

                            main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Remote access", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                public void onOK() {
                                    Boolean use_remote_access = (Boolean)yy_view_self.yy_temp_data.get( "remote_access" );
                                    if( use_remote_access != null ) {
                                        main_activity.yy_data_source.setIsUseRemoteAccess( use_remote_access );

                                        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                        task.execute();
                                    }
                                }
                                public void onCancel() { }
                            });
                        }
                    });
                }
            });
            ret_data.add( map );

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Change access PIN
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                @Override
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Change access PIN", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View v ) {
                            main_activity.yy_input_number_pin_view.showInputNumberView( "Confirm your PIN", "", yy_view_self.getViewBackHandler(), "first", new YYInputNumberPINView.onYYInputNumberPINHandler() {
                                public void onSuccessful( String number ) {
                                    YYViewBase.onBackClick();

                                    remote_access_view.setView( true, yy_view_self.getViewBackHandler() );

                                    main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention_2, new YYShowAlertDialog.onAlertDialogHandler() {
                                        public void onInit( AlertDialog ad, View view ) {
                                            String text1 = "Please remember this Access PIN is used for both remote access and outgoing call control";
                                            TextView tv = (TextView)view.findViewById( R.id.attention_text );
                                            tv.setText( text1 );
                                        }
                                        public void onOK() { }
                                        public void onCancel() { }
                                    });
                                }
                                public boolean onCheckNumber( String number ) {
                                    return true;
                                }
                            });
                        }
                    });
                }
            });
            ret_data.add( map );

            return ret_data;
        }
    }
}

