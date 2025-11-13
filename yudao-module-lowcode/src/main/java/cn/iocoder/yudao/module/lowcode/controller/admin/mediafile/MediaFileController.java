package cn.iocoder.yudao.module.lowcode.controller.admin.mediafile;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.lowcode.controller.admin.mediafile.vo.MediaFileBatchDeleteReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.mediafile.vo.MediaFileBatchSaveReqVO;
import cn.iocoder.yudao.module.lowcode.service.mediafile.MediaFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @author leo
 */
@Tag(name = "管理后台 - 低代码-媒体库文件")
@RestController
@RequestMapping("/lowcode/media-file")
@Validated
public class MediaFileController {

    @Resource
    private MediaFileService mediaFileService;

    @PostMapping("/batch-save")
    @Operation(summary = "批量保存低代码-媒体库文件")
    @PreAuthorize("@ss.hasPermission('lowcode:media:manage')")
    public CommonResult<Boolean> batchSave(@Valid @RequestBody MediaFileBatchSaveReqVO batchSaveReqVO) {
        return success(mediaFileService.batchSave(batchSaveReqVO));
    }

    @PostMapping("/batch-delete")
    @Operation(summary = "删除低代码-媒体库文件")
    @PreAuthorize("@ss.hasPermission('lowcode:media:manage')")
    public CommonResult<Boolean> batchDelete(@Valid @RequestBody MediaFileBatchDeleteReqVO batchDeleteReqVO) {
        return success(mediaFileService.batchDelete(batchDeleteReqVO));
    }

}