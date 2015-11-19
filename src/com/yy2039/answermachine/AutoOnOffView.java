package com.yy2039.answermachine_1;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import android.text.SpannableString;
import android.view.View;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import java.util.Calendar;

public class AutoOnOffView extends YYViewBackList implements TimePickerDialog.OnTimeSetListener {
    public static final String TIMEPICKER_TAG = "timepicker";
    public TimePickerDialog timePickerDialog;
    private boolean is_pick_start_time;
    private boolean bIsInitSwitchBtnState;

    AutoOnOffView() {
        is_pick_start_time = true;

        Calendar calendar = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance( this, calendar.get( Calendar.HOUR_OF_DAY ) ,calendar.get( Calendar.MINUTE ), false, false );

        TimePickerDialog tpd = (TimePickerDialog)main_activity.getSupportFragmentManager().findFragmentByTag( TIMEPICKER_TAG );
        if( tpd != null )
            tpd.setOnTimeSetListener( this );
    }

    public String getViewTitle() { return "Auto on/off"; }

    public void initItemData( List<Map<Integer,YYListAdapter.onYYListItemHandler>> item_list_data, final onUpdateTextHandler update_text_handler, final View.OnClickListener click_listener ) {
        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( update_text_handler.getText() );
                btn_obj.setOnClickListener( click_listener );
            }
        });

        item_list_data.add( map );
    }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // off / on once / Daily / Monday-Friday / Saturday / Sunday
        initItemData( ret_data, getUpdateTextHandler_DateTime(), new View.OnClickListener() {
            public void onClick( View v ) {
                List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "Off"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "date_time_type", YYDataSource.DATE_TIME_TYPE_OFF ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDateTimeType() == YYDataSource.DATE_TIME_TYPE_OFF; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "On once"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "date_time_type", YYDataSource.DATE_TIME_TYPE_ON_ONCE ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDateTimeType() == YYDataSource.DATE_TIME_TYPE_ON_ONCE; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "Daily"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "date_time_type", YYDataSource.DATE_TIME_TYPE_DAILY ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDateTimeType() == YYDataSource.DATE_TIME_TYPE_DAILY; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "Monday-Friday"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "date_time_type", YYDataSource.DATE_TIME_TYPE_MONDAY_FRIDAY ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDateTimeType() == YYDataSource.DATE_TIME_TYPE_MONDAY_FRIDAY; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "Saturday"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "date_time_type", YYDataSource.DATE_TIME_TYPE_SATURDAY ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDateTimeType() == YYDataSource.DATE_TIME_TYPE_SATURDAY; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "Sunday"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "date_time_type", YYDataSource.DATE_TIME_TYPE_SUNDAY ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDateTimeType() == YYDataSource.DATE_TIME_TYPE_SUNDAY; }
                });

                main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Auto on/off", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                    public void onOK() {
                        Integer nCurSel = (Integer)yy_view_self.yy_temp_data.get( "date_time_type" );
                        if( nCurSel != null ) {
                            main_activity.yy_data_source.setDateTimeType( nCurSel );

                            main_activity.yy_data_source.updateAutoOnOffDataTime( new YYDataSource.onTreatMsgLinstener() {
                                public void onSuccessfully() {
                                }
                                public void onFailure() {
                                    Toast.makeText( main_activity, "update auto on/off status failed", Toast.LENGTH_LONG ).show();
                                }
                            });

                            yy_view_self.yy_list_adapter.list_data = yy_view_self.getItemListData();

                            YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                            task.execute();
                        }
                    }
                    public void onCancel() { }
                });
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if( main_activity.yy_data_source.getDateTimeType() != YYDataSource.DATE_TIME_TYPE_OFF )
        {
            initItemData( ret_data, getUpdateTextHandler_OnTime(), new View.OnClickListener() {
                public void onClick( View v ) {
                    is_pick_start_time = true;

                    timePickerDialog.setVibrate( true );
                    timePickerDialog.setCloseOnSingleTapMinute( false );
                    timePickerDialog.show( main_activity.getSupportFragmentManager(), TIMEPICKER_TAG );
                }
            });
            initItemData( ret_data, getUpdateTextHandler_OffTime(), new View.OnClickListener() {
                public void onClick( View v ) {
                    is_pick_start_time = false;

                    timePickerDialog.setVibrate( true );
                    timePickerDialog.setCloseOnSingleTapMinute( false );
                    timePickerDialog.show( main_activity.getSupportFragmentManager(), TIMEPICKER_TAG );
                }
            });
        }

        return ret_data;
    }

    public onUpdateTextHandler getUpdateTextHandler_DateTime() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "on/off";
                String text2 = "Off";
                switch( main_activity.yy_data_source.getDateTimeType() )
                {
                    case YYDataSource.DATE_TIME_TYPE_OFF:
                        text2 = "Off";
                        break;
                    case YYDataSource.DATE_TIME_TYPE_ON_ONCE:
                        text2 = "On once";
                        break;
                    case YYDataSource.DATE_TIME_TYPE_DAILY:
                        text2 = "Daily";
                        break;
                    case YYDataSource.DATE_TIME_TYPE_MONDAY_FRIDAY:
                        text2 = "Monday-Friday";
                        break;
                    case YYDataSource.DATE_TIME_TYPE_SATURDAY:
                        text2 = "Saturday";
                        break;
                    case YYDataSource.DATE_TIME_TYPE_SUNDAY:
                        text2 = "Sunday";
                        break;
                }

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_OnTime() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "On time";
                String text2 = String.format( "%02d:%02d", main_activity.yy_data_source.getAutoOnHour(), main_activity.yy_data_source.getAutoOnMinue() );

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_OffTime() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "Off time";
                String text2 = String.format( "%02d:%02d", main_activity.yy_data_source.getAutoOffHour(), main_activity.yy_data_source.getAutoOffMinue() );

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    @Override
    public void onTimeSet( RadialPickerLayout view, int hourOfDay, int minute ) {
        if( is_pick_start_time ) {
            main_activity.yy_data_source.setAutoOnHour( hourOfDay );
            main_activity.yy_data_source.setAutoOnMinue( minute );
        }
        else {
            main_activity.yy_data_source.setAutoOffHour( hourOfDay );
            main_activity.yy_data_source.setAutoOffMinue( minute );
        }

        main_activity.yy_data_source.updateAutoOnOffDataTime( new YYDataSource.onTreatMsgLinstener() {
            public void onSuccessfully() {
            }
            public void onFailure() {
                Toast.makeText( main_activity, "update auto on/off status failed", Toast.LENGTH_LONG ).show();
            }
        });

        // list view
        yy_list_adapter.list_data = getItemListData();

        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
        task.execute();
    }
}
