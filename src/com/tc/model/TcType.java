package com.tc.model;

import java.util.Map;

import com.jfinal.plugin.activerecord.Model;

public class TcType extends Model<TcType> {
	public static final TcType dao = new TcType().dao();
	public static String TYPE_ID = "type_id";
	public static String TYPE_NAME = "type_name";
	public static String UP_TYPE_ID = "up_type_id";
	public static String SCHOOL_ID = "school_id";

	@Override
	public Map<String, Object> getAttrs() {
		// TODO Auto-generated method stub
		return super.getAttrs();
	}
}
