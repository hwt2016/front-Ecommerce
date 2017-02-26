package com.controller;

import com.OSS.ListObjects;
import com.constant.PageSizeConstant;
import com.em.OperateEnum;
import com.entity.CommodityDO;
import com.entity.ShopDO;
import com.entity.UserDO;
import com.mapper.CommodityDOMapper;
import com.service.CommodityService;
import com.service.ShopService;
import com.service.UserService;
import com.util.Pager;
import com.vo.CommodityVO;
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
@RequestMapping("/commodity")
public class CommodityController {

    @Autowired
    private CommodityDOMapper commodityDOMapper;

    @Autowired
    private CommodityService commodityService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShopService shopService;

    //根据id产看商品信息
    @RequestMapping("/{id}")
    public String view(@PathVariable("id") Long id, ModelMap modelMap) {
        CommodityDO commodity = commodityDOMapper.selectByPrimaryKey(id);
        modelMap.addAttribute("commodity", commodity);
        return "commodity/commodityInfo";
    }

    //根据页面获取商品类表
    @RequestMapping("/commodityList/{page}")
    public String viewList(@PathVariable("page") Integer page, ModelMap modelMap) {
        Pager pager = new Pager(page, PageSizeConstant.pageSize);
        List<CommodityVO> commoditys = commodityService.searchCommoditysByPage(pager);
        modelMap.addAttribute("commoditys", commoditys);
        modelMap.addAttribute("pager", pager);
        return "commodity/commodityList";
    }

    //根据id编辑货品
    @RequestMapping("/edit/{id}")
    public String editCommodity(@PathVariable("id") Long id, ModelMap modelMap) {
        CommodityDO commodity = commodityDOMapper.selectByPrimaryKey(id);
        modelMap.addAttribute("commodity", commodity);
        modelMap.addAttribute("operateEn", "edit/" + id);
        modelMap.addAttribute("operateCh", OperateEnum.UPDATE.code());
        return "commodity/add";
    }

    //编辑货品
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String editCommodity(@Valid CommodityDO commodityDO, BindingResult bindingResult, ModelMap modelMap) {
        if(bindingResult.hasErrors()){
            modelMap.addAttribute("bindingResult",bindingResult);
            return "commodity/commodity_add";
        }
        commodityDO.setUpdatetime(new Date(System.currentTimeMillis()));
        commodityDOMapper.updateByPrimaryKeySelective(commodityDO);
        return "redirect:commodity/commodityList/1.vm";
    }

    //添加商品
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addCommodity(ModelMap modelMap, HttpSession session) {
        modelMap.addAttribute("commodity", new CommodityDO());
        modelMap.addAttribute("operateEn", "add");
        modelMap.addAttribute("operateCh", OperateEnum.ADD.code());
        UserDO userDO = userService.selectUserByNickName(session.getAttribute("nickname").toString());
        ShopDO shopDO = shopService.selectShopByUser(userDO);
        modelMap.addAttribute("shopId",shopDO.getId());
        return "commodity/commodity_add";
    }


    //添加商品
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addCommodity(@Valid CommodityDO commodityDO, BindingResult bindingResult, ModelMap modelMap) {
        if(bindingResult.hasErrors()){
            System.out.print("出错了");
            modelMap.addAttribute("bindingResult",bindingResult);
            return "commodity/commodity_add";
        }
        commodityDO.setCommodityImage(commodityDO.getCommodityName());
        commodityDO.setCreatetime(new Date(System.currentTimeMillis()));
        commodityDO.setUpdatetime(new Date(System.currentTimeMillis()));
        commodityDOMapper.insert(commodityDO);
        System.out.print("添加成功");
        return "redirect:commodity_detail/"+commodityDO.getId();
    }

    //商品细节
    @RequestMapping(value = "/commodity_detail",method = RequestMethod.GET)
    public String commodity_detail(){
        return "commodity/commodity_detail";
    }

//    //商品图片上传
//    @RequestMapping(value = "/createPolicy/{dir}")
//    @ResponseBody
//    public String createPolicy(@PathVariable("dir") String dir){
//        return PostObjectPolicy.createPolicy(dir+"/").toJSONString();
//    }

    @RequestMapping(value = "/commodity_detail/{commodityId}")
    public String product_detail(@PathVariable ("commodityId") String commodityId,ModelMap modelMap){
        CommodityDO commodityDO = commodityDOMapper.selectByPrimaryKey(Long.parseLong(commodityId));//根据商品id找到商品
        ShopDO shopDO = shopService.selectShopByCommodity(commodityDO);//根据商品信息找到所属店铺
        System.out.print("店主ID="+shopDO.getUserId());
        UserDO userDO = userService.selectUserByShop(shopDO);//根据店铺信息找到店主信息
        modelMap.addAttribute("qq","tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin="+userDO.getQq());//注入店主qq
        modelMap.addAttribute("phone",userDO.getPhone());//注入店主phone
        modelMap.addAttribute("commodity",commodityDO);//注入商品信息
        ListObjects oss= new ListObjects();//连接OSS服务器
        List <String> list=  oss.SelectImagesByUserDir(userDO.getNickname());//获取图片列表
        modelMap.addAttribute("ImageFirst",list.get(0));//获取首张展示的图片
        modelMap.addAttribute("imgSmall","?x-oss-process=image/resize,m_lfit,h_90,w_80");//缩图，高低比例为：90：80
        modelMap.addAttribute("imgBig","?x-oss-process=image/resize,m_lfit,h_550,w_400");
        modelMap.addAttribute("Images",list);//注入图片列表
        modelMap.addAttribute("listSize",list.size());//图片总数
        modelMap.addAttribute("cmoName",commodityDO.getCommodityName());//该商品名称
        return "commodity/commodity_detail";
    }


}
