/**
 * 工程名: xiaomayasi
 * 文件名: BaseStepThirdActivity.java
 * 包名: com.xiaoma.ielts.android.view.step
 * 日期: 2015年10月9日上午10:00:46
 * QQ: 378640336
 *
*/

package com.xiaoma.ielts.android.step;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ielts.android.AppConstant;
import com.xiaoma.ielts.android.BaseFragmentActivity;
import com.xiaoma.ielts.android.R;

/**
 * 类名: BaseStepThirdActivity <br/>
 * 功能: TODO 添加功能描述. <br/>
 * 日期: 2015年10月9日 上午10:00:46 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class BaseStepThirdActivity extends BaseFragmentActivity implements View.OnClickListener{

	StepThirdFragment thirdFragment = null;
	
	private String flag = "";
	private ImageView iv_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step);
		getIntents();
		initView();
		thirdFragment = new StepThirdFragment();
		FragmentTransaction transactioin = getSupportFragmentManager().beginTransaction();
		transactioin.add(R.id.step_container, thirdFragment);
		transactioin.commit();
//		setProgressScore(StudyContollerImpl.getInstance(this).getSumScore(),
//				StudyContollerImpl.getInstance(this).getTotalScore());
		AppConstant.countSteps++;
	}

	@Override
	protected void init() {
	}

	@Override
	protected void initView() {
		String subject = "";
		iv_back = (ImageView) findViewById(R.id.tv_login_back);
		iv_back.setOnClickListener(this);
		((TextView)findViewById(R.id.tv_login_title)).setText(subject+" - "+flag);
		
	}
	
	/*
	 * 接收Intent传值
	 */
	private void getIntents(){
		if(null != getIntent()){
			if(null != getIntent().getExtras() && !"".equals(getIntent().getExtras().getString("flag"))){
				flag  = getIntent().getExtras().getString("flag");
			}else{
				return;
			}
		}else{
			return;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_login_back:
			finish();
			break;			
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			getSupportFragmentManager().beginTransaction().remove(thirdFragment);
			thirdFragment.onDestroy();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}

