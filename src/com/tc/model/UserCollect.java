package com.tc.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * 吐槽内容收藏表
 * @author PB
 *
 */
public class UserCollect extends Model<UserCollect> {

	public static final UserCollect dao = new UserCollect().dao();
	public static String USER_ID = "user_id";
	public static String CONTENT_ID = "content_id";
}
