package cn.iocoder.yudao.module.lowcode.querier.common;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leo
 */
@Data
public class QueryDomainWhereParams implements Serializable {

    /**
     * and 条件
     */
    private List<QueryDomainWhereParams> ands;

    /**
     * or 条件
     */
    private List<QueryDomainWhereParams> ors;

    /**
     * 条件名
     */
    private String name;

    /**
     * 条件符号
     */
    private QueryDomainSymbolType symbol;

    /**
     * 条件值
     */
    private Object value;

    /**
     * 多个条件值
     */
    private List<Object> values;

    public QueryDomainWhereParams() {
    }

    public QueryDomainWhereParams(String name, QueryDomainSymbolType symbol, Object value) {
        this.name = name;
        this.symbol = symbol;
        this.value = value;
    }

    public QueryDomainWhereParams(String name, QueryDomainSymbolType symbol, List<Object> values) {
        this.name = name;
        this.symbol = symbol;
        this.values = values;
    }

    public QueryDomainWhereParams or(QueryDomainWhereParams args) {
        if (this.ors == null) {
            this.ors = new ArrayList<>();
        }
        this.ors.add(args);
        return this;
    }

    public QueryDomainWhereParams and(QueryDomainWhereParams args) {
        if (this.ands == null) {
            this.ands = new ArrayList<>();
        }
        this.ands.add(args);
        return this;
    }

}
