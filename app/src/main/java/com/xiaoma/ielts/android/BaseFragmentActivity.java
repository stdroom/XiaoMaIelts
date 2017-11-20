package com.xiaoma.ielts.android;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;



public abstract class BaseFragmentActivity extends FragmentActivity {
	/**是否退出应用 true 退出   默认false 关闭activity*/
	protected boolean back_exit = false;

	public FragmentManager fm;
	/**
     * 数据加载动画布局
     */
    protected RelativeLayout mLoadingBar_Rl;

    protected BroadcastReceiver broadcastReceiver;
    protected boolean isRegistered;
    
    private int progress;
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(arg0);

	}
	
	/**
	 * 绑定控件id
	 */
	protected abstract void init();

	/**
	 * 初始化控件
	 */
	protected abstract void initView();

	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 */
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	/**
	 * 通过类名启动Activity，并且含有Bundle数据
	 * 
	 * @param pClass
	 * @param pBundle
	 */
	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	/**
	 * 通过Action启动Activity
	 * 
	 * @param pAction
	 */
	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	/**
	 * 通过Action启动Activity，并且含有Bundle数据
	 * 
	 * @param pAction
	 * @param pBundle
	 */
	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
        if (isRegistered && null != broadcastReceiver) {
            unregisterReceiver(broadcastReceiver);
            isRegistered = false;
        }
	}

	private long backTime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!back_exit){
				finish();
			}else {
				if (System.currentTimeMillis() - backTime > 1800) {
					backTime = System.currentTimeMillis();
					Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
					return true;
				}else {
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    // 设置进度颜色
    protected void setProgressScore(int progress , int max){
    	View view = findViewById(R.id.score_progressbar);
    	this.progress = progress;
    	if(view!=null){
    		ProgressBar bar = (ProgressBar)view;
    		bar.setVisibility(View.VISIBLE);
			bar.setMax(max*10);
			if(AppConstant.prevSumScore==0 || progress==0){
				AppConstant.prevSumScore = progress;
			}else if(AppConstant.prevSumScore>0){
				bar.setProgress(AppConstant.prevSumScore*10);
				bar.setSecondaryProgress(progress*10);
			}
			if(progress!=AppConstant.prevSumScore){
				Message msg = new Message();
				msg.what = 0x11;
				msg.obj = 2;
				updateScoreHandler.sendMessageDelayed(msg, 100);
			}
    	}
    	
    }
    
    
    private Handler updateScoreHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0x13:
    			Message msga = obtainMessage();
    			msga.what = 0x11;
    			msga.obj = (Integer)(msg.obj)+2;
    			updateScoreHandler.sendMessageDelayed(msga, 250);
    			break;
			case 0x11:
				View view = findViewById(R.id.score_progressbar);
				Integer added = (Integer)msg.obj;
				ProgressBar bar = null;
		    	if(view!=null){
		    		bar = (ProgressBar)view;
		    		bar.setVisibility(View.VISIBLE);
		    	}
		    	if(AppConstant.prevSumScore - progress < 0 ){
		    		if(AppConstant.prevSumScore*10+added<progress*10){
		    			bar.setProgress(AppConstant.prevSumScore*10+added);
		    			Message msgs = obtainMessage();
		    			msgs.what = 0x13;
		    			msgs.obj = (Integer)added;
		    			updateScoreHandler.sendMessage(msgs);
		    		}else{	// 
		    			bar.setProgress(progress*10);
		    			AppConstant.prevSumScore = progress;
		    		}
		    	}else{
		    		if(AppConstant.prevSumScore*10-added>progress*10){
		    			bar.setProgress(AppConstant.prevSumScore*10-added);
		    			Message msgs = obtainMessage();
		    			msgs.what = 0x13;
		    			msgs.obj = (Integer)added;
		    			updateScoreHandler.sendMessage(msgs);
		    		}else{	// 
		    			bar.setProgress(progress*10);
		    			AppConstant.prevSumScore = progress;
		    		}
		    	}
				break;
			}
		}
    	
    };
}
