package com.cw.constants;

public class Constants {
	public static final String redPacketKey = "redPacket:info:";
	public static final String grabRedPacketKey = "redPacket:grabInfo:";
	public static final String  redPacketAmountListKey = "redPacket:amountList:";
	public static final String  redPacketIndexKey = "redPacket:index:";
	
	
	public static final int TIME_SIZE = 1000;//每一次从redPacket:grabInfo:一次取出的记录条数
	
	public static final int LAST_RED_PACKET = 1;
	public static final int Grab_SUCCESS = 1;
	public static final int Grab_FAILED = 0;
	public static final int RED_PACKET_ZERO = 0;
}
