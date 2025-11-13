package cn.iocoder.yudao.module.lowcode.controller.admin.materialfile;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lowcode.controller.admin.BaseLowcodeController;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo.*;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile.MaterialFileDO;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileSource;
import cn.iocoder.yudao.module.lowcode.service.materialfile.MaterialFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @author leo
 */
@Tag(name = "管理后台 - 低代码-物料文件")
@RestController
@RequestMapping("/lowcode/material-file")
@Validated
public class MaterialFileController extends BaseLowcodeController {

    @Resource
    private MaterialFileService materialFileService;

    @GetMapping("/folder-list")
    @Operation(summary = "获得低代码-物料文件目录List")
    public CommonResult<List<MaterialFileRespVO>> getMaterialFileFolderList(@Valid MaterialFilePageReqVO reqVO) {
        if(Boolean.TRUE.equals(reqVO.getIntegratorSelectable())){
            checkSource(EDITOR, MaterialFileSource.INTEGRATOR);
        }else {
            checkSource(QUERY, reqVO.getSource());
            reqVO.setCreator(getOperator());
        }
        reqVO.setIsFile(Boolean.FALSE);
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<MaterialFileDO> pageResult = materialFileService.getMaterialFilePage(reqVO);
        return success(BeanUtils.toBean(pageResult.getList(), MaterialFileRespVO.class));
    }

    @GetMapping("/file-page")
    @Operation(summary = "获得低代码-物料文件分页")
    public CommonResult<PageResult<MaterialFileRespVO>> getMaterialFilePage(@Valid MaterialFilePageReqVO reqVO) {
        if(Boolean.TRUE.equals(reqVO.getIntegratorSelectable())){
            checkSource(EDITOR, MaterialFileSource.INTEGRATOR);
        }else {
            checkSource(QUERY, reqVO.getSource());
            reqVO.setCreator(getOperator());
        }
        PageResult<MaterialFileDO> pageResult = materialFileService.getMaterialFilePage(reqVO);
        return success(BeanUtils.toBean(pageResult, MaterialFileRespVO.class));
    }

    @GetMapping("/get-file")
    @Operation(summary = "获得低代码-物料文件")
    public CommonResult<MaterialFileRespVO> getMaterialFile(@Valid GetMaterialFileReqVO reqVO) {
        checkSourceForRead(QUERY, reqVO.getId());
        var fileDO = materialFileService.getMaterialFileById(reqVO.getId());
        return success(BeanUtils.toBean(fileDO, MaterialFileRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "创建低代码-物料文件")
    public CommonResult<Long> createMaterialFile(@Valid @RequestBody MaterialFileSaveReqVO createReqVO) {
        checkSource(EDITOR, createReqVO.getSource());
        createReqVO.setCreator(getOperator());
        return success(materialFileService.createMaterialFile(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新低代码-物料文件")
    public CommonResult<Boolean> updateMaterialFile(@Valid @RequestBody MaterialFileUpdateReqVO updateReqVO) {
        checkSourceAndOperator(EDITOR, updateReqVO.getId());
        return success(materialFileService.updateMaterialFile(updateReqVO));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新状态低代码-物料文件")
    public CommonResult<Boolean> updateMaterialFileStatus(@Valid @RequestBody MaterialFileUpdateStatusReqVO updateReqVO) {
        checkSourceAndOperator(EDITOR, updateReqVO.getId());
        return success(materialFileService.updateMaterialFileStatus(updateReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除低代码-物料文件")
    public CommonResult<Boolean> deleteMaterialFile(@Valid @RequestBody MaterialFileDeleteReqVO deleteReqVO) {
        checkSourceAndOperator(EDITOR, deleteReqVO.getId());
        return success(materialFileService.deleteMaterialFile(deleteReqVO));
    }

    @PutMapping("/transfer")
    @Operation(summary = "转移所有权-物料文件")
    public CommonResult<Boolean> transferMaterialFile(@Valid @RequestBody MaterialFileTransferReqVO transferReqVO) {
        checkSourceAndOperator(EDITOR, transferReqVO.getId());
        transferReqVO.setCreator(getOperator());
        return success(materialFileService.transferMaterialFile(transferReqVO));
    }

    @PutMapping("/move")
    @Operation(summary = "移动位置-物料文件")
    public CommonResult<Boolean> moveMaterialFile(@Valid @RequestBody MaterialFileMoveReqVO transferReqVO) {
        checkSourceAndOperator(EDITOR, transferReqVO.getId());
        return success(materialFileService.moveMaterialFile(transferReqVO));
    }

}