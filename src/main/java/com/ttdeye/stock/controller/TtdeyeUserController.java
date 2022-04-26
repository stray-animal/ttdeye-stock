package com.ttdeye.stock.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttdeye.stock.common.base.controller.BaseController;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.common.domain.GlobalBusinessConstant;
import com.ttdeye.stock.common.utils.JwtUtils;
import com.ttdeye.stock.common.utils.PasswordUtil;
import com.ttdeye.stock.common.utils.RedisTemplateUtil;
import com.ttdeye.stock.domain.dto.UserLoginDto;
import com.ttdeye.stock.entity.TtdeyeUser;
import com.ttdeye.stock.mapper.TtdeyeUserMapper;
import com.ttdeye.stock.service.ITtdeyeUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Slf4j
@RestController
@RequestMapping("/ttdeye-user")
public class TtdeyeUserController extends BaseController {

    @Autowired
    private TtdeyeUserMapper ttdeyeUserMapper;
    @Autowired
    private ITtdeyeUserService ttdeyeUserService;

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    public static final String SALT = "zjJBgWlC";

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ApiResponseT<TtdeyeUser> login(@RequestBody UserLoginDto userLoginDto){

        String username = userLoginDto.getLoginAccount();
        String password = userLoginDto.getLoginPassword();

        //1. 校验用户是否有效
        TtdeyeUser sysUser = ttdeyeUserService.getOne(new QueryWrapper<TtdeyeUser>().lambda().eq(TtdeyeUser::getLoginAccount, username).eq(TtdeyeUser::getDeleteFlag,0).eq(TtdeyeUser::getState,1));

        if(sysUser == null ) {
           return ApiResponseT.failed();
        }
        //前端密码名文
        String userpassword = PasswordUtil.encrypt(username, password, SALT);
        //2. 校验用户名或密码是否正确

        String syspassword = sysUser.getLoginPassword();
        if (!syspassword.equals(userpassword)) {
            return ApiResponseT.failed("密码错误");
        }

        //用户登录信息
        String token = JwtUtils.createJwt(sysUser);
        //设置用户token和缓存
        log.info("登陆用户token为：{}", token);
        sysUser.setToken(token);
        redisTemplateUtil.set(token, sysUser, GlobalBusinessConstant.EXPIRE_TIMES.WEEKS_ONE);
        return ApiResponseT.ok(sysUser);
    }

    /**
     * 新增用户
     * @param ttdeyeUser
     * @return
     */
    @PostMapping(value = "add")
    public ApiResponseT saveUser(@RequestBody TtdeyeUser ttdeyeUser){
        TtdeyeUser ttdeyeUserAdmin = getTtdeyeUser();

        if(ttdeyeUserAdmin == null && ttdeyeUserAdmin.getAdminFlag() != 1){
            return ApiResponseT.failed("非管理员禁止操作！");
        }
        String userpassword = PasswordUtil.encrypt(ttdeyeUser.getLoginAccount(), ttdeyeUser.getLoginPassword(), SALT);
        ttdeyeUser.setLoginPassword(userpassword);
        ttdeyeUser.setUpdateTime(new Date());
        ttdeyeUser.setUpdateUserAccount(ttdeyeUserAdmin.getUpdateUserAccount());
        ttdeyeUserService.save(ttdeyeUser);
        return ApiResponseT.ok();

    }


    /**
     * 编辑用户
     * @param ttdeyeUser
     * @return
     */
    @PostMapping(value = "edit")
    public ApiResponseT editUser(@RequestBody TtdeyeUser ttdeyeUser){
        TtdeyeUser ttdeyeUserAdmin = getTtdeyeUser();

        if(ttdeyeUserAdmin == null && ttdeyeUserAdmin.getAdminFlag() != 1){
            return ApiResponseT.failed("非管理员禁止操作！");
        }
        String userpassword = PasswordUtil.encrypt(ttdeyeUser.getLoginAccount(), ttdeyeUser.getLoginPassword(), SALT);
        ttdeyeUser.setLoginPassword(userpassword);
        ttdeyeUser.setUpdateTime(new Date());
        ttdeyeUser.setUpdateUserAccount(ttdeyeUserAdmin.getUpdateUserAccount());
        ttdeyeUserService.updateById(ttdeyeUser);
        return ApiResponseT.ok();
    }

    /**
     * 分页查询用户列表
     * @param ttdeyeUser
     * @param current 当前页数
     * @Param size 每页条数
     * @return
     */
    @PostMapping(value = "getList")
    public ApiResponseT<Page<TtdeyeUser>> getList(@RequestBody TtdeyeUser ttdeyeUser){
        Page page = getPage();
        Page<TtdeyeUser> result = ttdeyeUserService.selectUserPage(page, ttdeyeUser);
        return ApiResponseT.ok(result);
    }


    @GetMapping(value = "/getById")
    public ApiResponseT<TtdeyeUser> getTtdeyeUser(Long userId){

        TtdeyeUser ttdeyeUser = ttdeyeUserService.getOne(new QueryWrapper<TtdeyeUser>().lambda().eq(TtdeyeUser::getUserId, userId));
        return ApiResponseT.ok(ttdeyeUser);
    }




}
