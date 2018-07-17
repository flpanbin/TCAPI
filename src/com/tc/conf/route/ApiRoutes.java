package com.tc.conf.route;

import com.jfinal.config.Routes;
import com.tc.controller.CommonController;
import com.tc.controller.HelloController;
import com.tc.controller.MessageController;
import com.tc.controller.TcController;
import com.tc.controller.UserController;

public class ApiRoutes extends Routes
{

	@Override
	public void config()
	{
		// TODO Auto-generated method stub
		add("/api/user", UserController.class);
		add("/api/hello", HelloController.class);
		add("/api/common", CommonController.class);
		add("/api/tc", TcController.class);
		add("/api/message", MessageController.class);

	}
}
