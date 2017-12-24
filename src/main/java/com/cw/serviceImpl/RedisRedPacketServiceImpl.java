package com.cw.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cw.constants.Constants;
import com.cw.dao.UserRedPacketDao;
import com.cw.entity.UserRedPacket;
import com.cw.service.RedisRedPacketService;

@Service
public class RedisRedPacketServiceImpl implements RedisRedPacketService {
	
	private static Logger logger = LoggerFactory.getLogger(com.cw.serviceImpl.RedisRedPacketServiceImpl.class);
	
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private UserRedPacketDao userRedPacketDao;

	@Async
	public void saveUserRedPacketByRedis(Long redPacketId, Double unitAmount) {
		logger.info("==============开始导入数据==============");
		Long start = System.currentTimeMillis();
		BoundListOperations ops = redisTemplate.boundListOps(Constants.grabRedPacketKey + redPacketId);
		Long size = ops.size();
		Long times = size%Constants.TIME_SIZE==0 ? size/Constants.TIME_SIZE : size/Constants.TIME_SIZE + 1;
		
		List<UserRedPacket> userRedPacketList = new  ArrayList<UserRedPacket>();
		int count = 0;
		
		for(int i=0;i<times;i++){
			List userIdList = null;
			if(i == 0){
				userIdList = ops.range(0,Constants.TIME_SIZE-1);
			}else{
				userIdList = ops.range(i*Constants.TIME_SIZE,(i+1)*(Constants.TIME_SIZE-1) );
			}
			userRedPacketList.clear();
			for(int j=0;j<userIdList.size();j++){
				String args = (String) userIdList.get(j);
				String[] userRedPacketInfo = args.split(",");
				Long userId =Long.parseLong(userRedPacketInfo[0]);
				Double unitAmount_random =Double.parseDouble(userRedPacketInfo[1]);
				Long time = Long.parseLong(userRedPacketInfo[2]);
				
				UserRedPacket userRedPacket = new UserRedPacket();
				userRedPacket.setUserId(userId);
				userRedPacket.setGrabTime(new Timestamp(time));
				userRedPacket.setRedPacketId(redPacketId);
				userRedPacket.setAmount(unitAmount_random);
				userRedPacket.setNote("抢红包 " + redPacketId);
				
				userRedPacketList.add(userRedPacket);
			}
			count += userRedPacketDao.insetUserRedPacketBatch(userRedPacketList);
		}
		redisTemplate.delete(Constants.grabRedPacketKey + redPacketId);
		Long end = System.currentTimeMillis();
		logger.info("批量导入数据结束，耗时：" + (end - start) + "毫秒，共" + count + "条记录被保存。");

	}

}
