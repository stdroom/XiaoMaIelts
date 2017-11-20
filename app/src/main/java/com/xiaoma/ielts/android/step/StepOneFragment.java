/**
 * 工程名: xiaomayasi
 * 文件名: StepOneFragment.java
 * 包名: com.xiaoma.ielts.android.view.step
 * 日期: 2015年9月25日上午9:50:03
 * QQ: 2920084022
 *
 */

package com.xiaoma.ielts.android.step;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.czt.mp3recorder.MP3Recorder;
import com.xiaoma.ielts.android.BaseFragment;
import com.xiaoma.ielts.android.ListTimeCount;
import com.xiaoma.ielts.android.R;
import com.xiaoma.ielts.android.model.Sentence;
import com.xiaoma.ielts.android.widget.DaubTextView;
import com.xiaoma.ielts.android.widget.RoundProgressBar;

import java.io.File;

/**
 * 类名: StepOneFragment <br/>
 * 功能: TODO 添加功能描述. <br/>
 * 日期: 2015年9月25日 上午9:50:03 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class StepOneFragment extends BaseFragment implements OnClickListener{

	private TextView mTextView_chin;
	private DaubTextView mTextView_eng;
	private RelativeLayout mRelativeLayout;
	/**
	 * 对应的提示语显示标志位	确保每一次启动只显示一次
	 */
	private boolean mRecordTipsBtnFlag = false;
	
	private MP3Recorder mp3Recorder;

	private RelativeLayout mRecordingContainerRl;
	private ImageView iv_recorder;
	private TextView mRecordCountTimeTv;
	private RoundProgressBar mRecordProgress;
	
	private RelativeLayout mPlayContainerRl;
	private ImageView mPlayAnimationImg;
	private RelativeLayout mClickPlayRl;
	private TextView mPlayCountTimeTv;
	private ImageView mPlayDeleteImg;
	
//	播放器
//	private static LrcMediaPlayer lrcMediaPlayer;
	/**
	 * 上传录音的播放按钮动画
	 */
	AnimationDrawable mAnimationDrawable;
	/**
	 * 上传录音的倒计时器
	 */
	ListTimeCount mUploadRecordTimeCount = null;
	
	private int recorderStatus = 1;//录音状态
	private static int recording = 0;//正在录音 
	private static int unrecording = 1;//未录音
	private String audioPath;
	private String filePath ;
	
	private int playStatus_record = 1;//播放状态
	private static int playing = 0;//正在播放
	private static int unplaying = 1;//未播放
	private Context mContext;
	private PowerManager mPowerManager; //电源控制管理器，比如防锁屏
	private WakeLock mWakeLock;//唤醒锁？
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		this.mContext =getActivity();
		contentView = inflater.inflate(R.layout.step_one, container, false);
		init();
		initView();
		mRecordTipsBtnFlag = true;
		return contentView;
	}

	@Override
	protected void init() {
		mRelativeLayout = (RelativeLayout) findViewById(R.id.basestep_fragment_next);
		mTextView_chin = (TextView) findViewById(R.id.stepOne_stepChin);
		mTextView_eng = (DaubTextView) findViewById(R.id.step_one_stepEng);

		/** 录音区域 */
		mRecordingContainerRl = (RelativeLayout)contentView.findViewById(R.id.recording_container);
		iv_recorder = (ImageView) findViewById(R.id.iv_exer_recorder);
		iv_recorder.setOnClickListener(this);
		mRecordCountTimeTv = (TextView)findViewById(R.id.tv_recorder_time);
		mRecordProgress = (RoundProgressBar)contentView.findViewById(R.id.roundProgressBar);
		
		/** 播放录音区域 */
		mPlayContainerRl = (RelativeLayout)contentView.findViewById(R.id.record_play_container);
		mClickPlayRl = (RelativeLayout)contentView.findViewById(R.id.item_record_layout);
		mClickPlayRl.setOnClickListener(this);
		mPlayAnimationImg = (ImageView)contentView.findViewById(R.id.item_play_img);
		mPlayCountTimeTv = (TextView)contentView.findViewById(R.id.item_count_time_tv);
		mPlayDeleteImg = (ImageView)contentView.findViewById(R.id.post_delete_img);
		mPlayDeleteImg.setOnClickListener(this);
		mAnimationDrawable = (AnimationDrawable)mPlayAnimationImg.getDrawable();
		mRecordingContainerRl.setVisibility(View.GONE);
		mPlayContainerRl.setVisibility(View.GONE);
		mRelativeLayout.setVisibility(View.GONE);
//		super.mItemAnimationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.step_record_animation_list);
//		super.mRecord_img.setImageResource(R.drawable.sound_bf);
	}

	@Override
	protected void initView() {
//		super.mRecord_img.setOnClickListener(this);
		Sentence sen = new Sentence();//数据库中得到需要学习的中英文句子
		if(null != sen){
			mTextView_chin.setText(sen.getTopicChin());
			mTextView_eng.setText(sen.getTopicEng());
		}
		mTextView_eng.setOnWipeListener(new DaubTextView.onWipeListener() {

			@Override
			public void onWipe(int percent) {
				if(percent >= 80){//刮开屏幕大于80%自动清除其它的
					mRecordingContainerRl.setVisibility(View.VISIBLE);
					mPlayContainerRl.setVisibility(View.GONE);
					mHandler.sendEmptyMessageDelayed(4, 200);
				}
			}
		});
		/*
		 * 下一步
		 */
		mRelativeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				click2PlayMyRecord();
				getActivity().finish();
			}
		});
		mPowerManager = (PowerManager) mContext.getSystemService(mContext.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());
	}
	@Override
	public void onResume() {
		super.onResume();
		if(mRecordTipsBtnFlag){
			countRecordTipsTimer.start();
			mRecordTipsBtnFlag = false;
		}
		mWakeLock.acquire(); //恢复时解除锁屏
	}
	
	/*
	 * 提示语倒计时
	 */
	private CountDownTimer countRecordTipsTimer = new CountDownTimer(3*1000, 1000) {

		@Override
		public void onTick(long millisUntilFinished) { 
		}  

		@Override
		public void onFinish() {  
		}  
	};  

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.stepOne_playRecord:
			if(playing == playStatus_record){
				stopRecordPlay();
			}
			if(mp3Recorder != null && mp3Recorder.isRecording()){
				mHandler.sendEmptyMessage(0x06);
			}else{
//				if(playStatus == STAGE_STOP){	// 未播放->点击后开始播放
//					startMusic();
//					changeUploadPlayUI(true);
//				} else if(playStatus == STAGE_PLAY){	//	播放状态 -> 点击后停止
//					stopMusic();
//				} else {
//					changeUploadPlayUI(false);
//					Logger.d(TAG,"逻辑错误");
//				}
			}
			break;
		case R.id.iv_exer_recorder:	// 点击录音按钮	： 停止录音 或者开始录音
//			if(playStatus == STAGE_PLAY){	//	播放状态 -> 点击后停止
//				stopMusic();
//			}
			clickToRecording();
			break;
		case R.id.item_record_layout:	// 点击播放按钮
//			if(playStatus == STAGE_PLAY){	//	播放状态 -> 点击后停止
//				stopMusic();
//			}
			click2PlayMyRecord();
			break;
		case R.id.post_delete_img:	//	删除录音
			filePath = "";
			currentTime = 0;
			stopRecordPlay();
			setRecordingViewVisible(1);
			mRecordingContainerRl.setVisibility(View.VISIBLE);
			mPlayContainerRl.setVisibility(View.GONE);
			// 删除本地文件
			break;
		}
	}
	
	
	/**
	 * 开始录音 或者 停止录音
	 */
	private void clickToRecording(){
		if(mp3Recorder != null && mp3Recorder.isRecording() && currentTime<= 10){
//			showToast("录音不能低于10s");
//			return ;
		}

		if(mp3Recorder != null && mp3Recorder.isRecording()){
			recorderStatus =unrecording;
			timer.cancel();
			// ---
			stopProgress();
			mp3Recorder.stop();
			setRecordingViewVisible(2);
			filePath = audioPath;
			Message msg = mHandler.obtainMessage();
			msg.what = 2;
			mHandler.sendMessageDelayed(msg,200);
			return ;
		}

		if(recorderStatus == unrecording){
			if(mp3Recorder == null){
				mp3Recorder = new MP3Recorder(new File(audioPath));
			}
			try {
				// 有可能用户拒绝录音授权
				mp3Recorder.start();
			}catch(IllegalStateException e){
				audioPath = "";
			} catch (Exception e) {
				e.printStackTrace();
			}	
			// 录音器有一个小小的启动时间 ，所以加一个短暂计时等待 added by leixun
			Message msg = mHandler.obtainMessage();
			msg.what = 3;
			iv_recorder.setEnabled(false);
			new CountDownTimer(1000,500) {
				@Override
				public void onTick(long millisUntilFinished) {
				}
				
				@Override
				public void onFinish() {
					iv_recorder.setEnabled(true);
				}
			}.start();
			mHandler.sendMessageDelayed(msg,200);
		}else{
			recorderStatus = unrecording;
			timer.cancel();
			stopProgress();
			mp3Recorder.stop();
			setRecordingViewVisible(1);
			setRecordingViewVisible(2);
			filePath = audioPath;
			mPlayCountTimeTv.setText(currentTime+"\"");
			mHandler.sendEmptyMessageDelayed(2, 2000);
		}
	}
	
	private int currentTime = 0;
	private CountDownTimer timer = new CountDownTimer(241*1000, 1000) {

		@Override
		public void onTick(long millisUntilFinished) { 
			int lastTime = (int) ((millisUntilFinished) / 1000);
			currentTime = 241 - lastTime;
			setProgressValue(currentTime);
			int recordTime = 241 - currentTime;
			if(recordTime < 0)
				this.cancel();
			if(currentTime == 10 && mp3Recorder.getVolume()<=0){
				backListener(false);
				return;
			}
			Log.d("onTick", "currentTime:"+currentTime +"recordTime:"+recordTime);
			mRecordCountTimeTv.setText(String.format("%02d", recordTime) + "S");

		}  

		@Override
		public void onFinish() {  
			mRecordCountTimeTv.setText(String.format("%02d", 241 - currentTime) + "S");
			mp3Recorder.stop();
			mRecordCountTimeTv.setVisibility(View.GONE);
			setRecordingViewVisible(1);
			setRecordingViewVisible(2);
			mHandler.sendEmptyMessageDelayed(2, 200);
			stopProgress();
			iv_recorder.setEnabled(true);
			clickToRecording();
		}  
	}; 
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what) {
			case 2:
				mPlayCountTimeTv.setText(currentTime+"\"");
				mRecordCountTimeTv.setText(currentTime+"\"");
				break;
			case 3:
				if(mp3Recorder!=null && mp3Recorder.isRecording()){
					recorderStatus = recording;
					setRecordingViewVisible(3);
					setMax(240);
					timer.start();
			    }else{
			    	return;
			    }
				break;
			case 4:
				break;
			case 0x06:
				break;
			}
		}
	};
	
	/**
	 * 点击播放我的录音
	 */
	private void click2PlayMyRecord(){
		if(unplaying == playStatus_record){
			if(filePath == null || "".equals(filePath)){
				return;
			}
			startAnimationDrawable();
//			if(lrcMediaPlayer != null){
//				lrcMediaPlayer.resetLrcMediaPlayer();
//				lrcMediaPlayer = null;
//			}
//
			File file = new File(filePath);
			long length = file.length();
			
			mUploadRecordTimeCount = new ListTimeCount(currentTime*1000,1000,new ListTimeCount.FinishListener() {

				@Override
				public void onFinish(int time) {
					mPlayCountTimeTv.setText(currentTime+"\"");
				}
			});
			mUploadRecordTimeCount.setOnTickListener(new ListTimeCount.OnTickListener() {

				@Override
				public void onTick(long time) {
					mPlayCountTimeTv.setText(time+"\"");
				}
			});
			if(currentTime>0){
				mUploadRecordTimeCount.start();
			}
//			if(file != null && file.exists()){
//				lrcMediaPlayer = new LrcMediaPlayer(getActivity(),filePath,1,null,null,0,this);
//			}else{
//				if(audioPath != null){
//					lrcMediaPlayer = new LrcMediaPlayer(getActivity(),filePath,1,null,null,0,this);
//				}
//			}
			playStatus_record = playing;
		}else if(playing == playStatus_record){
			stopRecordPlay();
		}
	}
	
	private int mMax;

	public void setProgressValue(int value) {
		mRecordProgress.setProgress(value);
	}
	
	public void setMax(int max) {
		if (mRecordProgress != null) {
			mRecordProgress.setMax(max);
		} else {
			mMax = max;
		}
	}
	
	private void stopProgress() {
		setProgressValue(0);
		mRecordProgress.setVisibility(View.GONE);
	}
	
	private void setRecordingViewVisible(int status){
		switch(status){
		case 1:	// 停止录音状态
			mRecordProgress.setProgress(0);
			mRecordProgress.setVisibility(View.INVISIBLE);
			mRecordCountTimeTv.setVisibility(View.INVISIBLE);
			iv_recorder.setBackgroundResource(R.drawable.record_btn2_nor);
			iv_recorder.setVisibility(View.VISIBLE);
			break;
		case 2:	// 显示播放区域
			mPlayContainerRl.setVisibility(View.VISIBLE);
			mRecordingContainerRl.setVisibility(View.GONE);
			mRelativeLayout.setVisibility(View.VISIBLE);
			break;
		case 3: // 正在录音状态
			mPlayContainerRl.setVisibility(View.GONE);
			mRecordingContainerRl.setVisibility(View.VISIBLE);
			mRecordProgress.setProgress(0);
			mRecordProgress.setVisibility(View.VISIBLE);
			mRecordCountTimeTv.setVisibility(View.VISIBLE);
			iv_recorder.setBackgroundResource(R.drawable.bg_record_ing);
			iv_recorder.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	/*
	 *  true 显示回退按钮 false 显示 授权提示
	 */
	private void backListener(boolean isBackDialog){
		stopProgress();
		stopAnimDrawable();
		if(currentTime>0){
			mRecordCountTimeTv.setText(currentTime+"\"");
		}
//		if(lrcMediaPlayer != null && lrcMediaPlayer.getMediaPlayer() != null && lrcMediaPlayer.getMediaPlayer().isPlaying()){
//			pausePlayer();
//		}
		playStatus_record = unplaying;
		if(mp3Recorder != null && mp3Recorder.isRecording()){
			timer.cancel();
			mp3Recorder.stop();
			recorderStatus = unrecording;
			mRecordCountTimeTv.setVisibility(View.GONE);
			setRecordingViewVisible(1);
		}
	}
	
	private void stopAnimDrawable(){
		if (mAnimationDrawable!=null){
			mAnimationDrawable.setOneShot(true);
		}
	}

	private void startAnimationDrawable(){
		if(mAnimationDrawable!=null){
			mAnimationDrawable.setOneShot(false);
			mAnimationDrawable.stop();
			mAnimationDrawable.start();
		}
	}
	
	/**
	 * 
	 * 暂停播放
	 * */
	public void pausePlayer(){
//		if(lrcMediaPlayer != null && lrcMediaPlayer.getMediaPlayer() != null && lrcMediaPlayer.getMediaPlayer().isPlaying()){
//			lrcMediaPlayer.getMediaPlayer().pause();
//			lrcMediaPlayer.resetLrcMediaPlayer();
//		}
	}

//	@Override
//	public void setPlayStatus(int status) {
//		stopAnimDrawable();
//		playStatus_record = status;
//	}
	
	/*
	 * 停止录音的播放状态
	 */
	private void stopRecordPlay(){
		stopAnimDrawable();
		if(mUploadRecordTimeCount!=null){
			mUploadRecordTimeCount.stop();
		}
		if(currentTime>0){
			mPlayCountTimeTv.setText(currentTime+"\"");
		}
//		if(lrcMediaPlayer != null && lrcMediaPlayer.getMediaPlayer() != null && lrcMediaPlayer.getMediaPlayer().isPlaying()){
//			pausePlayer();
//		}
		playStatus_record = unplaying;
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		stopMusic();
		stopRecordPlay();
		mWakeLock.release();
	}
}

