package cn.iocoder.yudao.module.lowcode.dal.dataobject.mediadir;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 低代码-媒体库目录 DO
 *
 * @author 芋道源码
 */
@TableName("lowcode_media_dir")
@KeySequence("lowcode_media_dir_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDirDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 目录名称
     */
    private String name;
    /**
     * 目录id路径
     */
    private String idPath;
    /**
     * 父id
     */
    private Long parentId;


}