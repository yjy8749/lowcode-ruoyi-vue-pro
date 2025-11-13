package cn.iocoder.yudao.module.lowcode.controller.admin.materialfilelog.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件操作日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaterialFileLogPageReqVO extends PageParam {

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "文件ID", example = "22863")
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @Schema(description = "来源 0-无 1-查询器 2-设计器 3-集成器")
    @NotNull(message = "来源不能为空")
    private Integer fileSource;

    @Schema(description = "数据类型 0-主数据", example = "2")
    private Integer dataType;

    @Schema(description = "操作类型", example = "2")
    private Integer opType;

}