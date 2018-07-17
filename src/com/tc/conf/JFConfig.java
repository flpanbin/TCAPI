package com.tc.conf;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.tc.conf.route.ApiRoutes;
import com.tc.controller.HelloController;
import com.tc.model.Activity;
import com.tc.model.Comment;
import com.tc.model.Message;
import com.tc.model.TcContent;
import com.tc.model.TcTarget;
import com.tc.model.TcType;
import com.tc.model.User;
import com.tc.model.UserCollect;
import com.tc.model.UserLike;
import com.tc.model.Version;

public class JFConfig extends JFinalConfig
{

	@Override
	public void configConstant(Constants me)
	{
		// TODO Auto-generated method stub
		// me.setBaseDownloadPath(Config.UPLOAD_PATH);
		// me.setBaseUploadPath(Config.UPLOAD_PATH);
		// Config.UPLOAD_PATH = me.getBaseUploadPath();
	}

	@Override
	public void configRoute(Routes me)
	{
		// TODO Auto-generated method stub
		// me.add("/hello", HelloController.class);
		me.add(new ApiRoutes());
	}

	@Override
	public void configEngine(Engine me)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void configPlugin(Plugins me)
	{
		// TODO Auto-generated method stub
		loadPropertyFile("jdbc.properties");
		DruidPlugin dp = new DruidPlugin(getProperty("jdbcUrl"), getProperty("user"), getProperty("password"),
				getProperty("driverClass"));
		// dp.setInitialSize(initialSize)
		// dp.set(INITIALSIZE, MIDIDLE, MAXACTIVEE);
		// dp.addFilter(new StatFilter());
		dp.setValidationQuery("SELECT 1");
		dp.setConnectionInitSql("set names utf8mb4;");
		dp.setTestWhileIdle(true);
		dp.setTestOnBorrow(true);
		dp.setTestOnReturn(true);
		// WallFilter wall = new WallFilter();
		// wall.setDbType("mysql");
		// druidPlugin.addFilter(wall);
		dp.setFilters("stat,wall");
		me.add(dp);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		me.add(arp);
		arp.addMapping("t_user", "user_id", User.class);
		arp.addMapping("t_tc_type", "type_id", TcType.class);
		arp.addMapping("t_tc_content", "content_id", TcContent.class);
		arp.addMapping("t_tc_content_comment", "comment_id", Comment.class);
		arp.addMapping("t_tc_user_like", "like_id", UserLike.class);
		arp.addMapping("t_tc_user_collect", "collect_id", UserCollect.class);
		arp.addMapping("t_tc_target", "target_id", TcTarget.class);
		arp.addMapping("t_message", "message_id", Message.class);
		arp.addMapping("t_activity", "activity_id", Activity.class);
		arp.addMapping("t_version", "version_id", Version.class);

	}

	@Override
	public void configInterceptor(Interceptors me)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void configHandler(Handlers me)
	{
		// TODO Auto-generated method stub

	}

}
