/**
 * 
 */
package com.tc.utils;

import com.tc.data.Constant;
import com.tc.model.Message;
import com.tc.push.MessagePush;

/**
 * @author PB
 *
 */
public class CommonUtil
{
	/**
	 * 发送一条系统消息并通知
	 * @param msgContent
	 * @param userId
	 */
	public static void sendSystemMessage(String msgContent, String userId)
	{
		new Message().set(Message.CONTENT, msgContent).set(Message.USER_ID, userId)
				.set(Message.CREATE_TIME, DateUtil.getCurrentTime()).save();
		MessagePush.sendMessage(userId, 2);
	}
}
