package cn.iocoder.yudao.module.lowcode.dal.mysql.materialfile;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile.MaterialFileDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 低代码-物料文件 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MaterialFileMapper extends BaseMapperX<MaterialFileDO> {

    void transferMaterialFile(@Param("ids") List<Long> ids, @Param("oldCreator") String oldCreator, @Param("newCreator") String newCreator);

}