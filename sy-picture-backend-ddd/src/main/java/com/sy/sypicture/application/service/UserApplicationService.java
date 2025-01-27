package com.sy.sypicture.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sy.sypicture.infrastructure.common.DeleteRequest;
import com.sy.sypicture.interfaces.dto.user.UserLoginRequest;
import com.sy.sypicture.interfaces.dto.user.UserQueryRequest;
import com.sy.sypicture.domain.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sy.sypicture.interfaces.dto.user.UserRegisterRequest;
import com.sy.sypicture.interfaces.vo.user.LoginUserVO;
import com.sy.sypicture.interfaces.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
* @author 诺诺
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-12-18 10:35:27
*/
public interface UserApplicationService  {





	/**
	 * 加密密码
	 * @param userPassword 用户密码
	 * @return
	 */
	String getEncryptPassword(String userPassword);

	/**
	 * 用户注册
	 * @param userRegisterRequest 用户注册请求
	 * @return 新用户id
	 */
	long userRegister(UserRegisterRequest userRegisterRequest);


	/**
	 * 获取当前登录用户
	 *
	 * @param request
	 * @return
	 */
	User getLoginUser(HttpServletRequest request);

	/**
	 * 用户登录状态
	 * @param userLoginRequest 账号
	 * @param request request请求
	 * @return
	 */
	LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);


	/**
	 * 获取脱敏后的已登录用户的信息
	 * @param user
	 * @return
	 */
	LoginUserVO  getLoginUserVO(User user);

	/**
	 * 获取脱敏后的已登录用户的信息
	 * @param user
	 * @return
	 */
	UserVO getUserVO(User user);

	/**
	 * 获取脱敏后的已登录用户列表的信息
	 * @param userList
	 * @return
	 */
	List<UserVO> getUserVOList(List<User> userList);

	/**
	 * 用户注销
	 * @param request request 请求
	 * @return
	 */
	boolean userLogout(HttpServletRequest request);

	/**
	 * 分页查询列表
	 * @param userQueryRequest 分页查询
	 * @return
	 */
	QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

	long addUser(User user);

	User getUserById(long id);

	UserVO getUserVOById(long id);

	boolean deleteUser(DeleteRequest deleteRequest);

	void updateUser(User user);

	Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);


	List<User> listByIds(Set<Long> userIdSet);
}
