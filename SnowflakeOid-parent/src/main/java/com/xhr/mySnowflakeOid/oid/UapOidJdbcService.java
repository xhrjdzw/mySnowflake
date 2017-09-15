package com.xhr.mySnowflakeOid.oid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * @author 徐浩然
 * @version UapOidJdbcService, 2017-09-14
 */
public class UapOidJdbcService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UapOidJdbcService.class);

    private JdbcTemplate jt;

    private int stepSize = UapOidGenerator.OID_AMOUNT;

    public JdbcTemplate getJdbcTemplate() {
        return jt;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
        UapOidGenerator.OID_AMOUNT = this.stepSize;
    }

    //新启动事务
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public String getInitValue(String schemacode) throws RuntimeException {

        String querySql = "select oidbase from pub_oid where schemacode = ? for update";

        String oidBase;
        List<String> list = jt.queryForList(querySql,new Object[]{schemacode}, String.class);

        if(list==null || list.size()==0) {
            oidBase = UapOidGenerator.OID_BASE_INITIAL_VAL;
            String newOidBase = UapOidGenerator.stepOidBase(oidBase);
            String insertSql = "insert into pub_oid(id,schemacode,oidbase,ts) values(?,?,?,?)";
            String id = UUID.randomUUID().toString();
            long ts = System.currentTimeMillis();
            jt.update(insertSql, new Object[]{id, schemacode, newOidBase, new Timestamp(ts)});
            LOGGER.info("init pub_oid for {} with init value {} at {}.", schemacode, newOidBase, new Timestamp(ts));

            LOGGER.error("pub_oid step value should be inited in scripts, not init by sql here!!!");
        } else {
            oidBase = list.get(0);
            String newOidBase = UapOidGenerator.stepOidBase(oidBase);
            String updateSql = "update pub_oid set oidbase = ?,ts=? where schemacode = ?";
            long ts = System.currentTimeMillis();
            jt.update(updateSql, new Object[]{newOidBase, new Timestamp(ts), schemacode});
            LOGGER.info("update pub_oid for {} with setp value {} at {}.", schemacode, newOidBase, new Timestamp(ts));
        }

        return oidBase;
    }
}
