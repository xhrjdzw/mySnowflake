import com.xhr.mySnowflakeOid.oid.IOidProvider;

/**
 * @author 徐浩然
 * @version CostomIdProvider, 2017-09-14
 */
public class CostomIdProvider implements IOidProvider
{

    @Override
    public String generatorID(String module) {
        return String.valueOf(module) + "_" + System.currentTimeMillis();
    }

}

