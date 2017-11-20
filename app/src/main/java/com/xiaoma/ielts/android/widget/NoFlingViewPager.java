package com.xiaoma.ielts.android.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoFlingViewPager extends ViewPager {

    private boolean isflingEnable = true;
    public NoFlingViewPager(Context context) {
        super(context);
    }

    public NoFlingViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isflingEnable && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isflingEnable && super.onInterceptTouchEvent(ev);
    }

    public boolean isflingEnable() {
        return isflingEnable;
    }

    public void setFlingEnable(boolean isflingEnable) {
        this.isflingEnable = isflingEnable;
    }

}
