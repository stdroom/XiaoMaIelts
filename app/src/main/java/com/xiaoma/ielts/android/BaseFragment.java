package com.xiaoma.ielts.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;


public abstract class BaseFragment extends Fragment {

	protected View contentView;
	protected ProgressDialog mProgressDialog;
	protected RelativeLayout mLoadingBar_Rl;

	protected void setContentViewParams(int w,int h){
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.setLayoutParams(params);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProgressDialog = new ProgressDialog(getActivity());
	}

	/** 绑定控件id
	 */
	public View findViewById(int id){
		return contentView.findViewById(id);
	}

	protected abstract void init();

	/**  初始化控件
	 */
	protected abstract void initView();

	/**  通过类名启动Activity
	 */
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	public void onResume() {
		super.onResume();
	}
	public void onPause() {
		super.onPause();
	}

	/**   通过类名启动Activity，并且含有Bundle数据
	 */
	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(getActivity(), pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	/**
	 * 通过Action启动Activity 
	 */
	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	/**  通过Action启动Activity，并且含有Bundle数据
	 */
	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}


}
