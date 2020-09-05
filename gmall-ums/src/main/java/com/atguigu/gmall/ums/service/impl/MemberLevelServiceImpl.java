package com.atguigu.gmall.ums.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.ums.dao.MemberLevelDao;
import com.atguigu.gmall.ums.entity.MemberLevelEntity;
import com.atguigu.gmall.ums.service.MemberLevelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

//@RabbitListener(queues = "order-queue")
@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

//    @RabbitHandler
//    public void handlerMessage(Message message, String s){
//        System.out.println("new Date() = " + new Date());
//        System.out.println(message);
//        System.out.println("s = " + s);
//    }

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberLevelEntity> page = this.page(
                new Query<MemberLevelEntity>().getPage(params),
                new QueryWrapper<MemberLevelEntity>()
        );

        return new PageVo(page);
    }

}
