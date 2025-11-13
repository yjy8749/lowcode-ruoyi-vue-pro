package cn.iocoder.yudao.module.lowcode.controller.admin.mediadir;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.lowcode.controller.admin.mediadir.vo.MediaDirSaveReqVO;
import cn.iocoder.yudao.module.lowcode.service.mediadir.MediaDirService;
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
@Tag(name = "管理后台 - 低代码-媒体库目录")
@RestController
@RequestMapping("/lowcode/media-dir")
@Validated
public class MediaDirController {

    @Resource
    private MediaDirService mediaDirService;

    @PostMapping("/create")
    @Operation(summary = "创建低代码-媒体库目录")
    @PreAuthorize("@ss.hasPermission('lowcode:media:manage')")
    public CommonResult<Long> createMediaDir(@Valid @RequestBody MediaDirSaveReqVO createReqVO) {
        return success(mediaDirService.createMediaDir(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新低代码-媒体库目录")
    @PreAuthorize("@ss.hasPermission('lowcode:media:manage')")
    public CommonResult<Boolean> updateMediaDir(@Valid @RequestBody MediaDirSaveReqVO updateReqVO) {
        mediaDirService.updateMediaDir(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除低代码-媒体库目录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('lowcode:media:manage')")
    public CommonResult<Boolean> deleteMediaDir(@RequestParam("id") Long id) {
        mediaDirService.deleteMediaDir(id);
        return success(true);
    }

}