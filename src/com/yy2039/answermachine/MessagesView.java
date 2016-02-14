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
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    String name = item_info.getMsgName();
                    if( name.equals( "" ) ) {
                        name = item_info.getMsgNumber();
                    }
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

        return ret_list_data;
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
                                    if( name.equals( "" ) ) {
                                        name = msg_info.getMsgNumber();
                                    }
                                    String tips = String.format( "playing message from %s", name );
                                    int nResOK = R.drawable.alert_dialog_ok;
                                    int nResDelete = R.drawable.alert_delete;
                                    main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.play_message, tips, nResOK, nResDelete, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                        public void onOK() { stopPlayMessage(); }
                                        public void onCancel() { deleteMessage(); }
                                    });
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
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                @Override
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Delete Messages", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View v ) { deleteMessage(); }
                    });
                }
            });
            ret_data.add( map );

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
                                            Intent intent1 = new Intent( "com.mid.phone.psvo" );
                                            intent1.putExtra( "number", number );
                                            intent1.putExtra( "name", msg_info.getMsgName() );
                                            intent1.putExtra( "callout", true );
                                            intent1.putExtra( "quickdial", true );
                                            intent1.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                            main_activity.startActivity( intent1 );

                                            main_activity.finish();
                                        }
                                        public void onFailure() {
                                            Intent intent1 = new Intent( "com.mid.phone.psvo" );
                                            intent1.putExtra( "number", number );
                                            intent1.putExtra( "name", msg_info.getMsgName() );
                                            intent1.putExtra( "callout", true );
                                            intent1.putExtra( "quickdial", true );
                                            intent1.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                            main_activity.startActivity( intent1 );

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
                    String name = msg_info.getMsgName();
                    if( name.equals( "" ) ) {
                        name = msg_info.getMsgNumber();
                    }

                    String text1 = "";
                    if( name.equals( "" ) ) {
                        text1 = "Are you sure that you want to delete this message?";
                    } else {
                        text1 = String.format( "Are you sure that you want to delete the message you have received from %s?", name );
                    }

                    TextView tv = (TextView)view.findViewById( R.id.attention_text );
                    tv.setText( text1 );

                    // 又是 OK 当 CANCEL 用，CANCEL 当 OK 用
                    ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                    btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_cancel ) );

                    ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                    btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                }
                public void onOK() { }
                public void onCancel() {
                    //main_activity.yy_data_source.treatMsg( YYDataSource.TREAT_MSG_OPERATION_DELETE_ONE, msg_index, new YYDataSource.onTreatMsgLinstener() {
                    main_activity.yy_data_source.treatMsg_test( YYDataSource.TREAT_MSG_OPERATION_DELETE_ONE, msg_info.getMsgIndex(), new YYDataSource.onTreatMsgLinstener() {
                        public void onSuccessfully() {
                            main_activity.yy_data_source.removeLocalMessageFromList( msg_index );

                            YYViewBase.onBackClick();
                        }
                        public void onFailure() {
                            Toast.makeText( main_activity, "delete message failed", Toast.LENGTH_SHORT ).show();
                        }
                    });
                }
            });
        }
    }
}

