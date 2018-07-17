package com.tc.model;

import com.jfinal.plugin.activerecord.Model;

public class TcContent extends Model<TcContent> {

	public static final TcContent dao = new TcContent().dao();
	public static String USER_ID = "user_id";
	public static String CONTENT_ID = "content_id";
	public static String RELEASE_TIME = "release_time";
	public static String CONTENT = "content";
	public static String PIC_LIST = "pic_list";
	public static String COUNT_LIKE = "content_like";
	public static String COUNT_COMMENT = "count_commnet";
	public static String TARGET_ID = "target_id";
	public static String TYPE_ID = "type_id";
	public static String ANONYMOUS = "anonymous";
	public static String LIKE_ID = "like_id";
	public static String TARGET_NAME = "target_name";
	public static String TOTAL_PAGE = "total_page";
	public static String COLLECT_ID = "collect_id";
	public static String SCHOOL_ID = "school_id";

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return CONTENT_ID + "--->" + get(CONTENT_ID) + USER_ID + "--->"
				+ get(USER_ID);
	}
}
