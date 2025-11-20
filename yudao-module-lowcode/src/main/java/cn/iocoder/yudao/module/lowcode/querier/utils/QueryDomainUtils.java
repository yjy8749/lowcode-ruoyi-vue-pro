package cn.iocoder.yudao.module.lowcode.querier.utils;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainPageParams;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainParams;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainSymbolType;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainWhereParams;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author leo
 */
@Slf4j
public class QueryDomainUtils {

    public static void addWhere(QueryDomainParams params, String name, String symbol, Object value) {
        addWhere(params, name, QueryDomainSymbolType.valueOf(symbol.toUpperCase()), value);
    }

    public static void addWhere(QueryDomainParams params, String name, QueryDomainSymbolType symbol, Object value) {
        if (params.getWhereParams() == null) {
            params.setWhereParams(new ArrayList<>());
        }
        var exist = params.getWhereParams().stream().filter(i -> i.getName().equals(name)).findFirst();
        if (exist.isEmpty()) {
            if (value instanceof List<?>) {
                params.getWhereParams().add(new QueryDomainWhereParams(name, symbol, (List<Object>) value));
            } else {
                params.getWhereParams().add(new QueryDomainWhereParams(name, symbol, value));
            }
        } else {
            exist.get().setSymbol(symbol);
            if (value instanceof List<?>) {
                exist.get().setValues((List<Object>) value);
            } else {
                exist.get().setValue(value);
            }
        }
    }

    public static QueryDomainWhereParams getWhere(QueryDomainParams params, String name) {
        if (params.getWhereParams() == null) {
            return null;
        }
        return params.getWhereParams().stream().filter(i -> i.getName().equals(name)).findFirst().orElse(null);
    }

    public static void setPage(QueryDomainParams params, Integer pageNo, Integer pageSize) {
        if (params.getPageParams() == null) {
            params.setPageParams(new QueryDomainPageParams());
        }
        params.getPageParams().setPageNo(pageNo);
        params.getPageParams().setPageSize(pageSize);
    }

    public static void addSort(QueryDomainParams params, String field, String order) {
        if (params.getPageParams() == null) {
            params.setPageParams(new QueryDomainPageParams());
        }
        if (params.getPageParams().getSortingFields() == null) {
            params.getPageParams().setSortingFields(new ArrayList<>());
        }
        params.getPageParams().getSortingFields().add(new SortingField(field, order));
    }

    public static void log(String type, String msg, Object... args) {
        if (ArrayUtil.isNotEmpty(args)) {
            var holders = new String[args.length];
            Arrays.fill(holders, "{}");
            var params = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                params[i] = JSON.toJSONString(args[i]);
            }
            msg = msg + ArrayUtil.join(holders, ",");
            args = params;
        }
        if ("error".equals(type)) {
            log.error(msg, args);
        } else if ("warn".equals(type)) {
            log.warn(msg, args);
        } else if ("debug".equals(type)) {
            log.debug(msg, args);
        } else {
            log.info(msg, args);
        }
    }

    public static void logInfo(String s, Object... args) {
        log("info", s, args);
    }

    public static void logError(String s, Object... args) {
        log("error", s, args);
    }

    public static void logWarn(String s, Object... args) {
        log("warn", s, args);
    }

    public static void logDebug(String s, Object... args) {
        log("debug", s, args);
    }

}
