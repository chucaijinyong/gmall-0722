package com.atguigu.gmall.pms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.BrandDao;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private BrandDao brandDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
//        IPage<BrandEntity> page = this.page(
//                new Query<BrandEntity>().getPage(params),
//                new QueryWrapper<BrandEntity>()
//        );

        IPage<BrandEntity> page = brandDao.selectPage(new Query<BrandEntity>().getPage(params),new QueryWrapper<BrandEntity>());
        return new PageVo(page);
    }

}
