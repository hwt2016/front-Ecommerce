package com.controller;

import com.constant.PageSizeConstant;
import com.em.OperateEnum;
import com.em.ShopStatusEnum;
import com.entity.ShopDO;
import com.entity.UserDO;
import com.mapper.ShopDOMapper;
import com.service.CommodityService;
import com.service.ShopService;
import com.service.UserService;
import com.util.Pager;
import com.vo.CommodityVO;
import com.vo.ShopVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * Created by jiangchao08 on 16/12/5.
 */
@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopDOMapper shopDOMapper;
    
    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommodityService commodityService;

    @RequestMapping("/{id}")
    public String view(@PathVariable("id") Long id, ModelMap modelMap) {
        ShopDO shop = shopDOMapper.selectByPrimaryKey(id);
        modelMap.addAttribute("shop", shop);
        return "shop/shopInfo";
    }

    @RequestMapping("/shopList/{page}")
    public String viewList(@PathVariable("page") Integer page, ModelMap modelMap) {
        Pager pager = new Pager(page, PageSizeConstant.pageSize);
        List<ShopVO> shops = shopService.searchShopsByPage(pager);
        modelMap.addAttribute("shops", shops);
        modelMap.addAttribute("pager", pager);
        return "shop/shopList";
    }

    @RequestMapping("/edit/{id}")
    public String editShop(@PathVariable("id") Long id, ModelMap modelMap) {
        ShopDO shop = shopDOMapper.selectByPrimaryKey(id);
        List<UserDO> users = userService.searchAllUsers();
        modelMap.addAttribute("shop", shop);
        modelMap.addAttribute("users", users);
        modelMap.addAttribute("operateEn", "edit/" + id);
        modelMap.addAttribute("operateCh", OperateEnum.UPDATE.code());
        return "shop/add";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String editShop(@Valid ShopDO shopDO, BindingResult bindingResult, ModelMap modelMap) {
        if(bindingResult.hasErrors()){
            modelMap.addAttribute("bindingResult",bindingResult);
            return "shop/shop_add";
        }
        shopDO.setUpdatetime(new Date(System.currentTimeMillis()));
        shopDOMapper.updateByPrimaryKeySelective(shopDO);
        return "redirect:/shop/shopList/1.vm";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addShop(ModelMap modelMap, HttpSession session) {
        List<UserDO> users = userService.searchAllUsers();
        modelMap.addAttribute("shop", new ShopDO());
        modelMap.addAttribute("operateEn", "add");//添加变量add
        modelMap.addAttribute("operateCh", OperateEnum.ADD.code());
        String nickname=session.getAttribute("nickname").toString();//获取nickname
        UserDO userDO=userService.selectUserByNickName(nickname);//获取该用户的信息
        ShopDO shopDO=shopService.selectShopByUser(userDO);
        if(shopDO!=null){
            return "redirect:../shop/shop_commodityList/1";//！！！！不清楚为什么这里需要加..才能找到该页面
        }
        modelMap.addAttribute("qq",userDO.getQq());//将用户的qq信息加入缓存
        modelMap.addAttribute("phone",userDO.getPhone());//将用户的phone信息加入缓存
        modelMap.addAttribute("userid",userDO.getId());//将用户的id加入，方便存入商店表中的userid
        return "shop/shop_add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addShop(@Valid ShopDO shopDO, BindingResult bindingResult, ModelMap modelMap,UserDO userDO) {
        if(bindingResult.hasErrors()){
            modelMap.addAttribute("bindingResult",bindingResult);
            return "shop/shop_add";
        }
      //  System.out.println("nickname="+userDO.getNickname()+"   qq="+userDO.getQq() +"  phone="+userDO.getPhone());
        userService.updateUserByNickName(userDO);//更新用户的信息（更新qq和phone的信息）
        UserDO userDO1 = userService.selectUserByNickName(userDO.getNickname());//获取该用户的信息
        shopDO.setUserId(userDO1.getId());//注入用户ID
        shopDO.setCreatetime(new Date(System.currentTimeMillis()));//注入创建时间
        shopDO.setUpdatetime(new Date(System.currentTimeMillis()));//注入更新时间
        shopDO.setStatus(ShopStatusEnum.NORMAL.code());//注入状态
        shopDOMapper.insert(shopDO);
        return "redirect:shop/shop_commodityList/1";
    }

    @RequestMapping(value = "/shop_commodityList/{page}")
    public  String selectUserShopCommodityList(@PathVariable("page") Integer page, ModelMap modelMap,HttpSession session){
        String nickname = session.getAttribute("nickname").toString();
        Pager pager = new Pager(page, PageSizeConstant.pageSize);
        List<CommodityVO> commoditys = commodityService.searchCommoditysByPageAndNickName(pager,nickname);
        modelMap.addAttribute("commoditys", commoditys);
        modelMap.addAttribute("pager", pager);
        return "shop/shop_commodityList";
    }
}

