/**
 * 
 */
package com.tc.push.template;

import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.tc.conf.Config;

/**
 * @author PB
 *
 */
public class TemplateUtil
{

	public static TransmissionTemplate getTransmissionTemplate(String content)
	{
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(Config.appId);
		template.setAppkey(Config.appKey);
		template.setTransmissionType(2);
		template.setTransmissionContent(content);
		return template;
		
	}
	
	  public static LinkTemplate getLinkTemplate() {
          LinkTemplate template = new LinkTemplate();
          // 设置APPID与APPKEY
          template.setAppId(Config.appId);
          template.setAppkey(Config.appKey);

          Style0 style = new Style0();
          // 设置通知栏标题与内容
          style.setTitle("请输入通知栏标题");
          style.setText("请输入通知栏内容");
          // 配置通知栏图标
          style.setLogo("icon.png");
          // 配置通知栏网络图标
          style.setLogoUrl("");
          // 设置通知是否响铃，震动，或者可清除
          style.setRing(true);
          style.setVibrate(true);
          style.setClearable(true);
          template.setStyle(style);

          // 设置打开的网址地址
          template.setUrl("http://www.baidu.com");
          return template;
      }
}
