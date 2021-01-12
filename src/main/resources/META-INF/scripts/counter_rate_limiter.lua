local key = KEYS[1]
local limit = tonumber(ARGV[1])
local curentLimit = tonumber(redis.call("get", key) or "0")
if curentLimit + 1 > limit then
    return 0
else
    redis.call("INCRBY", key, 1)
    redis.call("EXPIRE", key, ARGV[2])
    return curentLimit + 1
end