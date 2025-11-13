package cn.iocoder.yudao.module.lowcode.querier;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.infra.dal.dataobject.db.DataSourceConfigDO;
import cn.iocoder.yudao.module.infra.service.db.DataSourceConfigService;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryDomain;
import cn.iocoder.yudao.module.lowcode.utils.LowcodeJdbcUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.QUERY_DOMAIN_BUILD_ERROR;

/**
 * @author leo
 */
@Slf4j
@Component
public class QueryDomainExecutorFactory {

    @Value("${yudao.tenant.enable:false}")
    private Boolean tenantEnable;

    @Value("${mybatis-plus.global-config.db-config.logic-delete-value:1}")
    private String logicDeleteValue;

    private static final long PRINT_INTERVAL_MS = 5 * 60 * 1000;

    private final Cache<String, QueryDomainExecutor> CACHE = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build();

    private final AtomicLong lastPrintTime = new AtomicLong(0);

    @Resource
    private DataSourceConfigService dataSourceConfigService;

    // 日志打印逻辑
    private void checkAndPrintStats() {
        long now = System.currentTimeMillis();
        if (now - lastPrintTime.get() >= PRINT_INTERVAL_MS && lastPrintTime.compareAndSet(lastPrintTime.get(), now)) {
            log.info("QueryDomainExecutorFactory CACHE size:{} stat {}", CACHE.size(), JSON.toJSONString(CACHE.stats()));
        }
    }

    public QueryDomain parseQueryDomainXml(String queryXml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(QueryDomain.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (QueryDomain) unmarshaller.unmarshal(new ByteArrayInputStream(queryXml.getBytes(StandardCharsets.UTF_8)));
        } catch (Throwable e) {
            log.error(String.valueOf(QUERY_DOMAIN_BUILD_ERROR), e);
            throw exception(QUERY_DOMAIN_BUILD_ERROR);
        }
    }

    public String toQueryDomainXml(QueryDomain queryDomain) {
        try {
            StringWriter sw = new StringWriter();
            JAXBContext jaxbContext = JAXBContext.newInstance(QueryDomain.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(queryDomain, sw);
            return XmlUtil.format(sw.toString());
        } catch (Throwable e) {
            log.error(String.valueOf(QUERY_DOMAIN_BUILD_ERROR), e);
            throw exception(QUERY_DOMAIN_BUILD_ERROR);
        }
    }

    private QueryDomainExecutor getQueryDomainExecutor(Long dataSourceId, String queryXml) throws ExecutionException {
        String queryDomainId = SecureUtil.md5(dataSourceId + "(" + queryXml + ")");
        return CACHE.get(queryDomainId, () -> {
            DataSourceConfigDO config = dataSourceConfigService.getDataSourceConfig(dataSourceId);
            if (config == null) {
                config = dataSourceConfigService.getDataSourceConfig(0L);
            }

            initDataSource(config);

            var queryDomain = parseQueryDomainXml(queryXml);

            QueryDomainExecutor queryDomainExecutor;
            if (Boolean.TRUE.equals(queryDomain.getCache())) {
                queryDomainExecutor = new QueryDomainCacheExecutor(tenantEnable, logicDeleteValue, queryDomainId, config.getName(), queryDomain);
            } else {
                queryDomainExecutor = new QueryDomainExecutor(tenantEnable, logicDeleteValue, queryDomainId, config.getName(), queryDomain);
            }
            return queryDomainExecutor;
        });
    }

    private void initDataSource(DataSourceConfigDO dataSourceConfig) {
        var dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setUrl(dataSourceConfig.getUrl());
        dataSourceProperty.setUsername(dataSourceConfig.getUsername());
        dataSourceProperty.setPassword(dataSourceConfig.getPassword());
        LowcodeJdbcUtils.addDataSource(dataSourceConfig.getName(), dataSourceProperty);
    }

    public QueryDomainExecutor createExecutor(Long dataSourceId, String queryXml) {
        try {
            return this.getQueryDomainExecutor(dataSourceId, queryXml);
        } catch (ServiceException e) {
            throw e;
        } catch (Throwable e) {
            log.error(String.valueOf(QUERY_DOMAIN_BUILD_ERROR), e);
            throw exception(QUERY_DOMAIN_BUILD_ERROR);
        } finally {
            checkAndPrintStats();
        }
    }

}
