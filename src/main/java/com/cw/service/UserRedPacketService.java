package com.cw.service;

import com.cw.entity.RedPacket;

public interface UserRedPacketService {
	
	/**
	 * 保存抢红包信息
	 */
	public int grapRedPacket(Long redPacketId,Long userId);
}
