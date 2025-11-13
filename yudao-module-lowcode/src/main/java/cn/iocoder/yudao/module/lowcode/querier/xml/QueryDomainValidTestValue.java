package cn.iocoder.yudao.module.lowcode.querier.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author leo
 */
@Getter
@Setter
@XmlRootElement(name = "QueryDomainValidTestValue")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryDomainValidTestValue {

    @XmlAttribute(name = "class")
    private String clazz;

    @XmlValue
    private String value;

}
