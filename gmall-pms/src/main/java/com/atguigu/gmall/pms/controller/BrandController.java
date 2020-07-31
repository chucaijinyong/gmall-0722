package com.atguigu.gmall.pms.controller;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.service.BrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 品牌
 *
 * @author guest
 * @email lxf@atguigu.com
 * @date 2019-12-02 11:23:36
 */
@Api(tags = "品牌 管理")
@RestController
@RequestMapping("pms/brand")
public class BrandController {
    private final static Logger log = LoggerFactory.getLogger(BrandController.class);
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:brand:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = brandService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{brandId}")
//    @PreAuthorize("hasAuthority('pms:brand:info')")
    public Resp<BrandEntity> info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return Resp.ok(brand);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
//    @PreAuthorize("hasAuthority('pms:brand:save')")
    public Resp<Object> save(@Validated @RequestBody BrandEntity brand, BindingResult bindingResult) {
        System.out.println(1);
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        // list集合不为空
        if (CollectionUtils.isNotEmpty(allErrors)){
            String collect = allErrors.stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining(","));
//            collect.
            log.info("collect = " + collect);
            byte[] bytes = collect.getBytes(Charset.defaultCharset());
            collect = new String(bytes);
            return Resp.fail(collect);
        }
        brandService.save(brand);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:brand:update')")
    public Resp<Object> update(@RequestBody BrandEntity brand) {
        brandService.updateById(brand);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:brand:delete')")
    public Resp<Object> delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return Resp.ok(null);
    }

    @PostMapping("/findBrandPagination")
    public Resp<Object> findBrandPagination() {
        PageVo brandPagination = brandService.findBrandPagination();

        return Resp.ok(brandPagination);
    }


}
