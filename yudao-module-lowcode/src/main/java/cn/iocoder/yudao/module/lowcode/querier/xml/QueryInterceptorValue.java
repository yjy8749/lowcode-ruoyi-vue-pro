package cn.iocoder.yudao.module.lowcode.querier.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author leo
 */
@Getter
@Setter
@XmlRootElement(name = "QueryInterceptorValue")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryInterceptorValue {

    @XmlAttribute(name = "class")
    private String clazz;

    @XmlElement(name = "QueryInterceptorPreHandleValue")
    private QueryInterceptorPreHandleValue preHandleValue;

    @XmlElement(name = "QueryInterceptorPostHandleValue")
    private QueryInterceptorPostHandleValue postHandleValue;

}

