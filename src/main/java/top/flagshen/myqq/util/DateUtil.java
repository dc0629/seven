package top.flagshen.myqq.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    /**
     * 获取到午夜的毫秒数
     * @return
     */
    public static long getMidnightMillis() {
        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return ChronoUnit.MILLIS.between(LocalDateTime.now(), midnight);
    }

}
