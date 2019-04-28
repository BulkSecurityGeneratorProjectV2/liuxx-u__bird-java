package com.bird.service.cms.impl;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bird.core.Check;
import com.bird.service.cms.CmsAttributeService;
import com.bird.service.cms.dto.CmsAttributeDTO;
import com.bird.service.cms.mapper.CmsAttributeMapper;
import com.bird.service.cms.model.CmsAttribute;
import com.bird.service.common.service.AbstractService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "cms_attribute")
@org.apache.dubbo.config.annotation.Service
public class CmsAttributeServiceImpl extends AbstractService<CmsAttributeMapper,CmsAttribute> implements CmsAttributeService {

    /**
     * 根据分类id获取属性集合
     *
     * @param classifyId 分类id
     * @return
     */
    @Override
    public List<CmsAttributeDTO> getClassifyAttribute(Long classifyId) {
        Check.GreaterThan(classifyId,0L,"classifyId");

        EntityWrapper<CmsAttribute> wrapper = new EntityWrapper<>();
        wrapper.where("delFlag=0 and classifyId="+classifyId);
        List<CmsAttribute> attributes = selectList(wrapper);

        return dozer.mapList(attributes, CmsAttributeDTO.class);
    }
}
