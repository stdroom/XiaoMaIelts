package com.xiaoma.ielts.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.xiaoma.ielts.android.R;

import java.util.Hashtable;

/**
 * Created by leixun on 17/11/20.
 */

public class FlowLayout extends RelativeLayout {
    int mLeft, mRight, mTop, mBottom, currentBottom;
    Hashtable<View, Position> map = new Hashtable<View, FlowLayout.Position>();
    /*
     * 控件距离右边的距离
     */
    private int margin_right;
    /*
     * 控件距离上边的距离
     */
    private int margin_top;


    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
    }

    public FlowLayout(Context context) {
        super(context);
        initAttr(null);
    }

    private void initAttr(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        margin_top = (int) a.getDimensionPixelSize(R.styleable.FlowLayout_marginTop, 10);
        margin_right = (int) a.getDimensionPixelSize(R.styleable.FlowLayout_marginRight, 10);
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int maxHeigth = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            Position pos = map.get(child);
            if (null != pos) {
                if (pos.bottom - pos.top > maxHeigth) {
                    maxHeigth = pos.bottom - pos.top;
                }
            }

        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            Position pos = map.get(child);
            if (pos.bottom - pos.top < maxHeigth) {
                int Yoffset = (maxHeigth - (pos.bottom - pos.top) )/2;
                pos.top = pos.top + Yoffset;
                pos.bottom = pos.bottom + Yoffset;
                //map.put(key, value)
            }
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            Position pos = map.get(child);
            if (pos != null) {
                child.layout(pos.left, pos.top, pos.right, pos.bottom);
            } else {
            }
        }

    }

    public int getPosition(int IndexInRow, int childIndex) {
        if (IndexInRow > 0) {
            return getPosition(IndexInRow - 1, childIndex - 1)
                    + getChildAt(childIndex - 1).getMeasuredWidth()+margin_right;
        }
        return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        mLeft = 0;
        mRight = 0;
        mTop = 0;
        mBottom = 0;
        int j = 0;
        int maxHeigth = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            Position position = new Position();
            View view = getChildAt(i);
            mLeft = getPosition(i - j, i);
            mRight = mLeft + view.getMeasuredWidth();
            if (mRight >= width) {
                j = i;
                mLeft = getPosition(i - j, i);
                mRight = mLeft + view.getMeasuredWidth();
                mTop += getChildAt(i).getMeasuredHeight()+margin_top;
            }
            mBottom = mTop + view.getMeasuredHeight();
            position.left = mLeft;
            position.top = mTop;
            position.right = mRight;
            position.bottom = mBottom;
            position.yIndex = j;
            map.put(view, position);
        }
        setMeasuredDimension(width, mBottom);
    }

    private class Position {
        int left, top, right, bottom, yIndex;
    }

}