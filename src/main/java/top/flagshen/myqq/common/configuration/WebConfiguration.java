package top.flagshen.myqq.common.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.flagshen.myqq.common.context.InvocationContextSetupInterceptor;


/**
 * @author dengchao
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor getInvocationContextSetupInterceptor(){
        return new InvocationContextSetupInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getInvocationContextSetupInterceptor());
    }
}