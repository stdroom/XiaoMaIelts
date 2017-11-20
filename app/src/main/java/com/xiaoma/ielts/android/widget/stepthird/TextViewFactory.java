/**
 * 工程名: xiaomayasi
 * 文件名: TextViewFactory.java
 * 包名: com.xiaoma.ielts.android.common.weight.stepthird
 * 日期: 2015年9月28日上午10:00:13
 * QQ: 378640336
 *
*/

package com.xiaoma.ielts.android.widget.stepthird;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.ielts.android.R;

import java.util.ArrayList;
import java.util.Random;


/**
 * 类名: TextViewFactory <br/>
 * 功能: TODO 添加功能描述. <br/>
 * 日期: 2015年9月28日 上午10:00:13 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class TextViewFactory {

Context mContext;
	
	/**
	 * 内边距
	 */
	int paddingLeft = 0;
	int paddingRight = 0;
	int paddingTop = 0;
	int paddingBottom = 0;
	
	int drawable ;
	int color ;
	
	public TextViewFactory(int drawable,int textColor){
		this.drawable = drawable;
		this.color = textColor;
	}
	
	public ArrayList<TextView> createFactory(Context context , String sentence){
		return createFactory(context,sentence,false);
	}
	
	/**
	 * 
	 * createFactory:(根据句子构建TextView). <br/>
	 *
	 *
	 * @author leixun
	 * 2015年9月23日上午11:09:13
	 * @param context
	 * @param sentence
	 * 			句子
	 * @param randomSortFlag
	 * 			是否乱序
	 * @return
	 * @since 1.0
	 */
	public ArrayList<TextView> createFactory(Context context, String sentence, boolean randomSortFlag){
		
		return createFactory(context, createStrings(sentence,randomSortFlag));
		
	}
	
	/**
	 * 
	 * createFactory:(根据单词数组构建TextView). <br/>
	 *
	 * @author leixun
	 * 2015年9月23日上午11:09:57
	 * @param context
	 * @param words
	 * @return
	 * @since 1.0
	 */
	public ArrayList<TextView> createFactory(Context context, ArrayList<String> words){
		mContext = context;
		initDimens();
		ArrayList<TextView> textViewList = null;
		ArrayList<String> str = words;
		if(str!=null){
			int size = str.size();
			textViewList =new ArrayList<TextView>();
			for(int i = 0 ; i < size ; i++){
				textViewList.add(createTextView(str.get(i)));
			}
		}
		return textViewList;
		
	}
	
	/**
	 * 
	 * createStrings:(). <br/>
	 * @author leixun
	 * 2015年9月23日上午10:10:51
	 * @param sentence
	 * @param randomSort
	 * 			是否乱序
	 * @return
	 * @since 1.0
	 */
	public static ArrayList<String> createStrings(String sentence, boolean randomSort){
		ArrayList<String> arrayString = null;
		if(!"" .equals(sentence) && sentence!=null){
			String str[] = sentence.split(" ");
			if(randomSort){
				str = getSequence(str);
			}
			if(str!=null && str.length>0){
				arrayString = new ArrayList<String>();
				for(int i  = 0 ; i < str.length ;i++){
					arrayString.add(str[i]);
				}
			}
		}
		return arrayString;
	}
	
	@SuppressLint("NewApi") private TextView createTextView(String word){
		TextView mTextView = new TextView(mContext);

		// 第一个参数为宽的设置，第二个参数为高的设置。
		mTextView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT));
		// 设置mTextView的文字
		mTextView.setText(word);
		// 设置字体大小
		mTextView.setTextSize(20);
		// 设置背景
		if(Build.VERSION.SDK_INT >= 16){
			mTextView.setBackground(mContext.getResources().getDrawable(drawable));
		}else{
			mTextView.setBackgroundDrawable(mContext.getResources().getDrawable(drawable));
		}
		
		// 设置字体颜色
		mTextView.setTextColor(mContext.getResources().getColor(color));
		//设置居中
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);//left, top, right, bottom
		
		float width =  mTextView.getPaint().measureText(mTextView.getText().toString())+paddingLeft+paddingRight;
		float height = mTextView.getTextSize()+paddingTop+paddingBottom;
		WordRect rect = new WordRect(width,height);
		mTextView.setTag(rect);
		return mTextView;
	}
	
	
	private void initDimens(){
		paddingLeft = (int)mContext.getResources().getDimension(R.dimen.custom_textview_padding_left);
		paddingRight = (int)mContext.getResources().getDimension(R.dimen.custom_textview_padding_right);
		paddingTop = (int)mContext.getResources().getDimension(R.dimen.custom_textview_padding_top);
		paddingBottom = (int)mContext.getResources().getDimension(R.dimen.custom_textview_padding_bottom);
	}

	 /**
     * 对给定数目的自0开始步长为1的数字序列进行乱序
     * @param no 给定数目
     * @return 乱序后的数组
     */
    public static String[] getSequence(String[] sequence) {
    	int no = sequence.length;
        Random random = new Random();
        for(int i = 0; i < no; i++){
            int p = random.nextInt(no);
            String tmp = sequence[i];
            sequence[i] = sequence[p];
            sequence[p] = tmp;
        }
        random = null;
        return sequence;
    }
}

