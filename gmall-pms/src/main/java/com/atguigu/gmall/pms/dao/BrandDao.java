package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.dto.BrandDTO;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌
 *
 * @author guest
 * @email lxf@atguigu.com
 * @date 2019-12-02 11:23:36
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {

    IPage<BrandDTO> findBrandPagination(IPage<BrandEntity> page);
}
