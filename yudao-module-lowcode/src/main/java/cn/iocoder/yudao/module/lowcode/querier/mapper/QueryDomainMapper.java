package cn.iocoder.yudao.module.lowcode.querier.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * @author leo
 */
@Mapper
public interface QueryDomainMapper {
    @SelectProvider(type = QueryDomainSqlProvider.class, method = "selectList")
    List<Map<Object, Object>> selectList(Map<String, Object> map);

}
