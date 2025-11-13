package cn.iocoder.yudao.module.lowcode.querier.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.Setter;

/**
 * @author leo
 */
@Getter
@Setter
@XmlRootElement(name = "QueryInterceptorPreHandleValue")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryInterceptorPreHandleValue {

    @XmlValue
    private String value;

}
