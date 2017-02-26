package com.controller;

import com.constant.PageSizeConstant;
import com.em.OperateEnum;
import com.em.UserStatusEnum;
import com.entity.UserDO;
import com.mapper.UserDOMapper;
import com.service.UserService;
import com.util.Pager;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserService userService;

    @RequestMapping("/{id}")
    public String view(@PathVariable("id") Long id, ModelMap modelMap) {
        UserDO user = userDOMapper.selectByPrimaryKey(id);
        modelMap.addAttribute("user", user);
        return "user/userInfo";
    }

    @RequestMapping("/userList/{page}")
    public String viewList(@PathVariable("page") Integer page, ModelMap modelMap) {
        Pager pager = new Pager(page, PageSizeConstant.pageSize);
        List<UserDO> users = userService.searchUsersByPage(pager);
        modelMap.addAttribute("users", users);
        modelMap.addAttribute("pager", pager);
        return "user/userList";
    }

    @RequestMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, ModelMap modelMap) {
        UserDO user = userDOMapper.selectByPrimaryKey(id);
        modelMap.addAttribute("user", user);
        modelMap.addAttribute("operateEn", "edit/" + id);
        modelMap.addAttribute("operateCh", OperateEnum.UPDATE.code());
        return "user/add";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String editUser(@Valid UserDO userDO, BindingResult bindingResult, ModelMap modelMap) {
        if(bindingResult.hasErrors()){
            modelMap.addAttribute("bindingResult",bindingResult);
            return "user/add";
        }
        userDO.setUpdatetime(new Date(System.currentTimeMillis()));
        userDOMapper.updateByPrimaryKeySelective(userDO);
        return "redirect:user/userList/1.vm";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addUser(ModelMap modelMap) {
        modelMap.addAttribute("user", new UserDO());
        modelMap.addAttribute("operateEn", "add");
        modelMap.addAttribute("operateCh", OperateEnum.ADD.code());
        return "user/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addUser(@Valid UserDO userDO, BindingResult bindingResult, ModelMap modelMap) {
        if(bindingResult.hasErrors()){
            modelMap.addAttribute("bindingResult",bindingResult);
            return "user/add";
        }
        userDO.setCreatetime(new Date(System.currentTimeMillis()));
        userDO.setStatus(UserStatusEnum.NORMAL.code());
        userDOMapper.insert(userDO);
        return "redirect:user/userList/1.vm";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String userLogin(ModelMap modelMap){
        return "front/login";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String userLogin(UserDO userDO, ModelMap modelMap, HttpSession session){
        UserDO user= userService.selectUserByNickName(userDO.getNickname());
        if(user !=null && userDO.getPassword().equals(user.getPassword())){
            session.setAttribute("nickname",userDO.getNickname());
            return "redirect:/front/index";
        }else{
            System.out.print("用户名和密码输入不正确");
            modelMap.addAttribute("msg","用户名和密码输入不正确");
            return "user/login";
        }

    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String userRegister( UserDO userDO,ModelMap modelMap,HttpSession session){


        UserDO ado=userService.selectUserByNickName(userDO.getNickname());//获取管理员各属性
        if(ado==null&&userDO.getNickname()!=null)
        {
            userDO.setCreatetime(new Date(System.currentTimeMillis()));
            userService.insertUser(userDO);//如果尚未注册则注册
            modelMap.addAttribute("msg","成功注册");
            session.setAttribute("nickname",userDO.getNickname());
            return "redirect:/front/index";
        }
        else
        {
            modelMap.addAttribute("msg","用户名已经存在");
            return "user/login";//如果已注册则需重新注册(该步骤不清楚是否需要重新写页面，留待研究）
        }
    }

}
