package com.cw.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cw.constants.Constants;
import com.cw.service.UserRedPacketService;
import com.cw.utils.GenerateRetrunMesssage;

@Controller
@RequestMapping(value="/userRedPacket")
public class UserRedPacketController {
	private static Logger logger = LoggerFactory.getLogger(com.cw.controller.UserRedPacketController.class);
	@Autowired
	private UserRedPacketService userRedPacketService = null;
	
	@RequestMapping(value="/grapRedPacket")
	@ResponseBody
	public JSONObject grapRedPacket(Long redPacketId,Long userId){
		logger.info("=====================开始抢红包====================");
		int result = userRedPacketService.grapRedPacketForVersion2(redPacketId, userId);
		boolean flag = result>0;
		JSONObject json = new JSONObject();
		json.put("success", flag);
		json.put("message", flag?"抢红包成功":"抢红包失败");
		logger.info("=====================抢红包结束====================");
		return json;
	} 
	
	@RequestMapping(value="/grapRedPacketByRedis")
	@ResponseBody
	public JSONObject grapRedPacketByRedis(Long redPacketId,Long userId){
		logger.info("=====================开始抢红包====================");
		int result = userRedPacketService.grapRedPacketByRedis(redPacketId, userId);
		boolean flag = result!=Constants.Grab_FAILED;
		logger.info("=====================抢红包结束====================");
		return flag ? GenerateRetrunMesssage.createSuccessMessage(null, "恭喜您抢红包成功！")
				    : GenerateRetrunMesssage.createFailMessage("抱歉，抢红包失败！");
	} 
	
	@RequestMapping(value="/grapRandomRedPacketByRedis")
	@ResponseBody
	public JSONObject grapRandomRedPacketByRedis(Long redPacketId,Long userId){
		logger.info("=====================开始抢红包====================");
		int result = userRedPacketService.grapRedPacketByRedisAlgnorithm(redPacketId, userId);
		boolean flag = result!=Constants.Grab_FAILED;
		logger.info("=====================抢红包结束====================");
		return flag ? GenerateRetrunMesssage.createSuccessMessage(null, "恭喜您抢红包成功！")
				    : GenerateRetrunMesssage.createFailMessage("抱歉，抢红包失败！");
	} 
}
