package com.scott.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.scott.dao.FiDAO;

import net.sf.json.JSONObject;

public class Business implements IBusiness {

    private FiDAO fiDAO;	
	public FiDAO getFiDAO() {
		return fiDAO;
	}
	public void setFiDAO(FiDAO fiDAO) {
		this.fiDAO = fiDAO;
	}

	@Override
	public void sendPost(Connection conn, HttpClient client, PostMethod method, String url, String vsid, String depCode, String sign, JSONObject param, String idCard) {
		
		method = new PostMethod(url);
		try {
			NameValuePair[] body = new NameValuePair[] { 
				new NameValuePair("id", depCode),
				new NameValuePair("sign", sign), 
				new NameValuePair("param", param.toString()) 
			};
			method.setRequestBody(body);
			
			client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			method.addRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
			method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
						
			int statusCode = client.executeMethod(method);
			
			System.out.println("码值 = " + statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				String string = method.getResponseBodyAsString();

				String regexResult = ".*result\\\":\"(\\d)\\\".*";
				String result = string.replaceAll(regexResult, "$1");
				/** 返回错误 */
				if (!result.equals("1")) {					
					System.out.println("\t" + string.replaceAll(".*message\\\":\\\"(.*?)\\\",.*", "$1"));
					return;
				}
				
				String regex = ".*columnCount\\\":(\\d*),\\\"rowCount\\\":(\\d*).*";
				System.out.println(string.replaceAll(regex, "\t数据项个数\t$1\n数据量条数\t$2"));
				String[] arr = string.replaceAll(regex, "$1,$2").split(",");
				/** 单页超过50条 */
				if (Integer.valueOf(arr[1]) > 50) {
					System.out.println("\t身份证号码 " + idCard + "**** 超过50条");
					File file = new File("/home/idCard.txt");
					if (!file.exists()) file.mkdirs();
					PrintStream out = new PrintStream(new FileOutputStream(file, true), true, "UTF-8");
					out.println(idCard);
					out.close();
					return;
				}
				
				String regexColumns = ".*columns\\\":\\[(.*)\\],\\\"rows.*";
				System.out.println(string.replaceAll(regexColumns, "\t数据项名称\t$1"));
				
				String regexRows = ".*rows\\\":\\[(.*)\\]\\}\\}";
				string = string.replaceAll(regexRows, "$1");
				if (string.equals("")) return;
				
				String[] arrData = string.split("(?<=\\]),(?=\\[)");
				
				for(String dt : arrData) {
					String regexRow = "\\[(.*)\\]";
					dt = dt.replaceAll(regexRow, "$1");
					String[] arrD = dt.split("(?<=\\}),(?=\\{)");
					StringBuffer sbf = new StringBuffer();
					for (String d : arrD) {
						sbf.append("'");
						sbf.append(d.replaceAll(".*value\\\":\\\"(.*)\\\",.*", "$1"));
						sbf.append("',");	
					}
					String sql = sbf.toString();
					sql = sql.substring(0, sql.length() - 1);
					
					sql = "insert into zrrInfo(id,gmsfhm,xm,zym,xb,ssxq,mz,csrq,hh,hlx,yhzgx,jgssx,zt,pcs,csdxz,ssjwh,hsql,rownum_) values(sys_guid()," + sql + ")";
					System.out.println("执行语句\t" + sql);
//					fiDAO.save(conn, sql);
					
				}
			}
		} catch (Exception e) {
			
		}
		
	}

}
