/**
 * 工程名: xiaomayasi
 * 文件名: StepOneFragment.java
 * 包名: com.xiaoma.ielts.android.view.step
 * 日期: 2015年9月25日上午9:50:03
 * QQ: 2920084022
 *
 */

package com.xiaoma.ielts.android.step;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.xiaoma.ielts.android.BaseFragment;
import com.xiaoma.ielts.android.R;
import com.xiaoma.ielts.android.model.Sentence;
import com.xiaoma.ielts.android.widget.FlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 类名: StepOneFragment <br/>
 * 功能: TODO 添加功能描述. <br/>
 * 日期: 2015年9月25日 上午9:50:03 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class StepTwoFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = "StepTwoFragment";
	/*
	 * 句子中文
	 */
	private TextView mTextView_chin;
	/*
	 * 句子英文
	 */
	private FlowLayout mTextView_eng;
	/*
	 * 答案英文
	 */
	private TextView mTextView_success;
	/*
	 * 答案提示
	 */
	private ImageView mImageView;
	/*
	 * 正确答案
	 */
	private LinearLayout mLinearLayout;
	/*
	 * 答案提示的点击次数响应
	 */
	private int count = 1;
	private static final int BUTTON_TIPS_ONE = 0x04;
	private static final int BUTTON_TIPS_TWO = 0x05;


	private static final float P = 0.5F; //挖空比例
	private List<String> splits = new ArrayList<String>();
	private List<Integer> wordIndex = new ArrayList<Integer>();
	private List<Integer> blanks;

	// 第一个输入框的位置
	private int firstEditViewIndex = -1;
	
	boolean answerRightFlag = true;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.step_two, container, false);
		init();
		initView();
		return contentView;
	}

	@Override
	protected void init() {
//		super.mRecord_img = (ImageView) findViewById(R.id.steptwo_playRecord);
		mTextView_chin = (TextView) findViewById(R.id.steptwo_stepChin);
		mTextView_eng = (FlowLayout) findViewById(R.id.steptwo_stepEng);
//		super.mItemAnimationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.step_record_animation_list);
//		super.mRecord_img.setImageResource(R.drawable.sound_bf);
		mImageView = (ImageView) findViewById(R.id.steptwo_answer_hint);
		mLinearLayout = (LinearLayout) findViewById(R.id.steptwo_line_success_answer);
		mTextView_success = (TextView) findViewById(R.id.steptwo_answer);
	}

	@Override
	protected void initView() {
//		super.mRecord_img.setOnClickListener(this);
		mImageView.setOnClickListener(this);
		Sentence sen = new Sentence();//数据库中得到需要学习的中英文句子
		if(null != sen){
			mTextView_chin.setText(sen.getTopicChin());
			createWordLayout(sen.getTopicEng());
			mTextView_success.setText(sen.getTopicEng());
		}else{
			Log.i(getActivity().getClass().getSimpleName(), "获取数据库数据为空");
			return;
		}
	}

	private void createWordLayout(String sentence){
		split(sentence);
		blanks = sample(wordIndex);
		Collections.sort(blanks);
		for (int i = 0; i < splits.size(); i++) {
			if (blanks.contains(i)) {
				if(firstEditViewIndex<0){
					firstEditViewIndex = i;
				}
				EditText edit = new EditText(getActivity());
				edit.setBackgroundResource(R.drawable.wakong_false);
				edit.setTextSize(18F);
				float w = edit.getPaint().measureText(splits.get(i));
				edit.setPadding(0, 0, 0,10); 
				edit.setGravity(Gravity.BOTTOM);
				edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(splits.get(i).length() + 1), new UpcaseFilter(splits.get(i))});
				MyWatcher watcher = new MyWatcher(i);
				edit.addTextChangedListener(watcher);
				edit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				LayoutParams params = new LayoutParams((int)w,(int) getResources().getDimension(R.dimen.steptwo_edittext_height));
				mTextView_eng.addView(edit, params);

			} else {
				createTextView(i);
			}
		}

	}

    private void createTextView(int index) {
        TextView textView = new TextView(getActivity());
        textView.setText(splits.get(index));
        textView.setTextSize(18F);
        textView.setPadding(0, 0, 0, 10);
        float w = textView.getPaint().measureText(splits.get(index));
        textView.setHeight((int) getResources().getDimension(R.dimen.steptwo_edittext_height));
        LayoutParams params = new LayoutParams((int)w,(int) getResources().getDimension(R.dimen.steptwo_edittext_height));
        textView.setGravity(Gravity.BOTTOM);
        mTextView_eng.addView(textView, index, params);
    }
    /*
     * 输入的小写字母，在答案中对应的是大写字母，那么自动转换成大写字母。
     */
    class UpcaseFilter implements InputFilter {
        private String word; //答案

        public UpcaseFilter(String word) {
            super();
            this.word = word;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend){
            if (TextUtils.isEmpty(source)) {
                return source;
            }
            int resultLength = word.length(); //答案的长度
            int length = dest.length();//输入的文本的长度
            if (source.length()>0 && length <= resultLength && dstart < resultLength) {
                String a = word.substring(dstart, dstart + 1);
                String s = source.toString();
                if(a.equals(s.toUpperCase())) {
                    String result = s.toUpperCase();
                    if (source instanceof Spannable) {
                        SpannableString ss = new SpannableString(s.toUpperCase());
                        TextUtils.copySpansFrom((Spanned)source,
                                start, end, null, ss, 0);
                        return ss;
                    } else {
                        return result;
                    }
                }
            }
            return source;
        }
    }
    class MyWatcher implements TextWatcher {
        private int index;
        private String word;
        private CharSequence mInputContent;
        private int mStart = 0;
        private int mCount = 0;
        private TextView mTextView;
        private ForegroundColorSpan mAppColorSpan;
        public MyWatcher(int index) {
            super();
            this.index = index;
            this.word = splits.get(index);
            mTextView = (TextView) mTextView_eng.getChildAt(index);
            mAppColorSpan = new ForegroundColorSpan(
                    getResources().getColor(R.color.app_color));
        }

        
        public MyWatcher() {
            super();
        }


        public void setIndex(int index) {
            this.index = index;
            this.word = splits.get(index);
            mTextView = (TextView) mTextView_eng.getChildAt(index);
            mStart = 0;
            mCount = 0;
        }


        @Override
        public void beforeTextChanged(CharSequence content,
                int start, int count, int after) {
            Log.i(TAG, "beforeTextChanged:content == " + content + "**start=="  + start + " count==" + count + " after==" + after);
        }
        
        @Override
        public void onTextChanged(CharSequence content,
                int start, int before, int count) {
            Log.i(TAG, "onTextChanged:content == " + content + "**start=="  + start + " before==" + before + " count==" + count);
            mStart = start;
            mCount = count;
            mInputContent = content;
            if (content instanceof Spannable){
                Spannable span = (Spannable)content;
                if (mCount > 0) { //输入了一个字符
                	mTextView_chin.performClick();
                    if (mStart < word.length()) {//输入的字符长度小于答案的总长度
                        Log.i(TAG, "input is OK** " + word + " mInputContent==" + mInputContent);
                        if (mInputContent.charAt(mStart) == 32) { //输入了一个空格
                            if (mStart ==0) { //第一个字符就输入了一个空格

                            } else {
                                ForegroundColorSpan mAppColorSpan = new ForegroundColorSpan(
                                        getResources().getColor(android.R.color.holo_red_light));
                                    span.setSpan(mAppColorSpan, 0, mInputContent.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            }
                            focusNectEditTextView(index);
                        } else if (word.charAt(mStart) == mInputContent.charAt(mStart)) { //输入了一个正确的
                            if (word.subSequence(0, mStart+1).equals(mInputContent.toString().trim())) {//已经输入的部分全部正确
                                span.setSpan(mAppColorSpan, 0, mInputContent.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                if (word.equals(mInputContent.toString().trim())) { //全部输对了 跳到下一个空
                                    mTextView_eng.removeViewAt(index);
                                    createTextView(this.index);
                                    blanks.remove(blanks.indexOf(index));
                                    if (blanks.size() == 0) { //所有空都已经填满，并且全部正确，结束此页面
                                        Log.i(TAG, "blanks size " + blanks.size());
                                    }
                                }
                            }

                        } else { //输入一个错误的字符
                            ForegroundColorSpan mAppColorSpan = new ForegroundColorSpan(
                                    getResources().getColor(android.R.color.holo_red_light));
                                span.setSpan(mAppColorSpan, 0, mInputContent.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                    } else {
                            if (word.equals(mInputContent.toString().trim())){
                                mTextView_eng.removeViewAt(index);
                                mTextView_eng.addView(getTextView(word), index);
                                blanks.remove(blanks.indexOf(index));
                                if (blanks.size() == 0) {
                                    Log.i(TAG, "blanks size " + blanks.size());
                                }
                            } else { //输入的长度超过了单词的长度，无论是输入一个空格还是其他字符，将导致焦点离开
                                focusNectEditTextView(index);
                            }
                    } 
                }
                if (before > 0) { //删除一个字符
                    if (start - before >= 0 && word.substring(0, start) .equals(mInputContent.subSequence(0, start).toString())) {
                        span.removeSpan(mAppColorSpan);
                        span.setSpan(mAppColorSpan, 0, mInputContent.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    } else {
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    };

    private  void focusNectEditTextView(int index){
        int i = 0;
        index ++;
        int count  = mTextView_eng.getChildCount();
        while (index < count) {
            if (i == count) {
                return;
            }
            TextView et = (TextView) mTextView_eng.getChildAt(index);
            if (et instanceof EditText) {
                et.requestFocus();
                et.setCursorVisible(true);
                return;
            } else {
                index ++;
                if (index >= count -1) {
                    index = 0;
                }
            }
            i ++;
        }
    }
    private TextView getTextView (String word) {
        TextView textView = new TextView(getActivity());
        textView.setText(word);
        textView.setTextSize(18F);
        textView.setHeight(120);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        return textView;
    }

	@Override
	public void onResume() {
		super.onResume();
		VisibilityOrGone(1);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if(null != mHandler){
			if(mHandler.hasMessages(BUTTON_TIPS_TWO)){
				mHandler.removeMessages(BUTTON_TIPS_TWO);
			}
			if(mHandler.hasMessages(BUTTON_TIPS_ONE)){
				mHandler.removeMessages(BUTTON_TIPS_ONE);
			}
		}
	}

	/*
	 * 答案提示/答案显示的隐藏和显示
	 */
	 private void VisibilityOrGone(int status){
		switch (status) {
		case 1:
			mImageView.setVisibility(View.VISIBLE);
			mLinearLayout.setVisibility(View.GONE);
			break;
		case 2:
			mImageView.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.VISIBLE);
			break;
		}
	 }

	 @Override
	 public void onPause() {
		 super.onPause();
	 }

	 @Override
	 public void onClick(View v) {
		 switch (v.getId()) {
		 case R.id.steptwo_playRecord:
			 break;
		 case R.id.steptwo_answer_hint:		//点击一次播放音频二次显示答案
			 if(count < 2){
			 }else{
				 answerRightFlag = false;
				 VisibilityOrGone(2);
			 }
			 //			openActivity(BaseStepFourActivity.class);
			 //			MyFavDialog.showDialog(getActivity(), R.drawable.right_pop);
			 break;
		 }
	 }
	 /*
	  * 分割句子，以单词或者标点符号为单位
	  */
	 private void split(String sentence) {
		 int length = sentence.length();
		 int wordBegin = -1;
		 int wordEnd = -1;
		 int splitsIndex = 0;
		 for (int i = 0; i < length; i++) {
			 char cr = sentence.charAt(i);
			 if(isCharater(cr)) {
				 if (wordBegin == -1) {
					 wordBegin = i;
				 }
			 } else if (isSeparator(cr)) {
				 String separator = String.valueOf(cr);
				 wordEnd = i;
				 splits.add(splitsIndex, sentence.substring(wordBegin, wordEnd));
				 wordIndex.add(splitsIndex);
				 splitsIndex ++;

				 splits.add(splitsIndex, separator);
				 splitsIndex ++;
				 wordBegin = -1;
				 wordEnd = -1;
			 } else {
				 if (wordBegin > -1) {
					 wordEnd = i;
					 splits.add(splitsIndex, sentence.substring(wordBegin, wordEnd));
					 wordIndex.add(splitsIndex);
					 splitsIndex ++;
					 wordBegin = -1;
					 wordEnd = -1;
				 }
			 }

		 }
	 }
	 private boolean isCharater(char input) {
		 if ((input >= 97 && input <= 122) || (input >= 65 && input <= 90) || input == 39 || input == 45) {
			 return true;
		 }
		 return false;
	 }

	 private boolean isSeparator (char input) {
		 if (input == 33 || input == 44 || input == 46 || input == 63) {
			 return true;
		 }
		 return false;
	 }


	 /*
	  * 从集合中随机抽取样本的个数为： 集合长度 * 每个单词抽取的概率P
	  * 样本的结果为每个单词在原句子中的索引的集合
	  */
	 private List<Integer> sample (List<Integer> sample) {
		 if (null == sample) {
			 return null;
		 }
		 int sampleCount = sample.size();
		 int resultCount = (int) Math.ceil(sampleCount * P);
		 List<Integer> result = new ArrayList<Integer>();
		 while (result.size() < resultCount) {
			 result.add(rand(sample));
		 }
		 return result;
	 }
	 //从集合中随机去除一个元素，并把此元素从集合中删除。
	 private Integer rand(List<Integer> sample) {
		 int size = sample.size();
		 Random rand = new Random();
		 int result = rand.nextInt(size);
		 int sampleIndex = sample.get(result);
		 sample.remove(result);
		 return sampleIndex;
	 }

	 private Handler mHandler = new Handler(){

		 @Override
		 public void handleMessage(Message msg) {
			 switch(msg.what){
			 case BUTTON_TIPS_ONE:
				 break;
			 case BUTTON_TIPS_TWO:
				 break;
			 }
		 }

	 };
}

