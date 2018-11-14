package com.bird.service.cms.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.bird.service.common.mapper.permission.DataRule;
import com.bird.service.common.mapper.permission.NullDataRuleProvider;
import com.bird.service.common.model.AbstractModel;

@TableName("cms_classify")
public class CmsClassify extends AbstractModel {
    @DataRule(name = "数据规则1")
    private String name;
    @DataRule(name = "数据规则2",provider = NullDataRuleProvider.class)
    private Long parentId;
    private String parentIds;
    private Integer orderNo;
    private String logo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
