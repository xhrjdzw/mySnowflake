package com.xhr.mySnowflakeOid.oid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * @author 徐浩然
 * @version UapOidGenerator, 2017-09-14
 * 基于uap的OidGenerator改造,8位数据库schema编号+12位流水
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class UapOidGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UapOidGenerator.class);

    //步长，单jvm一次性取出的oid范围, 在OidJdbcService的属性中注入修改
    public static int OID_AMOUNT = 5000;

    //默认初始值100000000000，初始值之前的留给预置数据使用
    public static final String OID_BASE_INITIAL_VAL = UapOidAlgorithm.INIT_VALUE;

    //默认的租户id（schema）
    public static final String DEFAULT_SCHEMACODE = "OID";

    private Object lock;

    private Map<String, Lock> locks = new ConcurrentHashMap<String, Lock>();

    //初始化
    private UapOidGenerator() {
        lock = new Object();
    }

    //单例
    private static UapOidGenerator instance = null;

    //哈希表，租户id键值，存放该租户当前 OidBase 和已取数量
    private static Map oidMap = new ConcurrentHashMap();

    static {
        instance = new UapOidGenerator();
    }

    //静态工厂方法
    public static UapOidGenerator getInstance() {
        return instance;
    }

    /**
     * 取得下一个 OID
     *
     * @param sid schemacode
     * @return 总长度为20字符的oid
     */
    public final String genOid(String sid){
        return nextOid(sid);
    }

    /**
     * 取得下一批 OID
     *
     * @param tid
     * @param date
     * @param count
     * @return String[] OID数组
     */
    public final String[] genBatchOids(String sid ,int count) {
        if(StringUtils.isBlank(sid)){
            sid = DEFAULT_SCHEMACODE;
        }

        String[] oids = new String[count];
        for (int i = 0; i < count; i++) {
            String oid = nextOid(sid);
            oids[i] = oid;
        }
        return oids;
    }

    /**
     * 获取下一个步长的oidbase
     */
    public static String stepOidBase(String currentOid){
        String result = null;
        UapOidAlgorithm inst = UapOidAlgorithm.getInstance(currentOid);
        for (int i = 0; i < OID_AMOUNT; i++) {
            result = inst.nextOidBase();
        }
        return result;
    }

    /**
     * 取得下一个 OID
     */
    private String nextOid(String sid) {
        if(StringUtils.isBlank(sid)){
            //sid = DEFAULT_SCHEMACODE;
            /**
             * oid前八位默认取租户id
             */
            sid = InvocationInfoProxy.getTenantid();
            if(sid == null || "".equals(sid)){
                sid = DEFAULT_SCHEMACODE;
            }
        }

        String key = sid;
        if(StringUtils.isBlank(key)){
            throw new RuntimeException("schema code can not be null! please check global conf and context info！");
        }

        //获取锁,key为全局唯一的schemacode
        Lock l = getLock(key);

        OidCounter oidCounter = null;
        String oidBase = null;
        String nextOid = null;

        try {
            // 加锁
            l.lock();

            oidCounter = (OidCounter) oidMap.get(key);
            if (oidCounter == null) {
                oidCounter = new OidCounter();
                oidMap.put(key, oidCounter);
            }

            if (oidCounter.amount % OID_AMOUNT == 0 || 0 == oidCounter.amount) {
                oidBase = getNewBaseId(key);
            } else {
                oidBase = oidCounter.oidBase;
            }

            nextOid = UapOidAlgorithm.getInstance(oidBase).nextOidBase();
            oidCounter.oidBase = nextOid;
            ++oidCounter.amount;
        } catch(Exception e){
            LOGGER.error("get oid error!", e);
            throw e;
        } finally {
            // 解锁
            l.unlock();
        }

        if(nextOid == null){
            return null;
        }

        return getWholeOid(sid,nextOid);
    }


    /**
     * 取得当前的编码
     */
    private final String getWholeOid(String prefix, String nextOid) {
        return (prefix + nextOid);
    }

    private String getNewBaseId(String key) {
        // 数据库操作，从数据库中取最新值，如果没有则初始化，需要新启动事务
        UapOidJdbcService service = (UapOidJdbcService)ContextHolder.getContext().getBean("uapOidJdbcService");
        return service.getInitValue(key);
    }

    private Lock getLock(String lockKey) {
        Lock l = locks.get(lockKey);
        if (l == null) {
            synchronized (lock) {
                l = locks.get(lockKey);
                if (l == null) {
                    l = new ReentrantLock();
                    locks.put(lockKey, l);
                }
            }
        }
        return l;
    }

    /**
     * 用来存放当前 OidBase 和已取数量的类
     */
    private class OidCounter {

        public String oidBase;

        public int amount;

        public OidCounter() {
            this.amount = 0;
            this.oidBase = OID_BASE_INITIAL_VAL;
        }
    }
}