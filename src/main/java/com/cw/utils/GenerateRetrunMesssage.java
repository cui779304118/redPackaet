package com.cw.utils;

import com.alibaba.fastjson.JSONObject;

public class GenerateRetrunMesssage {
	
	public static JSONObject createSuccessMessage(JSONObject data, String message){
		JSONObject json = new JSONObject();
		json.put("errorCode", 0);
		json.put("data", data);
		json.put("message", message);
		return json;
	}
	
	public static JSONObject createFailMessage(String message){
		JSONObject json = new JSONObject();
		json.put("errorCode", -1);
		json.put("message", message);
		return json;
	}


}
