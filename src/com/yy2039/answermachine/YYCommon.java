package com.yy2039.answermachine;

public class YYCommon {
    public final static Integer ANSWER_DELAY_2_RINGS =  0;
    public final static Integer ANSWER_DELAY_3_RINGS =  1;
    public final static Integer ANSWER_DELAY_4_RINGS =  2;
    public final static Integer ANSWER_DELAY_5_RINGS =  3;
    public final static Integer ANSWER_DELAY_6_RINGS =  4;
    public final static Integer ANSWER_DELAY_7_RINGS =  5;
    public final static Integer ANSWER_DELAY_8_RINGS =  6;
    public final static Integer ANSWER_DELAY_9_RINGS =  7;
    public final static Integer ANSWER_DELAY_10_RINGS =  8;
    public final static Integer ANSWER_DELAY_AMEND = 2;

    public final static Integer RECORDING_QUALITY_HIGH = 0;
    public final static Integer RECORDING_QUALITY_STANDARD = 1;

    public final static Integer ANSWER_MODE_ANSWER_AND_RECORD = 0;
    public final static Integer ANSWER_MODE_ANSWER_ONLY = 1;
    public final static Integer ANSWER_MODE_OFF = 2;

    // ListView 对应的 item 可能使用到的信息
    public class YYViewInfo {
        public YYViewInfo( int nObjectType, int nTextType ) {
            this.nObjectType = nObjectType;
            this.nTextType = nTextType;
        }

        int nObjectType;        // YY_VIEW_OBJECT_TYPE_BUTTON 等等
        int nTextType;          // YY_LIST_ADAPTER_UPDATE_TEXT_TYPE_STRING 等等
    }
}
