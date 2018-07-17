package com.tc.model;

import com.jfinal.plugin.activerecord.Model;

public class TcTarget extends Model<TcTarget>
{
	public static final TcTarget dao = new TcTarget().dao();
	public static String TARGET_ID = "target_id";
	public static String TARGET_NAME = "target_name";
	public static String TYPE_ID = "type_id";
	public static String TYPE_NAME = "type_name";
	public static String UP_TYPE_NAME = "up_type_name";
	public static String UP_TYPE_ID = "up_type_id";

}
