package com.cw.dao;

import org.springframework.stereotype.Repository;

import com.cw.entity.UserRedPacket;
@Repository
public interface UserRedPacketDao {
	
	public int grapRedPacket(UserRedPacket userRedPacket);
}
