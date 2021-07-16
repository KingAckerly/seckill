print(KEYS[1])
local stock = redis.call('get', KEYS[1])
if null == stock then
    return false
else
    if stock >= 1 then
        redis.call('decr')
        return true;
    else
        return false
    end
end
