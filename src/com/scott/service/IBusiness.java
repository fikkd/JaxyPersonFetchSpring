package com.scott.service;

import java.sql.Connection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import net.sf.json.JSONObject;

public interface IBusiness {
		
	void sendPost(Connection conn, HttpClient client, PostMethod method, String url, String vsid, String depCode, String sign, JSONObject param, String idCard);
	
	

}
