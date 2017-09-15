package com.xhr.mySnowflakeOid.oid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 徐浩然
 * @version SnowFlakeProvider, 2017-09-07
 */
public class SnowFlakeProvider implements IOidProvider {

    /** 增加业务相关性  workerId 工作ID (0~31) */
    private final long workerId;

    /** 开始时间截 (2015-01-01) */
    private final static long twepoch = 1456714605277L;

    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;

    /** 机器id所占的位数 */
    private final static long workerIdBits = 5L;

    /** 数据标识id所占的位数 */
    private final  static long datacenterIdBits = 5L;

    /** 数据中心ID(0~31) */
    private long datacenterId;

    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    public final static long maxWorkerId = -1L ^ -1L << workerIdBits;

    /** 序列在id中占的位数 */
    private final static long sequenceBits = 12L;

    /** 机器ID向左移12位 */
    private final static long workerIdShift = sequenceBits;

    /** 数据标识id向左移17位(12+5) */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /** 时间截向左移22位(5+5+12) */
    private final static long timestampLeftShift = sequenceBits + workerIdBits+ datacenterIdBits;

    /** 支持的最大数据标识id，结果是31 */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    public final static long sequenceMask = -1L ^ -1L << sequenceBits;

    /** 业务ID  **/
    public static final String OID_WORKID = "OID_WORKERID";

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    private static Logger logger = LoggerFactory.getLogger(SnowFlakeProvider.class);

    // workid方案待考虑
    public SnowFlakeProvider() {
        super();

        String workerIdStr = System.getProperty(OID_WORKID);
        if (workerIdStr == null) {
            workerIdStr = System.getenv(OID_WORKID);
        }

        if (workerIdStr == null) {
            String err = "get workerid form system env error!, please set env for oid!";
            logger.error(err);
            throw new IllegalArgumentException(err);
        }

        long wid = Long.parseLong(workerIdStr);
        this.workerId = wid;
    }

    public SnowFlakeProvider(final long workerId) {
        super();
        if (workerId > SnowFlakeProvider.maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", SnowFlakeProvider.maxWorkerId));
        }
        this.workerId = workerId;
    }

    /**
     * 线程安全的
    * */
    public synchronized long nextId() {

        long timestamp = this.timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & SnowFlakeProvider.sequenceMask;
            //毫秒内序列溢出
            if (this.sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                logger.info("sequenceMask value is {}." + sequenceMask);
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            //时间戳改变，毫秒内序列重置
            this.sequence = 0;
        }

        //上次生成ID的时间截
        this.lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        long nextId = ((timestamp - twepoch << timestampLeftShift))
                      | (datacenterId << datacenterIdShift) //
                      | (this.workerId << SnowFlakeProvider.workerIdShift)
                      | (this.sequence);
        logger.info("timestamp:" + timestamp + ",timestampLeftShift:" + timestampLeftShift + ",nextId:" + nextId + ",workerId:" + workerId + ",sequence:" + sequence);
        return nextId;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    @Override
    public String generatorID(String module) {
        String id = String.valueOf(this.nextId());
        return id;
    }

    public static void main(String[] args) {
        System.setProperty("OID_WORKERID", "1");
        SnowFlakeProvider provider = new SnowFlakeProvider();
        String id = provider.generatorID(null);
        logger.info(id);
    }
}

