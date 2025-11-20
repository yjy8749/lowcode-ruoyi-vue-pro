package cn.iocoder.yudao.module.lowcode.querier.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainWhereParams;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryField;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception0;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.QUERY_DOMAIN_QUERY_REQUIRED_ERROR;

/**
 * @author leo
 */
public class QueryDomainParamsRequiredFilter implements QueryDomainFilter {

    public static volatile QueryDomainParamsRequiredFilter INSTANCE = new QueryDomainParamsRequiredFilter();

    private QueryDomainParamsRequiredFilter() {
    }

    @Override
    public void doFilter(QueryDomainContext context) {
        var requiredFields = context.queryFieldFilter((table, field) -> StrUtil.isNotEmpty(field.getRequired()));
        if (CollectionUtil.isEmpty(requiredFields)) {
            return;
        }

        Map<String, List<QueryField>> requiredQueryFields = new HashMap<>();
        for (QueryField queryField : requiredFields) {
            for (String groupName : queryField.getRequired().split(",")) {
                requiredQueryFields.computeIfAbsent(groupName, (key) -> new ArrayList<>());
                requiredQueryFields.get(groupName).add(queryField);
            }
        }

        var queryParams = context.getParams();
        Set<String> hasValueParamNameSet = new HashSet<>();
        if (queryParams != null && CollectionUtil.isNotEmpty(queryParams.getWhereParams())) {
            hasValueParamNameSet = queryParams.getWhereParams().stream()
                    .filter(e -> {
                        if (e.getValue() != null) {
                            if (e.getValue() instanceof String) {
                                return StrUtil.isNotEmpty((String) e.getValue());
                            } else {
                                return true;
                            }
                        } else {
                            return CollectionUtil.isNotEmpty(e.getValues());
                        }
                    }).map(QueryDomainWhereParams::getName).collect(Collectors.toSet());
        }

        List<String> requiredMsgList = new ArrayList<>();
        for (List<QueryField> requirdFiedList : requiredQueryFields.values()) {
            boolean isPass = true;
            StringBuilder sb = new StringBuilder();
            if (CollectionUtil.isNotEmpty(requirdFiedList)) {
                for (QueryField field : requirdFiedList) {
                    sb.append(field.getComment()).append('[').append(field.getName()).append(']').append(",");
                    isPass = isPass && hasValueParamNameSet.contains(field.getName());
                }
            }
            if (isPass) {
                return;
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" ").append(requirdFiedList.size() > 1 ? "全不为空" : "不为空");
            requiredMsgList.add(sb.toString());
        }

        throw exception0(QUERY_DOMAIN_QUERY_REQUIRED_ERROR.getCode(), "查询参数校验失败 {}", StrUtil.join("或", requiredMsgList));
    }

}
