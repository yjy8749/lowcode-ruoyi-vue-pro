package cn.iocoder.yudao.module.lowcode.querier.common;

import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import lombok.Builder;
import lombok.Data;

/**
 * @author leo
 */
@Data
@Builder
public class QueryDomainPageParams extends SortablePageParam {

    public QueryDomainPageParams() {
    }
    
}
