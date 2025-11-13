package cn.iocoder.yudao.module.lowcode.dal.dataobject.deploymenu;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 低代码-部署菜单 DO
 *
 * @author 芋道源码
 */
@TableName("lowcode_deploy_menu")
@KeySequence("lowcode_deploy_menu_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployMenuDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 菜单源文件ID
     */
    private Long sourceFileId;
    /**
     * 菜单源文件版本号
     */
    private Integer sourceFileVersion;
    /**
     * 菜单状态 1-已上线 2-已下线
     */
    private Integer menuStatus;

}