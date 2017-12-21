package com.cw.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.cw.entity.RedPacket;
@Repository
public interface RedPacketDao {
	/**
	 * 获取红包信息
	 */
	public RedPacket getRedPacket(Long id);
	
	/**
	 * 扣减抢红包数
	 */
	public int decreaseRedPacket(Long id);
	
	/**
	 * 扣减抢红包数
	 */
	public int decreaseRedPacketByVersion(@Param("id") Long id,@Param("version") Long version);
}

