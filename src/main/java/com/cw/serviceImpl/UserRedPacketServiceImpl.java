package com.cw.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cw.dao.UserRedPacketDao;
import com.cw.entity.RedPacket;
import com.cw.entity.UserRedPacket;
import com.cw.service.UserRedPacketService;
@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {
	
	@Autowired
	UserRedPacketDao userRedPacketDao = null;
	@Autowired
	RedPacketServiceImpl redPacketService = null;
	
	private static final int FAILED = 0;
	
	@Transactional(isolation=Isolation.READ_COMMITTED,propagation=Propagation.REQUIRED)
	public int grapRedPacket(Long redPacketId, Long userId) {
		RedPacket redPacket = redPacketService.getRedPacket(redPacketId);
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

}
