redis.call('del', KEYS[1])
if redis.call('exists', KEYS[2]) == 1 then
    redis.call('incr', KEYS[2])
end
