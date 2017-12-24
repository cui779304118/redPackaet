package com.cw.service;

import com.cw.entity.RedPacket;

public interface UserRedPacketService {
	
	/**
	 * 保存抢红包信息，加入版本号判断，乐观锁实现
	 */
	public int grapRedPacketForVersion(Long redPacketId,Long userId);
	public int grapRedPacketForVersion2(Long redPacketId,Long userId);
	public int grapRedPacketByRedis(Long redPacketId,Long userId);
	public int grapRedPacketByRedisAlgnorithm(Long redPacketId, Long userId);
}
