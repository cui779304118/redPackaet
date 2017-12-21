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
	
	/**
	 * 扣减红包,加入版本控制，乐观锁
	 */
	public int decreaseRedPacketByVersion(Long id,Long version);
}
