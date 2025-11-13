package cn.iocoder.yudao.module.lowcode.dal.dataobject.mediafile;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 低代码-媒体库文件 DO
 *
 * @author 芋道源码
 */
@TableName("lowcode_media_file")
@KeySequence("lowcode_media_file_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 目录id
     */
    private Long dirId;
    /**
     * 目录id路径
     */
    private String dirIdPath;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 文件Url
     */
    private String url;

}