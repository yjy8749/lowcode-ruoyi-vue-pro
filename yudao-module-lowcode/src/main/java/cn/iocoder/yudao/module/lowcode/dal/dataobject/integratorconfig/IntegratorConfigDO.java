package cn.iocoder.yudao.module.lowcode.dal.dataobject.integratorconfig;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 低代码-集成器配置 DO
 *
 * @author 芋道源码
 */
@TableName("lowcode_integrator_config")
@KeySequence("lowcode_integrator_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegratorConfigDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 配置名称
     */
    private String configName;
    /**
     * 配置类型 0-本机 1-远程
     */
    private Integer configType;
    /**
     * 集成器入口
     */
    private String integrateEntry;
    /**
     * 集成校验KEY
     */
    private String integrateKey;
    /**
     * 备注
     */
    private String comment;

}