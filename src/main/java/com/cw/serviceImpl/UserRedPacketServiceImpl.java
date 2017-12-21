package com.cw.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cw.dao.UserRedPacketDao;
import com.cw.entity.RedPacket;
import com.cw.entity.UserRedPacket;
import com.cw.service.RedPacketService;
import com.cw.service.UserRedPacketService;
@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {
	private static Logger logger = LoggerFactory.getLogger(com.cw.serviceImpl.UserRedPacketServiceImpl.class);
	
	@Autowired
	UserRedPacketDao userRedPacketDao = null;
	@Autowired
	RedPacketService redPacketService = null;
	
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

}
