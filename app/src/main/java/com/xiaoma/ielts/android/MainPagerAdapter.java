/**
 * 工程名: xiaomayasi
 * 文件名: MainPagerAdapter.java
 * 包名: com.xiaoma.ielts.android.view.adapter
 * 日期: 2015-7-10下午2:18:56
 * Copyright (c) 2015, 北京小马过河教育科技有限公司 All Rights Reserved.
 * http://www.xiaoma.com/
 * Mail: leixun@xiaoma.cn
 * QQ: 378640336
 *
*/

package com.xiaoma.ielts.android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * 类名: MainPagerAdapter <br/>
 * 功能: 首页面的滑动适配器. <br/>
 * 日期: 2015-7-10 下午2:18:56 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class MainPagerAdapter extends NameVisibleFragmentPagerAdapter{

	private ArrayList<String> fragmentNameList;
	private ArrayList<Fragment> fragmentList;
	private int primaryPosition;
	public String[] titles;
	
	public MainPagerAdapter(FragmentManager fm, ArrayList<String> fragmentNameList) {
		super(fm);
		this.fragmentNameList = fragmentNameList;
	}

    @Override
    public Fragment getItem(int position) {
        String className = fragmentNameList.get(position);
        Class clazz;
        try {
            clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor();
            Fragment fragment = (Fragment) constructor.newInstance();
            if (primaryPosition == position) {
                setPrimaryItem(null, position, fragment);
            }
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
    public void setTitleArray(String[] str){
    	this.titles = str;
    }
    
    public void setPrimaryPosition(int primaryPosition) {
        this.primaryPosition = primaryPosition;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
    	if(null == titles)
    		return null;
    	return titles[position];
    }

	@Override
	public int getCount() {
		return fragmentNameList!=null ? fragmentNameList.size():0;
	}

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((Fragment)object).getView();
    }
}

