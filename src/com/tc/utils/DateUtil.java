package com.tc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil
{
	
	/**
	 * 获取当前时间 格式为yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurrentDate()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = format.format(new Date());
		return strDate;
	}

	/**
	 * 获取当前时间精确到秒 格式为 yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentTime()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = format.format(new Date());
		return strDate;
	}

	/**
	 * 获取当前时间精确到秒 格式为 yyyyMMddHHmmss
	 * 
	 * @return
	 */
	public static String getCurrentTimeNoDel()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String strDate = format.format(new Date());
		return strDate;
	}

	/**
	 * 获取系统时间戳
	 * 
	 * @return
	 */
	public static Long getCurrentTimeStamp()
	{
		return System.currentTimeMillis();
	}

	/**
	 * 获取当前系统时间戳 精确到秒
	 * 
	 * @return
	 */
	public static Long getNowDateTime()
	{
		return new Date().getTime() / 1000;
	}

	/**
	 * 获取时间差
	 * 
	 * @param start
	 * @param end
	 * @return 返回相差天数
	 */
	public static int timeDiff(String start, String end)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			Date startDate = format.parse(start);
			Date endDate = format.parse(end);
			long days = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
			return (int) days;
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	/**
	 * 将时间字符转成Unix时间戳
	 * 
	 * @param timeStr
	 * @return
	 * @throws java.text.ParseException
	 */
	public static Long getTime(String timeStr) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(timeStr);
		return date.getTime() / 1000;
	}

	/**
	 * 将Unix时间戳转成时间字符
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getTime(long timestamp)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(timestamp * 1000);
		return sdf.format(date);
	}

}
