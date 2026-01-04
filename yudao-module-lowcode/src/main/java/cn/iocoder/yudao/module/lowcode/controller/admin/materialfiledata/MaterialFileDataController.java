package cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.lowcode.controller.admin.BaseLowcodeController;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.GetMaterialFileDataReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.MaterialFileDataRespVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.MaterialFileDataSaveReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;
import cn.iocoder.yudao.module.lowcode.service.materialfiledata.MaterialFileDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @author leo
 */
@Tag(name = "管理后台 - 低代码-物料文件数据")
@RestController
@RequestMapping("/lowcode/material-file-data")
@Validated
public class MaterialFileDataController extends BaseLowcodeController {

    @Resource
    private MaterialFileDataService materialFileDataService;

    @GetMapping("/get")
    @Operation(summary = "获得低代码-物料文件数据")
    @TenantIgnore
    public CommonResult<MaterialFileDataRespVO> getMaterialFileData(@Valid GetMaterialFileDataReqVO getReqVO) {
        MaterialFileDataDO materialFileData = materialFileDataService.getMaterialFileData(getReqVO);
        return success(BeanUtils.toBean(materialFileData, MaterialFileDataRespVO.class));
    }

    @PostMapping("/save")
    @Operation(summary = "保存低代码-物料文件数据")
    public CommonResult<MaterialFileDataRespVO> saveMaterialFileData(@Valid @RequestBody MaterialFileDataSaveReqVO saveReqVO) {
        checkSourceAndOperator(EDITOR, saveReqVO.getFileId());
        MaterialFileDataDO materialFileData = materialFileDataService.saveMaterialFileData(saveReqVO);
        return success(BeanUtils.toBean(materialFileData, MaterialFileDataRespVO.class));
    }

}