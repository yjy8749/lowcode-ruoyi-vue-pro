package cn.iocoder.yudao.module.lowcode.utils;

import cn.iocoder.yudao.framework.common.util.spring.SpringUtils;
import cn.iocoder.yudao.framework.mybatis.core.util.JdbcUtils;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.YmlDynamicDataSourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * @author leo
 */
@Slf4j
public class LowcodeJdbcUtils extends JdbcUtils {

    /**
     * 构建动态数据源
     *
     * @param ds                 数据源name
     * @param dataSourceProperty 数据源配置参数
     */
    public static DataSource buildDataSource(String ds, DataSourceProperty dataSourceProperty) {
        try {
            DefaultDataSourceCreator defaultDataSourceCreator = SpringUtils.getBean(DefaultDataSourceCreator.class);
            var dataSourcePropertiesMap = new HashMap<String, DataSourceProperty>();
            dataSourcePropertiesMap.put(ds, dataSourceProperty);
            var dataSourceProvider = new YmlDynamicDataSourceProvider(defaultDataSourceCreator, dataSourcePropertiesMap);
            return dataSourceProvider.loadDataSources().get(ds);
        } catch (NoSuchBeanDefinitionException e) {
            log.error("DefaultDataSourceCreator not exist", e);
            throw new RuntimeException("DefaultDataSourceCreator not exist, please enable dynamic datasource first!!!");
        }
    }

    /**
     * 增加动态数据源
     *
     * @param ds                 数据源name
     * @param dataSourceProperty 数据源配置参数
     */
    public static void addDataSource(String ds, DataSourceProperty dataSourceProperty) {
        try {
            DynamicRoutingDataSource dynamicRoutingDataSource = SpringUtils.getBean(DynamicRoutingDataSource.class);
            if (dynamicRoutingDataSource.getDataSource(ds) == null) {
                synchronized (JdbcUtils.class) {
                    if (dynamicRoutingDataSource.getDataSource(ds) == null) {
                        var dataSource = buildDataSource(ds, dataSourceProperty);
                        dynamicRoutingDataSource.addDataSource(ds, dataSource);
                    }
                }
            }
        } catch (NoSuchBeanDefinitionException e) {
            log.error("DynamicRoutingDataSource not exist", e);
            throw new RuntimeException("DynamicRoutingDataSource not exist, please enable dynamic datasource first!!!");
        }
    }

    /**
     * 获取数据源
     *
     * @param ds 数据源name
     */
    public static DataSource getDataSource(String ds) {
        try {
            DynamicRoutingDataSource dynamicRoutingDataSource = SpringUtils.getBean(DynamicRoutingDataSource.class);
            return dynamicRoutingDataSource.getDataSource(ds);
        } catch (NoSuchBeanDefinitionException e) {
            log.error("DynamicRoutingDataSource not exist", e);
            throw new RuntimeException("DynamicRoutingDataSource not exist, please enable dynamic datasource first!!!");
        }
    }

}
