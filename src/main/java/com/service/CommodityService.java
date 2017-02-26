package com.service;

import com.convert.CommodityConverter;
import com.entity.*;
import com.mapper.CategoryDOMapper;
import com.mapper.CommodityDOMapper;
import com.mapper.ShopDOMapper;
import com.util.Pager;
import com.vo.CommodityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangchao08 on 17/2/3.
 */
@Service
public class CommodityService {

    @Autowired
    private CommodityDOMapper commodityDOMapper;

    @Autowired
    private ShopDOMapper shopDOMapper;

    @Autowired
    private CategoryDOMapper categoryDOMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ShopService shopService;

    public List<CommodityVO> searchCommoditysByPage(Pager pager) {
        CommodityDOExample commodityDOExample = new CommodityDOExample();
        pager.setCount(commodityDOMapper.countByExample(commodityDOExample));
        commodityDOExample.setLimitStart(pager.getBegin());
        commodityDOExample.setLimitEnd(pager.getLength());
        commodityDOExample.setOrderByClause("updatetime DESC");
        List<CommodityDO> commodityDOs = commodityDOMapper.selectByExample(commodityDOExample);
        List<CommodityVO> commodityVOs = new ArrayList<>();
        if (commodityDOs != null) {
            for (CommodityDO commodityDO : commodityDOs) {
                ShopDO shopDO = shopDOMapper.selectByPrimaryKey(commodityDO.getShopId());//通过shopid获取店铺信息
                UserDO userDO = userService.selectUserByShop(shopDO);//通过店铺信息获取用户信息
                CategoryDO categoryDO = categoryDOMapper.selectByPrimaryKey(commodityDO.getCategoryId());
                CommodityVO commodityVO = CommodityConverter.convert(commodityDO, shopDO, categoryDO,userDO);//将数据进行集成
                commodityVOs.add(commodityVO);
            }
        }
        return commodityVOs;
    }

    //根据用户名选取用户店铺内的所有商品信息
    public List<CommodityVO> searchCommoditysByPageAndNickName(Pager pager ,String nickName){
        UserDO userDO = userService.selectUserByNickName(nickName);//根据用户nickName获取用户信息
        ShopDO shopDO =shopService.selectShopByUser(userDO);//根据用户信息获取用户的shopID
        CommodityDOExample commodityDOExample = new CommodityDOExample();
        CommodityDOExample.Criteria criteria = commodityDOExample.createCriteria();
        criteria.andShopIdEqualTo(shopDO.getId());
        pager.setCount(commodityDOMapper.countByExample(commodityDOExample));//统计店铺商品总数
        commodityDOExample.setLimitStart(pager.getBegin());
        commodityDOExample.setLimitEnd(pager.getLength());
        commodityDOExample.setOrderByClause("updatetime DESC");//按照时间最近上架商品排序
        List<CommodityDO> commodityDOs = commodityDOMapper.selectByExample(commodityDOExample);
        List<CommodityVO> commodityVOs = new ArrayList<>();
        if (commodityDOs != null) {
            for (CommodityDO commodityDO : commodityDOs) {
                CategoryDO categoryDO = categoryDOMapper.selectByPrimaryKey(commodityDO.getCategoryId());
                CommodityVO commodityVO = CommodityConverter.convert(commodityDO, shopDO, categoryDO,userDO);//将数据进行集成
                commodityVOs.add(commodityVO);
            }
        }
        return commodityVOs;
    }
}
