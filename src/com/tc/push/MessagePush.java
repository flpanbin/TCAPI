/**
 * 
 */
package com.tc.push;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.google.gson.Gson;
import com.tc.conf.Config;
import com.tc.model.Notification;
import com.tc.model.User;
import com.tc.push.template.TemplateUtil;

/**
 * @author PB
 *
 */
public class MessagePush
{

	public static void pushMessage(String cid, String content)
	{
		IGtPush push = new IGtPush(Config.gt_host, Config.appKey, Config.masterSecret);
		TransmissionTemplate template = TemplateUtil.getTransmissionTemplate(content);
		// LinkTemplate template = TemplateUtil.getLinkTemplate();
		SingleMessage message = new SingleMessage();
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 3600 * 1000);
		message.setData(template);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		message.setPushNetWorkType(0);
		Target target = new Target();
		target.setAppId(Config.appId);
		target.setClientId(cid);
		IPushResult ret = null;
		try
		{
			ret = push.pushMessageToSingle(message, target);
		} catch (RequestException e)
		{
			e.printStackTrace();
			ret = push.pushMessageToSingle(message, target, e.getRequestId());
		}
		if (ret != null)
		{
			System.out.println(ret.getResponse().toString());
		} else
		{
			System.out.println("服务器响应异常");
		}

	}

	/**
	 * Send a message,notify needed.
	 * 
	 * @param toUserId
	 *            receiver userId
	 * @param type
	 *            message type 0:评论消息 1:点赞消息 2:系统消息 3:其他设备登录
	 * 
	 */
	public static void sendMessage(String toUserId, int type)
	{

		sendMessage(toUserId, type, true);
	}

	/**
	 * Send a message.
	 * 
	 * @param toUserId
	 * @param type
	 *            0:评论消息 1:点赞消息 2:系统消息
	 * @param notify
	 *            whether need notify
	 */
	public static void sendMessage(String toUserId, int type, boolean notify)
	{
		new Thread()
		{
			public void run()
			{
				User user = User.dao.findById(toUserId);
				Notification notification = new Notification(type, user.getUserId(), notify);
				Gson gson = new Gson();
				String content = gson.toJson(notification);
				// System.out.println("user.getUserCid()--->" +
				// user.getUserCid());
				// System.out.println("content--->" + content);
				MessagePush.pushMessage(user.getUserCid(), content);
			};
		}.start();
	}

	public static void sendMessage(String toUserId, String userCid, int type)
	{
		new Thread()
		{
			public void run()
			{
				// User user = User.dao.findById(toUserId);
				Notification notification = new Notification(type, toUserId, false);
				Gson gson = new Gson();
				String content = gson.toJson(notification);
				MessagePush.pushMessage(userCid, content);
			};
		}.start();
	}
}
