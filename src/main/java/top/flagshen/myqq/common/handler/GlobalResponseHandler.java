package top.flagshen.myqq.common.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import top.flagshen.myqq.common.ResponseResult;

import java.util.Objects;

/**
 * 统一格式返回
 *
 * @author dengchao
 */
@RestControllerAdvice(annotations = {Controller.class, RestController.class})
@Slf4j
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

	@Autowired
    MessageSource messageSource;

    private ThreadLocal<ObjectMapper> mapperThreadLocal = ThreadLocal.withInitial(ObjectMapper::new);

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 检查注解是否存在
        if (returnType.getDeclaringClass().isAnnotationPresent(IgnoreGlobalResponse.class)) {
            return false;
        }
        if (Objects.requireNonNull(returnType.getMethod()).isAnnotationPresent(IgnoreGlobalResponse.class)) {
            return false;
        }
        final String returnTypeName = returnType.getParameterType().getName();
        return !"top.flagshen.myqq.common.ResponseResult".equals(returnTypeName);
    }

    @Override
    @ResponseBody
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        ObjectMapper mapper = mapperThreadLocal.get();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (body == null) {
            return ResponseResult.success(null);
        }
        if (body instanceof String) {
            ResponseResult result = ResponseResult.success(body);
            try {
                //因为是String类型，我们要返回Json字符串，否则SpringBoot框架会转换出错
                return mapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                ResponseResult.fail(500, "Json parse fail.");
            }
        }
        int status = ((ServletServerHttpResponse) response).getServletResponse().getStatus();
        if (status >= HttpStatus.BAD_REQUEST.value()) {
            return body;
        }
        return ResponseResult.success(body);
    }

}
