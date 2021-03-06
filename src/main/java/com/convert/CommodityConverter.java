package com.convert;

import com.entity.CategoryDO;
import com.entity.CommodityDO;
import com.entity.ShopDO;
import com.entity.UserDO;
import com.vo.CommodityVO;

/**
 * Created by jiangchao08 on 17/2/5.
 */
public class CommodityConverter {

    public static CommodityVO convert(CommodityDO commodityDO, ShopDO shopDO, CategoryDO categoryDO , UserDO userDO) {
        String shopname = null;
        String categoryname = null;
        String qq=null;
        Long phone=null;
        if (shopDO != null) {
            shopname = shopDO.getShopname();
        }
        if (categoryDO != null) {
            categoryname = categoryDO.getCategoryName();
        }
        if (userDO !=null) {
            qq=userDO.getQq();
            phone=userDO.getPhone();
        }
        CommodityVO commodityVO = new CommodityVO();
        commodityVO.setId(commodityDO.getId());
        commodityVO.setShopId(commodityDO.getShopId());
        commodityVO.setShopName(shopname);
        commodityVO.setCommodityImage(commodityDO.getCommodityImage());
        commodityVO.setCommodityName(commodityDO.getCommodityName());
        commodityVO.setDescription(commodityDO.getDescription());
        commodityVO.setPrice(commodityDO.getPrice());
        commodityVO.setCount(commodityDO.getCount());
        commodityVO.setCategoryId(commodityDO.getCategoryId());
        commodityVO.setCategoryName(categoryname);
        commodityVO.setCreatetime(commodityDO.getCreatetime());
        commodityVO.setUpdatetime(commodityDO.getUpdatetime());
        commodityVO.setQq(qq);
        commodityVO.setPhone(phone);
        return commodityVO;
    }

}
