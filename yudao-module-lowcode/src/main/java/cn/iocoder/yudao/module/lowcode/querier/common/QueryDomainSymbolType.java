package cn.iocoder.yudao.module.lowcode.querier.common;

/**
 * @author leo
 */

public enum QueryDomainSymbolType {
    NONE,               //none
    LLIKE,              //left like
    RLIKE,              //right like
    LIKE,               //like
    NOTLIKE,            //not like
    EQ,                 //equal
    NE,                 //not equal
    GE,                 //greater than or equal
    GT,                 //greater than
    LE,                 //lesser than or equal
    LT,                 //lesser than
    IN,                 //in
    NOTIN,              //not in
    BETWEEN,            //between
    ISNULL,             //is null
    NOTNULL,            //is not null
    JSON_CONTAINS,      // json_contains function
    FIND_IN_SET,        // find_in_set function
}
