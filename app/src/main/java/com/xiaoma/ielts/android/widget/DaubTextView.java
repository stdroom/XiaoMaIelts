/**
 * 工程名: xiaomayasi
 * 文件名: DaubTextView.java
 * 包名: com.xiaoma.ielts.android.common.weight
 * 日期: 2015年9月21日上午11:53:03
 * QQ: 2920084022
 *
*/

package com.xiaoma.ielts.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 类名: DaubTextView <br/>
 * 功能: 可实现涂抹的TextView <br/>
 * 日期: 2015年9月21日 上午11:53:03 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class DaubTextView extends TextView {

	private float TOUCH_TOLERANCE;
	private Bitmap mBitmap;//盖在字上面的背景图
	private Canvas mCanvas;//用于在mBitmap上画线
	private Paint mPaint;//用来画线的
	private Path mPath;//线
	private float mX,mY;
	private boolean mRun;
	private boolean caculate;
	private Thread mThread;
	private onWipeListener mWipeListener;
	private boolean isInited = false;
	private int[] mPixels;

	public DaubTextView(Context context) {
		super(context);
		drawColor();
	}

	public DaubTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		drawColor();
	}

	public DaubTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		drawColor();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isInited) {
			mCanvas.drawPath(mPath, mPaint);//把线画到mCanvas上,mCanva会把线画到mBitmap
			canvas.drawBitmap(mBitmap, 0, 0, null);//把mBitmap画到textview上
		}
	}

	private void drawColor() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				initScratchCard(Color.RED,20, 1f);
			}
		},10);
	}
	/**
	 * 初始化刮刮卡
	 * @param bgColor 刮刮卡背景色，用于盖住下面的字
	 * @param paintStrokeWidth 擦除线宽
	 * @param touchTolerance 画线容差
	 */
	public void initScratchCard(final int bgColor,final int paintStrokeWidth,float touchTolerance) {
		TOUCH_TOLERANCE = touchTolerance;
		mPaint = new Paint();
		mPaint.setAlpha(0);//alpha值：00表示完全透明，ff表示完全不透明
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//
		mPaint.setAntiAlias(true);//抗锯齿
		mPaint.setDither(true);//防抖动
		mPaint.setStyle(Paint.Style.STROKE);//画笔类型： STROKE空心 FILL实心 FILL_AND_STROKE用契形填充
		mPaint.setStrokeJoin(Paint.Join.ROUND);//画笔接洽点类型
		mPaint.setStrokeCap(Paint.Cap.ROUND);//画笔笔刷类型
		mPaint.setStrokeWidth(paintStrokeWidth);//画笔笔刷宽度

		mPath =  new Path();

//		mBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Config.ARGB_8888);
//		mCanvas = new Canvas(mBitmap);
//		mCanvas.drawColor(bgColor);//绘制颜色块用于盖住下面的字
		coverWord();
		//		mCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(),//绘制图片块用于盖住下面的字
		//				bgColor), null, new RectF(0, 0, getWidth(), getHeight()),null);

		isInited = true;
		invalidate();
		mRun = true;
		mThread = new Thread(mRunnable);
		mThread.start();
	}
	
	private void coverWord() {
		CharSequence content = getText();
		int padingLeft = getPaddingLeft();
		int padingTop = getPaddingTop();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.parseColor("#dbdbdb"));;
		paint.setStyle(Style.FILL);
		
		mBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		
		int length = content.length();
		int wordBegin = -1;
		int wordEnd = -1;
		for (int i = 0; i < length; i++) {
			char cr = content.charAt(i);
			if(isCharater(cr)) {
				if (wordBegin == -1) {
					wordBegin = i;
				}
                if (i == length -1 && wordBegin > -1) {
                    wordEnd = i;
                    //到达句末的时候 覆盖到句号后面
                    cover(paint, wordBegin, wordEnd +1);
                    wordBegin = -1;
                    wordEnd = -1;
                }
			} else {
				if (wordBegin > -1) {
					wordEnd = i;
					Layout layout = getLayout();
					float wordleft =  layout.getPrimaryHorizontal(wordBegin) + getPaddingLeft();
					float wordrigth =  layout.getSecondaryHorizontal(wordEnd) + getPaddingLeft();
					int line = layout.getLineForOffset(wordEnd);
					int lineBotttom = layout.getLineBottom(line);
					float textSiz = getTextSize();
					RectF f = new RectF(wordleft -3, lineBotttom - textSiz - 2, wordrigth + 1, lineBotttom + 4);
					mCanvas.drawRoundRect(f,0,0, paint);
                    cover(paint, wordBegin, wordEnd);
					wordBegin = -1;
					wordEnd = -1;
				}
			}
		}
	}

    private void cover(Paint paint, int wordBegin, int wordEnd) {
        Layout layout = getLayout();
        float wordleft =  layout.getPrimaryHorizontal(wordBegin) + getPaddingLeft();
        float wordrigth =  layout.getSecondaryHorizontal(wordEnd) + getPaddingLeft();
        int line = layout.getLineForOffset(wordEnd);
        int lineBotttom = layout.getLineBottom(line);
        float textSiz = getTextSize();
        RectF f = new RectF(wordleft -3, lineBotttom - textSiz - 2, wordrigth + 1, lineBotttom + 3);
        mCanvas.drawRoundRect(f, 4F, 4F, paint);
    }

	private boolean isCharater(char input) {
		if ((input >= 97 && input <= 122) || (input >= 65 && input <= 90 || input == 39 || input == 44 || input == 46)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isInited) {
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchDown(event.getX(), event.getY());
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touchMove(event.getX(), event.getY());
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touchUp(event.getX(), event.getY());
			invalidate();
			break;
		}
		return true;
	}


	private void touchDown(float x,float y){
		caculate = false;
		mPath.reset();
		mPath.moveTo(x,y);
		mX = x;
		mY = y;
	}

	private void touchMove(float x,float y){
		caculate = false;
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
			mX = x;
			mY = y;
		}

	}

	private void touchUp(float x,float y){
		caculate = true;
		mPath.lineTo(x, y);
		mCanvas.drawPath(mPath, mPaint);
		mPath.reset();
	}

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {

			while (mRun) {

				SystemClock.sleep(100);

				// 收到计算命令，立即开始计算
				if (caculate) {

					caculate = false;

					int w = getWidth();
					int h = getHeight();

					float wipeArea = 0;
					float totalArea = w * h;

					// 计算耗时100毫秒左右
					Bitmap bitmap = mBitmap;

					if (mPixels == null) {
						mPixels = new int[w * h];
					}

					bitmap.getPixels(mPixels, 0, w, 0, 0, w, h);

					for (int i = 0; i < w; i++) {
						for (int j = 0; j < h; j++) {
							int index = i + j * w;
							if (mPixels[index] == 0) {
								wipeArea++;
							}
						}
					}

					if (wipeArea > 0 && totalArea > 0) {
						int percent = (int) (wipeArea * 100 / totalArea);
						Message msg = mHandler.obtainMessage();
						msg.what = 0x1;
						msg.arg1 = percent;
						mHandler.sendMessage(msg);
					}

				}

			}

		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (mWipeListener != null) {
				int percent = msg.arg1;
				mWipeListener.onWipe(percent);
				if(percent >= 80){  //判断如果刮开80%的区域自动清除其它的
					isInited = false;
					postInvalidate();
				}
			}

		};
	};

	public interface onWipeListener {
		public void onWipe(int percent);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mRun = false;
	}
	
	public void setOnWipeListener(onWipeListener listerer) {
		this.mWipeListener = listerer;
	}
	
}

