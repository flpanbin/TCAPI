package com.tc.controller;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.tc.bean.BaseResponse;
import com.tc.bean.Code;
import com.tc.bean.DataResponse;
import com.tc.bean.Require;
import com.tc.model.Activity;
import com.tc.model.TcTarget;
import com.tc.model.TcType;
import com.tc.model.Version;
import com.tc.utils.StringUtil;

public class CommonController extends BaseController
{
	/**
	 * 获取吐槽类型
	 */

	public void getTcTypeInfo()
	{
		String schoolId = getPara("schoolId");
		if (StringUtil.isEmpty(schoolId))
		{
			renderJson(new BaseResponse(Code.PARAM_ERROR, "schoolId is null"));
			return;
		}
		String strSql = "select * from t_tc_type where school_id=? and up_type_id = type_id";
		List<TcType> tcTypes = TcType.dao.find(strSql, schoolId);

		renderJson(new DataResponse<List<TcType>>(Code.SUCCESS, "", tcTypes));
	}

	/**
	 * 搜索吐槽对象
	 */
	public void searchTarget()
	{
		String text = getPara("text");
		// try
		// {
		// text = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		// } catch (UnsupportedEncodingException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		String schoolId = getPara("schoolId");
		if (!renderParamsNull(new Require().put(text, "text is null").put(schoolId, "schoolId is null")))
			return;

		String strSql = "select a.target_id,a.target_name,b.type_id,b.type_name,c.type_id up_type_id,c.type_name up_type_name from t_tc_target a,t_tc_type b,t_tc_type c where target_name  LIKE ? and a.school_id = ? and a.type_id=b.type_id and b.up_type_id = c.type_id";
		List<Record> records = Db.find(strSql, '%' + text + '%', schoolId);
		TcTarget target;
		List<TcTarget> targets = new ArrayList<TcTarget>();
		for (Record record : records)
		{
			target = new TcTarget();
			target.set(TcTarget.TARGET_ID, record.get(TcTarget.TARGET_ID))
					.set(TcTarget.TARGET_NAME, record.get(TcTarget.TARGET_NAME))
					.set(TcTarget.TYPE_ID, record.get(TcTarget.TYPE_ID))
					.set(TcTarget.UP_TYPE_ID, record.get(TcTarget.UP_TYPE_ID))
					.put(TcTarget.UP_TYPE_NAME, record.get(TcTarget.UP_TYPE_NAME))
					.put(TcTarget.TYPE_NAME, record.get(TcTarget.TYPE_NAME));
			targets.add(target);
		}
		renderJson(new DataResponse<List<TcTarget>>(Code.SUCCESS, targets));
	}

	/**
	 * 根据顶级吐槽类型获取吐槽对象
	 */
	public void getTargetByType()
	{
		String schoolId = getPara("schoolId");
		String typeId = getPara("typeId");

		if (!renderParamsNull(new Require().put(schoolId, "schoolId is null").put(typeId, "typeId is null")))
			return;
		String strSql = "select a.target_id,a.target_name,a.type_id,a.up_type_id,b.type_name from t_tc_target a,t_tc_type b where a.up_type_id = ? and a.school_id = ? and a.type_id = b.type_id";
		List<TcTarget> targets = TcTarget.dao.find(strSql, typeId, schoolId);
		renderJson(new DataResponse<List<TcTarget>>(Code.SUCCESS, "", targets));
	}

	/**
	 * 搜索吐槽对象
	 */
	public void searchTargetInType()
	{
		String text = getPara("text");
		// System.out.println("text---" + text);
		// try
		// {
		// text = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		// } catch (UnsupportedEncodingException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		String schoolId = getPara("schoolId");
		String typeId = getPara("typeId");
		if (!renderParamsNull(new Require().put(text, "text is null").put(schoolId, "schoolId is null")
				.put(typeId, "typeId is null")))
			return;
		String strSql = "select a.target_id,a.target_name,a.type_id,c.type_name from t_tc_target a,t_tc_type b,t_tc_type c where target_name LIKE ? and a.school_id = ? and a.up_type_id = ? and a.up_type_id=b.type_id and a.type_id = c.type_id";
		List<Record> records = Db.find(strSql, '%' + text + '%', schoolId, typeId);
		TcTarget target;
		List<TcTarget> targets = new ArrayList<TcTarget>();
		for (Record record : records)
		{
			target = new TcTarget();
			target.set(TcTarget.TARGET_ID, record.get(TcTarget.TARGET_ID))
					.set(TcTarget.TARGET_NAME, record.get(TcTarget.TARGET_NAME))
					.set(TcTarget.TYPE_ID, record.get(TcTarget.TYPE_ID))
					.put(TcTarget.TYPE_NAME, record.get(TcTarget.TYPE_NAME));
			targets.add(target);
		}
		renderJson(new DataResponse<List<TcTarget>>(Code.SUCCESS, targets));
	}

	public void getActivities()
	{
		String schoolId = getPara("schoolId");
		if (StringUtil.isEmpty(schoolId))
		{
			renderJson(new BaseResponse(Code.PARAM_ERROR, "schoolId is null"));
			return;
		}
		List<Activity> activites = Activity.dao.find("select * from t_activity  ORDER BY end_time ASC");
		renderJson(new DataResponse<List<Activity>>(Code.SUCCESS, activites));
	}

	/**
	 * 获取最新的版本信息
	 */
	public void getVersion()
	{
		String schoolId = getPara("schoolId");
		if (StringUtil.isEmpty(schoolId))
		{
			renderJson(new BaseResponse(Code.PARAM_ERROR, "schoolId is null"));
			return;
		}
		Version version = Version.dao.findFirst("select * from t_version order by version_code desc");
		renderJson(new DataResponse<Version>(Code.SUCCESS, version));
	}

	public void getVersionInfoByVersionCode()
	{
		String versionCode = getPara("version_code");
		if (StringUtil.isEmpty(versionCode))
		{
			renderJson(new BaseResponse(Code.PARAM_ERROR, "version code is null"));
			return;
		}
		String strSql = "select * from t_version where version_code =?";
		Version version = Version.dao.findFirst(strSql, versionCode);
		renderJson(new DataResponse<Version>(Code.SUCCESS, version));
	}
}
