package cn.iocoder.yudao.module.lowcode.querier.xml;

import com.google.common.collect.Lists;
import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author leo
 */
@Getter
@Setter
@XmlRootElement(name = "QueryDomain")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryDomain {

    @XmlAttribute(name = "desc")
    private String desc;

    @XmlAttribute(name = "maxReturnRows")
    private Integer maxReturnRows;

    @XmlAttribute(name = "cache")
    private Boolean cache;

    @XmlAttribute(name = "ttl")
    private Integer ttl;

    @XmlAttribute(name = "login")
    private Boolean login;

    @XmlAttribute(name = "permission")
    private String permission;

    @XmlElement(name = "MainTable")
    private List<QueryTable> mainTableList = Lists.newArrayList();

    @XmlElement(name = "QueryTable")
    private List<QueryTable> queryTableList = Lists.newArrayList();

    @XmlElement(name = "QueryField")
    private List<QueryField> queryFieldList = Lists.newArrayList();

    @XmlElement(name = "QueryWhere")
    private QueryWhere queryWhere;

    @XmlElement(name = "QueryInterceptor")
    private QueryInterceptor queryInterceptor;

    @XmlElement(name = "QueryFilter")
    private QueryFilter queryFilter;

}
