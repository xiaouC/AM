package com.yy2039.answermachine;

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
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.provider.ContactsContract;

public class YYDataSource {
    private Boolean bOutgoingIsUseDefaultMessage0;
    private Boolean bOutgoingIsUseDefaultMessage1;

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
        String getPhoneBookName();
        String getMsgNumber();
        String getMsgDateTime();
    }
    public List<onMsgInfo> msg_list = new ArrayList<onMsgInfo>();
    private int nMsgCount;
    private int nNewMsgCount;

    private AnswerMachineActivity main_activity;
    public YYDataSource( AnswerMachineActivity activity ) {
        main_activity = activity;

        bOutgoingIsUseDefaultMessage0 = true;
        bOutgoingIsUseDefaultMessage1 = true;

        bIsUseRemoteAccess = false;
        nAnswerDelayType = YYCommon.ANSWER_DELAY_2_RINGS;
        nRecordingQuality = YYCommon.RECORDING_QUALITY_HIGH;
        nAnswerMode = YYCommon.ANSWER_MODE_ANSWER_AND_RECORD;
    }

    public int getMessageCount() {
        return nMsgCount;
    }

    public void setMessageCount( int nCount ) {
        nMsgCount = nCount;
    }

    public int getNewMessageCount() {
        return nNewMsgCount;
    }

    public void setNewMessageCount( int nCount ) {
        nNewMsgCount = nCount;
    }

    public interface onTreatMsgLinstener {
        void onSuccessfully();
        void onFailure();
    }

    public void refreshMessageCount( final onTreatMsgLinstener msg_lisenter ) {
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_GDES_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent gdesIntent = new Intent( YYCommand.CALL_GUARDIAN_GDES );
                gdesIntent.putExtra( "data", "03" );
                main_activity.sendBroadcast( gdesIntent );
                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : send" );
            }
            public void onRecv( String data, String data2 ) {
                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : recv data : " + data );
                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : recv data2 : " + data2 );
                if( data == null ) {
                    String text = String.format( "%s recv : null", YYCommand.CALL_GUARDIAN_GDES_RESULT );
                    //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                }
                else {
                    String[] results = data.split( "," );
                    if( results.length < 2 ) {
                        String text = String.format( "%s recv data error : %s", YYCommand.CALL_GUARDIAN_GDES_RESULT, data );
                        //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                    }
                    else {
                        try {
                            nMsgCount = Integer.valueOf( results[0] );
                            nNewMsgCount = Integer.valueOf( results[1] );

                            msg_lisenter.onSuccessfully();
                        } catch ( Exception e ) {
                            String text = String.format( "%s recv data error : %s", YYCommand.CALL_GUARDIAN_GDES_RESULT, data );
                            //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                        }
                    }
                }
            }
            public void onFailure() {
                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : failed " );
                String text = String.format( "%s recv failed", YYCommand.CALL_GUARDIAN_GDES_RESULT );
                //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                msg_lisenter.onFailure();
            }
        });

        //main_activity.yy_command.executeCommand( YYCommand.PAGE_MSG_COUNT_RESULT, new YYCommand.onCommandListener() {
        //    public void onSend() {
        //        main_activity.yy_show_alert_dialog.showWaitingAlertDialog();
        //        main_activity.sendBroadcast( new Intent( YYCommand.COMMAD_PAGE_MSG_COUNT ) );
        //    }
        //    public void onRecv( String data, String data2 ) {
        //        if( data == null ) {
        //            String text = String.format( "%s recv : null", YYCommand.PAGE_MSG_COUNT_RESULT );
        //            Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
        //        }
        //        else {
        //            String[] results = data.split( "," );
        //            if( results.length < 2 ) {
        //                String text = String.format( "%s recv data error : %s", YYCommand.PAGE_MSG_COUNT_RESULT, data );
        //                Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
        //            }
        //            else {
        //                nMsgCount = Integer.valueOf( results[0] );
        //                nNewMsgCount = Integer.valueOf( results[1] );

        //                msg_lisenter.onSuccessfully();
        //                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
        //            }
        //        }
        //    }
        //    public void onFailure() {
        //        msg_lisenter.onFailure();
        //        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
        //    }
        //});
    }

    public void removeLocalMessageFromList( int nIndex ) {
        msg_list.remove( nIndex );
    }

    private String valid_chars = new String( "0123456789PR*#" );
    private boolean isValidNumber( char ch ) {
        for( int i=0; i < valid_chars.length(); ++i ) {
            if( ch == valid_chars.charAt( i ) ) {
                return true;
            }
        }
        return false;
    }

    public interface contactsListItem {
        String getName();
        List<String> getNumber();
    }

    public List<contactsListItem> contacts_list = null;
    public List<contactsListItem> getContactsList() {
        Map<String,List<String>> name_number_list = new HashMap<String,List<String>>();

        List<String> sortList = new ArrayList<String>();

        Cursor cursor = null;
        try {
            cursor = main_activity.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null );
            while( cursor.moveToNext() ) {
                String displayName = cursor.getString( cursor.getColumnIndex( ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME ) );
                String number = cursor.getString( cursor.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER ) );
                String new_number = number.replace(" ", "").replace("-", "").replace("(", "").replace(")", "");

                //String new_number = "";
                //for( int i=0; i < number.length(); ++i ) {
                //    if( isValidNumber( number.charAt( i ) ) ) {
                //        new_number += number.charAt( i );
                //    }
                //}

                List<String> number_list = name_number_list.get( displayName );
                if( number_list == null ) {
                    name_number_list.put( displayName, new ArrayList<String>() );
                    number_list = name_number_list.get( displayName );

                    sortList.add( displayName );
                }
                number_list.add( new_number );
            }
        } catch ( Exception e ) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if( cursor != null ){
                cursor.close();
            }
        }

        List<contactsListItem> ret_contacts_list = new ArrayList<contactsListItem>();

        for( int i=0; i < sortList.size(); ++i ) {
            final String displayName = sortList.get( i );
            final List<String> numbers = name_number_list.get( displayName );

            ret_contacts_list.add( new contactsListItem() {
                public String getName() { return displayName; }
                public List<String> getNumber() { return numbers; }
            });
        }

        //for( Map.Entry<String,List<String>> item_entry : name_number_list.entrySet() ) {
        //    final String displayName = item_entry.getKey();
        //    final List<String> numbers = item_entry.getValue();

        //    ret_contacts_list.add( new contactsListItem() {
        //        public String getName() { return displayName; }
        //        public List<String> getNumber() { return numbers; }
        //    });
        //}

        return ret_contacts_list;
    }
















    public String getNameByNumber( String number )
    {
        Log.v( "cocos", "getNameByNumber number 1 : " + number );
        String name = "";

        number = number.replace(" ", "").replace("-", "").replace("(", "").replace(")", "");		
        Log.v( "cocos", "getNameByNumber number 2 : " + number );
        name = getContactNameByPhoneNumber( number, 0 );
        Log.v( "cocos", "getNameByNumber name 1 : " + name );
        if( TextUtils.isEmpty( name ) ) {
            if( number.length() >= 11 ) {
                String number_11 = number.substring( number.length() - 11, number.length() );
                name = getContactNameByPhoneNumber( number_11, 1 );
                Log.v( "cocos", "getNameByNumber name 2 : " + name );
                if( TextUtils.isEmpty( name ) ) {
                    String number_6 = number.substring( number.length() - 6, number.length() );
                    name = getContactNameByPhoneNumber( number_6, 1 );
                    Log.v( "cocos", "getNameByNumber name 3 : " + name );
                }
            } else {
                if( number.length() >= 6 ) {
                    String number_6 = number.substring( number.length() - 6, number.length() );
                    name = getContactNameByPhoneNumber( number_6, 1 );
                    Log.v( "cocos", "getNameByNumber name 4 : " + name );
                }
            }
        }

        Log.v( "cocos", "getNameByNumber return name 5 : " + name );
        return name;
    }

    /*
    public String getContactNameByPhoneNumber( String num, int type )
    {
        Log.v( "cocos", "num : " + num );
        Log.v( "cocos", "type : " + type );
        String[] projection = { ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.PhoneLookup.STARRED };

        Cursor cursor = main_activity.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.NUMBER + " like '%" + num.charAt( num.length() - 1 ) + "'", null, ContactsContract.PhoneLookup.DISPLAY_NAME );
        if( cursor == null ) {
            Log.v( "cocos", "get People null" );
            return "";
        }

        String name = "";
        while( cursor.moveToNext() ) {
            int nameFieldColumnIndex = cursor.getColumnIndex( ContactsContract.PhoneLookup.DISPLAY_NAME );
            int numFieldColumnIndex = cursor.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER );
            String numString = cursor.getString( numFieldColumnIndex ).replace(" ", "").replace("-", "").replace("(", "").replace(")", "");
            Log.v( "cocos", "numString : " + numString );
            switch( type ) {
                case 0:
                    if( numString.equals(num) ) {
                        name = cursor.getString( nameFieldColumnIndex );
                    }
                    break;
                case 1:
                    if( numString.endsWith( num ) ) {
                        name = cursor.getString( nameFieldColumnIndex );
                    }
                    break;
                default:
                    break;
            }

            Log.v( "cocos", "name : " + name );
            if( !TextUtils.isEmpty( name ) ) {
                break;
            }
        }

        cursor.close();

        Log.v( "cocos", "return name : " + name );
        return name;
    }
    */


    public String getContactNameByPhoneNumber( String number, int type ) {
        Log.v( "cocos", "getContactNameByPhoneNumber number : " + number );
        Log.v( "cocos", "getContactNameByPhoneNumber type : " + type );

        String ret_name = "";

        for( int i=0; i < contacts_list.size(); ++i ) {
            List<String> num_list = contacts_list.get( i ).getNumber();
            for( int j=0; j < num_list.size(); ++j ) {
                String number_tmp = num_list.get( j );
                Log.v( "cocos", "getContactNameByPhoneNumber number_tmp : " + number_tmp );
                if( type == 0 ) {
                    if( number_tmp.equals( number ) ) {
                        ret_name = contacts_list.get( i ).getName();
                        Log.v( "cocos", "getContactNameByPhoneNumber ret_name 0 : " + ret_name );
                    }
                } else {
                    if( number_tmp.endsWith( number ) ) {
                        ret_name = contacts_list.get( i ).getName();
                        Log.v( "cocos", "getContactNameByPhoneNumber ret_name 1 : " + ret_name );
                    }
                }

                if( !TextUtils.isEmpty( ret_name ) ) {
                    Log.v( "cocos", "getContactNameByPhoneNumber ret_name 3 : " + ret_name );
                    return ret_name;
                }
            }
        }

        Log.v( "cocos", "getContactNameByPhoneNumber ret_name 4 : " + ret_name );
        return ret_name;
    }































    public String getMessageName( String number, String name ) {
        Log.v( "cocos", "getMessageName number : " + number );
        Log.v( "cocos", "getMessageName name : " + name );
        String ret_name = getNameByNumber( number );
        Log.v( "cocos", "getMessageName ret_name : " + ret_name );
        if( !ret_name.equals( "" ) ) {
            return ret_name;
        }

        //for( int i=0; i < contacts_list.size(); ++i ) {
        //    List<String> num_list = contacts_list.get( i ).getNumber();
        //    for( int j=0; j < num_list.size(); ++j ) {
        //        if( number.equals( num_list.get( j ) ) ) {
        //            return contacts_list.get( i ).getName();
        //        }
        //    }
        //}

        return "";
    }

    public final static Integer TREAT_MSG_OPERATION_PLAY = 0;
    public final static Integer TREAT_MSG_OPERATION_DELETE_ONE = 1;
    public final static Integer TREAT_MSG_OPERATION_DELETE_ALL = 2;
    public final static Integer TREAT_MSG_OPERATION_STOP_PLAY = 3;
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
                if( data != null && data.equals( "SUCCESS" ) ) {
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

    public void treatMsg_test( final int nOpType, final String nIndex, final onTreatMsgLinstener treat_msg_linstener ) {
        main_activity.yy_command.executeAnswerMachineCommandEx( YYCommand.ANSWER_MACHINE_CONM_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent msgIntent = new Intent( YYCommand.ANSWER_MACHINE_CONM );
                msgIntent.putExtra( "operation", String.format( "%d", nOpType ) );
                msgIntent.putExtra( "index", nIndex );
                main_activity.sendBroadcast( msgIntent );
            }
            public void onRecv( String data, String data2 ) {
                Log.v( "cconn", "treatMsg data : " + data );
                Log.v( "cconn", "treatMsg data2 : " + data2 );
                Log.v( "prot", "treatMsg data : " + data );
                Log.v( "prot", "treatMsg data2 : " + data2 );
                if( data != null && data.equals( "SUCCESS" ) ) {
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
    public void treatOutgoingMsg( final int nOpType, final int nMsgType, final onTreatMsgLinstener treat_msg_linstener ) {
        main_activity.yy_command.executeAnswerMachineCommand( YYCommand.ANSWER_MACHINE_COOM_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent msgIntent = new Intent( YYCommand.ANSWER_MACHINE_COOM );
                msgIntent.putExtra( "operation", String.format( "%d", nOpType ) );
                msgIntent.putExtra( "type", String.format( "%d", nMsgType ) );
                main_activity.sendBroadcast( msgIntent );
            }
            public void onRecv( String data, String data2 ) {
                if( data != null && data.equals( "SUCCESS" ) ) {
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

    public void getDTAMSetting( final onTreatMsgLinstener treat_msg_linstener ) {
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.ANSWER_MACHINE_GDTS_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( YYCommand.ANSWER_MACHINE_GDTS ) );
                Log.v( "cconn", "ANSWER_MACHINE_GDTS_RESULT send" );
            }
            public void onRecv( String data, String data2 ) {
                if( data == null ) {
                    String text = String.format( "%s recv : null", YYCommand.ANSWER_MACHINE_GDTS_RESULT );
                    //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                }
                else {
                    String[] results = data.split( "," );
                    if( results.length < 4 ) {
                        String text = String.format( "%s recv data error : %s", YYCommand.ANSWER_MACHINE_GDTS_RESULT, data );
                        //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                    }
                    else {
                        try {
                            bIsUseRemoteAccess = ( Integer.valueOf( results[0] ) == 0 ? true : false ); // 0 : on, 1 : off
                            nAnswerDelayType = Integer.valueOf( results[1] );
                            nRecordingQuality = Integer.valueOf( results[2] );
                            nAnswerMode = Integer.valueOf( results[3] );

                            treat_msg_linstener.onSuccessfully();
                        } catch ( Exception e ) {
                            String text = String.format( "%s recv data error : %s", YYCommand.ANSWER_MACHINE_GDTS_RESULT, data );
                            //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                        }
                    }
                }

                Log.v( "cconn", "ANSWER_MACHINE_GDTS_RESULT success" );
            }
            public void onFailure() {
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
            }
            public void onRecv( String data, String data2 ) {
                if( data != null && data.equals( "SUCCESS" ) ) {
                    // 成功
                }
                else {
                    // 失败
                    //Toast.makeText( main_activity, "update answer machine settings failed", Toast.LENGTH_LONG ).show();
                }
            }
            public void onFailure() {
				//Toast.makeText( main_activity, "update answer machine settings failed", Toast.LENGTH_LONG ).show();
            }
        });
    }

    public Boolean getOutgoingIsUseDefaultMessage( int nType ) {
        if( nType == 0 )
            return bOutgoingIsUseDefaultMessage0;

        return bOutgoingIsUseDefaultMessage1;
    }

    public void initOutgoingIsUseDefaultMessage0( Boolean bUseDefaultMessage ) {
        bOutgoingIsUseDefaultMessage0 = bUseDefaultMessage;
    }

    public void initOutgoingIsUseDefaultMessage1( Boolean bUseDefaultMessage ) {
        bOutgoingIsUseDefaultMessage1 = bUseDefaultMessage;
    }

    public void setOutgoingIsUseDefaultMessage( final int nType, final Boolean bUseDefaultMessage ) {
        if( nType == 0 ) {
            bOutgoingIsUseDefaultMessage0 = bUseDefaultMessage;
        } else {
            bOutgoingIsUseDefaultMessage1 = bUseDefaultMessage;
        }

        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.ANSWER_MACHINE_SDMS_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent dmIntent = new Intent( YYCommand.ANSWER_MACHINE_SDMS );
                dmIntent.putExtra( "status", bUseDefaultMessage ? "0" : "1" );
                dmIntent.putExtra( "type", String.format( "%d", nType ) );
                main_activity.sendBroadcast( dmIntent );
            }
            public void onRecv( String data, String data2 ) {
                if( data != null && data.equals( "SUCCESS" ) ) {
                    // 成功
                }
                else {
                    // 失败
                    //Toast.makeText( main_activity, "operation failed", Toast.LENGTH_LONG ).show();
                }
            }
            public void onFailure() {
				//Toast.makeText( main_activity, "operation failed", Toast.LENGTH_LONG ).show();
            }
        });
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

    public final static int DATE_TIME_TYPE_OFF = 0;
    public final static int DATE_TIME_TYPE_ON_ONCE = 1;
    public final static int DATE_TIME_TYPE_DAILY = 2;
    public final static int DATE_TIME_TYPE_MONDAY_FRIDAY = 3;
    public final static int DATE_TIME_TYPE_SATURDAY = 4;
    public final static int DATE_TIME_TYPE_SUNDAY = 5;

    private int nDateTimeType = DATE_TIME_TYPE_OFF;
    public int getDateTimeType() {
        return nDateTimeType;
    }

    public void setDateTimeType( int nType ) {
        nDateTimeType = nType;
    }

    public void initDateTimeType( int nType ) {
        nDateTimeType = nType;
    }

    private int nOnHour = 22;
    private int nOnMinue = 0;
    private int nOffHour = 22;
    private int nOffMinue = 0;
    public int getAutoOnHour() {
        return nOnHour;
    }
    public int getAutoOnMinue() {
        return nOnMinue;
    }
    public void setAutoOnHour( int nHour ) {
        nOnHour = nHour;
    }
    public void setAutoOnMinue( int nMinue ) {
        nOnMinue = nMinue;
    }
    public int getAutoOffHour() {
        return nOffHour;
    }
    public int getAutoOffMinue() {
        return nOffMinue;
    }
    public void setAutoOffHour( int nHour ) {
        nOffHour = nHour;
    }
    public void setAutoOffMinue( int nMinue ) {
        nOffMinue = nMinue;
    }
    public void initAutoOnTime( int nHour, int nMinue ) {
        nOnHour = nHour;
        nOnMinue = nMinue;
    }

    public void initAutoOffTime( int nHour, int nMinue ) {
        nOffHour = nHour;
        nOffMinue = nMinue;
    }

    public void updateAutoOnOffDataTime( final onTreatMsgLinstener update_linstener ) {
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.ANSWER_MACHINE_SATS_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent banbIntent = new Intent( YYCommand.ANSWER_MACHINE_SATS );
                banbIntent.putExtra( "status", String.format( "%d", nDateTimeType ) );
                banbIntent.putExtra( "on_off_time", String.format( "%02d%02d%02d%02d", nOnHour, nOnMinue, nOffHour, nOffMinue ) );
                main_activity.sendBroadcast( banbIntent );
                Log.v( "cconn", "ANSWER_MACHINE_SATS send" );
            }
            public void onRecv( String data, String data2 ) {
                Log.v( "cconn", "ANSWER_MACHINE_GATS : recv data : " + data );
                Log.v( "cconn", "ANSWER_MACHINE_GATS : recv data2 : " + data2 );
                if( data == null || !data.equals( "SUCCESS" ) ) {
                    update_linstener.onFailure();
                }
                else {
                    update_linstener.onSuccessfully();
                }
            }
            public void onFailure() {
                Log.v( "cconn", "ANSWER_MACHINE_GATS failed" );
                update_linstener.onFailure();
            }
        });
    }
}
