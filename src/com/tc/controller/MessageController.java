/**
 * 
 */
package com.tc.controller;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.tc.bean.Code;
import com.tc.bean.DataResponse;
import com.tc.interceptor.TokenInterceptor;
import com.tc.model.Message;
import com.tc.utils.StringUtil;

/**
 * @author PB
 *
 */
@Before(TokenInterceptor.class)
public class MessageController extends BaseController
{

	public void getMessage()
	{

		String userId = getPara("userId");
		if (StringUtil.isEmpty(userId))
		{
			renderParamsError("userId is null");
			return;
		}
		int pageNum = getParaToInt("pageNum", 1);
		Page<Message> page = Message.dao.paginate(pageNum, 20, "select *",
				"from t_message where user_id=? ORDER BY create_time DESC", userId);
		List<Message> data = page.getList();
		renderJson(new DataResponse<List<Message>>(Code.SUCCESS, page.getTotalRow() + "", data));
	}
}
