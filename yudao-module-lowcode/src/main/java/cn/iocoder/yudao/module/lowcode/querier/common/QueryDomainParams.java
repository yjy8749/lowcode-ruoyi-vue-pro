package cn.iocoder.yudao.module.lowcode.querier.common;

import lombok.Data;

import java.util.List;

/**
 * @author leo
 */
@Data
public class QueryDomainParams {

    /**
     * 分页参数
     */
    private QueryDomainPageParams pageParams;

    /**
     * 查询参数
     */
    private List<QueryDomainWhereParams> whereParams;

}
