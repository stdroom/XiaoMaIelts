/**
 * 工程名: xiaomayasi
 * 文件名: TextViewDisplay.java
 * 包名: com.xiaoma.ielts.android.common.weight.stepthird
 * 日期: 2015年9月28日上午9:58:41
 * QQ: 378640336
 *
*/

package com.xiaoma.ielts.android.widget.stepthird;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.xiaoma.ielts.android.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 类名: TextViewDisplay <br/>
 * 功能: TextView布局到相应的RelativeLayout容器中. <br/>
 * 日期: 2015年9月28日 上午9:58:41 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class TextViewDisplay {

	private int DELAYTIME = 200;

	// 屏幕宽度
	float viewContainerWidth;
	
	// 水平分割间隔
	int tvGapsWidth;
	// 垂直分割间隔
	int tvGapsHeight;
	
	Context mContext;
	
	RelativeLayout relativeLayout;
	
	ArrayList<TextView> copyTextView;
	
	Timer timer;
	
	TimerTask timerTask;
	
	private Handler myHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0x01:
				Bundle bundle = msg.getData();
				scrollTextView(relativeLayout,bundle.getInt("index"),bundle.getInt("size"),copyTextView);
				Message msg2 = new Message();
				msg2.what = 0x02;
				msg2.setData(bundle);
				myHandler.sendMessageDelayed(msg2, DELAYTIME);
				break;
			case 0x02:
				Bundle bundle2 = msg.getData();
				removeTextView(relativeLayout,bundle2.getInt("index"),bundle2.getInt("size"),copyTextView);
				break;
			}
		}
		
	};
	
	public ArrayList<TextView> displayAtRelativeLayout(RelativeLayout relativeLayout, ArrayList<TextView> tvs, Context context, int width){
		mContext = context;
		initDimens();
		viewContainerWidth = width;
		//
		int size = (tvs!=null ? tvs.size():0);
		for(int i = 0 ; i < size ;i ++){
			TextView tv = tvs.get(i);
			if(i == 0 && tv.getWidth() > viewContainerWidth){	// 第一个单词 就超出了范围  ---不应该这么个样子
				Log.d("","the params may be not right tv.getWidth:"+tv.getWidth()+" > viewContainerWidth:"+viewContainerWidth);
				break;
			}else if(i==0){	// 第一个单词直接塞进去
				MyRect(tv,tvGapsWidth,tvGapsWidth);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.leftMargin = tvGapsWidth;
				params.topMargin = tvGapsHeight;
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				tv.setLayoutParams(params);
				tv.setId(i+1);
				relativeLayout.addView(tv,i);
			}else{	// 后续单词 先判断是否超出容器宽度
				WordRect lastTv = (WordRect)tvs.get(i-1).getTag();
				WordRect nowRect = (WordRect)tvs.get(i).getTag();

				if(lastTv.getRight()+tvGapsWidth+nowRect.getWidth()  > viewContainerWidth){	// 超出容器范围
					MyRect(tv,tvGapsWidth,(int)lastTv.getBottom()+tvGapsHeight);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.leftMargin = 0;
					params.topMargin = tvGapsHeight;
					params.addRule(RelativeLayout.BELOW, relativeLayout.getChildAt(i-1).getId());
					params.addRule(RelativeLayout.ALIGN_LEFT, relativeLayout.getChildAt(0).getId());
					tv.setLayoutParams(params);
					tv.setId(i+1);
					relativeLayout.addView(tv, i);
				}else{
					MyRect(tv,(int)lastTv.getRight()+tvGapsWidth,(int)lastTv.getTop());
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.leftMargin = tvGapsWidth;
					params.topMargin = tvGapsHeight;
					params.addRule(RelativeLayout.ALIGN_BOTTOM, relativeLayout.getChildAt(i-1).getId());
					params.addRule(RelativeLayout.RIGHT_OF, relativeLayout.getChildAt(i-1).getId());
					tv.setId(i+1);
					tv.setLayoutParams(params);
					relativeLayout.addView(tv, i);
				}
			}
			tvs.set(i, tv);
		}

		return tvs;
	}

	private void initDimens(){
		tvGapsWidth = (int)mContext.getResources().getDimension(R.dimen.custom_textview_gaps_x);
		tvGapsHeight = (int)mContext.getResources().getDimension(R.dimen.custom_textview_gaps_y);
	}

	private  WordRect MyRect(TextView tv, int left, int top){
		if(tv.getTag()!=null && tv.getTag() instanceof WordRect){
			WordRect tag = (WordRect)tv.getTag();
			WordRect temp = new WordRect(tag.getWidth(), tag.getHeight());
			temp.setLeft(left);
			temp.setRight(left+tag.getWidth());
			temp.setTop(top);
			temp.setBottom(top+tag.getHeight());
			tv.setTag(temp);
			return (WordRect)tv.getTag();
		}else{
			Log.d("","");
			return null;
		}
	}

	/**
	 *
	 * refreshRelativeLayout:(删除某个childview,并平滑移动). <br/>
	 *
	 * 1 找到要删的view
	 * 2 复制后续的view
	 * 3 添加复制的view
 	 * 4 动画移动后续的view
	 * 5 删除复制的view
	 *
	 * @author leixun
	 * 2015年9月23日下午3:36:06
	 * @param relativeLayout
	 * @param removeTv
	 * @param context
	 * @param width
	 * @since 1.0
	 */
	@SuppressWarnings("deprecation")
	public void refreshRelativeLayout(RelativeLayout relativeLayout, TextView removeTv, Context context, int width){
		mContext = context;
		initDimens();
		viewContainerWidth = width;
		//
		int size = relativeLayout.getChildCount();
		int i = 0 ;
		//1 找到要删的view
		for(;i < size ;i++){
			if(relativeLayout.getChildAt(i) == removeTv){
				removeTv.setVisibility(View.INVISIBLE);
				break;
			}
		}
		int index = i;
		// 2 复制后续的view
		copyTextView = createCopyTextView(relativeLayout, index, size);
		this.relativeLayout = relativeLayout;
		// 4 移动后续的view
		translateTextView(relativeLayout, index, size);

		// 3 添加复制的view
		copyTextView(relativeLayout,index,size,copyTextView);


		// 4 动画移动复制的view
		Message msg = new Message();
		msg.what = 0x01;
		Bundle bundle = new Bundle();
		bundle.putInt("size", size);
		bundle.putInt("index", index);
		msg.setData(bundle);

		myHandler.sendMessageDelayed(msg, DELAYTIME);
		// 5删除复制的view
//		relativeLayout.removeViewAt(index);
//		for(int j = 0 ; j < copyTextView.size();j++){
//			for(int k = index ; k < relativeLayout.getChildCount();k++){
//				if(copyTextView.get(j) == relativeLayout.getChildAt(k)){
//					relativeLayout.removeViewAt(k);
//					break;
//				}
//			}
//		}
	}



	//复制后续的view
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) private ArrayList<TextView> createCopyTextView(RelativeLayout relativeLayout, int index, int size){
		ArrayList<TextView> copyTextView = new ArrayList<TextView>();

		for(int i = index+1;i< size;i++){
			TextView tv = (TextView)relativeLayout.getChildAt(i);
			TextView mTextView = new TextView(mContext);
			mTextView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT));
			mTextView.setText(tv.getText());
			mTextView.setTag(tv.getTag());
			mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_green));
			mTextView.setPadding(tv.getPaddingLeft(), tv.getPaddingTop(), tv.getPaddingRight(), tv.getPaddingBottom());
			mTextView.setGravity(Gravity.CENTER);
			if(Build.VERSION.SDK_INT >= 16){
				mTextView.setBackground(tv.getBackground());
			}else{
				mTextView.setBackgroundDrawable(tv.getBackground());
			}
			mTextView.setTextSize(20);
			mTextView.setVisibility(View.VISIBLE);
			copyTextView.add(mTextView);
		}
		return copyTextView;
	}


	// 原位复制
	private void copyTextView(RelativeLayout relativeLayout, int index, int size, ArrayList<TextView> copyTextView){
		boolean isFirstFlag = true;
		for(int i= index+1;i<size;i++){
			if(isFirstFlag){	// 先处理第一个单词
					WordRect lastTv = (WordRect)relativeLayout.findViewById(i).getTag();
					TextView tv = (TextView)copyTextView.get(i-index-1);
					WordRect nowRect = (WordRect)tv.getTag();
					if(lastTv.getRight()+tvGapsWidth+nowRect.getWidth()  > viewContainerWidth){	// 超出容器范围 则不进行移动
						MyRect(tv,tvGapsWidth,(int)lastTv.getBottom()+tvGapsHeight);
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						params.leftMargin = 0;
						params.topMargin = tvGapsHeight;
						params.addRule(RelativeLayout.BELOW, i);
						params.addRule(RelativeLayout.ALIGN_LEFT, 1);
						tv.setLayoutParams(params);
						tv.setId(i+size);
						relativeLayout.addView(tv,i+size-1-index);
						copyTextView.set(i-index-1, tv);
						// 不需要移动
					}else{
						MyRect(tv,(int)lastTv.getRight()+tvGapsWidth,(int)lastTv.getTop());
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						params.leftMargin = tvGapsWidth;
						params.topMargin = tvGapsHeight;
						params.addRule(RelativeLayout.ALIGN_BOTTOM, i);
						params.addRule(RelativeLayout.RIGHT_OF, i);
						tv.setLayoutParams(params);
						tv.setId(i+size);
						relativeLayout.addView(tv,i+size-1-index);
						copyTextView.set(i-index-1, tv);
					}
				isFirstFlag = false;
			}else{	// 后续单词 先判断是否超出容器宽度
				WordRect lastTv = (WordRect)copyTextView.get(i-index-2).getTag();
				TextView tv = (TextView)copyTextView.get(i-index-1);
				WordRect nowRect = (WordRect)tv.getTag();
				if(lastTv.getRight()+tvGapsWidth+nowRect.getWidth()  > viewContainerWidth){	// 超出容器范围
					MyRect(tv,tvGapsWidth,(int)lastTv.getBottom()+tvGapsHeight);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.leftMargin = 0;
					params.topMargin = tvGapsHeight;
					params.addRule(RelativeLayout.BELOW, copyTextView.get(i-index-2).getId());
					params.addRule(RelativeLayout.ALIGN_LEFT, 1);
					tv.setLayoutParams(params);
					tv.setId(i+size);
					relativeLayout.addView(tv,i+size-1-index);
					copyTextView.set(i-index-1, tv);
				}else{
					MyRect(tv,(int)lastTv.getRight()+tvGapsWidth,(int)lastTv.getTop());
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.leftMargin = tvGapsWidth;
					params.topMargin = tvGapsHeight;
					params.addRule(RelativeLayout.ALIGN_BOTTOM, copyTextView.get(i-index-2).getId());
					params.addRule(RelativeLayout.RIGHT_OF, copyTextView.get(i-index-2).getId());
					tv.setLayoutParams(params);
					tv.setId(i+size);
					relativeLayout.addView(tv,i+size-1-index);
					copyTextView.set(i-index-1, tv);
				}
			}
		}
	}

//	// 移位复制
//	private void addCopyTextView(RelativeLayout relativeLayout,int index,int size,ArrayList<TextView> copyTextView){
//		boolean isFirstFlag = true;
//		for(int i= index+1;i<size;i++){
//			if(isFirstFlag){	// 先处理第一个单词
//				if(i == 1){	// 左上角的话直接塞进去
//					TextView tv = (TextView)copyTextView.get(0);
//					MyRect(tv,tvGapsWidth,tvGapsWidth);
//					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//					params.leftMargin = tvGapsWidth;
//					params.topMargin = tvGapsHeight;
//					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//					params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//					tv.setLayoutParams(params);
//					tv.setId(i+size);
//					relativeLayout.addView(tv,i+size-1);
//					copyTextView.set(i-index-1, tv);
//				}else{	//
//					WordRect lastTv = (WordRect)relativeLayout.getChildAt(i-2).getTag();
//					TextView tv = (TextView)copyTextView.get(i-index-1);
//					WordRect nowRect = (WordRect)tv.getTag();
//					if(lastTv.getRight()+tvGapsWidth+nowRect.getWidth()  > viewContainerWidth){	// 超出容器范围 则不进行移动
//						MyRect(tv,tvGapsWidth,(int)lastTv.getBottom()+tvGapsHeight);
//						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//						params.leftMargin = 0;
//						params.topMargin = tvGapsHeight;
//						params.addRule(RelativeLayout.BELOW, relativeLayout.getChildAt(i-2).getId());
//						params.addRule(RelativeLayout.ALIGN_LEFT, relativeLayout.getChildAt(0).getId());
//						tv.setLayoutParams(params);
//						tv.setId(i+size);
//						relativeLayout.addView(tv,i+size-1-index);
//						copyTextView.set(i-index-1, tv);
//						// 不需要移动
//					}else{
//						MyRect(tv,(int)lastTv.getRight()+tvGapsWidth,(int)lastTv.getTop());
//						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//						params.leftMargin = tvGapsWidth;
//						params.topMargin = tvGapsHeight;
//						params.addRule(RelativeLayout.ALIGN_BOTTOM, relativeLayout.getChildAt(i-2).getId());
//						params.addRule(RelativeLayout.RIGHT_OF, relativeLayout.getChildAt(i-2).getId());
//						tv.setLayoutParams(params);
//						tv.setId(i+size);
//						relativeLayout.addView(tv,i+size-1-index);
//						copyTextView.set(i-index-1, tv);
//					}
//				}
//				isFirstFlag = false;
//			}else{	// 后续单词 先判断是否超出容器宽度
//				WordRect lastTv = (WordRect)copyTextView.get(i-index-2).getTag();
//				TextView tv = (TextView)copyTextView.get(i-index-1);
//				WordRect nowRect = (WordRect)tv.getTag();
//				if(lastTv.getRight()+tvGapsWidth+nowRect.getWidth()  > viewContainerWidth){	// 超出容器范围
//					MyRect(tv,tvGapsWidth,(int)lastTv.getBottom()+tvGapsHeight);
//					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//					params.leftMargin = 0;
//					params.topMargin = tvGapsHeight;
//					params.addRule(RelativeLayout.BELOW, copyTextView.get(i-index-2).getId());
//					params.addRule(RelativeLayout.ALIGN_LEFT, relativeLayout.getChildAt(0).getId());
//					tv.setLayoutParams(params);
//					tv.setId(i+size);
//					relativeLayout.addView(tv,i+size-1-index);
//					copyTextView.set(i-index-1, tv);
//				}else{
//					MyRect(tv,(int)lastTv.getRight()+tvGapsWidth,(int)lastTv.getTop());
//					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//					params.leftMargin = tvGapsWidth;
//					params.topMargin = tvGapsHeight;
//					params.addRule(RelativeLayout.ALIGN_BOTTOM, copyTextView.get(i-index-2).getId());
//					params.addRule(RelativeLayout.RIGHT_OF, copyTextView.get(i-index-2).getId());
//					tv.setLayoutParams(params);
//					tv.setId(i+size);
//					relativeLayout.addView(tv,i+size-1-index);
//					copyTextView.set(i-index-1, tv);
//				}
//			}
//		}
//	}

	// 移位后续的原view
	private void translateTextView(RelativeLayout relativeLayout, int index, int size){
		boolean isFirstFlag = true;
		for(int i= index+1;i<size;i++){
			if(isFirstFlag){	// 先处理第一个单词
				if(i == 1){	// 左上角的话直接塞进去
					TextView tv = (TextView)relativeLayout.findViewById(i+1);
					MyRect(tv,tvGapsWidth,tvGapsWidth);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.leftMargin = tvGapsWidth;
					params.topMargin = tvGapsHeight;
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					tv.setLayoutParams(params);
					tv.setVisibility(View.INVISIBLE);
					tv.requestLayout();
				}else{	//
					WordRect lastTv = (WordRect)relativeLayout.findViewById(i-1).getTag();
					TextView tv = (TextView)relativeLayout.findViewById(i+1);
					WordRect nowRect = (WordRect)tv.getTag();
					if(lastTv.getRight()+tvGapsWidth+nowRect.getWidth()  > viewContainerWidth){	// 超出容器范围 则不进行移动
						MyRect(tv,tvGapsWidth,(int)lastTv.getBottom()+tvGapsHeight);
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						params.leftMargin = 0;
						params.topMargin = tvGapsHeight;
						params.addRule(RelativeLayout.BELOW, i-1);
						params.addRule(RelativeLayout.ALIGN_LEFT, 1);
						tv.setLayoutParams(params);
						tv.setVisibility(View.INVISIBLE);
						tv.requestLayout();
						// 不需要移动
					}else{
						MyRect(tv,(int)lastTv.getRight()+tvGapsWidth,(int)lastTv.getTop());
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						params.leftMargin = tvGapsWidth;
						params.topMargin = tvGapsHeight;
						params.addRule(RelativeLayout.ALIGN_BOTTOM, i-1);
						params.addRule(RelativeLayout.RIGHT_OF, i-1);
						tv.setLayoutParams(params);
						tv.setVisibility(View.INVISIBLE);
						tv.requestLayout();
					}
				}
				isFirstFlag = false;
			}else{	// 后续单词 先判断是否超出容器宽度
				WordRect lastTv = (WordRect)relativeLayout.findViewById(i).getTag();
				TextView tv = (TextView)relativeLayout.findViewById(i+1);
				WordRect nowRect = (WordRect)tv.getTag();
				if(lastTv.getRight()+tvGapsWidth+nowRect.getWidth()  > viewContainerWidth){	// 超出容器范围
					MyRect(tv,tvGapsWidth,(int)lastTv.getBottom()+tvGapsHeight);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.leftMargin = 0;
					params.topMargin = tvGapsHeight;
					params.addRule(RelativeLayout.BELOW, i);
					params.addRule(RelativeLayout.ALIGN_LEFT, 1);
					tv.setLayoutParams(params);
					tv.setVisibility(View.INVISIBLE);
					tv.requestLayout();
				}else{
					MyRect(tv,(int)lastTv.getRight()+tvGapsWidth,(int)lastTv.getTop());
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.leftMargin = tvGapsWidth;
					params.topMargin = tvGapsHeight;
					params.addRule(RelativeLayout.ALIGN_BOTTOM, i);
					params.addRule(RelativeLayout.RIGHT_OF, i);
					tv.setLayoutParams(params);
					tv.setVisibility(View.INVISIBLE);
					tv.requestLayout();
				}
			}
		}
	}

	// 动画移动后续view
	private void scrollTextView(RelativeLayout relativeLayout, int index, int size, ArrayList<TextView> copyTextView){

		if(size - index -1== copyTextView.size()){
			for(int i = index+1 ; i < size ; i++ ){
				int startX = (int)relativeLayout.findViewById(i+1).getX();
				int startY = (int)relativeLayout.findViewById(i+1).getY();

				int endX = (int)relativeLayout.findViewById(copyTextView.get(i-index-1).getId()).getX();
				int endY = (int)relativeLayout.findViewById(copyTextView.get(i-index-1).getId()).getY();
				getAinmation(relativeLayout.findViewById(copyTextView.get(i-index-1).getId()),startX,startY,endX,endY);
			}

			for(int i = index+1 ; i < size ; i++ ){
				relativeLayout.findViewById(copyTextView.get(i-index-1).getId()).
				startAnimation(relativeLayout.findViewById(copyTextView.get(i-index-1).getId()).getAnimation());
			}
		}
	}

	private Animation getAinmation(View view, int startX, int startY , int endX, int endY){
		TranslateAnimation animation = new TranslateAnimation(0, startX-endX, 0,startY-endY);
		animation.setDuration(DELAYTIME);//设置动画持续时间
		animation.setFillAfter(true);
		view.setAnimation(animation);
		return animation;
	}

	private void removeTextView(RelativeLayout relativeLayout, int index, int size, ArrayList<TextView> copyTextView){
		if(size - index -1== copyTextView.size()){
			for(int i = index+1 ; i < size ; i++ ){
				relativeLayout.getChildAt(i).setVisibility(View.VISIBLE);
			}
			for(int i = 0 ; i < copyTextView.size();i++){
				relativeLayout.removeView(relativeLayout.findViewById(copyTextView.get(i).getId()));
			}
			relativeLayout.removeView(relativeLayout.findViewById(index+1));
			int length = relativeLayout.getChildCount();
			// 所有大于index的id-1
			for(int m=0;m<length;m++){

				View view= relativeLayout.getChildAt(m);

				if(view.getId()>index+1){
					view.setId(m+1);
					int [] mRules = ((RelativeLayout.LayoutParams)view.getLayoutParams()).getRules();
					int mSize = mRules.length;
					for(int i = 0;i<mSize;i++){
						if(mRules[i]>index+1){
							mRules[i] = mRules[i]-1;
						}
					}
				}
				view.requestLayout();
				int id = view.getId();
				String text = ((TextView)view).getText().toString();
				Log.d("", id+text);
			}
		}
	}
}

