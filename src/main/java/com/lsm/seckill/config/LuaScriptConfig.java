package com.lsm.seckill.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

@Component
public class LuaScriptConfig {

    public final RedisScript<Boolean> putOrder = getRedisScript("lua/put_order.lua", Boolean.class);
    public final RedisScript<Boolean> cancelOrder = getRedisScript("lua/cancel_order.lua", Boolean.class);

    private <T> RedisScript<T> getRedisScript(String path, Class<T> resultType) {
        DefaultRedisScript<T> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
        redisScript.setResultType(resultType);
        return redisScript;
    }
}
