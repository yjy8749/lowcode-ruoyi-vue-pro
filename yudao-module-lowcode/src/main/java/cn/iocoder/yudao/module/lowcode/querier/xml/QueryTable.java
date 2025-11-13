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
@XmlRootElement(name = "QueryTable")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryTable {

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "table")
    private String table;

    @XmlAttribute(name = "desc")
    private String desc;

    @XmlAttribute(name = "joinOn")
    private String joinOn;

    @XmlAttribute(name = "leftJoinOn")
    private String leftJoinOn;

    @XmlAttribute(name = "disableTenant")
    private Boolean disableTenant;

    @XmlAttribute(name = "disableLogicDelete")
    private Boolean disableLogicDelete;

    @XmlElement(name = "QuerySql")
    private QuerySql querySql;

    @XmlElement(name = "ValidTestValue")
    private QueryDomainValidTestValue validTestValue;

    @XmlElement(name = "QueryField")
    private List<QueryField> queryFieldList = Lists.newArrayList();

}
