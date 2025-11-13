package cn.iocoder.yudao.module.lowcode.querier.xml;

import com.google.common.collect.Lists;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author leo
 */
@Getter
@Setter
@XmlRootElement(name = "QueryFilter")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryFilter {

    @XmlElement(name = "QueryFilterValue")
    private List<QueryFilterValue> queryFilterValueList = Lists.newArrayList();

}

