package top.flagshen.myqq.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.flagshen.myqq.common.MyException;
import top.flagshen.myqq.common.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;

/**
 * 统一异常处理
 *
 * @author dengchao
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    MessageSource messageSource;

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseResult handleConventionException(HttpServletRequest request, Exception e, HttpServletResponse response) {
        //如果是业务逻辑异常，返回具体的错误码与提示信息
        String url = request.getRequestURI();
        if (e instanceof MyException) {
            MyException exception = (MyException) e;
            return ResponseResult.fail(exception.getErrCode(), exception.getErrMsg());
        } else if (e instanceof BindException) {
            // 请求参数校验异常处理
            BindException exception = (BindException) e;
            BindingResult bindingResult = exception.getBindingResult();
            return handleBindResult(url, bindingResult);
        } else if (e instanceof MethodArgumentNotValidException) {
            // 请求参数校验异常处理
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = exception.getBindingResult();
            return handleBindResult(url, bindingResult);
        } else if (e instanceof ClassCastException) {
            //数据库操作异常后会返回hashmap 转换成对应的结果时会报错
            return ResponseResult.fail(100002, "数据库异常");
        }  else if (e instanceof AccessDeniedException) {
            return ResponseResult.fail(403, "无权访问");
        } else {
            //对系统级异常进行日志记录
            return ResponseResult.fail(500, "系统异常，请联系管理员");
        }
    }

    private ResponseResult handleBindResult(String url, BindingResult bindingResult) {
        ObjectError objectError = bindingResult.getAllErrors().get(0);
        String errMsg = objectError.getDefaultMessage();
        return ResponseResult.fail(100001, errMsg);
    }

}
