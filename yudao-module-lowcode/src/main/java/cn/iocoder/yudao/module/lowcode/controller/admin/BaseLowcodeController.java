package cn.iocoder.yudao.module.lowcode.controller.admin;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkService;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile.MaterialFileDO;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileSource;
import cn.iocoder.yudao.module.lowcode.service.materialfile.MaterialFileService;
import jakarta.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.FORBIDDEN;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * @author leo
 */
public abstract class BaseLowcodeController {

    protected static final String QUERY = "lowcode:%s:query";

    protected static final String EDITOR = "lowcode:%s:editor";

    @Resource
    SecurityFrameworkService ss;

    @Resource
    MaterialFileService materialFileService;

    protected void checkSource(String formatStr, Integer srcVal) {
        checkSource(formatStr, MaterialFileSource.of(srcVal).orElse(null));
    }

    protected void checkSource(String formatStr, MaterialFileSource source) {
        if (source != null) {
            if (ss.hasPermission(String.format(formatStr, StrUtil.toCamelCase(source.getName())))) {
                return;
            }
        }
        throw exception(FORBIDDEN);
    }

    protected void checkSourceForRead(String formatStr, Long fileId) {
        MaterialFileDO materialFileDO = this.materialFileService.getMaterialFileById(fileId);
        checkSource(formatStr, materialFileDO.getSource());
        String creator = materialFileDO.getCreator();
        if (Boolean.TRUE.equals(materialFileDO.getIsPrivate()) && !StrUtil.equals(creator, getOperator())) {
            throw exception(FORBIDDEN);
        }
    }

    protected String getOperator() {
        return StrUtil.toStringOrNull(getLoginUserId());
    }

    protected void checkOperator(Long fileId) {
        MaterialFileDO materialFileDO = this.materialFileService.getMaterialFileById(fileId);
        checkOperator(materialFileDO);
    }

    protected void checkOperator(MaterialFileDO materialFileDO) {
        String creator = materialFileDO.getCreator();
        if (StrUtil.equals(creator, getOperator())) {
            return;
        }
        throw exception(FORBIDDEN);
    }

    protected void checkSourceAndOperator(String formatStr, Long fileId) {
        MaterialFileDO materialFileDO = this.materialFileService.getMaterialFileById(fileId);
        checkSource(formatStr, materialFileDO.getSource());
        checkOperator(materialFileDO);
    }

}
