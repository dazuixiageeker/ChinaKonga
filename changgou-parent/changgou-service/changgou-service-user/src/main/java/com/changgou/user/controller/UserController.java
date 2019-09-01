package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.*;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/****
 * @Author:Martin MYZ
 * @Description:
 *
 *****/

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;


    @Autowired
    private TokenParser tokenParser;

    /***
     * 增加用户积分
     * @param points:要添加的积分
     */
    @GetMapping(value = "/points/add")
    public Result addPoints(Integer points){
        //获取用户名
        Map<String, String> userMap = tokenParser.getUserInfo();
        String username = userMap.get("username");

        //添加积分
        userService.addUserPoints(username,points);
        return new Result(true,StatusCode.OK,"添加积分成功！");
    }


    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
   /* @RequestMapping("/login")
    public Result login(String username, String password){
        User user = userService.fingByUsername(username);
        if( user != null && BCrypt.checkpw(password,user.getPassword())){
            //设置令牌信息
            Map<String, Object> info = new HashMap<>();
            info.put("role","USER");
            info.put("success","SUCCESS");
            info.put("username",username);
            //生成令牌
            String jwt = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(info), null);
            return new Result(true, StatusCode.OK,"登录成功",jwt);
        }


        return new Result( false, StatusCode.LOGINERROR,"用户名或密码错误!");
    }*/

    /**
     * 对上面方法的优化,将令牌存入cookie中,
     * @param username
     * @param password
     * @param response
     * @return
     */
    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response){
        User user = userService.fingByUsername(username);
        if( user != null && BCrypt.checkpw(password,user.getPassword())){
            //登录成功,颁发令牌,设置令牌信息
            Map<String, Object> info = new HashMap<>();
            info.put("role","USER");
            info.put("success","SUCCESS");
            info.put("username",username);
            //生成令牌
            String token = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(info), null);
            Cookie cookie = new Cookie("Authorization",token);
            cookie.setDomain("localhost");
            cookie.setPath("/");
            response.addCookie(cookie);
            return new Result(true, StatusCode.OK,"登录成功",token);
        }


        return new Result( false, StatusCode.LOGINERROR,"用户名或密码错误!");
    }

    /***
     * User分页条件搜索实现
     * @param user
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false)  User user, @PathVariable  int page, @PathVariable  int size){
        //调用UserService实现分页条件查询User
        PageInfo<User> pageInfo = userService.findPage(user, page, size);
        return new Result(true, StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * User分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用UserService实现分页查询User
        PageInfo<User> pageInfo = userService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param user
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<User>> findList(@RequestBody(required = false)  User user){
        //调用UserService实现条件查询User
        List<User> list = userService.findList(user);
        return new Result<List<User>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    //只有admin角色才能访问此方法,其他角色无权访问
    @PreAuthorize("hasAnyAuthority('admin')")
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        //调用UserService实现根据主键删除
        userService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改User数据
     * @param user
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody  User user,@PathVariable String id){
        //设置主键值
        user.setUsername(id);
        //调用UserService实现修改User
        userService.update(user);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增User数据
     * @param user
     * @return
     */
    @PostMapping
    public Result add(@RequestBody   User user){
        //调用UserService实现添加User
        userService.add(user);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    //只有管理员才有权限访问
   // @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping({"/{id}","/load/{id}"})
    public Result<User> findById(@PathVariable String id){
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
        return new Result<User>(true,StatusCode.OK,"查询成功",user);
    }

    /***
     * 查询User全部数据
     * @return
     */
    @GetMapping
    public Result<List<User>> findAll(){
        //调用UserService实现查询所有User
        List<User> list = userService.findAll();
        return new Result<List<User>>(true, StatusCode.OK,"查询成功",list) ;
    }
}
