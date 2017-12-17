package com.cw.service;

import com.cw.entity.RedPacket;

public interface RedPacketService {
	
	/**
	 * 获取红包
	 */
	public RedPacket getRedPacket(Long id);
	/**
	 * 扣减红包
	 */
	public int decreaseRedPacket(Long id);
}
