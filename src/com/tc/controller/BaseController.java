package com.tc.controller;

import java.lang.reflect.Array;

import com.jfinal.core.Controller;
import com.tc.bean.BaseResponse;
import com.tc.bean.Code;
import com.tc.bean.Require;
import com.tc.model.User;
import com.tc.token.TokenManage;
import com.tc.utils.StringUtil;

public class BaseController extends Controller
{

	/**
	 * @param token
	 * @return
	 */
	protected User getUser()
	{
		User user = getAttr("user");
		if (user == null)
		{
			String token = getPara("token");
			return !StringUtil.isEmpty(token) ? null : TokenManage.getInstance().validToken(token);
		}
		return user;
	}

	/**
	 * 响应404
	 */
	public void render404()
	{
		renderJson(new BaseResponse(Code.NOT_FOUND));
	}

	/**
	 * 参数错误
	 */
	public void renderParamsError(String message)
	{
		renderJson(new BaseResponse(Code.PARAM_ERROR, message));
	}

	/**
	 * 禁言状态
	 */
	public void renderProhibit()
	{
		renderJson(new BaseResponse(Code.PROHIBIT));
	}

	/**
	 * 成功
	 */
	public void renderSuccess()
	{
		renderJson(new BaseResponse(Code.SUCCESS));
	}

	/**
	 * 失败
	 * 
	 * @param message
	 */
	public void renderFail(String message)
	{
		renderJson(new BaseResponse(Code.FAIL, message));
	}

	/**
	 * 检验参数是否不为空
	 * 
	 * @param require
	 * @return true:如果参数都不为空
	 */
	public boolean renderParamsNull(Require require)
	{

		if (require == null || require.getLength() == 0)
			return true;
		int len = require.getLength();
		for (int i = 0; i < len; i++)
		{
			Object condition = require.getCondition(i);
			String message = require.getMessage(i);
			BaseResponse response = new BaseResponse(Code.PARAM_ERROR);
			if (condition == null)
			{
				renderJson(response.setMessage(message));
				return false;
			}
			if (condition instanceof String && StringUtil.isEmpty((String) condition))
			{
				renderJson(response.setMessage(message));
				return false;
			}
			if (condition instanceof Array)
			{
				Object[] arr = (Object[]) condition;
				if (arr.length <= 0)
				{
					renderJson(response.setMessage(message));
					return false;
				}
			}
		}
		return true;
	}
}
