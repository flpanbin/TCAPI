package com.tc.model;

import java.util.Map;

import com.jfinal.plugin.activerecord.Model;

public class User extends Model<User>
{

	public static final User dao = new User().dao();
	public static String USER_ID = "user_id";
	public static String USER_NAME = "user_name";
	public static String NICK_NAME = "nick_name";
	public static String PASSWORD = "password";
	public static String SEX = "sex";
	public static String SCHOOL = "school";
	public static String CREATE_DATE = "create_time";
	public static String AVATAR = "avatar";
	public static String BIRTHDAY = "birthday";
	public static String IDENTITY_TYPE = "identity_type";
	public static String ENTRANCE_TIME = "entrance_time";
	public static String USER_STATE = "user_state";
	public static String USER_TYPE = "user_type";

	/**
	 * 获取UserId
	 * 
	 * @return USER_ID
	 */
	public String getUserId()
	{
		return getInt(USER_ID) + "";

	}

	/**
	 * 获取用户的cid
	 * 
	 * @return
	 */
	public String getUserCid()
	{
		return getStr("cid");
	}

	/**
	 * 检查性别
	 * 
	 * @param sex
	 */
	public static final boolean checkSex(int sex)
	{
		return sex == 1 || sex == 0;
	}

	@Override
	public Map<String, Object> getAttrs()
	{
		// TODO Auto-generated method stub
		return super.getAttrs();
	}

	public void setPassword(String password)
	{
		set(User.PASSWORD, password);
	}

}
