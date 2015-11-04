package com.yy2039.answermachine;

import android.view.View;
import android.widget.ImageButton;

public class YYViewBackList extends YYViewBack {
    public YYViewBackList() {
        view_layout_res_id = R.layout.title_back_listview;
    }

    public void setView( boolean bIsPush, onViewBackHandler handler ) {
        super.setView( bIsPush, handler );

        fillListView();
    }
}
