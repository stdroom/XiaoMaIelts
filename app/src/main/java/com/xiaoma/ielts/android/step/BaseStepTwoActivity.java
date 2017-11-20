/**
 * 工程名: xiaomayasi
 * 文件名: BaseStepFragment.java
 * 包名: com.xiaoma.ielts.android.common.base
 * 日期: 2015年9月24日下午3:40:14
 * QQ: 2920084022
 *
*/

package com.xiaoma.ielts.android.step;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ielts.android.AppConstant;
import com.xiaoma.ielts.android.BaseFragmentActivity;
import com.xiaoma.ielts.android.MainPagerAdapter;
import com.xiaoma.ielts.android.R;
import com.xiaoma.ielts.android.widget.NoFlingViewPager;

import java.util.ArrayList;

/**
 * 类名: BaseStepFragment <br/>
 * 功能: TODO 添加功能描述. <br/>
 * 日期: 2015年9月24日 下午3:40:14 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class BaseStepTwoActivity extends BaseFragmentActivity {

	private ImageView iv_back;
	private NoFlingViewPager mViewPager;
	private MainPagerAdapter mAdapter;
	private String flag = "";
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.basestep_fragment);
		getIntents();
		init();
		initView();
//		setProgressScore(StudyContollerImpl.getInstance(this).getSumScore(),
//				StudyContollerImpl.getInstance(this).getTotalScore());
		AppConstant.countSteps++;
	}
	
	@Override
	protected void init() {
		String subject = "";
		iv_back = (ImageView) findViewById(R.id.tv_login_back);
		((TextView)findViewById(R.id.tv_login_title)).setText(subject+" - "+flag);
		
		mViewPager = (NoFlingViewPager) findViewById(R.id.fragment_viewpager);
        mViewPager.setFlingEnable(false);
        ArrayList<String> fragmentNameList = new ArrayList<String>();
        fragmentNameList.add(StepTwoFragment.class.getName());
        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragmentNameList);
        mAdapter.setPrimaryPosition(0);
        mViewPager.setAdapter(mAdapter);
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
	protected void initView() {
		iv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			} 
		});
	}
}

