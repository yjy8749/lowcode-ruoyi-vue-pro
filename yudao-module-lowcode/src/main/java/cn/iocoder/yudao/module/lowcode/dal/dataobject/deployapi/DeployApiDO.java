package cn.iocoder.yudao.module.lowcode.dal.dataobject.deployapi;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 低代码-部署接口 DO
 *
 * @author 芋道源码
 */
@TableName("lowcode_deploy_api")
@KeySequence("lowcode_deploy_api_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployApiDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 接口编码
     */
    private String apiCode;
    /**
     * 接口名称
     */
    private String apiName;
    /**
     * 接口源文件ID
     */
    private Long sourceFileId;
    /**
     * 接口源文件版本号
     */
    private Integer sourceFileVersion;
    /**
     * 接口状态 0-待上线 1-已上线 2-已下线
     */
    private Integer apiStatus;

}