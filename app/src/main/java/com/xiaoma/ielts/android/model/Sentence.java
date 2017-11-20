/**
 * 工程名: xiaomayasi
 * 文件名: SqlSubList.java
 * 包名: com.xiaoma.ielts.android.model
 * 日期: 2015年9月23日下午4:59:52
 * QQ: 2920084022
 *
*/

package com.xiaoma.ielts.android.model;
/**
 * 类名: SqlSubList <br/>
 * 功能: TODO 添加功能描述. <br/>
 * 日期: 2015年9月23日 下午4:59:52 <br/>
 *
 * @author   leixun
 * @version  	 
 */
public class Sentence {

	private int id = 1;
	private int parentId = 12;
	private String subId = "subId";
	private String subject = "he baby eagle was hungry all the time.";
	private String topicChin = "我也不知道什么意思";
	private String topicEng = "he baby eagle was hungry all the time, but the mother eagle would always come just in time with the food and love and attention he craved.";
    private int score;
	
	@Override
	public String toString() {
		return "SqlSubList [id=" + id + ", parentId=" + parentId + ", subId="
				+ subId + ", subject=" + subject + ", topicChin=" + topicChin
				+ ", topicEng=" + topicEng + "]";
	}
	public Sentence() {
		super();
	}
	public Sentence(int id, int parentId, String subId,
                    String subject, String topicChin, String topicEng) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.subId = subId;
		this.subject = subject;
		this.topicChin = topicChin;
		this.topicEng = topicEng;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getSubId() {
		return subId;
	}
	public void setSubId(String subId) {
		this.subId = subId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTopicChin() {
		return topicChin;
	}
	public void setTopicChin(String topicChin) {
		this.topicChin = topicChin;
	}
	public String getTopicEng() {
		return topicEng;
	}
	public void setTopicEng(String topicEng) {
		this.topicEng = topicEng;
	}
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

}

