package top.flagshen.myqq;

import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author dengchao
 * @date 2021/6/20 20:15
 */
@ServletComponentScan(basePackages = "top.flagshen.myqq.common")
@MapperScan("top.flagshen.myqq.*.*.mapper")
@EnableSimbot
@SpringBootApplication
public class MyQQApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyQQApplication.class,args);
    }
}
