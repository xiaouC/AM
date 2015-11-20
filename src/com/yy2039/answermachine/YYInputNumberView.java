package com.yy2039.answermachine;

import java.util.ArrayList;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;

public class YYInputNumberView extends YYViewBase {
    private YYInputNumberView yy_input_number;

    public interface onYYInputNumberHandler {
        void onSave( String number );
    }

    private String in_title;
    private ArrayList<Character> input_numbers = new ArrayList<Character>();

    public YYInputNumberView() {
        yy_input_number = this;
        view_layout_res_id = R.layout.input_number;
    }

    public void setView( boolean bIsPush, onViewBackHandler handler ) {
        super.setView( bIsPush, handler );

        ( (ImageButton)main_activity.findViewById( R.id.btn_number_1 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '1' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_2 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '2' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_3 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '3' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_4 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '4' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_5 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '5' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_6 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '6' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_7 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '7' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_8 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '8' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_9 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '9' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_s ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '*' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_0 ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '0' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_j ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ){ yy_input_number.appendNumber( '#' ); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.input_number_return_button ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ) { yy_input_number.onBackClick(); }
        });
        ( (ImageButton)main_activity.findViewById( R.id.btn_delete ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ) {
                if( yy_input_number.input_numbers.size() > 0 )
                {
                    yy_input_number.input_numbers.remove( yy_input_number.input_numbers.size() - 1 );
                    yy_input_number.updateInputNumberText();
                }
            }
        });

        ImageButton btn_cancel = (ImageButton)main_activity.findViewById( R.id.btn_number_cancel );
        if( btn_cancel != null ) {
            btn_cancel.setOnClickListener( new View.OnClickListener () {
                public void onClick( View v ) { yy_input_number.onBackClick(); }
            });
        }
    }

    public String getViewTitle() {
        return in_title;
    }

    public void showInputNumberView( String title, String content, onViewBackHandler handler, final onYYInputNumberHandler in_handler )
    {
        in_title = title;

        setView( true, handler );

        // save
        ( (ImageButton)main_activity.findViewById( R.id.btn_number_save ) ).setOnClickListener( new View.OnClickListener () {
            public void onClick( View v ) {
                String number_text = "";
                for( int i=0; i < yy_input_number.input_numbers.size(); ++i )
                    number_text = number_text + yy_input_number.input_numbers.get( i );

                yy_input_number.input_numbers.clear();
                yy_input_number.updateInputNumberText();

                in_handler.onSave( number_text );
            }
        });

        // 
        input_numbers.clear();
        for( int i=0; i < content.length(); ++i )
            input_numbers.add( content.charAt( i ) );

        updateInputNumberText();
    }

    public void updateInputNumberText()
    {
        String text = "";

        for( int i=0; i < input_numbers.size(); ++i )
            text = text + input_numbers.get( i );

        TextView view = (TextView)main_activity.findViewById( R.id.input_text );
        view.setText( text );
    }

    public void appendNumber( Character ch )
    {
        input_numbers.add( ch );
        updateInputNumberText();
    }
}
