package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GroupVO extends AttrGroupEntity {

    // 封装属性集合
    private List<AttrEntity> attrEntities;
    // 封装分组和属性关系表集合
    private List<AttrAttrgroupRelationEntity> relations;
}
