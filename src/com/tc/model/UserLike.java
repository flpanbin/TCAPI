package com.tc.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * 吐槽内容点赞表
 * @author PB
 *
 */
public class UserLike extends Model<UserLike> {

	public static final UserLike dao = new UserLike().dao();
	public static String USER_ID = "user_id";
	public static String CONTENT_ID = "content_id";
	public static String LIKE_TIME = "like_time";
}
