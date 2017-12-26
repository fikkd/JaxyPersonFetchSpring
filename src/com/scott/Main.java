package com.scott;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.context.ApplicationContext;

import com.scott.common.CommonUtil;
import com.scott.service.IBusiness;
import com.thinvent.lifang.QuerySign;

import net.sf.json.JSONObject;

public class Main {

	public static void main(String[] args) {
		ApplicationContext context = CommonUtil.getSpringApplicationContext();
		IBusiness service = (IBusiness) context.getBean("business");

		run(context, service);		
	}
	
	
	public static void run(ApplicationContext context, IBusiness service) {
		Connection conn = CommonUtil.getConnection(context);
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod();
		try {

			InputStream input = new FileInputStream("conf/init.properties");
			Properties prop = new Properties();
			prop.load(input);
			
			/** 取出参数 */
			String vsid = prop.get("vsid").toString();
			String key = prop.get("key").toString();
			String depCode = prop.get("depCode").toString();
			String _servicecode = prop.get("_servicecode").toString();
			String _token = prop.get("_token").toString();
			String url = prop.get("url").toString();
			String zone =  prop.get("zone").toString();
			int year = Integer.valueOf(prop.get("year").toString());
			int month = Integer.valueOf(prop.get("month").toString());
			int day = Integer.valueOf(prop.get("day").toString());
			
			url = url + "?_servicecode=" + _servicecode + "&_token=" + _token + "&_refuladdress=" + vsid + "/query";

			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Date date;
			Calendar cEnd = Calendar.getInstance();
			long end = cEnd.getTimeInMillis();

			Calendar cStart = Calendar.getInstance();
			cStart.set(year, month - 1, day);
			long start = cStart.getTimeInMillis();
			long days = (end - start) / (1000 * 3600 * 24);
			
			System.out.println("区划\t" + zone);
			String[] zArr = zone.split(",");
			if (zArr.length == 0) return;
			
			for (String z : zArr) {
				for (int i = 0; i < days; i++) {
					System.out.println("当前区划\t" + z);
					try {
						cStart.add(Calendar.DAY_OF_MONTH, 1);
						date = cStart.getTime();
						String d = format.format(date);
						sendPost(prop, conn, client, method, service, d, url, vsid, depCode, key, z);
					} catch (Exception e) {						
					}
				}
			}
		} catch (Exception e) {
		}

		method.releaseConnection();
	}
	

	/**
	 * 
	 * 发送请求
	 *
	 * @变更记录 2017年12月21日 下午1:36:10 李瑞辉 创建
	 *
	 */
	public static void sendPost(Properties prop, Connection conn, HttpClient client, PostMethod method, IBusiness service, String cardYYMMDD, String url, String vsid, String depCode, String key, String zone) {
		try {

			String sign = null;
			JSONObject param = new JSONObject();

			/** 构造查询参数 */
			param.put("idcard_IS", zone + cardYYMMDD);
			param.put("pagesize", Integer.valueOf(prop.get("pagesize").toString()));
			param.put("currentpage", Integer.valueOf(prop.get("currentpage").toString()));
			System.out.println("参数\t" + param.toString());
			/** 生成sign */
			sign = QuerySign.signQuery(vsid, depCode, key, param.toString());

			System.out.println("sign\t" + sign);
			if (sign == null || sign == "") {
				System.out.println("生成sign失败");
			}

			/** 发送请求获取数据 */
			service.sendPost(conn, client, method, url, vsid, depCode, sign, param, zone + cardYYMMDD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
