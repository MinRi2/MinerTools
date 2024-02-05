package MinerTools.annotation;

import java.lang.annotation.*;

public class Annotations{

    /**
     * 将枚举类转化为获取设置数值的对象
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface SettingData{

    }
}
