package cn.iocoder.yudao.module.lowcode.dal.mysql.deployapi;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.DeployApiPageReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deployapi.DeployApiDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 低代码-部署接口 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DeployApiMapper extends BaseMapperX<DeployApiDO> {

    default PageResult<DeployApiDO> selectPage(DeployApiPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DeployApiDO>()
                .eqIfPresent(DeployApiDO::getSourceFileId, reqVO.getSourceFileId())
                .eqIfPresent(DeployApiDO::getApiStatus, reqVO.getApiStatus())
                .orderByDesc(DeployApiDO::getId));
    }

    @Select("select count(id) from lowcode_deploy_api where api_name = #{apiName}")
    Long selectCountByApiName(@Param("apiName") String apiName);

}