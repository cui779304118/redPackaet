package com.cw.manually;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import com.cw.constants.Constants;
import com.cw.dao.RedPacketDao;
import com.cw.entity.RedPacket;
import com.cw.utils.RedPacketAlgorithm;

public class InsertRedPacket2RedisAndMysql {

	private static Logger logger = LoggerFactory.getLogger(com.cw.manually.InsertRedPacket2RedisAndMysql.class);
//	@Autowired
//	private RedisTemplate redisTemplate;
	
	public static void main(String [] args){
		
		logger.info("开始插入redPacket");
		String redPacketId = "33";
		long total = 100_0000L;
		int count = 10_000;
		long max = 200;
		long min = 1;
		
		InsertRedPacket2RedisAndMysql IRM = new InsertRedPacket2RedisAndMysql();
		IRM.insertRedPacketInfo(redPacketId);
//		IRM.generateRedPacket(total, count, max, min, redPacketId);
		logger.info("插入结束！");
	}
	
	public  void insertRedPacketInfo(String redPacketId) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:env/dev/applicationContext.xml");
		final String userId = "00";
		final String amount ="1000000";
		final String total = "10000";
		final String unitAmount = "00";
		final String stock = "1000000";
		final String note = "1万元金额，1万个小红包";
		
		RedisTemplate redisTemplate = (RedisTemplate) context.getBean(RedisTemplate.class);
		String key = Constants.redPacketKey + redPacketId;
		String redPacketIndexKey = Constants.redPacketIndexKey + redPacketId;
		
		Map<String,String> redPacketInfo = new HashMap<String,String>(){
			{
				put("user_id",userId);
				put("amount",amount);
				put("total",total);
				put("unit_amount",unitAmount);
				put("stock",stock);
				put("note",note);
			}
		};
		redisTemplate.opsForHash().putAll(key, redPacketInfo);
		
		//插入红包表索引
		redisTemplate.opsForValue().set(redPacketIndexKey,"0");
		
		logger.info("插入Redis成功！");
		
		RedPacketDao redPacketDao =(RedPacketDao) context.getBean("redPacketDao");
		
		RedPacket redPacket = new RedPacket();
		redPacket.setUserId(Long.parseLong(userId));
		redPacket.setAmount(Double.parseDouble(amount));
		redPacket.setUnitAmount(Double.parseDouble(unitAmount));
		redPacket.setTotal(Integer.parseInt(total));
		redPacket.setStock(Integer.parseInt(stock));
		redPacket.setNote(note);
		redPacket.setVersion(0L);
		
		redPacketDao.insertRedPacketInfo(redPacket);
		logger.info("插入Mysql成功！");
		
	}
	
	private void generateRedPacket(long total, int count, long max, long min,String redPacketId){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:env/dev/applicationContext.xml");
		RedisTemplate redisTemplate = (RedisTemplate) context.getBean(RedisTemplate.class);
		
		String key = Constants.redPacketAmountListKey + redPacketId;
		
		long [] redArray = RedPacketAlgorithm.generate(total, count, max, min);
//		List<Long> redList = new ArrayList<Long>();
		for(long l:redArray){
			redisTemplate.opsForList().leftPush(key, String.valueOf(l));
		}
//		redisTemplate.opsForList().leftPushAll(key, redList);
		logger.info("生成红包表成功！");
	}
	
}
