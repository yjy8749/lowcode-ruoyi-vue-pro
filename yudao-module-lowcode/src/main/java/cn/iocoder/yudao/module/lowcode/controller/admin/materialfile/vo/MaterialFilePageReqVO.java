package cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaterialFilePageReqVO extends PageParam {

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "名称", example = "王五")
    private String name;

    @Schema(description = "来源 0-无 1-查询器 2-设计器 3-集成器")
    @NotNull(message = "来源不能为空")
    private Integer source;

    @Schema(description = "父ID", example = "21763")
    private Long parentId;

    @Schema(description = "是否是文件")
    private Boolean isFile;

    @Schema(description = "状态 0-禁用 1-正常 2-锁定 3-弃用", example = "1")
    private Integer status;

    @Schema(description = "创建人", example = "王五")
    private String creator;

    @Schema(description = "是否为集成器选择查询")
    private Boolean integratorSelectable;

    @Schema(description = "Id列表")
    private List<Integer> ids;

}