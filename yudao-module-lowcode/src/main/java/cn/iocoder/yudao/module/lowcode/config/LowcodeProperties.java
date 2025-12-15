package cn.iocoder.yudao.module.lowcode.config;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

/**
 * @author leo
 */
@Data
@ConfigurationProperties(prefix = "yudao.lowcode")
public class LowcodeProperties {

    /**
     * 允许的数据源名称列表
     */
    private Map<Long,List<String>> enabledDataSources;

    /**
     * 禁用的数据源名称列表
     */
    private Map<Long,List<String>> disabledDataSources;

}
