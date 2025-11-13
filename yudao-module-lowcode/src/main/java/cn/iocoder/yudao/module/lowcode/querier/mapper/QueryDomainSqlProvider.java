package cn.iocoder.yudao.module.lowcode.querier.mapper;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author leo
 */
@Slf4j
public class QueryDomainSqlProvider {

    public String selectList(Map<String, Object> map) {
        var sql = (String) map.get("sql");
        Assert.isTrue(StrUtil.isNotEmpty(sql));
        map.remove("sql");
        log.info("selectList sql: {}; params：{}", sql, JSON.toJSONString(map));
        return sql;
    }

}
