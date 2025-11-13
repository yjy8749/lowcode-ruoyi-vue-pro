package cn.iocoder.yudao.module.lowcode.querier.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author leo
 */
@Getter
@Setter
@XmlRootElement(name = "QueryField")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryField {

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "comment")
    private String comment;

    @XmlAttribute(name = "hidden")
    private Boolean hidden;

    @XmlAttribute(name = "required")
    private String required;

    @XmlAttribute(name = "symbols")
    private String symbols;

    @XmlAttribute(name = "sortable")
    private Boolean sortable;

    @XmlAttribute(name = "authType")
    private String authType;

    @XmlAttribute(name = "disableExpt")
    private Boolean disableExpt;

    @XmlValue
    private String value;

}
