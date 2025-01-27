package com.sy.sypicture.application.service.impI;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sy.sypicture.domain.user.service.UserDomainService;
import com.sy.sypicture.infrastructure.common.DeleteRequest;
import com.sy.sypicture.infrastructure.exception.BusinessException;
import com.sy.sypicture.infrastructure.exception.ErrorCode;
import com.sy.sypicture.infrastructure.exception.ThrowUtils;
import com.sy.sypicture.interfaces.dto.user.UserLoginRequest;
import com.sy.sypicture.interfaces.dto.user.UserQueryRequest;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.interfaces.dto.user.UserRegisterRequest;
import com.sy.sypicture.interfaces.vo.user.LoginUserVO;
import com.sy.sypicture.interfaces.vo.user.UserVO;
import com.sy.sypicture.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;


/**
* @author 诺诺
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-12-18 10:35:27
*/
@Service
@Slf4j
public class UserApplicationServiceImpl implements UserApplicationService {

	@Resource
	private UserDomainService userDomainService;
	/**
	 * 用户注册
	 * @param userRegisterRequest 用户信息
	 * @return 新用户id
	 */
	@Override
	public long userRegister(UserRegisterRequest userRegisterRequest) {
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String checkPassword = userRegisterRequest.getCheckPassword();
		   // 校验
           User.validUserRegister(userAccount, userPassword, checkPassword);
           // 执行
           return userDomainService.userRegister(userAccount, userPassword, checkPassword);
	}

	@Override
	public User getLoginUser(HttpServletRequest request) {
		return userDomainService.getLoginUser(request);
	}

	@Override
	public LoginUserVO userLogin(UserLoginRequest userLoginRequest,HttpServletRequest request) {
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		//校验
		User.validUserLogin(userAccount, userPassword);
		// 执行
		return userDomainService.userLogin(userAccount, userPassword, request);
	}

	/**
	 * 获取脱敏后的数据
	 * @param user
	 * @return
	 */
	@Override
	public LoginUserVO getLoginUserVO(User user) {
		return userDomainService.getLoginUserVO(user);
	}

	@Override
	public UserVO getUserVO(User user) {
		return userDomainService.getUserVO(user);
	}

	@Override
	public List<UserVO> getUserVOList(List<User> userList) {
	    return userDomainService.getUserVOList(userList);
	}

	/**
	 * 用户注销
	 * @param request request 请求
	 * @return
	 */
	@Override
	public boolean userLogout(HttpServletRequest request) {
		return userDomainService.userLogout(request);
	}


	/**
	 * 加密密码
	 * @param userPassword 用户密码
	 * @return
	 */
	@Override
	public String getEncryptPassword(String userPassword) {
		return userDomainService.getEncryptPassword(userPassword);
	}

	@Override
	public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
		return userDomainService.getQueryWrapper(userQueryRequest);
	}

	@Override
	public long addUser(User user) {
		return userDomainService.addUser(user);
	}

	@Override
	public User getUserById(long id) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		User user = userDomainService.getById(id);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
		return user;
	}

	@Override
	public UserVO getUserVOById(long id) {
		return userDomainService.getUserVO(getUserById(id));
	}

	@Override
	public boolean deleteUser(DeleteRequest deleteRequest) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		return userDomainService.removeById(deleteRequest.getId());
	}

	@Override
	public void updateUser(User user) {
		boolean result = userDomainService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
	}

	@Override
	public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
		ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		Page<User> userPage = userDomainService.page(new Page<>(current, size),
				userDomainService.getQueryWrapper(userQueryRequest));
		Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
		List<UserVO> userVO = userDomainService.getUserVOList(userPage.getRecords());
		userVOPage.setRecords(userVO);
		return userVOPage;
	}


	@Override
	public List<User> listByIds(Set<Long> userIdSet) {
		return userDomainService.listByIds(userIdSet);
	}


}




