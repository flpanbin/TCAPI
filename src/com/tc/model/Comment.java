package com.tc.model;

import com.jfinal.plugin.activerecord.Model;

public class Comment extends Model<Comment> {

	public static final Comment dao = new Comment().dao();
	public static String COMMENT_ID = "comment_id";
	public static String COMMENT_CONTENT = "comment_content";
	public static String COMMENT_TIME = "comment_time";
	public static String USER_ID = "from_user_id";
	public static String NICK_NAME = "nick_name";
	public static String AVATAR = "avatar";
	public static String TO_NICK_NAME = "to_nick_name";
	public static String TO_COMMENT_CONTENT = "to_comment_content";
	public static String ANONYMOUS = "anonymous";
	public static String TO_USER_ID = "to_user_id";
	public static String CONTENT_ID = "content_id";
	public static String TO_COMMENT_ID = "to_comment_id";

}
