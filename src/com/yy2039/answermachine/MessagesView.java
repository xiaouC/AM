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

public class MessagesView extends YYViewBackList {
    private MessageOperationView msg_op_view;

    public MessagesView() {
        msg_op_view = new MessageOperationView();
    }

    public String getViewTitle() { return "Messages"; }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        for( int i=0; i < main_activity.yy_data_source.msg_list.size(); ++i ) {
            final int index = i;
            final YYDataSource.onMsgInfo item_info = main_activity.yy_data_source.msg_list.get( i );

            Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_image, new YYListAdapter.onYYListItemHandler() {
                @Override
                public void item_handle( Object view_obj ) {
                    ((ImageView)view_obj).setBackgroundResource( item_info.getMsgType() == 0 ? R.drawable.msg_new_1 : R.drawable.msg_new_2 );
                }
            });
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    String name = item_info.getMsgName();
                    btn_obj.setText( YYViewBase.transferText( name, item_info.getMsgDateTime() ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) {
                            msg_op_view.msg_index = index;
                            msg_op_view.msg_info = item_info;
                            msg_op_view.setView( true, yy_view_self.getViewBackHandler() );
                        }
                    });
                }
            });

            ret_list_data.add( map );
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Delete Old Messages
        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( YYViewBase.transferText( "Delete old messages", "This will delete all old messages" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) { deleteOldMessages(); }
                });
            }
        });

        ret_list_data.add( map );

        return ret_list_data;
    }

    public void deleteOldMessages() {
        main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
            public void onInit( AlertDialog ad, View view ) {
                String text1 = "All old messages that have been\r\nlistened to will be delete. Are you\r\nsure you wish to continue?";

                TextView tv = (TextView)view.findViewById( R.id.attention_text );
                tv.setText( text1 );

                // 又是 OK 当 CANCEL 用，CANCEL 当 OK 用
                ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_cancel ) );

                ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
            }
            public boolean getIsCancelEnable() { return true; }
            public int getKeybackIsCancel() { return 1; }
            public void onOK() { }
            public void onKeyback() {}
            public void onCancel() {
                main_activity.yy_data_source.treatMsg_test( YYDataSource.TREAT_MSG_OPERATION_DELETE_ALL, "0", new YYDataSource.onTreatMsgLinstener() {
                    public void onSuccessfully() {
                        main_activity.yy_data_source.msg_list.clear();

                        yy_view_self.yy_list_adapter.list_data = yy_view_self.getItemListData();

                        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                        task.execute();

                        String title = "All old messages\r\ndeleted";
                        String tips = "Press OK to finish";
                        int image_id = R.drawable.successfully;
                        main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, image_id, tips, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() { }
                            public void onCancel() { }
                            public boolean getIsCancelEnable() { return true; }
                            public int getKeybackIsCancel() { return 2; }
                            public void onKeyback() {}
                        });
                    }
                    public void onFailure() {
                        String title = "Error deleting old\r\nmessages";
                        String tips = "Press OK to return";
                        int image_id = R.drawable.failure;
                        main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, image_id, tips, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() { }
                            public void onCancel() { }
                            public boolean getIsCancelEnable() { return true; }
                            public int getKeybackIsCancel() { return 2; }
                            public void onKeyback() {}
                        });
                    }
                });
                }
            });
        }

    public class MessageOperationView extends YYViewBackList {
        private int msg_index;
        private YYDataSource.onMsgInfo msg_info;

        public String getViewTitle() { return msg_info.getMsgName(); }

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
                        public void onClick( View v ) {
                            //main_activity.yy_data_source.treatMsg( YYDataSource.TREAT_MSG_OPERATION_PLAY, msg_index, new YYDataSource.onTreatMsgLinstener() {
                            main_activity.yy_data_source.treatMsg_test( YYDataSource.TREAT_MSG_OPERATION_PLAY, msg_info.getMsgIndex(), new YYDataSource.onTreatMsgLinstener() {
                                public void onSuccessfully() {
                                    String title = "Playing message";
                                    String name = msg_info.getMsgName();
                                    String tips = String.format( "playing message from %s", name );
                                    int nResOK = R.drawable.alert_dialog_ok;
                                    int nResDelete = R.drawable.alert_delete;
                                    main_activity.yy_playing_msg_dlg = main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.play_message, tips, nResOK, nResDelete, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                        public void onOK() {
                                            main_activity.yy_playing_msg_dlg = null;
                                            main_activity.changeShengDao( true );

                                            stopPlayMessage();
                                        }
                                        public void onCancel() {
                                            main_activity.yy_playing_msg_dlg = null;
                                            main_activity.changeShengDao( true );

                                            deleteMessage();
                                        }
                                        public boolean getIsCancelEnable() { return false; }
                                        public int getKeybackIsCancel() { return 1; }
                                        public void onKeyback() {}
                                    });
                                    main_activity.changeShengDao( false );
                                }
                                public void onFailure() {
                                }
                            });
                        }
                    });
                }
            });
            ret_data.add( map );

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Delete message
            if( msg_info.getMsgType() == 1 ) {      // 0 is new msg, 1 is old msg
                map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
                map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                    @Override
                    public void item_handle( Object view_obj ) {
                        Button btn_obj = (Button)view_obj;

                        btn_obj.setText( YYViewBase.transferText( "Delete message", "" ) );
                        btn_obj.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick( View v ) { deleteMessage(); }
                        });
                    }
                });
                ret_data.add( map );
            }

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Call back
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                @Override
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Call back", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View v ) {
                            main_activity.yy_input_number_callback_view.showInputNumberView( msg_info.getMsgName(), msg_info.getMsgNumber(), yy_view_self.getViewBackHandler(), new YYInputNumberView.onYYInputNumberHandler() {
                                public void onSave( final String number ) {
                                    main_activity.yy_command.disconnectAllLink( new YYCommand.onConnLisenter() {
                                        public void onSuccessfully() {
                                            onCallback( number, msg_info.getMsgName() );

                                            main_activity.finish();
                                        }
                                        public void onFailure() {
                                            onCallback( number, msg_info.getMsgName() );

                                            main_activity.finish();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
            ret_data.add( map );

            return ret_data;
        }

        public void stopPlayMessage() {
            //main_activity.yy_data_source.treatMsg( YYDataSource.TREAT_MSG_OPERATION_STOP_PLAY, msg_index, new YYDataSource.onTreatMsgLinstener() {
            main_activity.yy_data_source.treatMsg_test( YYDataSource.TREAT_MSG_OPERATION_STOP_PLAY, msg_info.getMsgIndex(), new YYDataSource.onTreatMsgLinstener() {
                public void onSuccessfully() {
                }
                public void onFailure() {
                }
            });
        }

        public void deleteMessage() {
            main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
                public void onInit( AlertDialog ad, View view ) {
                    String text1 = "";
                    if( msg_info.getMsgType() == 0 ) {
                        text1 = String.format( "Are you sure that you want to delete\r\nthe message you have received from\r\n%s without listening\r\nto the message first?", msg_info.getMsgName() );
                    } else {
                        text1 = String.format( "Are you sure that you want to delete\r\nthe message you have received from\r\n%s?", msg_info.getMsgName() );
                    }

                    TextView tv = (TextView)view.findViewById( R.id.attention_text );
                    tv.setText( text1 );

                    // 又是 OK 当 CANCEL 用，CANCEL 当 OK 用
                    ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                    btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_cancel ) );

                    ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                    btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                }
                public boolean getIsCancelEnable() { return true; }
                public int getKeybackIsCancel() { return 1; }
                public void onKeyback() {}
                public void onOK() { }
                public void onCancel() {
                    //main_activity.yy_data_source.treatMsg( YYDataSource.TREAT_MSG_OPERATION_DELETE_ONE, msg_index, new YYDataSource.onTreatMsgLinstener() {
                    main_activity.yy_data_source.treatMsg_test( YYDataSource.TREAT_MSG_OPERATION_DELETE_ONE, msg_info.getMsgIndex(), new YYDataSource.onTreatMsgLinstener() {
                        public void onSuccessfully() {
                            main_activity.yy_data_source.removeLocalMessageFromList( msg_index );

                            YYViewBase.onBackClick();

                            // delete msg prompt
                            String title = "Message deleted";
                            String tips = "Press OK to finish";
                            int image_id = R.drawable.successfully;
                            main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, image_id, tips, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                public void onOK() { }
                                public void onCancel() { }
                                public boolean getIsCancelEnable() { return true; }
                                public int getKeybackIsCancel() { return 1; }
                                public void onKeyback() {}
                            });
                        }
                        public void onFailure() {
                            // delete msg prompt
                            String title = "Error deleting message";
                            String tips = "Press OK to return";
                            int image_id = R.drawable.failure;
                            main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, image_id, tips, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                public void onOK() { }
                                public void onCancel() { }
                                public boolean getIsCancelEnable() { return true; }
                                public int getKeybackIsCancel() { return 1; }
                                public void onKeyback() {}
                            });

                        }
                    });
                }
            });
        }

        public void onCallback( String num, String name ) {
            Intent intent = new Intent( "com.mid.phone.call.status.receiver" );
            intent.putExtra( "number", num );
            intent.putExtra( "name", name );
            intent.putExtra( "callout", true );
            intent.putExtra( "quickdial", true );
            main_activity.sendBroadcast( intent );
        }
    }
}

