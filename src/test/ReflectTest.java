import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class ReflectTest {
    private static final Log logger  = LogFactory.getLog(ReflectTest.class);

    public static void main(String[] args){
        try
        {
            Class jobclass = Class.forName(args[0]);
//            Class argsClass = args.getClass();
            @SuppressWarnings("unchecked")
//            Method method = jobclass.getMethod("getHi", String.class);
//            logger.info(method.invoke(jobclass.newInstance(), "diaojin"));
//
                    Object obj = jobclass.newInstance();

            // 可以直接对 private 的属性赋值
            Field field = jobclass.getDeclaredField("t_name");
            field.setAccessible(true);
//            field.set(obj, "Java反射机制");
            System.out.println(field.get(obj));

        }
        catch(Exception e)
        {
            logger.error(e, e);
        }
    }
}
