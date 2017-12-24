package com.cw.serviceImpl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cw.constants.Constants;
import com.cw.dao.UserRedPacketDao;
import com.cw.entity.RedPacket;
import com.cw.entity.UserRedPacket;
import com.cw.service.RedPacketService;
import com.cw.service.RedisRedPacketService;
import com.cw.service.UserRedPacketService;
@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {
	private static Logger logger = LoggerFactory.getLogger(com.cw.serviceImpl.UserRedPacketServiceImpl.class);
	
	@Autowired
	UserRedPacketDao userRedPacketDao = null;
	@Autowired
	RedPacketService redPacketService = null;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private RedisRedPacketService redisRedPacketService;
	
	private static final int FAILED = 0;
	
	@Transactional(isolation=Isolation.READ_COMMITTED,propagation=Propagation.REQUIRED)
	public int grapRedPacket(Long redPacketId, Long userId) {
		RedPacket redPacket = redPacketService.getRedPacket(redPacketId);
		if(redPacket==null){
			return FAILED;
		}
		if(redPacket.getStock()>0){
			redPacketService.decreaseRedPacket(redPacketId);
			
			UserRedPacket userRedPacket = new UserRedPacket();
			userRedPacket.setUserId(userId);
			userRedPacket.setRedPacketId(redPacketId);
			userRedPacket.setAmount(redPacket.getUnitAmount());
			userRedPacket.setNote("抢红包 " + redPacketId);
			int result = userRedPacketDao.grapRedPacket(userRedPacket);
			return result;
		}
		return FAILED;
	}

	@Transactional(isolation=Isolation.READ_COMMITTED,propagation=Propagation.REQUIRED)
	public int grapRedPacketForVersion(Long redPacketId, Long userId) {
		long start = System.currentTimeMillis();
		while(true){
			long end = System.currentTimeMillis();
			if(end - start > 100){
				logger.error("抢红包超时！");
				return FAILED;
			}
			RedPacket redPacket = redPacketService.getRedPacket(redPacketId);
			if(redPacket == null){
				logger.error("没有该红包！");
				return FAILED;
			}
			if(redPacket.getStock()>0){
				int update = redPacketService.decreaseRedPacketByVersion(redPacketId,redPacket.getVersion());
				if(update == 0){
					continue;
				}
				UserRedPacket userRedPacket = new UserRedPacket();
				userRedPacket.setUserId(userId);
				userRedPacket.setRedPacketId(redPacketId);
				userRedPacket.setAmount(redPacket.getUnitAmount());
				userRedPacket.setNote("抢红包 " + redPacketId);
				int result = userRedPacketDao.grapRedPacket(userRedPacket);
				logger.info("抢红包成功！");
				return result;
			}else{
				logger.info("红包已经抢完！");
				return FAILED;
			}
		}
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED,propagation=Propagation.REQUIRED)
	public int grapRedPacketForVersion2(Long redPacketId, Long userId) {
		int times = 3;
		int time = 1;
		while(true){
			if(time>times){
				logger.error("抢红包超时！");
				return FAILED;
			}
			RedPacket redPacket = redPacketService.getRedPacket(redPacketId);
			if(redPacket == null){
				logger.error("没有该红包！");
				return FAILED;
			}
			if(redPacket.getStock()>0){
				int update = redPacketService.decreaseRedPacketByVersion(redPacketId,redPacket.getVersion());
				if(update == 0){
					time++;
					continue;
				}
				UserRedPacket userRedPacket = new UserRedPacket();
				userRedPacket.setUserId(userId);
				userRedPacket.setRedPacketId(redPacketId);
				userRedPacket.setAmount(redPacket.getUnitAmount());
				userRedPacket.setNote("抢红包 " + redPacketId);
				int result = userRedPacketDao.grapRedPacket(userRedPacket);
				logger.info("抢红包成功！");
				return result;
			}else{
				logger.info("红包已经抢完！");
				return FAILED;
			}
		}
	}
	public int grapRedPacketByRedis(Long redPacketId, Long userId) {
		String redPacketKey = Constants.redPacketKey + redPacketId;
		String grabRedPacketKey = Constants.grabRedPacketKey + redPacketId;
		String args = userId + "," + System.currentTimeMillis();
		
		Map<String,String> redPacketInfo = redisTemplate.opsForHash().entries(redPacketKey);
		if(redPacketInfo == null || redPacketInfo.size() == 0){
			logger.info("对不起，没有该红包！");
			return Constants.Grab_FAILED;
		}
		Long stock = null;
		try {
			stock = Long.parseLong(redPacketInfo.get("stock").trim());
		} catch (NumberFormatException e) {
			logger.error("stock转化为整数失败！异常为：{}",new Object[]{e});
		}
		
		Double unitAmount = Double.parseDouble(redPacketInfo.get("unit_amount"));
		if(stock>Constants.LAST_RED_PACKET){
			redisTemplate.opsForHash().increment(redPacketKey,"stock", -1);
			redisTemplate.opsForList().leftPush(grabRedPacketKey, args);
			logger.info("恭喜您，抢红包成功！");
			return Constants.Grab_SUCCESS;
		}
		if(stock == Constants.LAST_RED_PACKET){
			redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
			redisTemplate.opsForHash().increment(redPacketKey,"stock", -1);
			redisTemplate.opsForList().leftPush(grabRedPacketKey, args);
			logger.info("恭喜您，抢红包成功！");
			Long startTime =(Long) redisTemplate.opsForList().range(grabRedPacketKey, 0, 0).get(0);
			Long endTime =(Long) redisTemplate.opsForList().range(grabRedPacketKey, 20000, 20000).get(0);
			logger.info("红包{}---共耗时：{}毫秒结束！",new Object[]{redPacketId,(endTime - startTime)});
			return Constants.Grab_SUCCESS;
		}
			logger.info("对不起，红包已经抢完！");
			return Constants.Grab_FAILED;
		
	}
	
	public int grapRedPacketByRedisAlgnorithm(Long redPacketId, Long userId) {
		String redPacketKey = Constants.redPacketKey + redPacketId;
		String grabRedPacketKey = Constants.grabRedPacketKey + redPacketId;
		String redPacketAmountListKey =Constants.redPacketAmountListKey;
		String redPacketIndexKey = Constants.redPacketIndexKey;
		
		int index =Integer.parseInt((String)redisTemplate.opsForValue().get(redPacketIndexKey)); 
		String unitAmountStr =(String) redisTemplate.opsForList().range(redPacketAmountListKey,index, index).get(0);
		Double unitAmount = Double.parseDouble(unitAmountStr);
		String args = userId + "," + unitAmount + System.currentTimeMillis();
		
		Map<String,String> redPacketInfo = redisTemplate.opsForHash().entries(redPacketKey);
		if(redPacketInfo == null || redPacketInfo.size() == 0){
			logger.info("对不起，没有该红包！");
			return Constants.Grab_FAILED;
		}
		Long stock = null;
		try {
			stock = Long.parseLong(redPacketInfo.get("stock").trim());
		} catch (NumberFormatException e) {
			logger.error("stock转化为整数失败！异常为：{}",new Object[]{e});
		}
		
		
		if(stock>Constants.LAST_RED_PACKET){
			redisTemplate.opsForHash().increment(redPacketKey,"stock", -1*unitAmount);
			redisTemplate.opsForValue().increment(redPacketAmountListKey, 1);
			redisTemplate.opsForList().leftPush(grabRedPacketKey, args);
			logger.info("恭喜您，抢到红包 ： " + unitAmount/100 + "元");
			return Constants.Grab_SUCCESS;
		}
		if(stock == Constants.RED_PACKET_ZERO){
			redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
			Long startTime =(Long) redisTemplate.opsForList().range(grabRedPacketKey, 0, 0).get(0);
			Long endTime =(Long) redisTemplate.opsForList().range(grabRedPacketKey, 20000, 20000).get(0);
			logger.info("红包{}---共耗时：{}毫秒结束！",new Object[]{redPacketId,(endTime - startTime)});
			return Constants.Grab_SUCCESS;
		}
			logger.info("对不起，红包已经抢完！");
			return Constants.Grab_FAILED;
		
	}

}
