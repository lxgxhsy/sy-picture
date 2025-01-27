package com.sy.sypicture.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sy.sypicture.infrastructure.annotation.AuthCheck;
import com.sy.sypicture.infrastructure.common.BaseResponse;
import com.sy.sypicture.infrastructure.common.DeleteRequest;
import com.sy.sypicture.infrastructure.common.ResultUtils;
import com.sy.sypicture.domain.user.constant.UserConstant;
import com.sy.sypicture.infrastructure.exception.BusinessException;
import com.sy.sypicture.infrastructure.exception.ErrorCode;
import com.sy.sypicture.infrastructure.exception.ThrowUtils;
import com.sy.sypicture.interfaces.assember.UserAssembler;
import com.sy.sypicture.interfaces.dto.user.*;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.interfaces.vo.user.LoginUserVO;
import com.sy.sypicture.interfaces.vo.user.UserVO;
import com.sy.sypicture.application.service.UserApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: sy
 * @CreateTime: 2024-12-18
 * @Description: 用户相关接口
 * @Version: 1.0
 */

@RestController
@RequestMapping("/user")
public class UserController {

	@Resource
	private UserApplicationService userApplicationService;

	/**
	 * 用户注册
	 */
	@PostMapping("/register")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
		ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
		long result = userApplicationService.userRegister(userRegisterRequest);
		return ResultUtils.success(result);
	}

	/**
	 * 用户登录
	 */
	@PostMapping("/login")
	public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

		ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
		LoginUserVO loginUserVO = userApplicationService.userLogin(userLoginRequest,request);
		return ResultUtils.success(loginUserVO);
	}

	/**
	 * 获取当前登录用户
	 */
	@GetMapping("/get/login")
	public BaseResponse<LoginUserVO> getLoginUser(@RequestBody HttpServletRequest request) {

		User loginUser = userApplicationService.getLoginUser(request);
		return ResultUtils.success(userApplicationService.getLoginUserVO(loginUser));
	}

	/**
	 *  用户注销
	 */
	@PostMapping("/logout")
	public BaseResponse<Boolean> userLogout(@RequestBody HttpServletRequest request) {

		ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
		boolean result = userApplicationService.userLogout( request);
		return ResultUtils.success(result);
	}

	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
		ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
		User user = UserAssembler.toUserEntity(userAddRequest);
		long result = userApplicationService.addUser(user);
		return ResultUtils.success(result);
	}

	/**
	 * 根据 id 获取用户（仅管理员）
	 */
	@GetMapping("/get")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<User> getUserById(long id) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		User user = userApplicationService.getUserById(id);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
		return ResultUtils.success(user);
	}

	/**
	 * 根据 id 获取包装类
	 */
	@GetMapping("/get/vo")
	public BaseResponse<UserVO> getUserVOById(long id) {
		BaseResponse<User> response = getUserById(id);
		User user = response.getData();
		return ResultUtils.success(userApplicationService.getUserVO(user));
	}

	/**
	 * 删除用户
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean b = userApplicationService.deleteUser(deleteRequest);
		return ResultUtils.success(b);
	}
	/**
	 * 更新用户
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
		if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
	    User user = UserAssembler.toUserEntity(userUpdateRequest);
		 userApplicationService.updateUser(user);
		return ResultUtils.success(true);
	}

	/**
	 * 分页获取用户封装列表（仅管理员）
	 *
	 * @param userQueryRequest 查询请求参数
	 */
	@PostMapping("/list/page/vo")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
		ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
		return ResultUtils.success( userApplicationService.listUserVOByPage(userQueryRequest));

	}
}
