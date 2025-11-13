package cn.iocoder.yudao.module.lowcode.controller.admin.materialfilelog;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lowcode.controller.admin.BaseLowcodeController;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfilelog.vo.MaterialFileLogPageReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfilelog.vo.MaterialFileLogRespVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfilelog.MaterialFileLogDO;
import cn.iocoder.yudao.module.lowcode.service.materialfilelog.MaterialFileLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @author leo
 */
@Tag(name = "管理后台 - 低代码-物料文件操作日志")
@RestController
@RequestMapping("/lowcode/material-file-log")
@Validated
public class MaterialFileLogController extends BaseLowcodeController {

    @Resource
    private MaterialFileLogService materialFileLogService;

    @GetMapping("/page")
    @Operation(summary = "获得低代码-物料文件操作日志分页")
    public CommonResult<PageResult<MaterialFileLogRespVO>> getMaterialFileLogPage(@Valid MaterialFileLogPageReqVO pageReqVO) {
        checkSourceAndOperator(QUERY, pageReqVO.getFileId());
        PageResult<MaterialFileLogDO> pageResult = materialFileLogService.getMaterialFileLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialFileLogRespVO.class));
    }

}