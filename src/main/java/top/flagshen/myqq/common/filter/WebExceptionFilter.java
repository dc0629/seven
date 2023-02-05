package top.flagshen.myqq.common.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.flagshen.myqq.common.constants.SystemConstants;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

/**
 * @author dengchao
 */
@Component
@Slf4j
public class WebExceptionFilter extends OncePerRequestFilter {

    /**随机数位数*/
    private static final int RANDOM_BOUND = 9000;
    /**随机数填充位数*/
    private static final int RANDOM_PAD_SIZE = 1000;
    /**随机数循环次数*/
    private static final int RANDOM_COUNT = 3;

    private static Random random = new Random();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        long start = System.currentTimeMillis();
        String traceId = getTraceId();
        MDC.put(SystemConstants.MDC_TRACE_KEY, traceId);

        log.info("url:{}, params:{}",
                request.getRequestURL(),
                JSON.toJSONString(request.getParameterMap()));
        try {
            //转换成代理类
            filterChain.doFilter(request, response);
        } catch (Exception cause) {
            log.error("Filter error:{}", cause.getMessage());
        } finally {
            long end = System.currentTimeMillis();
            log.info("请求耗时:{} ms", end - start);
            MDC.remove(SystemConstants.MDC_TRACE_KEY);
        }
    }

    /**
     * 时间戳+12位随机数
     * @return
     */
    private String getTraceId() {

        String dateStr = String.valueOf(System.currentTimeMillis());
        StringBuilder randomStr = new StringBuilder(StringUtils.EMPTY);

        for (int i = 0; i < RANDOM_COUNT; i++) {
            randomStr.append(random.nextInt(RANDOM_BOUND) + RANDOM_PAD_SIZE);
        }
        return dateStr + randomStr.toString();

    }
}
