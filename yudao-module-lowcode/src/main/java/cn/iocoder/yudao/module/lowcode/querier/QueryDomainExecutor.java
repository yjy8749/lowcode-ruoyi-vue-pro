package cn.iocoder.yudao.module.lowcode.querier;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.converters.longconverter.LongStringConverter;
import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.spring.SpringUtils;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainPageParams;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainParams;
import cn.iocoder.yudao.module.lowcode.querier.filter.*;
import cn.iocoder.yudao.module.lowcode.querier.interceptor.JavaScriptInterceptor;
import cn.iocoder.yudao.module.lowcode.querier.interceptor.QueryDomainAuthTypeInterceptor;
import cn.iocoder.yudao.module.lowcode.querier.interceptor.QueryDomainInterceptor;
import cn.iocoder.yudao.module.lowcode.querier.interceptor.QueryFieldHiddenInterceptor;
import cn.iocoder.yudao.module.lowcode.querier.mapper.QueryDomainMapper;
import cn.iocoder.yudao.module.lowcode.querier.valid.JavaScriptValidTest;
import cn.iocoder.yudao.module.lowcode.querier.valid.QueryDomainValidTest;
import cn.iocoder.yudao.module.lowcode.querier.xml.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import javax.script.ScriptException;
import java.util.*;
import java.util.function.Function;

import static cn.hutool.core.date.DatePattern.PURE_DATETIME_PATTERN;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.QUERY_DOMAIN_EXECUTE_ERROR;

/**
 * @author leo
 */
@Slf4j
public class QueryDomainExecutor {

    private final Boolean tenantEnable;
    private final String logicDeleteValue;
    protected final String queryDomainId;
    protected final String dataSourceName;
    protected final QueryDomain queryDomain;
    private final List<QueryDomainFilter> filterList;
    private final List<QueryDomainInterceptor> interceptorList;
    private final Map<String, QueryDomainValidTest> validTestMap;
    private final QueryDomainMapper queryDomainMapper;

    public QueryDomainExecutor(Boolean tenantEnable, String logicDeleteValue, String queryDomainId, String dataSourceName, QueryDomain queryDomain) throws ClassNotFoundException {
        this.tenantEnable = tenantEnable;
        this.logicDeleteValue = logicDeleteValue;
        this.queryDomainId = queryDomainId;
        this.dataSourceName = dataSourceName;
        this.queryDomain = queryDomain;
        this.filterList = Lists.newArrayList();
        this.interceptorList = Lists.newArrayList();
        this.validTestMap = new HashMap<>();
        this.initFilter();
        this.initInterceptor();
        this.initValidTestMap();
        this.queryDomainMapper = SpringUtils.getBean(QueryDomainMapper.class);
    }

    private void initFilter() throws ClassNotFoundException {
        // 添加默认 Filter
        this.filterList.add(QueryDomainLoginFilter.INSTANCE);
        this.filterList.add(SpringUtils.getBean(QueryDomainPermissionFilter.class));
        this.filterList.add(QueryDomainParamsRequiredFilter.INSTANCE);
        // 添加自定义 Filter
        QueryFilter queryFilter = this.queryDomain.getQueryFilter();
        if (queryFilter != null && CollectionUtil.isNotEmpty(queryFilter.getQueryFilterValueList())) {
            for (QueryFilterValue queryFilterValue : queryFilter.getQueryFilterValueList()) {
                if (StrUtil.isNotEmpty(queryFilterValue.getClazz())) {
                    this.filterList.add((QueryDomainFilter) SpringUtils.getBean(Class.forName(queryFilterValue.getClazz())));
                } else if (StrUtil.isNotEmpty(queryFilterValue.getValue())) {
                    this.filterList.add(new JavaScriptFilter(queryFilterValue.getValue()));
                }
            }
        }
    }

    private void initInterceptor() throws ClassNotFoundException {
        this.interceptorList.add(QueryDomainAuthTypeInterceptor.INSTANCE);
        this.interceptorList.add(QueryFieldHiddenInterceptor.INSTANCE);
        QueryInterceptor queryInterceptor = this.queryDomain.getQueryInterceptor();
        if (queryInterceptor != null && CollectionUtil.isNotEmpty(queryInterceptor.getQueryInterceptorValueList())) {
            for (QueryInterceptorValue queryInterceptorValue : queryInterceptor.getQueryInterceptorValueList()) {
                if (StrUtil.isNotEmpty(queryInterceptorValue.getClazz())) {
                    this.interceptorList.add((QueryDomainInterceptor) SpringUtils.getBean(Class.forName(queryInterceptorValue.getClazz())));
                } else {
                    String preHandleValue = null;
                    if (queryInterceptorValue.getPreHandleValue() != null) {
                        preHandleValue = queryInterceptorValue.getPreHandleValue().getValue();
                    }
                    String postHandleValue = null;
                    if (queryInterceptorValue.getPostHandleValue() != null) {
                        postHandleValue = queryInterceptorValue.getPostHandleValue().getValue();
                    }
                    if (StrUtil.isNotEmpty(preHandleValue) || StrUtil.isNotEmpty(postHandleValue)) {
                        this.interceptorList.add(new JavaScriptInterceptor(preHandleValue, postHandleValue));
                    }
                }
            }
        }
    }

    private void initValidTestMap(List<QueryTable> tableList) throws ClassNotFoundException {
        if (CollectionUtil.isNotEmpty(tableList)) {
            for (QueryTable queryTable : tableList) {
                if (queryTable.getValidTestValue() != null) {
                    if (StrUtil.isNotEmpty(queryTable.getValidTestValue().getClazz())) {
                        var validBean = SpringUtils.getBean(Class.forName(queryTable.getValidTestValue().getClazz()));
                        this.validTestMap.put(queryTable.getValidTestValue().getClazz(), (QueryDomainValidTest) validBean);
                    } else if (StrUtil.isNotEmpty(queryTable.getValidTestValue().getValue())) {
                        this.validTestMap.put(queryTable.getValidTestValue().getValue(), new JavaScriptValidTest(queryTable.getValidTestValue().getValue()));
                    }
                }
            }
        }
    }

    private void initValidTestMap() throws ClassNotFoundException {
        this.initValidTestMap(this.queryDomain.getMainTableList());
        this.initValidTestMap(this.queryDomain.getQueryTableList());
    }

    private void doFilter(QueryDomainContext context) throws ScriptException {
        for (QueryDomainFilter queryDomainFilter : this.filterList) {
            queryDomainFilter.doFilter(context);
        }
    }

    private void doInterceptPreHandle(QueryDomainContext context) throws ScriptException {
        for (QueryDomainInterceptor queryDomainInterceptor : this.interceptorList) {
            context.setParams(queryDomainInterceptor.preHandle(context));
        }
    }

    private void doInterceptPostHandle(QueryDomainContext context) throws ScriptException {
        for (int i = this.interceptorList.size(); i > 0; i--) {
            QueryDomainInterceptor queryDomainInterceptor = this.interceptorList.get(i - 1);
            context.setResults(queryDomainInterceptor.postHandle(context));
        }
    }

    private boolean doValidTest(QueryTable table, QueryDomainContext context) {
        if (table.getValidTestValue() != null) {
            var validTest = this.validTestMap.get(StrUtil.emptyToDefault(table.getValidTestValue().getClazz(), table.getValidTestValue().getValue()));
            if (validTest != null) {
                try {
                    return validTest.doTest(context);
                } catch (ScriptException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    private QueryTable getValidMainTable(QueryDomainContext context) {
        var opt = this.queryDomain.getMainTableList().stream().filter(table -> table.getValidTestValue() != null && doValidTest(table, context)).findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }
        opt = this.queryDomain.getMainTableList().stream().filter(table -> table.getValidTestValue() == null).findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }
        throw new RuntimeException("必须存在一个有效的主表");
    }

    private List<QueryTable> getValidQueryTableList(QueryDomainContext context) {
        return this.queryDomain.getQueryTableList().stream().filter(table -> doValidTest(table, context)).toList();
    }

    private List<Map<Object, Object>> execute(QueryDomainContext context, Function<QueryDomainSqlBuilder, String> sqlBuilderCall, Function<Map<String, Object>, List<Map<Object, Object>>> mapperCall, Boolean isPageCountQuery) {
        DynamicDataSourceContextHolder.push(dataSourceName);
        try {
            if (context.getMainTable() == null) {
                context.setMainTable(this.getValidMainTable(context));
            }
            if (context.getTableList() == null) {
                context.setTableList(this.getValidQueryTableList(context));
            }
            if (!Boolean.TRUE.equals(isPageCountQuery)) {
                this.doFilter(context);
            }

            this.doInterceptPreHandle(context);

            var sqlBuilder = new QueryDomainSqlBuilder(context);

            String sql = sqlBuilderCall.apply(sqlBuilder);
            Map<String, Object> sqlMap = sqlBuilder.getSqlParams();
            sqlMap.put("sql", sql);

            context.setResults(mapperCall.apply(sqlMap));
            if (!Boolean.TRUE.equals(isPageCountQuery)) {
                this.doInterceptPostHandle(context);
            }
            return context.getResults();
        } catch (ServiceException e) {
            throw e;
        } catch (Throwable e) {
            log.error(String.valueOf(QUERY_DOMAIN_EXECUTE_ERROR), e);
            throw exception(QUERY_DOMAIN_EXECUTE_ERROR);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    private QueryDomainContext buildContext(QueryDomainParams params) {
        QueryDomainContext context = new QueryDomainContext();
        context.setDomain(this.queryDomain);
        context.setTenantEnable(this.tenantEnable);
        context.setLogicDeleteValue(this.logicDeleteValue);
        context.setParams(params == null ? new QueryDomainParams() : params);
        return context;
    }

    public List<Map<Object, Object>> selectList(QueryDomainParams params) {
        return execute(buildContext(params), QueryDomainSqlBuilder::getSelectListSql, queryDomainMapper::selectList, false);
    }

    public Map<Object, Object> selectOne(QueryDomainParams params) {
        var list = execute(buildContext(params), QueryDomainSqlBuilder::getSelectOneSql, queryDomainMapper::selectList, false);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public Long selectCount(QueryDomainParams params) {
        return (Long) execute(buildContext(params), QueryDomainSqlBuilder::getSelectCountSql, queryDomainMapper::selectList, false).get(0).get("cnt");
    }

    public PageResult<Map<Object, Object>> selectPage(QueryDomainParams params) {
        var context = buildContext(params);
        var list = execute(context, QueryDomainSqlBuilder::getSelectListSql, queryDomainMapper::selectList, false);
        var count = execute(context, QueryDomainSqlBuilder::getSelectCountSql, queryDomainMapper::selectList, true);
        return new PageResult<>(list, (Long) count.get(0).get("cnt"));
    }

    @SneakyThrows
    public void export(QueryDomainParams params, HttpServletResponse response) {
        this.export(params, response, 1000000);
    }

    @SneakyThrows
    public void export(QueryDomainParams params, HttpServletResponse response, int maxExptCnt) {
        if (params == null) {
            params = new QueryDomainParams();
        }
        var pageParams = new QueryDomainPageParams();
        pageParams.setPageNo(1);
        pageParams.setPageSize(Math.min(maxExptCnt, 1000));
        if (params.getPageParams() != null) {
            pageParams.setSortingFields(params.getPageParams().getSortingFields());
        }
        params.setPageParams(pageParams);
        log.info("开始导出 每页 {} 条, 最多导出 {} 条, 导出参数 {}", pageParams.getPageSize(), maxExptCnt, JSON.toJSONString(params));

        long exportStartTime = System.currentTimeMillis();
        int totalCnt = 0;
        boolean isNeedWriteHead = true;
        String excelFileName = null;
        List<QueryField> excelHeadFieldList = null;
        ExcelWriterSheetBuilder exportWriter = null;
        do {
            long startTime = System.currentTimeMillis();
            var pageNo = params.getPageParams().getPageNo();
            var context = buildContext(params);
            var list = execute(context, QueryDomainSqlBuilder::getSelectListSql, queryDomainMapper::selectList, false);
            if (isNeedWriteHead) {
                isNeedWriteHead = false;
                var domainDesc = context.getDomain().getDesc();
                excelHeadFieldList = context.getTableFieldList().stream().filter(i -> !Boolean.TRUE.equals(i.getDisableExpt())).toList();
                var heads = excelHeadFieldList.stream().map((e) -> Collections.singletonList(e.getComment())).toList();
                exportWriter = EasyExcel.write(response.getOutputStream())
                        .autoCloseStream(false)
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .registerConverter(new LongStringConverter())
                        .head(heads)
                        .sheet(domainDesc);
                excelFileName = domainDesc + DateUtil.format(new Date(), PURE_DATETIME_PATTERN);
                log.info("{} 开始写入表头: {}", excelFileName, JSON.toJSONString(heads));
            }
            if (CollectionUtil.isNotEmpty((list))) {
                log.info("{} 开始导出第 {} 页, 已导出 {} 条, 待导出 {} 条", excelFileName, pageNo, totalCnt, list.size());
                List<QueryField> finalExcelHeadFieldList = excelHeadFieldList;
                List<List<Object>> dataList = list.stream()
                        .map(item -> finalExcelHeadFieldList.stream().map(head -> item.get(head.getName())).toList()).toList();
                exportWriter.doWrite(dataList);
                totalCnt += list.size();
                log.info("{} 第 {} 页导出成功, 耗时: {}ms", excelFileName, pageNo, System.currentTimeMillis() - startTime);
            }
            var isExptFinished = CollectionUtil.isEmpty(list) || maxExptCnt <= totalCnt;
            if (isExptFinished) {
                log.info("{} 导出完成, 全部数据 {} 条, 总耗时: {}ms", excelFileName, totalCnt, System.currentTimeMillis() - exportStartTime);
                break;
            } else {
                params.getPageParams().setPageNo(pageNo + 1);
            }
        } while (true);
        response.addHeader("Content-Disposition", "attachment;filename=" + HttpUtils.encodeUtf8(excelFileName));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
    }

}
