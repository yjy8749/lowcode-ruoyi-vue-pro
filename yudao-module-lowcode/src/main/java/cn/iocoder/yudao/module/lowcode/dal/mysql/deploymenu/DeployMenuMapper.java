package cn.iocoder.yudao.module.lowcode.dal.mysql.deploymenu;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.DeployMenuPageReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deploymenu.DeployMenuDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 低代码-部署菜单 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DeployMenuMapper extends BaseMapperX<DeployMenuDO> {

    default PageResult<DeployMenuDO> selectPage(DeployMenuPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DeployMenuDO>()
                .eqIfPresent(DeployMenuDO::getSourceFileId, reqVO.getSourceFileId())
                .orderByDesc(DeployMenuDO::getId));
    }

    Long selectRefMenuId(@Param("sourceFileId") Long sourceFileId,
                         @Param("sourceFileVersion") Integer sourceFileVersion,
                         @Param("menuStatus") Integer menuStatus);

}