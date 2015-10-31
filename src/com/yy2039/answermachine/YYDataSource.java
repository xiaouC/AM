package com.yy2039.answermachine_1;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.app.AlertDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

public class YYDataSource {
    private Boolean bOutgoingIsUseDefaultMessage;

    private Boolean bIsUseRemoteAccess;
    private boolean bIsFirstTimeUseRemoteAccess;
    private Integer nAnswerDelayType;
    private Integer nRecordingQuality;
    private Integer nAnswerMode;

    public interface onPlayMessageListener {
        void onPlayEnd();
    }

    public interface onOutgoingMessageInfo {
    }

    public interface onMsgInfo {
        String getMsgIndex();
        int getMsgType();
        String getMsgName();
        String getMsgNumber();
        String getMsgDateTime();
    }
    public List<onMsgInfo> msg_list = new ArrayList<onMsgInfo>();
    private int nMsgCount;
    private int nNewMsgCount;

    private AnswerMachineActivity main_activity;
    public YYDataSource( AnswerMachineActivity activity ) {
        main_activity = activity;

        bOutgoingIsUseDefaultMessage = true;

        bIsUseRemoteAccess = false;
        nAnswerDelayType = YYCommon.ANSWER_DELAY_2_RINGS;
        nRecordingQuality = YYCommon.RECORDING_QUALITY_HIGH;
        nAnswerMode = YYCommon.ANSWER_MODE_ANSWER_AND_RECORD;

        // 
        main_activity.yy_command.executeCommand( YYCommand.PAGE_MSG_COUNT_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.yy_show_alert_dialog.showWaitingAlertDialog();
                main_activity.sendBroadcast( new Intent( YYCommand.COMMAD_PAGE_MSG_COUNT ) );
            }
            public void onRecv( String data, String data2 ) {
                String[] results = data.split( "," );

                nMsgCount = Integer.valueOf( results[0] );
                nNewMsgCount = Integer.valueOf( results[1] );

                main_activity.answer_machine_view.updateView();
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
            }
            public void onFailure() {
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
            }
        });

        /*
        getDTAMSetting( new onTreatMsgLinstener() {
            public void onSuccessfully() {}
            public void onFailure() {}
        });
        */
    }

    public int getMessageCount() {
        return nMsgCount;
    }

    public int getNewMessageCount() {
        return nNewMsgCount;
    }

    public interface onTreatMsgLinstener {
        void onSuccessfully();
        void onFailure();
    }

    public void refreshMessageCount( final onTreatMsgLinstener msg_lisenter ) {
        main_activity.yy_command.executeCommand( YYCommand.PAGE_MSG_COUNT_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.yy_show_alert_dialog.showWaitingAlertDialog();
                main_activity.sendBroadcast( new Intent( YYCommand.COMMAD_PAGE_MSG_COUNT ) );
            }
            public void onRecv( String data, String data2 ) {
                String[] results = data.split( "," );

                nMsgCount = Integer.valueOf( results[0] );
                nNewMsgCount = Integer.valueOf( results[1] );

                msg_lisenter.onSuccessfully();
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
            }
            public void onFailure() {
                msg_lisenter.onFailure();
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
            }
        });
    }

    public void removeLocalMessageFromList( int nIndex ) {
        msg_list.remove( nIndex );
    }

    public void getMessageList( final onTreatMsgLinstener msgListener ) {
        main_activity.yy_command.executeAnswerMachineCommandEx( YYCommand.ANSWER_MACHINE_GMSL_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( YYCommand.ANSWER_MACHINE_GMSL ) );
            }
            public void onRecv( String data, String data2 ) {
                String[] results = data2.split( "," );

                msg_list.clear();

                int count = results.length / 5;
                for( int i=0; i < count; ++i ) {
                    if( results[i*5+0].equals( "" ) ) {
                        continue;
                    }

                    Log.v( "cconn", "msg_index : " + results[i*5+0] );
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
                    msg_list.add( new onMsgInfo() {
                        public String getMsgIndex() { return msg_index; }
                        public int getMsgType() { return msg_type; }
                        public String getMsgName() { return msg_name; }
                        public String getMsgNumber() { return msg_number; }
                        public String getMsgDateTime() { return String.format( "%s/%s/%s %s:%s", month, day, year, hour, min ); }
                    });
                }
                msgListener.onSuccessfully();
            }
            public void onFailure() {
                Toast.makeText( main_activity, "request message list : failure", Toast.LENGTH_LONG ).show();
                msgListener.onFailure();
            }
        });

    }

    public final static Integer TREAT_MSG_OPERATION_PLAY = 0;
    public final static Integer TREAT_MSG_OPERATION_DELETE_ONE = 1;
    public final static Integer TREAT_MSG_OPERATION_DELETE_ALL = 2;
    public void treatMsg( final int nOpType, final int nIndex, final onTreatMsgLinstener treat_msg_linstener ) {
        main_activity.yy_command.executeAnswerMachineCommandEx( YYCommand.ANSWER_MACHINE_CONM_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent msgIntent = new Intent( YYCommand.ANSWER_MACHINE_CONM );
                msgIntent.putExtra( "operation", String.format( "%d", nOpType ) );
                msgIntent.putExtra( "index", Integer.toHexString( nIndex ) );
                main_activity.sendBroadcast( msgIntent );
            }
            public void onRecv( String data, String data2 ) {
                Log.v( "cconn", "treatMsg data : " + data );
                Log.v( "cconn", "treatMsg data2 : " + data2 );
                Log.v( "prot", "treatMsg data : " + data );
                Log.v( "prot", "treatMsg data2 : " + data2 );
                if( data.equals( "SUCCESS" ) ) {
                    // 处理成功
                    treat_msg_linstener.onSuccessfully();
                }
                else {
                    // 处理失败
                    treat_msg_linstener.onFailure();
                }
            }
            public void onFailure() {
                treat_msg_linstener.onFailure();
            }
        });
    }

    public final static Integer OUTGOING_MSG_OPERATION_PLAY = 0;
    public final static Integer OUTGOING_MSG_OPERATION_DELETE = 1;
    public final static Integer OUTGOING_MSG_OPERATION_CHANGE = 2;
    public final static Integer OUTGOING_MSG_OPERATION_STOP_PLAY = 3;
    public final static Integer OUTGOING_MSG_OPERATION_STOP_CHANGE = 4;

    public final static Integer OUTGOING_MSG_TYPE_ANSWER_AND_RECORDING = 0;
    public final static Integer OUTGOING_MSG_TYPE_ANSWER_ONLY = 1;
    public final static Integer OUTGOING_MSG_TYPE_DTAM = 2;
    public final static Integer OUTGOING_MSG_TYPE_ANNOUNCE_MSG = 3;

    // 在这里，nMsgType 一定使用 2
    public void treatOutgoingMsg( final int nOpType, final onTreatMsgLinstener treat_msg_linstener ) {
        main_activity.yy_command.executeAnswerMachineCommand( YYCommand.ANSWER_MACHINE_COOM_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent msgIntent = new Intent( YYCommand.ANSWER_MACHINE_COOM );
                msgIntent.putExtra( "operation", String.format( "%d", nOpType ) );
                msgIntent.putExtra( "type", "2" );
                main_activity.sendBroadcast( msgIntent );
            }
            public void onRecv( String data, String data2 ) {
                if( data.equals( "SUCCESS" ) ) {
                    // 处理成功
                    treat_msg_linstener.onSuccessfully();
                }
                else {
                    // 处理失败
                    treat_msg_linstener.onFailure();
                }

				Toast.makeText( main_activity, "request outgoing msg operation : successfully", Toast.LENGTH_LONG ).show();
            }
            public void onFailure() {
				Toast.makeText( main_activity, "request outgoing msg operation : failure", Toast.LENGTH_LONG ).show();
                treat_msg_linstener.onFailure();
            }
        });
    }

    public void getDTAMSetting( final onTreatMsgLinstener treat_msg_linstener ) {
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.ANSWER_MACHINE_GDTS_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( YYCommand.ANSWER_MACHINE_GDTS ) );
                Toast.makeText( main_activity, "request get DTAM setting : send", Toast.LENGTH_SHORT ).show();
                Log.v( "cconn", "ANSWER_MACHINE_GDTS_RESULT send" );
            }
            public void onRecv( String data, String data2 ) {
                String[] results = data.split( "," );
                bIsUseRemoteAccess = ( Integer.valueOf( results[0] ) == 0 ? true : false ); // 0 : on, 1 : off
                nAnswerDelayType = Integer.valueOf( results[1] );
                nRecordingQuality = Integer.valueOf( results[2] );
                nAnswerMode = Integer.valueOf( results[3] );

                treat_msg_linstener.onSuccessfully();

				Toast.makeText( main_activity, "request get DTAM setting : successfully", Toast.LENGTH_LONG ).show();
                Log.v( "cconn", "ANSWER_MACHINE_GDTS_RESULT success" );
            }
            public void onFailure() {
				Toast.makeText( main_activity, "request get DTAM setting : failure", Toast.LENGTH_LONG ).show();
                Log.v( "cconn", "ANSWER_MACHINE_GDTS_RESULT failed" );
                treat_msg_linstener.onFailure();
            }
        });
    }

    public void setDTAMSetting() {
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.ANSWER_MACHINE_SDTS_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent datmIntent = new Intent( YYCommand.ANSWER_MACHINE_SDTS );
                datmIntent.putExtra( "remote", bIsUseRemoteAccess ? "0" : "1" );
                datmIntent.putExtra( "answer_delay", String.format( "%d", nAnswerDelayType ) );
                datmIntent.putExtra( "recoding_quality", String.format( "%d", nRecordingQuality ) );
                datmIntent.putExtra( "answer_mode", String.format( "%d", nAnswerMode ) );
                datmIntent.putExtra( "remote_pin", "AAAA" );
                main_activity.sendBroadcast( datmIntent );
                Toast.makeText( main_activity, "request set DTAM setting : send", Toast.LENGTH_SHORT ).show();
            }
            public void onRecv( String data, String data2 ) {
                if( data.equals( "SUCCESS" ) ) {
                    // 成功
                }
                else {
                    // 失败
                }

				Toast.makeText( main_activity, "request set DTAM setting : successfully", Toast.LENGTH_LONG ).show();
            }
            public void onFailure() {
				Toast.makeText( main_activity, "request set DTAM setting : failure", Toast.LENGTH_LONG ).show();
            }
        });
    }

    public Boolean getOutgoingIsUseDefaultMessage() {
        return bOutgoingIsUseDefaultMessage;
    }

    public void setOutgoingIsUseDefaultMessage( Boolean bUseDefaultMessage ) {
        bOutgoingIsUseDefaultMessage = bUseDefaultMessage;
    }

    public boolean getIsFirstTimeUseRemoteAccess() {
        return bIsFirstTimeUseRemoteAccess;
    }

    public void setIsFirstTimeUseRemoteAccess( boolean bIsFirstTime ) {
        bIsFirstTimeUseRemoteAccess = bIsFirstTime;

        main_activity.saveSharedPreferences();
    }

    public Boolean getIsUseRemoteAccess() {
        return bIsUseRemoteAccess;
    }

    public void setIsUseRemoteAccess( Boolean bIsUse ) {
        bIsUseRemoteAccess = bIsUse;

        setDTAMSetting();
    }

    public Integer getAnswerDelayType() {
        return nAnswerDelayType;
    }

    public void setAnswerDelayType( Integer nDelayType ) {
        nAnswerDelayType = nDelayType;

        setDTAMSetting();
    }

    public Integer getRecordingQuality() {
        return nRecordingQuality;
    }

    public void setRecordingQuality( Integer nQuality ) {
        nRecordingQuality = nQuality;

        setDTAMSetting();
    }

    public Integer getAnswerMode() {
        return nAnswerMode;
    }

    public void setAnswerMode( Integer nMode ) {
        nAnswerMode = nMode;

        setDTAMSetting();
    }
}
