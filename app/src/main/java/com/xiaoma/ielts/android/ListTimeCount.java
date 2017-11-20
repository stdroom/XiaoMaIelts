/**
 * 工程名: Spoken
 * 文件名: ListTimeCount.java
 * 包名: com.xiaoma.spoken.utils
 * 日期: 2015-5-13上午11:31:47
 * Copyright (c) 2015, 北京小马过河教育科技有限公司 All Rights Reserved.
 * http://www.xiaoma.com/
 * Mail: leixun@xiaoma.cn
 * QQ: 378640336
 *
*/

package com.xiaoma.ielts.android;

import android.os.CountDownTimer;


/**
 * 类名: ListTimeCount <br/>
 * 功能: TODO 添加功能描述. <br/>
 * 日期: 2015-5-13 上午11:31:47 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class ListTimeCount extends CountDownTimer {

	/**当前时间*/
	private long countTime;
	/**计时时间*/
	private long time;
	
	private FinishListener finish;
	
	
	private OnTickListener onTick;
	
	public interface FinishListener{
		public void onFinish(int time);
	}
	
	public interface OnTickListener{
		public void onTick(long time);
	}
	public ListTimeCount(long millisInFuture, long countDownInterval, FinishListener finishListener) {
		super(millisInFuture, countDownInterval);
		time = millisInFuture;
		this.finish = finishListener;
	}

	/**
	 * 
	 * TODO 计时完成.
	 * @see android.os.CountDownTimer#onFinish()
	 */
	@Override
	public void onFinish() {
		if (finish!=null){
			finish.onFinish((int)((time-countTime)/1000)+1);
		}
	}
	/**
	 * 计时中
	 * @see android.os.CountDownTimer#onTick(long)
	 */
	@Override
	public void onTick(long millisUntilFinished) {
		countTime = millisUntilFinished;
		if (this.onTick!=null){
			onTick.onTick(millisUntilFinished/1000);
		}
	}
	/**
	 * 
	 * count:(开始计时). <br/>
	 */
	public void count(){
		this.start();
	}
	
	public int stop(){
		cancel();
		return (int)((time-countTime)/1000)+1;
	}
	
	
	public void setOnTickListener(OnTickListener tick){
		this.onTick = tick;
	}
}

