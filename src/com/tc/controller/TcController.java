/**
 * 
 */
package com.tc.controller;

import com.jfinal.aop.Before;
import com.tc.bean.Code;
import com.tc.bean.DataResponse;
import com.tc.bean.Require;
import com.tc.interceptor.TokenInterceptor;
import com.tc.model.TcContent;

/**
 * @author PB
 *
 */
@Before(TokenInterceptor.class)
public class TcController extends BaseController
{

	public void getTcDetailById()
	{
		String userId = getPara("userId");
		String contentId = getPara("contentId");
		String schoolId = getPara("schoolId");
		if (!renderParamsNull(new Require().put(userId, "userId is null").put(contentId, "contentId is null")
				.put(schoolId, "schoolId is null")))
			return;
		String strSql = "select a.*,b.like_id,e.collect_id,c.avatar,c.nick_name,d.target_name from t_user c,t_tc_content a left join (select like_id,content_id from t_tc_user_like where user_id=?) b on a.content_id = b.content_id left join (select collect_id,content_id from t_tc_user_collect where user_id=?) e on a.content_id = e.content_id LEFT JOIN t_tc_target d on a.target_id=d.target_id where a.school_id=? and a.content_id = ? and a.user_id = c.user_id";
		TcContent tcContent = TcContent.dao.findFirst(strSql, userId, userId, schoolId, contentId);
		renderJson(new DataResponse<TcContent>(Code.SUCCESS, tcContent));
	}
}
