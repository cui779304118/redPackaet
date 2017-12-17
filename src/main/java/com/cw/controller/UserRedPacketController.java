package com.cw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.cw.service.UserRedPacketService;

@RequestMapping(value="/userRedPacket")
public class UserRedPacketController {
	@Autowired
	private UserRedPacketService userRedPacketService = null;
	
	@RequestMapping(value="/grapRedPacket")
	public JSONObject grapRedPacket(Long redPacketId,Long userId){
		int result = userRedPacketService.grapRedPacket(redPacketId, userId);
		boolean flag = result>0;
		JSONObject json = new JSONObject();
		json.put("success", flag);
		json.put("message", flag?"抢红包成功！":"抢红包失败！");
		return json;
	} 
}
