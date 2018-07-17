package com.tc.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.tc.bean.BaseResponse;
import com.tc.bean.Code;
import com.tc.model.User;
import com.tc.token.TokenManage;
import com.tc.utils.StringUtil;

public class TokenInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		// TODO Auto-generated method stub
		Controller controller = inv.getController();
		String token = controller.getPara("token");
		if (StringUtil.isEmpty(token)) {
			controller.renderJson(new BaseResponse(Code.PARAM_ERROR,
					"token is null"));
			return;
		}
		User user = TokenManage.getInstance().validToken(token);
		if (user == null) {
			controller.renderJson(new BaseResponse(Code.TOKEN_INVALID,
					"token is invalid"));
			return;
		}
		controller.setAttr("user", user);
		inv.invoke();
	}

}
