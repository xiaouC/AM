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
import android.util.Log;
import android.widget.Toast;

public class OutgoingMessagesView extends YYViewBackList {
    private MsgOpView answer_record_view;
    private MsgOpView answer_only_view;
    public OutgoingMessagesView() {
        answer_record_view = new MsgOpView( "Answer & Record", YYDataSource.OUTGOING_MSG_TYPE_ANSWER_AND_RECORDING );
        answer_only_view = new MsgOpView( "Answer Only", YYDataSource.OUTGOING_MSG_TYPE_ANSWER_ONLY );
    }

    public String getViewTitle() { return "Outgoing Messages"; }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Answer & Record
        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                String text_1 = "Answer & Record";
                String text_2 = "";
                btn_obj.setText( YYViewBase.transferText( text_1, text_2 ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) { answer_record_view.setView( true, yy_view_self.getViewBackHandler() ); }
                });
            }
        });
        ret_data.add( map );

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Answer Only
        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( YYViewBase.transferText( "Answer Only", "" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) { answer_only_view.setView( true, yy_view_self.getViewBackHandler() ); }
                });
            }
        });
        ret_data.add( map );

        return ret_data;
    }

    public class MsgOpView extends YYViewBackList {
        private String strTitle;
        private int nMsgType;
        public MsgOpView( String title, int type ) {
            strTitle = title;
            nMsgType = type;
        }

        public String getViewTitle() { return strTitle; }

        public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
            List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Play
            Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                @Override
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    String text_1 = "Play";
                    String text_2 = "";
                    btn_obj.setText( YYViewBase.transferText( text_1, text_2 ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View v ) { playMessage(); }
                    });
                }
            });
            ret_data.add( map );

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Change
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                @Override
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Change", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View v ) { recordMessage(); }
                    });
                }
            });
            ret_data.add( map );

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Delete
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                @Override
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Delete", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View v ) { deleteMessage(); }
                    });
                }
            });
            ret_data.add( map );

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Use default message
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                @Override
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Use default message", main_activity.yy_data_source.getOutgoingIsUseDefaultMessage( nMsgType ) ? "on" : "off" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View v ) {
                            List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "On (default)"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "use_default_message", true ); }
                                public boolean isRadioChecked() { return main_activity.yy_data_source.getOutgoingIsUseDefaultMessage( nMsgType ); }
                            });
                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "Off"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "use_default_message", false ); }
                                public boolean isRadioChecked() { return !main_activity.yy_data_source.getOutgoingIsUseDefaultMessage( nMsgType ); }
                            });

                            main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Use default message", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                public void onOK() {
                                    Boolean use_default_msg = (Boolean)yy_view_self.yy_temp_data.get( "use_default_message" );
                                    if( use_default_msg != null ) {
                                        main_activity.yy_data_source.setOutgoingIsUseDefaultMessage( nMsgType, use_default_msg );

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

        public void playMessage() {
            main_activity.yy_data_source.treatOutgoingMsg( YYDataSource.OUTGOING_MSG_OPERATION_PLAY, nMsgType, new YYDataSource.onTreatMsgLinstener() {
                public void onSuccessfully() {
                    String title = "Play message";
                    String tips = "playing outgoing message";
                    int nResOK = R.drawable.alert_dialog_ok;
                    int nResDelete = R.drawable.alert_delete;
                    main_activity.yy_playing_msg_dlg = main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.play_message, tips, nResOK, nResDelete, new YYShowAlertDialog.onAlertDialogClickHandler() {
                        public void onOK() {
                            main_activity.yy_playing_msg_dlg = null;
                            main_activity.changeShengDao( true );
                            main_activity.yy_data_source.treatOutgoingMsg( YYDataSource.OUTGOING_MSG_OPERATION_STOP_PLAY, nMsgType, new YYDataSource.onTreatMsgLinstener() {
                                public void onSuccessfully() {
                                }
                                public void onFailure() {
                                    Toast.makeText( main_activity, "stop play outgoing message failed", Toast.LENGTH_SHORT ).show();
                                }
                            });
                        }
                        public void onCancel() {
                            main_activity.yy_playing_msg_dlg = null;
                            main_activity.changeShengDao( true );
                            deleteMessage();
                        }
                    });
                    main_activity.changeShengDao( false );
                }
                public void onFailure() {
                    Toast.makeText( main_activity, "play outgoing message failed", Toast.LENGTH_SHORT ).show();
                }
            });
        }

        public void deleteMessage() {
            main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
                public void onInit( AlertDialog ad, View view ) {
                    String text1 = "Are you sure that you want to delete the personalised outgoing message you have recorded?";
                    TextView tv = (TextView)view.findViewById( R.id.attention_text );
                    tv.setText( text1 );

                    // 又是 OK 当 CANCEL 用，CANCEL 当 OK 用
                    ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                    btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_cancel ) );

                    ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                    btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                }
                //public void onOK() { playMessage(); }
                public void onOK() { }
                public void onCancel() {
                    main_activity.yy_data_source.treatOutgoingMsg( YYDataSource.OUTGOING_MSG_OPERATION_DELETE, nMsgType, new YYDataSource.onTreatMsgLinstener() {
                        public void onSuccessfully() {
                            //recordMessage();
                        }
                        public void onFailure() {
                            Toast.makeText( main_activity, "delete outgoing message failed", Toast.LENGTH_SHORT ).show();
                        }
                    });
                }
            });
        }

        public void recordMessage() {
            main_activity.yy_data_source.treatOutgoingMsg( YYDataSource.OUTGOING_MSG_OPERATION_CHANGE, nMsgType, new YYDataSource.onTreatMsgLinstener() {
                public void onSuccessfully() {
                    String title = "Record message";
                    String tips = "Recording outgoing message";
                    main_activity.yy_playing_msg_dlg = main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.record_name, tips, R.drawable.alert_save, R.drawable.alert_delete, new YYShowAlertDialog.onAlertDialogClickHandler() {
                        public void onOK() {
                            main_activity.yy_playing_msg_dlg = null;
                            main_activity.yy_auto_save_listener = null;
                            main_activity.yy_data_source.treatOutgoingMsg( YYDataSource.OUTGOING_MSG_OPERATION_STOP_CHANGE, nMsgType, new YYDataSource.onTreatMsgLinstener() {
                                public void onSuccessfully() {
                                    if( nMsgType == 0 ) {
                                        main_activity.yy_data_source.initOutgoingIsUseDefaultMessage0( false );
                                    } else {
                                        main_activity.yy_data_source.initOutgoingIsUseDefaultMessage1( false );
                                    }

                                    yy_view_self.yy_list_adapter.list_data = yy_view_self.getItemListData();

                                    YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                    task.execute();

                                    main_activity.yy_schedule.scheduleOnceTime( 1000, new YYSchedule.onScheduleAction() {
                                        public void doSomething() {
                                            playMessage();
                                        }
                                    });
                                }
                                public void onFailure() {
                                    Toast.makeText( main_activity, "change outgoing message failed", Toast.LENGTH_SHORT ).show();
                                    if( nMsgType == 0 ) {
                                        main_activity.yy_data_source.initOutgoingIsUseDefaultMessage0( false );
                                    } else {
                                        main_activity.yy_data_source.initOutgoingIsUseDefaultMessage1( false );
                                    }

                                    yy_view_self.yy_list_adapter.list_data = yy_view_self.getItemListData();

                                    YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                    task.execute();

                                    main_activity.yy_schedule.scheduleOnceTime( 1000, new YYSchedule.onScheduleAction() {
                                        public void doSomething() {
                                            playMessage();
                                        }
                                    });
                                }
                            });
                        }
                        public void onCancel() {
                            main_activity.yy_playing_msg_dlg = null;
                            main_activity.yy_auto_save_listener = null;
                            main_activity.yy_data_source.treatOutgoingMsg( YYDataSource.OUTGOING_MSG_OPERATION_DELETE, nMsgType, new YYDataSource.onTreatMsgLinstener() {
                                public void onSuccessfully() {
                                    if( nMsgType == 0 ) {
                                        main_activity.yy_data_source.initOutgoingIsUseDefaultMessage0( true );
                                    } else {
                                        main_activity.yy_data_source.initOutgoingIsUseDefaultMessage1( true );
                                    }
                                }
                                public void onFailure() {
                                    Toast.makeText( main_activity, "delete outgoing message failed", Toast.LENGTH_SHORT ).show();
                                }
                            });
                        }
                    });
                    main_activity.yy_auto_save_listener = new AnswerMachineActivity.onAutoSaveListener() {
                        public void onAutoSave() {
                            if( main_activity.yy_playing_msg_dlg != null ) {
                                main_activity.yy_schedule.scheduleOnceTime( 100, new YYSchedule.onScheduleAction() {
                                    public void doSomething() {
                                        playMessage();
                                    }
                                });
                            }
                        }
                    };
                }
                public void onFailure() {
                    Toast.makeText( main_activity, "record outgoing message failed", Toast.LENGTH_SHORT ).show();
                }
            });
        }
    }
}

