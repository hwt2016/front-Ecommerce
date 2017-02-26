package com.service;

import com.convert.ShopConverter;
import com.entity.CommodityDO;
import com.entity.ShopDO;
import com.entity.ShopDOExample;
import com.entity.UserDO;
import com.mapper.ShopDOMapper;
import com.mapper.UserDOMapper;
import com.util.Pager;
import com.vo.ShopVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangchao08 on 17/2/3.
 */
@Service
public class ShopService {
    
    @Autowired
    private ShopDOMapper shopDOMapper;

    @Autowired
    private UserDOMapper userDOMapper;

    public List<ShopVO> searchShopsByPage(Pager pager) {
        ShopDOExample shopDOExample = new ShopDOExample();
        pager.setCount(shopDOMapper.countByExample(shopDOExample));
        shopDOExample.setLimitStart(pager.getBegin());
        shopDOExample.setLimitEnd(pager.getLength());
        shopDOExample.setOrderByClause("createtime DESC");
        List<ShopDO> shopDOS = shopDOMapper.selectByExample(shopDOExample);
        List<ShopVO> shopVOS = new ArrayList<>();
        if (shopDOS != null) {
            for (ShopDO shopDO : shopDOS) {
                UserDO userDO = userDOMapper.selectByPrimaryKey(shopDO.getUserId());
                ShopVO shopVO = ShopConverter.convert(shopDO, userDO);
                shopVOS.add(shopVO);
            }
        }
        return shopVOS;
    }

    //通过用户的信息获取用户的店铺信息
    public ShopDO selectShopByUser(UserDO userDO){
        ShopDOExample shopDOExample= new ShopDOExample();
        ShopDOExample.Criteria criteria=shopDOExample.createCriteria();
        criteria.andUserIdEqualTo(userDO.getId());//根据用户的id找到店铺
        List<ShopDO> shopDOS=shopDOMapper.selectByExample(shopDOExample);
        if(shopDOS.size()==0)
            return  null;
        return shopDOS.get(0);//由于店铺唯一，所以返回一个即可
    }

    //通过商品的信息获取店铺信息
    public ShopDO selectShopByCommodity(CommodityDO commodityDO){
        ShopDOExample shopDOExample= new ShopDOExample();
        ShopDOExample.Criteria criteria=shopDOExample.createCriteria();
        criteria.andIdEqualTo(commodityDO.getShopId());//根据商品所属的shopid找到店铺
        List<ShopDO> shopDOS=shopDOMapper.selectByExample(shopDOExample);
        if(shopDOS.size()==0)
            return  null;
        return shopDOS.get(0);//由于店铺唯一，所以返回一个即可
    }
}
