/**
 * 工程名: xiaomayasi
 * 文件名: WordRect.java
 * 包名: com.xiaoma.ielts.android.common.weight.stepthird
 * 日期: 2015年9月28日上午9:59:50
 * QQ: 378640336
 *
*/

package com.xiaoma.ielts.android.widget.stepthird;
/**
 * 类名: WordRect <br/>
 * 功能: TODO 添加功能描述. <br/>
 * 日期: 2015年9月28日 上午9:59:50 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class WordRect {
	private float left;
	private float top;
	private float right;
	private float bottom;
	
	private float width;
	private float height;
	
	public WordRect(float width,float height){
		this.width = width;
		this.height = height;
	}
	
	public float getLeft() {
		return left;
	}
	public void setLeft(float left) {
		this.left = left;
	}
	public float getTop() {
		return top;
	}
	public void setTop(float top) {
		this.top = top;
	}
	public float getRight() {
		return right;
	}
	public void setRight(float right) {
		this.right = right;
	}
	public float getBottom() {
		return bottom;
	}
	public void setBottom(float bottom) {
		this.bottom = bottom;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	
}

