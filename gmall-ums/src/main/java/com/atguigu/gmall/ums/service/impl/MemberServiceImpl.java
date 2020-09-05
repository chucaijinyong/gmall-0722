package com.atguigu.gmall.ums.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.exception.MemberException;
import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;


@Service("memberService")
@RabbitListener(queues = "stockback-queue")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public PageVo queryPage(QueryCondition params) {

        rabbitTemplate.convertAndSend("exchange","order-pay","我是消息");

        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @RabbitHandler
    public void handlerMessage(Message message,String s){
        System.out.println("new Date() = " + new Date());
        System.out.println(message);
        System.out.println("s = " + s);
    }

    @Override
    public Boolean checkData(String data, Integer type) {

        QueryWrapper<MemberEntity> queryWrapper = new QueryWrapper<>();
        switch (type) {
            case 1: queryWrapper.eq("username", data); break;
            case 2: queryWrapper.eq("mobile", data); break;
            case 3: queryWrapper.eq("email", data); break;
            default:
                return false;
        }
        return this.count(queryWrapper) == 0;
    }

    @Override
    public void register(MemberEntity memberEntity, String code) {
        // 校验手机验证码：TODO

        // 生成盐
        String salt = UUID.randomUUID().toString().substring(0, 6);
        memberEntity.setSalt(salt);

        // 加盐加密
        memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword() + salt));

        // 新增用户
        memberEntity.setGrowth(0);
        memberEntity.setIntegration(0);
        memberEntity.setLevelId(0L);
//        memberEntity.setCreateTime(new Date());
        memberEntity.setStatus(1);
        this.save(memberEntity);

        // 删除redis中的验证码: TODO
    }

    @Override
    public MemberEntity queryUser(String username, String password) {

        // 根据用户名查询用户
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username));

        // 判断用户是否存在
        if (memberEntity == null) {
//            return null;
            throw new MemberException("用户名不存在！");
        }

        // 先对用户输入的密码加盐加密
        password = DigestUtils.md5Hex(password + memberEntity.getSalt());

        // 比较数据库中的密码和用户输入的密码是否一致
        if (!StringUtils.equals(password, memberEntity.getPassword())) {
//            return memberEntity;
            throw new MemberException("密码错误！");
        }

        return memberEntity;
    }

}
