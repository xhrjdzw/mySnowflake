
import com.xhr.mySnowflakeOid.utils.ContextHolder;
import com.xhr.mySnowflakeOid.utils.IDGenerator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestIdGenerator
{

    public static ApplicationContext context;

    @Before
    public void setUp() throws Exception
    {
        context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml", "classpath:applicationContext-cache.xml", "classpath:applicationContext-oid.xml"});
        ContextHolder.setContext(context);
    }


    /**
     * 修改配置文件，设置id生成类型
     * <p>
     * idtype=snowflake
     */
    @Test
    public void testSnowFlake()
    {

        /*
        * 设置指定键对值的系统属性
        * setProperty (String prop, String value);
        *
        * 参数：
        * prop - 系统属性的名称。
        * value - 系统属性的值。
        *
        * 返回：
        * 系统属性以前的值，如果没有以前的值，则返回 null。
        *
        * 抛出：
        * SecurityException - 如果安全管理器存在并且其 checkPermission 方法不允许设置指定属性。
        * NullPointerException - 如果 key 或 value 为 null。
        * IllegalArgumentException - 如果 key 为空。
        * 注：这里的system，系统指的是 JRE (runtime)system，不是指 OS。
        *
        */
        //OID_WORKERID,系统属性或者环境变量中设置workid
        //全局变量
        System.setProperty("OID_WORKERID", "1");

        for (int i = 0; i < 100000; i++)
        {
            String id = IDGenerator.generateObjectID(null);
            System.out.println(id);
        }
    }

    @Test
    public void testUapOid()
    {
        // 八位的字符，与租户方案中的schema命名规范保持一致
        String defautlSchema = "uapcloud";
        for (int i = 0; i < 100; i++)
        {
            String id = IDGenerator.generateObjectID(defautlSchema);
            System.out.println(id);
        }
        defautlSchema = "tenant01";
        for (int i = 0; i < 100; i++)
        {
            String id = IDGenerator.generateObjectID(defautlSchema);
            System.out.println(id);
        }
    }

}
