/**
 * 
 */
package com.tc.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author PB
 *
 */
public class Message extends Model<Message>
{
	public static final Message dao = new Message().dao();

	public static final String CONTENT = "content";
	public static final String MESSAGE_ID = "message_id";
	public static final String CREATE_TIME = "create_time";
	public static final String USER_ID = "user_id";
}
