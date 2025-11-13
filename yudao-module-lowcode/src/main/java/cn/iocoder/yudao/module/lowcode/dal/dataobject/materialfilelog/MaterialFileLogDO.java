package cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfilelog;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 低代码-物料文件操作日志 DO
 *
 * @author 芋道源码
 */
@TableName("lowcode_material_file_log")
@KeySequence("lowcode_material_file_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialFileLogDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 文件ID
     */
    private Long fileId;
    /**
     * 来源 0-无 1-查询器 2-设计器 3-集成器
     */
    private Integer fileSource;
    /**
     * 数据类型 0-主数据
     */
    private Integer dataType;
    /**
     * 操作类型
     */
    private Integer opType;
    /**
     * 操作描述
     */
    private String opDesc;
    /**
     * 详细信息
     */
    private String opDetail;
    /**
     * 版本号
     */
    private Integer opVersion;
    /**
     * 文件数据
     */
    private String opData;

}