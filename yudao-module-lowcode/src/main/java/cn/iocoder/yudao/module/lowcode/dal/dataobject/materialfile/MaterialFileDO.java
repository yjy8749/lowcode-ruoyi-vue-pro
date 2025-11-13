package cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 低代码-物料文件 DO
 *
 * @author 芋道源码
 */
@TableName("lowcode_material_file")
@KeySequence("lowcode_material_file_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialFileDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 来源 0-无 1-查询器 2-设计器 3-集成器
     */
    private Integer source;
    /**
     * 父ID
     */
    private Long parentId;
    /**
     * 是否是文件
     */
    private Boolean isFile;
    /**
     * 是否私有
     */
    private Boolean isPrivate;
    /**
     * 状态 0-禁用 1-正常 2-锁定 3-弃用
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 来源文件ID
     */
    private Long sourceFileId;
    /**
     * 来源文件版本号
     */
    private Integer sourceFileVersion;

}