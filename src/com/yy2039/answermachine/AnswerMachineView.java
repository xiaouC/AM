package com.yy2039.answermachine;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import android.view.View;
import android.app.AlertDialog;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AnswerMachineView extends YYViewBase {
    private MessagesView msg_view;
    private OutgoingMessagesView outgoing_msg_view;
    private SettingsView settings_view;
    public AnswerMachineView() {
        view_layout_res_id = R.layout.title_listview;

        msg_view = new MessagesView();
        outgoing_msg_view = new OutgoingMessagesView();
        settings_view = new SettingsView();
    }

    public void setView( boolean bIsPush, onViewBackHandler handler ) {
        super.setView( bIsPush, handler );

        // 
        fillListView();
    }

    public String getViewTitle() { return "Answer Machine"; }

    // 返回到自己界面
    public onViewBackHandler getViewBackHandler() {
        if( vb_handler == null )
        {
            vb_handler = new onViewBackHandler() {
                public void onBack() {
                    //if( main_activity.yy_command.answer_machine_link ) {
                        main_activity.yy_show_alert_dialog.showWaitingAlertDialog();
                        main_activity.yy_command.disconnectAnswerMachine( new YYCommand.onConnLisenter() {
                            public void onSuccessfully() {
                                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();

                                main_activity.yy_data_source.refreshMessageCount( new YYDataSource.onTreatMsgLinstener() {
                                    public void onSuccessfully() { yy_view_self.setView( false, null ); }
                                    public void onFailure() { yy_view_self.setView( false, null ); }
                                });
                            }
                            public void onFailure() {
                                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();

                                main_activity.yy_data_source.refreshMessageCount( new YYDataSource.onTreatMsgLinstener() {
                                    public void onSuccessfully() { yy_view_self.setView( false, null ); }
                                    public void onFailure() { yy_view_self.setView( false, null ); }
                                });
                            }
                        });
                    //}
                    //else {
                    //    main_activity.yy_data_source.refreshMessageCount( new YYDataSource.onTreatMsgLinstener() {
                    //        public void onSuccessfully() { yy_view_self.setView( false, null ); }
                    //        public void onFailure() { yy_view_self.setView( false, null ); }
                    //    });
                    //}
                }
            };
        }

        return vb_handler;
    }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Messages
        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_image, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                ((ImageView)view_obj).setBackgroundResource( R.drawable.am_messages );
            }
        });
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                String text_1 = "Messages";
                String text_2 = String.format( "%d Messages, %d New.", main_activity.yy_data_source.getMessageCount(), main_activity.yy_data_source.getNewMessageCount() );
                btn_obj.setText( YYViewBase.transferText( text_1, text_2 ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        main_activity.yy_command.executeAnswerMachineCommand( YYCommand.ANSWER_MACHINE_GMSL_RESULT, new YYCommand.onCommandListener() {
                            public void onSend() {
                                main_activity.sendBroadcast( new Intent( YYCommand.ANSWER_MACHINE_GMSL ) );
                            }
                            public void onRecv( String data, String data2 ) {
                                String[] results = data2.split( "," );

                                main_activity.yy_data_source.msg_list.clear();

                                int count = results.length / 5;
                                for( int i=0; i < count; ++i ) {
                                    if( results[i*5+0].equals( "" ) ) {
                                        continue;
                                    }

                                    final String msg_index = results[i*5+0];
                                    final int msg_type = Integer.valueOf( results[i*5+1] );
                                    final String msg_name = results[i*5+2];
                                    final String msg_number = results[i*5+3];
                                    final String msg_datetime = results[i*5+4];
                                    final String year = msg_datetime.substring( 0, 4 );
                                    final String month = msg_datetime.substring( 4, 6 );
                                    final String day = msg_datetime.substring( 6, 8 );
                                    final String hour = msg_datetime.substring( 8, 10 );
                                    final String min = msg_datetime.substring( 10 );
                                    main_activity.yy_data_source.msg_list.add( new YYDataSource.onMsgInfo() {
                                        public String getMsgIndex() { return msg_index; }
                                        public int getMsgType() { return msg_type; }
                                        public String getMsgName() { return msg_name; }
                                        public String getMsgNumber() { return msg_number; }
                                        public String getMsgDateTime() { return String.format( "%s/%s/%s %s:%s", month, day, year, hour, min ); }
                                    });
                                }
                                msg_view.setView( true, yy_view_self.getViewBackHandler() );
                            }
                            public void onFailure() {
                            }

                            //main_activity.yy_data_source.getMessageList( new YYDataSource.onTreatMsgLinstener() {
                            //    public void onSuccessfully() {
                            //        msg_view.setView( true, yy_view_self.getViewBackHandler() );
                            //    }
                            //    public void onFailure() {
                            //    }
                            //});
                        });
                    }
                });
            }
        });
        ret_data.add( map );

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Outgoing Messages
        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_image, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                ((ImageView)view_obj).setBackgroundResource( R.drawable.am_outgoing_messages );
            }
        });
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( YYViewBase.transferText( "Outgoing Messages", "" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        outgoing_msg_view.setView( true, yy_view_self.getViewBackHandler() );
                    }
                });
            }
        });
        ret_data.add( map );

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Settings
        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_image, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                ((ImageView)view_obj).setBackgroundResource( R.drawable.am_settings );
            }
        });
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( YYViewBase.transferText( "Settings", "" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        main_activity.yy_data_source.getDTAMSetting( new YYDataSource.onTreatMsgLinstener() {
                            public void onSuccessfully() {
                                settings_view.setView( true, yy_view_self.getViewBackHandler() );
                            }
                            public void onFailure() {
                                Toast.makeText( main_activity, "get answer machine settings failed", Toast.LENGTH_LONG ).show();
                            }
                        });
                    }
                });
            }
        });
        ret_data.add( map );

        return ret_data;
    }

    public void updateView() {
        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
        task.execute();
    }
}
