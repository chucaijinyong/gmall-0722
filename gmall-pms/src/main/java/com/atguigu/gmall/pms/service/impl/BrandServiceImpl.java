package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.BrandDao;
import com.atguigu.gmall.pms.dto.BrandDTO;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private BrandDao brandDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<BrandEntity> page1 = new Query<BrandEntity>().getPage(params);
        IPage<BrandEntity> page = brandDao.selectPage(page1,new QueryWrapper<BrandEntity>());
        return new PageVo(page);
    }

    @Override
    public PageVo findBrandPagination() {
        IPage<BrandEntity> page = new Page<BrandEntity>(1, 1);
        IPage<BrandDTO> brandPagination = brandDao.findBrandPagination(page);

        return new PageVo(brandPagination);
    }

}
