local stock = redis.call('get', KEYS[1])
if stock == false or nil == stock then
    return false
else
    if tonumber(stock) >= 1 then
        redis.call('set', KEYS[2], ARGV[1])
        redis.call('expire', KEYS[2], 60 * 60 * 24)
        redis.call('decr', KEYS[1])
        return true;
    else
        return false
    end
end
