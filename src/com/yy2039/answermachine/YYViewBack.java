package com.yy2039.answermachine_1;

import android.view.View;
import android.widget.ImageButton;

public class YYViewBack extends YYViewBase {
    public YYViewBack() {
    }

    public void setView( boolean bIsPush, onViewBackHandler handler ) {
        super.setView( bIsPush, handler );

        ImageButton btn_obj = (ImageButton)main_activity.findViewById( R.id.back_button );
        btn_obj.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) { YYViewBase.onBackClick(); }
        });
    }
}
