package com.yy2039.answermachine;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field; 
import android.view.View;
import android.widget.TextView;
import android.widget.ListView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;
import android.text.Html.ImageGetter;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.util.Log;

public class YYViewBase {
    ////////////////////////////////////////////////////////////////////////////////////
    public interface onViewBackHandler {
        void onBack();
    }

    public static final List<onViewBackHandler> views_stack = new ArrayList<onViewBackHandler>();

    ////////////////////////////////////////////////////////////////////////////////////
    public interface onUpdateTextHandler {
        SpannableString getText();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    protected AnswerMachineActivity main_activity;
    protected YYViewBase yy_view_self;
    protected Map<String,Object> yy_temp_data = new HashMap<String,Object>();
    protected int view_layout_res_id;
    protected YYListAdapter yy_list_adapter = null;
    protected Integer yy_scrolled_x = null;
    protected Integer yy_scrolled_y = null;
    protected onViewBackHandler vb_handler;             // 返回到自己界面
    public boolean bQuitPause = false;

    public YYViewBase() {
        main_activity = AnswerMachineActivity.main_activity;
        yy_view_self = this;
    }

    public void setView( boolean bIsPush, onViewBackHandler handler ) {
        if( bIsPush ) {
            if( handler != null ) {
                views_stack.add( handler );
            }
        }

        main_activity.setContentView( view_layout_res_id );

        // set title
        TextView tv = (TextView)main_activity.findViewById( R.id.title_text );
        tv.setText( getViewTitle() );

        main_activity.yy_current_view = this;
    }

    public String getViewTitle() {
        return "";
    }

    public void fillListView() {
        final ListView lv = (ListView)main_activity.findViewById( R.id.item_list );
        lv.setOnScrollListener( new OnScrollListener() {
            /**
             * 滚动状态改变时调用
             */
            @Override
            public void onScrollStateChanged( AbsListView view, int scrollState ) {
                // 不滚动时保存当前滚动到的位置
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    /*
                    yy_scrolled_x = view.getScrollX();   
                    yy_scrolled_y = view.getScrollY();
                    Log.v( "sdk", "end yy_scrolled_x : " + yy_scrolled_x );
                    Log.v( "sdk", "end yy_scrolled_y : " + yy_scrolled_y );
                    View c = lv.getChildAt( 0 );
                    if( c == null ) {
                        yy_scrolled_x = 0;
                        yy_scrolled_y = 0;
                    } else {
                        yy_scrolled_x = 0;

                        int firstVisiblePosition = lv.getFirstVisiblePosition();
                        int top = c.getTop();
                        yy_scrolled_y = top - firstVisiblePosition * c.getHeight() ;
                    }
                    */
                    yy_scrolled_x = lv.getFirstVisiblePosition();
                    yy_scrolled_y = lv.getFirstVisiblePosition();
                }
            }

            /**
             * 滚动时调用
             */
            @Override
            public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount ) {
            }
        });

        yy_list_adapter = new YYListAdapter( main_activity, R.layout.listview_item_image_button, getItemListData() );
        lv.setAdapter( yy_list_adapter );

        //Log.v( "sdk", "set view yy_scrolled_x : " + yy_scrolled_x );
        //Log.v( "sdk", "set view yy_scrolled_y : " + yy_scrolled_y );
        if( yy_scrolled_x != null && yy_scrolled_y != null ) {
            //lv.scrollTo( yy_scrolled_x, yy_scrolled_y );
            lv.setSelection( yy_scrolled_x );
        }
    }

    public void resetLastPosition() {
        //Log.v( "sdk", "resetLastPosition" );
        yy_scrolled_x = null;
        yy_scrolled_y = null;
    }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        return null;
    }

    // 返回到自己界面
    public onViewBackHandler getViewBackHandler() {
        if( vb_handler == null )
        {
            vb_handler = new onViewBackHandler() {
                public void onBack() {
                    main_activity.yy_schedule.cancelAllSchedule();
                    yy_view_self.setView( false, null );
                }
            };
        }

        return vb_handler;
    }

    public static void onBackClick() {
        if( views_stack.size() == 0 )
        {
            AnswerMachineActivity.main_activity.finish();
        }
        else
        {
            onViewBackHandler vb_handler = views_stack.get( views_stack.size() - 1 );
            views_stack.remove( views_stack.size() - 1 );

            vb_handler.onBack();
        }
    }

    public static int getResourceId( String name ) {
        try {
            // 根据资源的ID的变量名获得Field的对象,使用反射机制来实现的  
            Field field = R.drawable.class.getField( name );
            // 取得并返回资源的id的字段(静态变量)的值，使用反射机制  
            return Integer.parseInt( field.get( null ).toString() );
        } catch (Exception e) {
            // TODO: handle exception
        }
        return 0;
    }

    public static SpannableString transferText( String text1, String text2 ) {
        String text = text1 + "\r\n" + text2;

        SpannableString msp = new SpannableString( text );
        msp.setSpan( new ForegroundColorSpan( Color.BLACK ), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        msp.setSpan( new ForegroundColorSpan( Color.GRAY ), text1.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

        return msp;
    }

    public static CharSequence transferText( String text ) {
        CharSequence charSequence = Html.fromHtml( text, new ImageGetter() {
            @Override
            public Drawable getDrawable( String source ) {
                Drawable drawable = AnswerMachineActivity.main_activity.getResources().getDrawable( getResourceId( source ) );
                drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight() );
                return drawable;
            }  
        }, null );
        
        return charSequence;
    }
}
