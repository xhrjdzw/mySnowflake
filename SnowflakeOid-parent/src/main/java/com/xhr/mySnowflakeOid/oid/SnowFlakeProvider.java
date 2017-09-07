package com.xhr.mySnowflakeOid.oid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 徐浩然
 * @version SnowFlakeProvider, 2017-09-07
 */
public class SnowFlakeProvider implements IOidProvider {

    private final long workerId;
    private final static long twepoch = 1456714605277L;
    private long sequence = 0L;
    private final static long workerIdBits = 6L;
    public final static long maxWorkerId = -1L ^ -1L << workerIdBits;
    private final static long sequenceBits = 10L;

    private final static long workerIdShift = sequenceBits;
    private final static long timestampLeftShift = sequenceBits + workerIdBits;
    public final static long sequenceMask = -1L ^ -1L << sequenceBits;

    public static final String OID_WORKID = "OID_WORKERID";

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

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & SnowFlakeProvider.sequenceMask;
            if (this.sequence == 0) {
                logger.info("sequenceMask value is {}." + sequenceMask);
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0;
        }
        if (timestamp < this.lastTimestamp) {
            try {
                throw new Exception(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp - timestamp));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        this.lastTimestamp = timestamp;
        long nextId = ((timestamp - twepoch << timestampLeftShift)) | (this.workerId << SnowFlakeProvider.workerIdShift) | (this.sequence);
        // logger.info("timestamp:" + timestamp + ",timestampLeftShift:" + timestampLeftShift + ",nextId:" + nextId + ",workerId:" + workerId + ",sequence:" + sequence);
        return nextId;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    @Override
    public String generatorID(String module) {
        String id = String.valueOf(this.nextId());
        return id;
    }

    public static void main(String[] args) {
        SnowFlakeProvider provider = new SnowFlakeProvider();
        String id = provider.generatorID(null);
        logger.info(id);
    }
}

