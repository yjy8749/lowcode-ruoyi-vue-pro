package cn.iocoder.yudao.module.lowcode.querier;

import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.spring.SpringUtils;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainParams;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryDomain;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author leo
 */
@Slf4j
public class QueryDomainCacheExecutor extends QueryDomainExecutor {

    private static final int DEFAULT_TTL = 5 * 1000;

    private static final String NULL_HOLDER = "NULL";

    private final AtomicLong totalCnt = new AtomicLong(0);
    private final AtomicLong evictCnt = new AtomicLong(0);


    public QueryDomainCacheExecutor(Boolean tenantEnable, String logicDeleteValue, String queryDomainId, String dataSourceName, QueryDomain queryDomain) throws ClassNotFoundException {
        super(tenantEnable, logicDeleteValue, queryDomainId, dataSourceName, queryDomain);
    }

    private RedisTemplate<String, Object> getRedisTemplate() {
        return SpringUtils.getBean("redisTemplate");
    }

    private int getTtl() {
        Integer ttl = super.queryDomain.getTtl();
        return ttl == null ? DEFAULT_TTL : ttl;
    }

    private String paramsToMd5(QueryDomainParams params) {
        return SecureUtil.md5(JSONObject.toJSONString(params));
    }

    private void putObj(String redisKey, Object object) {
        getRedisTemplate().opsForValue().set(redisKey, object == null ? NULL_HOLDER : object, getTtl(), TimeUnit.MILLISECONDS);
    }

    private Optional<Object> getObj(String redisKey) {
        var result = getRedisTemplate().opsForValue().get(redisKey);
        result = NULL_HOLDER.equals(result) ? null : result;
        return Optional.ofNullable(result);
    }

    private String cacheKey(QueryDomainParams params, String key) {
        return paramsToMd5(params) + key;
    }

    @SuppressWarnings("unchecked")
    private <T> T cacheExecute(QueryDomainParams params, Callable<T> callFunction, String key) {
        var totalCnt = this.totalCnt.addAndGet(1L);
        String cacheKey = cacheKey(params, key);
        var resultOpt = getObj(cacheKey);
        if (resultOpt.isPresent()) {
            return (T) resultOpt.get();
        } else {
            try {
                var evictCnt = this.evictCnt.addAndGet(1L);
                T result = callFunction.call();
                putObj(cacheKey, result);
                log.info("QueryDomainCache cache info. evict:{}, hit:{}, total:{}", evictCnt, totalCnt - evictCnt, totalCnt);
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Map<Object, Object>> selectList(QueryDomainParams params) {
        return this.cacheExecute(params, () -> super.selectList(params), "#selectList");
    }

    @Override
    public Map<Object, Object> selectOne(QueryDomainParams params) {
        return this.cacheExecute(params, () -> super.selectOne(params), "#selectOne");
    }

    @Override
    public Long selectCount(QueryDomainParams params) {
        return this.cacheExecute(params, () -> super.selectCount(params), "#selectCount");
    }

    @Override
    public PageResult<Map<Object, Object>> selectPage(QueryDomainParams params) {
        return this.cacheExecute(params, () -> super.selectPage(params), "#selectPage");
    }

    @Override
    public void export(QueryDomainParams params, HttpServletResponse response) {
        super.export(params, response);
    }

}
