package cn.iocoder.yudao.module.lowcode.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * lowcode 模块的 web 组件的 Configuration
 *
 * @author leo
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LowcodeProperties.class)
public class LowcodeWebConfiguration {

    @Bean
    public GroupedOpenApi lowcodeGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("lowcode");
    }

}
