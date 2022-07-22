package top.flagshen.myqq.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultScriptExecutor;
import org.springframework.data.redis.core.script.ScriptExecutor;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 相关配置
 *
 * @author YangYi
 * @version v1.0
 * @date 2019 /11/15
 */
@Configuration
public class RedisConfiguration<V> {

    /**
     * Fast json 2 json redis serializer redis serializer.
     *
     * @return the redis serializer
     */
    @Bean
    public RedisSerializer fastJson2JsonRedisSerializer() {
        return new FastJson2JsonRedisSerializer<>(Object.class);
    }


    /**
     * Fast json redis template redis template.
     *
     * @param factory                      the factory
     * @param fastJson2JsonRedisSerializer the fast json 2 json redis serializer
     * @return the redis template
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, V> fastJsonRedisTemplate(RedisConnectionFactory factory, RedisSerializer fastJson2JsonRedisSerializer) {
        RedisTemplate<String, V> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(fastJson2JsonRedisSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(fastJson2JsonRedisSerializer);
        template.setDefaultSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 对字符串类型数据操作
     *
     * @param redisTemplate the redis template
     * @return value operations
     */
    @Bean("valueOperations")
    public ValueOperations<String, V> valueOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 对hash类型的数据操作
     *
     * @param redisTemplate the redis template
     * @return hash operations
     */
    @Bean("hashOperations")
    public HashOperations<String, String, V> hashOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForHash();
    }


    /**
     * 对链表类型的数据操作
     *
     * @param redisTemplate the redis template
     * @return list operations
     */
    @Bean("listOperations")
    public ListOperations<String, V> listOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 对无序集合类型的数据操作
     *
     * @param redisTemplate the redis template
     * @return operations
     */
    @Bean("setOperations")
    public SetOperations<String, V> setOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 对有序集合类型的数据操作
     *
     * @param redisTemplate the redis template
     * @return z set operations
     */
    @Bean("zSetOperations")
    public ZSetOperations<String, V> zSetOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public ScriptExecutor scriptExecutor(RedisTemplate redisTemplate) {
        return new DefaultScriptExecutor(redisTemplate);
    }
}
