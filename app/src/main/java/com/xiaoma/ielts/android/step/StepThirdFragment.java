/**
 * 工程名: xiaomayasi
 * 文件名: StepThirdFragment.java
 * 包名: com.xiaoma.ielts.android.view.step
 * 日期: 2015年9月28日上午9:48:34
 * QQ: 378640336
 *
*/

package com.xiaoma.ielts.android.step;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.ielts.android.BaseFragment;
import com.xiaoma.ielts.android.R;
import com.xiaoma.ielts.android.widget.stepthird.ShadowBuilder;
import com.xiaoma.ielts.android.widget.stepthird.TextViewDisplay;
import com.xiaoma.ielts.android.widget.stepthird.TextViewFactory;
import com.xiaoma.ielts.android.widget.stepthird.WordRect;

import java.util.ArrayList;

/**
 * 类名: StepThirdFragment <br/>
 * 功能: TODO 添加功能描述. <br/>
 * 日期: 2015年9月28日 上午9:48:34 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class StepThirdFragment extends BaseFragment implements View.OnClickListener{
	
	private Context mContext;
	RelativeLayout mTvContainer;
	RelativeLayout mTvAnswer;
	
	TextView mTitleTv;
	
	// 将单词按照顺序拖动到下方的格子中，组成原句
	TextView mAnswerTips;
	
	TextView mRightAnswerTipsTv;
	RelativeLayout mRightAnswerRl;
	RelativeLayout mTopHalfRl;
	
	TextView mAnswerSentence;
	RelativeLayout mAnswerSentenceRRl;
	
	RelativeLayout mThirdContainer;
	
	private TextView mDragText;
	private ArrayList<TextView> textviewArrays = null;
	private ArrayList<TextView> textAnswerArrays = null;
	private ArrayList<String> answerStrings = null;
	
	private String rightAnswerStr = "He was one of the best string instrument players in our town.";
	//H He could not read music, 
	private int wrongCount = 0;
	
	Vibrator vibrator = null;
	
	boolean isRightAll = true;
	
	boolean isEntered = false;
	
	private float dragPositionY = 0.0f;
	private boolean isAsEntered = false;
	public StepThirdFragment(){
		
	}
	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wrongCount = 0;
	}



	@Override
	protected void init() {
		
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initView() {
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
		contentView = inflater.inflate(R.layout.fragment_step_third, null);
		mContext = getActivity();
		mAnswerTips = (TextView)contentView.findViewById(R.id.tv_tips);
		mRightAnswerRl = (RelativeLayout)contentView.findViewById(R.id.tv_answer_rl);
		mRightAnswerTipsTv = (TextView)contentView.findViewById(R.id.right_answer_tv);
		mAnswerSentence = (TextView)contentView.findViewById(R.id.sentenceAnswerTv);
		mAnswerSentenceRRl = (RelativeLayout)contentView.findViewById(R.id.sentenceAnswerRl);
		mThirdContainer = (RelativeLayout)contentView.findViewById(R.id.third_scroll);
		
		mRightAnswerTipsTv.setText(rightAnswerStr);
		mAnswerSentence.setText(rightAnswerStr);
		
		mTopHalfRl = (RelativeLayout)contentView.findViewById(R.id.top_half_rl);
		
		mTvContainer = (RelativeLayout)contentView.findViewById(R.id.tv_container_rl);
        mTvAnswer = (RelativeLayout)contentView.findViewById(R.id.sentenceAnswer);
        addTextView(rightAnswerStr,
        		mTvContainer,mTvAnswer,getScreenWidth()-(int)getResources().getDimension(R.dimen.custom_textview_gaps_x));
        // 
        int size = textAnswerArrays.size();
        if(size>0){
        	int bottom = 0;
        	WordRect lastRect = null;
        	for(int i = 0 ; i < size;i++){
        		lastRect = (WordRect)textAnswerArrays.get(i).getTag();
        		if(lastRect.getBottom()>bottom){
        			bottom = (int)lastRect.getBottom();
        		}
        	}
        	if(lastRect!=null){
        		bottom +=lastRect.getHeight();
        	}
        	int minHeight = (int) (getScreenHeight()-
        			bottom-
        			getResources().getDimension(R.dimen.px95)-
        			getResources().getDimension(R.dimen.px55)
        			-getResources().getDimension(R.dimen.min_tv_bottom_margin)
        			-3*getResources().getDimension(R.dimen.score_progress_height)
        			-3*getResources().getDimension(R.dimen.custom_textview_gaps_y));
        	mTopHalfRl.setMinimumHeight(minHeight);
        }
        //
		return contentView;
	}
	
	 public int getScreenWidth(){
			DisplayMetrics dm = new DisplayMetrics();
	        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
	        return dm.widthPixels;
		}
	 public int getScreenHeight(){
		 DisplayMetrics dm = new DisplayMetrics();
		 getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		 return dm.heightPixels;
	 }

	    public void addTextView(String sentence, RelativeLayout relativeLayout, RelativeLayout answer, int width){
			// 构建答案
			answerStrings = TextViewFactory.createStrings(sentence, false);
			TextViewFactory factory = new TextViewFactory(R.drawable.step_third_circle_rectangle_green_bg, R.color.text_color_green);
			TextViewDisplay display = new TextViewDisplay();
			textviewArrays = display.displayAtRelativeLayout(relativeLayout,factory.createFactory(mContext, sentence,true), 
					mContext,width);
			answer.setOnDragListener(new OnDragListener() {
				
				@Override
				public boolean onDrag(View v, DragEvent event) {
					final int action = event.getAction();
					switch(action){
					case DragEvent.ACTION_DRAG_STARTED:	// 处理是否可以接收拖拽数据
						mDragText.setVisibility(View.INVISIBLE);
						return true;
					case DragEvent.ACTION_DROP:
	                    // 显示拖拽数据中包含的信息.
						ClipData.Item item = event.getClipData().getItemAt(0);
	                    // 从item获得文本数据
						if(item!=null){
							CharSequence dragData = item.getText();
							if(dragData!=null && dragData.equals(answerStrings.get(0))){
								return true;
							}else{
								return false;
							}
						}
						return false;
					case DragEvent.ACTION_DRAG_ENDED:
						if(event.getResult()){
							dropEnded();
						}else{
							if(isEntered){
								wrongProcess();
							}else{
								if(isAsEntered){	// 也算拖入目标区域
									if(mDragText!=null){
										CharSequence dragData = mDragText.getText();
										if(dragData!=null && dragData.equals(answerStrings.get(0))){
											dropEnded();
										}else{
											wrongProcess();
										}
									}else{
										Toast.makeText(getActivity(), " ", Toast.LENGTH_SHORT).show();
										mDragText.setVisibility(View.VISIBLE);
									}
								}else{
									mDragText.setVisibility(View.VISIBLE);
								}
							}
						}
						isEntered = false;
						isAsEntered = false;
						mTvAnswer.setBackgroundColor(getResources().getColor(R.color.step_third_answer_rl_bg));
						return true;
					case DragEvent.ACTION_DRAG_EXITED:
						mTvAnswer.setBackgroundColor(getResources().getColor(R.color.step_third_answer_rl_bg));
						isEntered = false;
						// 解决出下边界也算进入拖拽目标的问题
						if(dragPositionY < event.getY()){
							isAsEntered = true;
						}
						return true;
					case DragEvent.ACTION_DRAG_ENTERED:
						dragPositionY = event.getY();
						mTvAnswer.setBackgroundColor(getResources().getColor(R.color.step_third_answer_rl_hover));
						isEntered = true;
						return true;
					}
					return false;
				}
			});
			int size = (textviewArrays!=null ? textviewArrays.size():0);
			for(int i = 0 ;i < size;i++){
				textviewArrays.get(i).setOnTouchListener(new OnTouchListener() {
					@SuppressLint("NewApi")
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch(event.getAction()){
						case MotionEvent.ACTION_DOWN:
							mDragText = (TextView)v;
							if(Build.VERSION.SDK_INT >= 16){
								mDragText.setBackground(mContext.getResources().getDrawable(R.drawable.step_third_circle_rectangle_green_pressed));
							}else{
								mDragText.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.step_third_circle_rectangle_green_pressed));
							}
							mDragText.setTextColor(mContext.getResources().getColor(R.color.text_color_white));
							mDragText.setDrawingCacheEnabled(true);
							Bitmap dragBitmap = Bitmap.createBitmap(mDragText.getDrawingCache());
							
							if(Build.VERSION.SDK_INT >= 16){
								mDragText.setBackground(mContext.getResources().getDrawable(R.drawable.step_third_circle_rectangle_green_normal));
							}else{
								mDragText.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.step_third_circle_rectangle_green_normal));
							}
							mDragText.setTextColor(mContext.getResources().getColor(R.color.text_color_green));
							// 创建一个ClipData对象
				            // 这里分为两步，第一步中方法ClipData.newPlainText()可以创建一个纯文本ClipData

				            // 根据ImageView的标签创建一个ClipData.Item对象
				            ClipData.Item item = new ClipData.Item(((TextView)v).getText());

				            // 使用标签，纯文本和已经创建的item来创建一个ClipData对象
				            // 这里将在ClipData中创建一个新的ClipDescription对象并设置它的MIME类型为"text/plain"
				            ClipData dragData = new ClipData(((TextView)v).getText(),
				                    new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN }, item);
				            // 实例化拖拽影子.
				            View.DragShadowBuilder myShadow = new ShadowBuilder(v,dragBitmap,mContext);
							v.startDrag(dragData, myShadow, null, 0);
							break;
						}
						return false;
					}
				});
					
			}
			
			
			TextViewFactory factory2 = new TextViewFactory(R.drawable.circle_rectangle_white_bg, R.color.text_color_white);
			
			textAnswerArrays = display.displayAtRelativeLayout(answer,factory2.createFactory(mContext, sentence), 
					mContext,width);
		}
	    
	    /*
	     * 找到对应的TextView 并移除 然后重新 排序
	     */
	    private void resortTextView(RelativeLayout relativeLayout , TextView tv, int width){
	    	boolean isRemoveFlag = false;
	    	if(textviewArrays!=null && textviewArrays.size()>0){
	    		int size = textviewArrays.size();
	    		for(int i = 0 ; i < size ; i++){
	    			if(tv == textviewArrays.get(i)){
    					textviewArrays.remove(i);
    					isRemoveFlag = true;
    					break;
	    			}
	    		}
	    	}
	    	if(isRemoveFlag){
	    		TextViewDisplay display = new TextViewDisplay();
	    		display.refreshRelativeLayout(relativeLayout,tv,mContext,width);
	    	}
	    }

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			}
		}

		


		@Override
		public void onStop() {
			super.onStop();
			if(vibrator!=null){
				vibrator.cancel();
			}
		}



		@Override
		public void onDestroy() {
			super.onDestroy();
			mTvContainer = null;
			mTvAnswer = null;
			
			mTitleTv = null;
			
			// 将单词按照顺序拖动到下方的格子中，组成原句
			mAnswerTips = null;
			
			mRightAnswerTipsTv = null;
			mRightAnswerRl = null;
			mTopHalfRl = null;
			
			mAnswerSentence = null;
			mAnswerSentenceRRl = null;
			
			mThirdContainer = null;
			
			mDragText = null;
		}
	
	
		private Handler mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case 0x01:	// 如果不设置延时 会发生奔溃
					mAnswerSentenceRRl.setVisibility(View.VISIBLE);
					mTvAnswer.setVisibility(View.INVISIBLE);
					break;
				}
			}
			
		};
		
		// 拖动错误的时候震动 + 抖动
		private void viewShake(View view){
			Animation shakeAnim = AnimationUtils.loadAnimation(mContext, R.anim.shake_umeng_socialize_edit_anim);
			view.startAnimation(shakeAnim);
			vibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
	        long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启   
	        vibrator.vibrate(pattern,-1); 
		}

		private void dropEnded(){
			mDragText.setVisibility(View.INVISIBLE);
			for(int i = 0 ; i < textAnswerArrays.size();i++){
				int color = textAnswerArrays.get(i).getTextColors().getDefaultColor();
				if(textAnswerArrays.get(i).getText().toString().equals(answerStrings.get(0))){
					if(color == getResources().getColor(R.color.text_color_white)){	// 必须是字体颜色为 白色的才行
						textAnswerArrays.get(i).setTextColor(getResources().getColor(R.color.text_color_green));
						textAnswerArrays.get(i).invalidate();
						break;
					}
				}
			}
			answerStrings.remove(0);
			resortTextView(mTvContainer,mDragText,getScreenWidth());
			if(answerStrings==null || answerStrings.size()<=0){
				mHandler.sendEmptyMessageDelayed(0x01, 200);
				if(isRightAll){
				}

			}
		}
		
		private void wrongProcess(){
			wrongCount ++;
			if(mDragText!=null){
				mDragText.setVisibility(View.VISIBLE);
				viewShake(mDragText);
			}
			if(wrongCount >= 3){
				mRightAnswerRl.setVisibility(View.VISIBLE);
				if(isRightAll == true){	// 只记录一次

				}
				isRightAll = false;
			}
		}
}

