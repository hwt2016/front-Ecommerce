package com.service;

import com.em.UserStatusEnum;
import com.entity.ShopDO;
import com.entity.UserDO;
import com.entity.UserDOExample;
import com.mapper.UserDOMapper;
import com.util.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jiangchao08 on 17/2/3.
 */
@Service
public class UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    public List<UserDO> searchUsersByPage(Pager pager) {
        UserDOExample userDOExample = new UserDOExample();
        pager.setCount(userDOMapper.countByExample(userDOExample));
        userDOExample.setLimitStart(pager.getBegin());
        userDOExample.setLimitEnd(pager.getLength());
        userDOExample.setOrderByClause("createtime DESC");
        List<UserDO> users = userDOMapper.selectByExample(userDOExample);
        return users;
    }

    public List<UserDO> searchAllUsers() {
        UserDOExample userDOExample = new UserDOExample();
        userDOExample.setOrderByClause("nickname ASC");
        UserDOExample.Criteria criteria = userDOExample.createCriteria();
        criteria.andStatusEqualTo(UserStatusEnum.NORMAL.code());
        List<UserDO> users = userDOMapper.selectByExample(userDOExample);
        return users;
    }

    public UserDO selectUserByUserName(String userName){
        UserDOExample userDOExample= new UserDOExample();
        UserDOExample.Criteria criteria=userDOExample.createCriteria();
        criteria.andUsernameEqualTo(userName);
        List<UserDO> users=userDOMapper.selectByExample(userDOExample);
        if(users.size()==0)
            return  null;
        return users.get(0);
    }

    public UserDO selectUserByNickName(String nickname){
        UserDOExample userDOExample= new UserDOExample();
        UserDOExample.Criteria criteria=userDOExample.createCriteria();
        criteria.andNicknameEqualTo(nickname);
        List<UserDO> users=userDOMapper.selectByExample(userDOExample);
        if(users.size()==0)
            return  null;
        return users.get(0);
    }


    public void insertUser(UserDO userDO ){
        userDOMapper.insert(userDO);
    }

    public void updateUserByNickName(UserDO userDO){
        UserDOExample userDOExample= new UserDOExample();
        UserDOExample.Criteria criteria=userDOExample.createCriteria();
        criteria.andNicknameEqualTo(userDO.getNickname());
        userDOMapper.updateByExampleSelective(userDO,userDOExample);
    }

    //通过店铺的信息获取用户信息
    public UserDO selectUserByShop(ShopDO shopDO){
        UserDOExample userDOExample =new UserDOExample();
        UserDOExample.Criteria criteria = userDOExample.createCriteria();
        criteria.andIdEqualTo(shopDO.getUserId());//设置条件：userID
        List<UserDO> userDOS=userDOMapper.selectByExample(userDOExample);
        if(userDOS.size()==0)
            return null;
        return userDOS.get(0);
    }
}
