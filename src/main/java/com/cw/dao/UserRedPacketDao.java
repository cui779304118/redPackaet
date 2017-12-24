package com.cw.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cw.entity.UserRedPacket;
@Repository
public interface UserRedPacketDao {
	
	public int grapRedPacket(UserRedPacket userRedPacket);
	
	public int insetUserRedPacketBatch(List<UserRedPacket> userRedPacketList);
}
