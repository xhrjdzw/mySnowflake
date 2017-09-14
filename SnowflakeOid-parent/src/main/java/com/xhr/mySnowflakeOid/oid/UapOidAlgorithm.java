package com.xhr.mySnowflakeOid.oid;

/**
 * @author 徐浩然
 * @version UapOidAlgorithm, 2017-09-14
 */
public class UapOidAlgorithm
{
    /** 最小编码 48 */
    private final static byte MIN_CODE = 48;

    /** 最大编码 122 */
    private final static byte MAX_CODE = 122;

    /** 码长度 */
    private final int CODE_LENGTH = 12;

    /** 6 位码 **/
    private byte[] oidBaseCodes = new byte[CODE_LENGTH];

    /** 默认起始值 **/
    public static final String INIT_VALUE = "100000000000";

    /**
     * 构造函数私有化，确保算法类的不可在外部实例化
     */
    private UapOidAlgorithm() {
    }

    private void setOidBaseCodes(byte[] oidBaseCodes) {
        if (oidBaseCodes.length != CODE_LENGTH)
            return;
        System.arraycopy(oidBaseCodes, 0, this.oidBaseCodes, 0, CODE_LENGTH);
    }

    /**
     * 获得算法实例的工厂方法
     */
    synchronized public static UapOidAlgorithm getInstance() {
        UapOidAlgorithm oidBase = new UapOidAlgorithm();
        oidBase.setOidBaseCodes(INIT_VALUE.getBytes());
        return oidBase;
    }

    /**
     * 获得算法实例的工厂方法
     */
    synchronized public static UapOidAlgorithm getInstance(String strOidBase) {
        UapOidAlgorithm oidBase = new UapOidAlgorithm();
        oidBase.setOidBaseCodes(strOidBase.getBytes());
        return oidBase;
    }

    /**
     * 取得下一个 oidBase 编码
     *
     * @return String 下一个 oidBase 编码
     */
    public String nextOidBase() {
        for (int i = oidBaseCodes.length - 1; i >= 0; --i) {
            byte code = (byte) (oidBaseCodes[i] + 1);
            boolean carryUp = false;
            byte newCode = code;
            if (code > MAX_CODE) {
                newCode = MIN_CODE;
                carryUp = true;
            }
            // 跳过数字与小写字母之间的其他字符
            if (newCode == 58) {
                newCode = 97;
            }
            oidBaseCodes[i] = newCode;

            if (!carryUp)
                break;
        }

        return new String(oidBaseCodes);
    }
}
