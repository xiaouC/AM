package com.yy2039.answermachine;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.util.Log;

public class YYCommand {
    public AnswerMachineActivity main_activity;

	// Result Final
	public final static String RESULT_LINK = "LINK";
	public final static String RESULT_NOLINK = "NOLINK";
	public final static String RESULT_SUCCESS = "SUCCESS";
	public final static String RESULT_FAIL = "FAIL";
	public final static String RESULT_EQUAL = "EQUAL";
	public final static String RESULT_NOEQUAL = "NOEQUAL";

    // 连接/断开
    public final static String ANSWER_MACHINE_BTCL = "andorid.intent.action.answer.machine.btcl";
    public final static String ANSWER_MACHINE_BTCL_RESULT = "com.action.dect.answer.machine.btcl.result";
	public final static String ANSWER_MACHINE_BTCR = "andorid.intent.action.answer.machine.btcr";
	public final static String ANSWER_MACHINE_BTCR_RESULT = "com.action.dect.answer.machine.btcr.result";
    public final static String ANSWER_MACHINE_GMSL = "andorid.intent.action.answer.machine.gmsl";
    public final static String ANSWER_MACHINE_GMSL_RESULT = "com.action.dect.answer.machine.gmsl.result";

    public final static String ANSWER_MACHINE_CONM = "andorid.intent.action.answer.machine.conm";
    public final static String ANSWER_MACHINE_CONM_RESULT = "com.action.dect.answer.machine.conm.result";

    public final static String ANSWER_MACHINE_COOM = "andorid.intent.action.answer.machine.coom";
    public final static String ANSWER_MACHINE_COOM_RESULT = "com.action.dect.answer.machine.coom.result";

    // 
    public final static String COMMAD_PAGE_MSG_COUNT = "andorid.intent.action.commad.page.msg.count";
    public final static String PAGE_MSG_COUNT_RESULT = "com.action.dect.page.msg.count.result";

    // 
    public final static String SETTINGS_BTCL = "andorid.intent.action.settings.btcl";
	public final static String SETTINGS_BASE_BTCL_RESULT = "com.action.dect.settings.base.btcl.result";
	public final static String SETTINGS_BTCR = "andorid.intent.action.settings.btcr";
	public final static String SETTINGS_BASE_BTCR_RESULT = "com.action.dect.settings.base.btcr.result";

    public final static String ANSWER_MACHINE_GDMS = "andorid.intent.action.answer.machine.gdms";
    public final static String ANSWER_MACHINE_GDMS_RESULT = "com.action.dect.answer.machine.gdms.result";

    public final static String ANSWER_MACHINE_SDMS = "andorid.intent.action.answer.machine.sdms";
    public final static String ANSWER_MACHINE_SDMS_RESULT = "com.action.dect.answer.machine.sdms.result";

    public final static String ANSWER_MACHINE_GDTS = "andorid.intent.action.answer.machine.gdts";
    public final static String ANSWER_MACHINE_GDTS_RESULT = "com.action.dect.answer.machine.gdts.result";

    public final static String ANSWER_MACHINE_SDTS = "andorid.intent.action.answer.machine.sdts";
    public final static String ANSWER_MACHINE_SDTS_RESULT = "com.action.dect.answer.machine.sdts.result";

    // 比较 pin 是否正确
    public final static String CALL_GUARDIAN_CMPC = "andorid.intent.action.call.guardian.cmpc";
    public final static String CALL_GUARDIAN_CMPC_RESULT = "com.action.dect.call.guardian.cmpc.result";


    // new pin
    public final static String CALL_GUARDIAN_SCCP = "andorid.intent.action.call.guardian.sccp";
    public final static String CALL_GUARDIAN_SCCP_RESULT = "com.action.dect.call.guardian.sccp.result";

    //public boolean settings_base_link = false;
    //public boolean call_list_link = false;
    //public boolean answer_machine_link = false;

    // 
    public YYCommand( AnswerMachineActivity activity ) {
        main_activity = activity;

        init();
    }

    public interface onCommandListener {
        void onSend();
        void onRecv( String data, String data2 );
        void onFailure();
    }

    public class CommandInfo {
        public String command_name;
        public onCommandListener command_listener;
        public CommandInfo( String cmd, onCommandListener cmdListener ) {
            command_name = cmd;
            command_listener = cmdListener;
        }
    }

    public interface onRecvActionListener {
        void onExecute( String data, String data2 );
    }
    public Map<String,onRecvActionListener> action_list = new HashMap<String,onRecvActionListener>();

	public BroadcastReceiver commandReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String data = intent.getExtras().getString("data");
			String data2 = intent.getExtras().getString("data2");
            Log.v( "cconn", "onReceive +++++++++++++++++++++++++++++++++++ action : " + action );
            Log.v( "cconn", "onReceive +++++++++++++++++++++++++++++++++++ data : " + data );
            Log.v( "cconn", "onReceive +++++++++++++++++++++++++++++++++++ data2 : " + data2 );


            if( cur_command_info != null ) {
                if( cur_command_info.command_name.equals( action ) ) {
                    onRecvActionListener ral = action_list.get( action );
                    ral.onExecute( data, data2 );
                }
            }
            else {
                onRecvActionListener ral = action_list.get( action );
                ral.onExecute( data, data2 );
            }

            //onRecvActionListener ral = action_list.get( action );
            //ral.onExecute( data, data2 );
        }
    };

    public List<CommandInfo> request_command_list = new ArrayList<CommandInfo>();
    public void executeCommand( String cmd, onCommandListener listener ) {
        CommandInfo cmd_info = new CommandInfo( cmd, listener );
        request_command_list.add( cmd_info );

        realExecuteCommand();
    }

    public CommandInfo cur_command_info;
    public void realExecuteCommand() {
        Log.v( "prot", "realExecuteCommand aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" );
        if( cur_command_info != null ) {
            Log.v( "prot", "realExecuteCommand bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" );
            return;
        }
            Log.v( "prot", "realExecuteCommand ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc" );

        if( request_command_list.size() > 0 ) {
            cur_command_info = request_command_list.get( 0 );
            request_command_list.remove( 0 );

            cur_command_info.command_listener.onSend();
        }
    }

    public void addDefaultProcessAction( String action_name, final String disconnect_name ) {
        action_list.put( action_name, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( disconnect_name != null ) {
                    main_activity.sendBroadcast( new Intent( disconnect_name ) );
                }
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }
            }
        });
    }

    public void init() {
        Log.v( "prot", "ANSWER_MACHINE init =========================================================================================================" );
        action_list.put( ANSWER_MACHINE_BTCL_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                Log.v( "cconn", "ANSWER_MACHINE_GMSL_RESULT 5555555555555555555555555555555555555555555555555555555" );
                Log.v( "cconn", "data : " + data );
                Log.v( "cconn", "data2 : " + data2 );
				if( data.equals( RESULT_LINK ) ) {
                    if( cur_command_info != null ) {
                        cur_command_info.command_listener.onRecv( data, data2 );
                    }
                }
                else {
                    if( cur_command_info != null ) {
                        cur_command_info.command_listener.onFailure();
                    }
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 断开连接，当断开之后，马上执行下一个请求
        action_list.put( ANSWER_MACHINE_BTCR_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( ANSWER_MACHINE_GMSL_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( ANSWER_MACHINE_CONM_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( ANSWER_MACHINE_COOM_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });

        action_list.put( SETTINGS_BASE_BTCL_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
				if( data.equals( RESULT_LINK ) ) {
                    if( cur_command_info != null ) {
                        cur_command_info.command_listener.onRecv( data, data2 );
                    }
                }
                else {
                    if( cur_command_info != null ) {
                        cur_command_info.command_listener.onFailure();
                    }
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 断开连接，当断开之后，马上执行下一个请求
        action_list.put( SETTINGS_BASE_BTCR_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( ANSWER_MACHINE_GDMS_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( ANSWER_MACHINE_SDMS_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( ANSWER_MACHINE_GDTS_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( ANSWER_MACHINE_SDTS_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( CALL_GUARDIAN_CMPC_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( CALL_GUARDIAN_SCCP_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( PAGE_MSG_COUNT_RESULT, new onRecvActionListener() {
            public void onExecute( String data, String data2 ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data, data2 );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });

        // 注册
        IntentFilter filter = new IntentFilter();
        for( Map.Entry<String,onRecvActionListener> entry : action_list.entrySet() ) {
            String action_name = entry.getKey();
            filter.addAction( action_name );
        }
		main_activity.registerReceiver( commandReceiver, filter );
    }

    public interface onConnLisenter {
        void onSuccessfully();
        void onFailure();
    }

    public void executeSettingsBaseCommand( final String cmd_result, final onCommandListener cmd_listener ) {
        main_activity.yy_show_alert_dialog.showWaitingAlertDialog();
        Log.v( "cconn", "showWaitingAlertDialog 55555555555555555555555555555555555555" );
        disconnectAllLink( new onConnLisenter() {
            public void onSuccessfully() {
                connectSettingsBase( new onConnLisenter() {
                    public void onSuccessfully() {
                        executeCommand( cmd_result, new onCommandListener() {
                            public void onSend() { cmd_listener.onSend(); }
                            public void onRecv( String data, String data2 ) {
                                Log.v( "cconn", "recv data : " + data );
                                Log.v( "cconn", "recv data2 : " + data2 );
                                final String recv_data = data;
                                final String recv_data2 = data2;
                                // 处理完后，马上断开
                                disconnectSettingsBase( new onConnLisenter() {
                                    public void onSuccessfully() {
                                        cmd_listener.onRecv( recv_data, recv_data2 );
                                        Log.v( "cconn", "hideWaitingAlertDialog 55555555555555555555555555555555555" );
                                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                    }
                                    public void onFailure() {
                                        cmd_listener.onRecv( recv_data, recv_data2 );
                                        Log.v( "cconn", "hideWaitingAlertDialog 55555555555555555555555555555555555" );
                                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                    }
                                });
                            }
                            public void onFailure() {
                                // 处理完后，马上断开
                                disconnectSettingsBase( new onConnLisenter() {
                                    public void onSuccessfully() {
                                        cmd_listener.onFailure();
                                        Log.v( "cconn", "hideWaitingAlertDialog 55555555555555555555555555555555555" );
                                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                    }
                                    public void onFailure() {
                                        cmd_listener.onFailure();
                                        Log.v( "cconn", "hideWaitingAlertDialog 55555555555555555555555555555555555" );
                                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                    }
                                });
                            }
                        });
                    }
                    public void onFailure() {
                        Toast.makeText( main_activity, "settings base link failed!", Toast.LENGTH_LONG ).show();
                        Log.v( "cconn", "hideWaitingAlertDialog 55555555555555555555555555555555555" );
                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                    }
                });
            }
            public void onFailure() {
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                Log.v( "cconn", "hideWaitingAlertDialog 55555555555555555555555555555555555" );
                Toast.makeText( main_activity, "disconnect link failed!", Toast.LENGTH_LONG ).show();
            }
        });
    }

    public void executeAnswerMachineCommandEx( final String cmd_result, final onCommandListener cmd_listener ) {
        main_activity.yy_show_alert_dialog.showWaitingAlertDialog();
        Log.v( "cconn", "showWaitingAlertDialog 333333333333333333333333333333333333333333333" );
        executeCommand( cmd_result, new onCommandListener() {
            public void onSend() { cmd_listener.onSend(); }
            public void onRecv( String data, String data2 ) {
                cmd_listener.onRecv( data, data2 );
                Log.v( "cconn", "hideWaitingAlertDialog 333333333333333333333333333333333333333333333333" );
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
            }
            public void onFailure() {
                cmd_listener.onFailure();
                Log.v( "cconn", "hideWaitingAlertDialog 333333333333333333333333333333333333333333333333" );
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
            }
        });
    }

    public void executeAnswerMachineCommand( final String cmd_result, final onCommandListener cmd_listener ) {
        main_activity.yy_show_alert_dialog.showWaitingAlertDialog();
        Log.v( "cconn", "showWaitingAlertDialog 44444444444444444444444444444444444444444" );
        disconnectAllLink( new onConnLisenter() {
            public void onSuccessfully() {
                connectAnswerMachine( new onConnLisenter() {
                    public void onSuccessfully() {
                        executeCommand( cmd_result, new onCommandListener() {
                            public void onSend() { cmd_listener.onSend(); }
                            public void onRecv( String data, String data2 ) {
                                cmd_listener.onRecv( data, data2 );
                                Log.v( "cconn", "hideWaitingAlertDialog 444444444444444444444444444444444444444444444444" );
                                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                            }
                            public void onFailure() {
                                cmd_listener.onFailure();
                                Log.v( "cconn", "hideWaitingAlertDialog 444444444444444444444444444444444444444444444444" );
                                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                            }
                        });
                    }
                    public void onFailure() {
                        Toast.makeText( main_activity, "answer machine link failed!", Toast.LENGTH_LONG ).show();
                        Log.v( "cconn", "hideWaitingAlertDialog 444444444444444444444444444444444444444444444444" );
                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                    }
                });
            }
            public void onFailure() {
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                Log.v( "cconn", "hideWaitingAlertDialog 444444444444444444444444444444444444444444444444" );
                Toast.makeText( main_activity, "disconnect link failed!", Toast.LENGTH_LONG ).show();
            }
        });
    }

    public void connectSettingsBase( final onConnLisenter conn_lisenter ) {
        Log.v( "cconn", "connect settings base begin" );
        executeCommand( YYCommand.SETTINGS_BASE_BTCL_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( SETTINGS_BTCL ) );
                Log.v( "cconn", "connect settings base send" );
            }
            public void onRecv( String data, String data2 ) {
                Log.v( "cconn", "connect settings base success" );

                //settings_base_link = true;
                conn_lisenter.onSuccessfully();
            }
            public void onFailure() {
                Log.v( "cconn", "connect settings base failed" );

                conn_lisenter.onFailure();
            }
        });
    }

    public void connectAnswerMachine( final onConnLisenter conn_lisenter ) {
        Log.v( "cconn", "connect answer machine begin" );
        executeCommand( YYCommand.ANSWER_MACHINE_BTCL_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( ANSWER_MACHINE_BTCL ) );
                Log.v( "cconn", "connect answer machine send" );
            }
            public void onRecv( String data, String data2 ) {
                Log.v( "cconn", "connect answer machine success" );

                //answer_machine_link = true;
                conn_lisenter.onSuccessfully();
            }
            public void onFailure() {
                Log.v( "cconn", "connect answer machine failed" );

                conn_lisenter.onFailure();
            }
        });
    }

    public void disconnectAllLink( final onConnLisenter disconnect_lisenter ) {
        Log.v( "cconn", "disconnectAllLink begin" );
        disconnectSettingsBase( new onConnLisenter() {
            public void onSuccessfully() {
                disconnectAnswerMachine( new onConnLisenter() {
                    public void onSuccessfully() {
                        disconnect_lisenter.onSuccessfully();
                    }
                    public void onFailure() { }
                });
            }
            public void onFailure() { }
        });
    }

    public void disconnectSettingsBase( final onConnLisenter disconnect_lisenter ) {
        Log.v( "cconn", "disconnect settings base" );
        //if( settings_base_link ) {
            executeCommand( YYCommand.SETTINGS_BASE_BTCR_RESULT, new YYCommand.onCommandListener() {
                public void onSend() {
                    Log.v( "cconn", "disconnect settings base begin" );
                    main_activity.sendBroadcast( new Intent( SETTINGS_BTCR ) );
                }
                public void onRecv( String data, String data2 ) {
                    Log.v( "cconn", "disconnect settings base success" );
                    //settings_base_link = false;
                    disconnect_lisenter.onSuccessfully();
                }
                public void onFailure() {
                    Log.v( "cconn", "disconnect settings base failed" );
                    Toast.makeText( main_activity, "disconnect settings base link failed!", Toast.LENGTH_LONG ).show();
                }
            });
        //}
        //else {
        //    disconnect_lisenter.onSuccessfully();
        //}
    }

    public void disconnectAnswerMachine( final onConnLisenter disconnect_lisenter ) {
        Log.v( "cconn", "disconnect answer machine" );
        //if( answer_machine_link ) {
            executeCommand( YYCommand.ANSWER_MACHINE_BTCR_RESULT, new YYCommand.onCommandListener() {
                public void onSend() {
                    Log.v( "cconn", "disconnect answer machine begin" );
                    main_activity.sendBroadcast( new Intent( ANSWER_MACHINE_BTCR ) );
                }
                public void onRecv( String data, String data2 ) {
                    Log.v( "cconn", "disconnect answer machine success" );
                    //answer_machine_link = false;
                    disconnect_lisenter.onSuccessfully();
                }
                public void onFailure() {
                    Log.v( "cconn", "disconnect answer machine failed" );
                    Toast.makeText( main_activity, "disconnect answer machine link failed!", Toast.LENGTH_LONG ).show();
                }
            });
        //}
        //else {
        //    disconnect_lisenter.onSuccessfully();
        //}
    }

}

