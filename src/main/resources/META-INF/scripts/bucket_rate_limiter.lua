-- request_rate_limiter.${id}.tokens 令牌桶剩余令牌数的key
local tokens_key = KEYS[1]
-- request_rate_limiter.${id}.timestamp 令牌桶最后填充令牌时间的key
local timestamp_key = KEYS[2]
--redis.log(redis.LOG_WARNING, "tokens_key " .. tokens_key)

local rate = tonumber(ARGV[1]) -- replenishRate 令牌桶填充速率
local capacity = tonumber(ARGV[2]) -- burstCapacity 令牌桶容量
local now = tonumber(ARGV[3]) -- 当前时间，从 1970-01-01 00:00:00 开始的秒数
local requested = tonumber(ARGV[4]) -- 本次获取令牌数量，默认1

local fill_time = capacity/rate -- 计算将令牌桶填充满所需时间
local ttl = math.floor(fill_time*2) -- 过期时间

--redis.log(redis.LOG_WARNING, "rate " .. ARGV[1])
--redis.log(redis.LOG_WARNING, "capacity " .. ARGV[2])
--redis.log(redis.LOG_WARNING, "now " .. ARGV[3])
--redis.log(redis.LOG_WARNING, "requested " .. ARGV[4])
--redis.log(redis.LOG_WARNING, "filltime " .. fill_time)
--redis.log(redis.LOG_WARNING, "ttl " .. ttl)

local last_tokens = tonumber(redis.call("get", tokens_key)) -- 获取剩余令牌数
if last_tokens == nil then -- 首次获取，没有令牌，直接填充满令牌
  last_tokens = capacity
end
--redis.log(redis.LOG_WARNING, "last_tokens " .. last_tokens)

local last_refreshed = tonumber(redis.call("get", timestamp_key)) -- 获取最后一次填充令牌的时间
if last_refreshed == nil then
  last_refreshed = 0
end
--redis.log(redis.LOG_WARNING, "last_refreshed " .. last_refreshed)

local delta = math.max(0, now-last_refreshed) --获取距离上一次填充的时间间隔
local filled_tokens = math.min(capacity, last_tokens+(delta*rate)) -- （时间间隔*速率）+ 现有剩余令牌数 = 本次请求前应有的令牌数
local allowed = filled_tokens >= requested
local new_tokens = filled_tokens
local allowed_num = 0
if allowed then
  new_tokens = filled_tokens - requested -- 现有令牌数减消耗令牌数，并设置allowed_num = 1 表示允许本次请求
  allowed_num = 1
end

--redis.log(redis.LOG_WARNING, "delta " .. delta)
--redis.log(redis.LOG_WARNING, "filled_tokens " .. filled_tokens)
--redis.log(redis.LOG_WARNING, "allowed_num " .. allowed_num)
--redis.log(redis.LOG_WARNING, "new_tokens " .. new_tokens)

if ttl > 0 then
  redis.call("setex", tokens_key, ttl, new_tokens) -- 更新令牌数及最后填充时间
  redis.call("setex", timestamp_key, ttl, now)
end

-- return { allowed_num, new_tokens, capacity, filled_tokens, requested, new_tokens }
return { allowed_num, new_tokens }
