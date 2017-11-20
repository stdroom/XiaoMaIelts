/**
 * 工程名: xiaomayasi
 * 文件名: ShadowBuilder.java
 * 包名: com.xiaoma.ielts.android.common.weight.stepthird
 * 日期: 2015年9月28日上午9:57:41
 * QQ: 378640336
 *
*/

package com.xiaoma.ielts.android.widget.stepthird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 类名: ShadowBuilder <br/>
 * 功能:  创建拖拽阴影. <br/>
 * 日期: 2015年9月28日 上午9:57:41 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class ShadowBuilder extends View.DragShadowBuilder{
	
	 // 拖动阴影的图像， 作为一个drawable来定义
    private static Drawable shadow;
    // 构造函数
    public ShadowBuilder(View v, Bitmap bmp, Context context) {
        // 通过myDragShadowBuilder存储View参数
        super(v);
        // 创建一个可拖拽的图像，此图像可以通过系统的Canvas来填充
        shadow = new BitmapDrawable(context.getResources(),bmp);
    }

    // 定义一个回调方法，将阴影的维度和触摸点返回给系统
    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        // 定义当地的变量
        int width;
        int height;
        // 设置阴影的宽度为视图一半
        width = getView().getWidth();
        // 设置阴影的高度为视图一半
        height = getView().getHeight();
        // 拖拽阴影是一个ColorDrawable. 这个集合的维度和系统所提供的Canvas是一样的
        // 因此，拖拽阴影将会被Canvas覆盖
        shadow.setBounds(0, 0, width, height);
        // 设置参数宽度和高度的大小.通过大小参数返回给系统
        size.set(width, height);
        // 设置触摸点的位置为拖拽阴影的中心
        touch.set(width / 2, height / 2);
    }

    // 在画布Canvas中定义一个回调函数来绘制拖拽的阴影，该画布是通过方法onProvideShadowMetrics()提供的维度
    // 由系统构造
    @Override
    public void onDrawShadow(Canvas canvas) {
        // 在由系统传递的Canvas上绘制ColorDrawable
        shadow.draw(canvas);
    }
}

