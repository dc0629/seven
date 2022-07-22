package top.flagshen.myqq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author 小棽
 * @date 2021/6/20 20:15
 */
@ServletComponentScan(basePackages = "top.flagshen.myqq.common")
@MapperScan("top.flagshen.myqq.*.*.mapper")
@SpringBootApplication
public class MyQQApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyQQApplication.class,args);
    }
}
