/**
 * 
 */
package com.tc.model;

/**
 * @author PB
 *
 */
public class Notification
{
	// 0:评论通知；1:点赞通知；2:系统消息
	private int notificationType;
	private String userId;
	// 是否需要通知消息
	private boolean notify;

	public boolean isNotify()
	{
		return notify;
	}

	public void setNotify(boolean notify)
	{
		this.notify = notify;
	}

	public int getNotificationType()
	{
		return notificationType;
	}

	public Notification()
	{
		super();
	}

	/**
	 * 默认需要通知
	 * @param notificationType
	 * @param userId
	 */
	public Notification(int notificationType, String userId)
	{
		super();
		this.notificationType = notificationType;
		this.userId = userId;
		this.notify = true;
	}

	public Notification(int notificationType, String userId, boolean notify)
	{
		super();
		this.notificationType = notificationType;
		this.userId = userId;
		this.notify = notify;
	}

	public void setNotificationType(int notificationType)
	{
		this.notificationType = notificationType;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

}
