package com.sy.sypicture.domain.user.service.impI;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sy.sypicture.domain.user.constant.UserConstant;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.domain.user.repository.UserRepository;
import com.sy.sypicture.domain.user.service.UserDomainService;
import com.sy.sypicture.domain.user.valueobject.UserRoleEnum;
import com.sy.sypicture.infrastructure.exception.BusinessException;
import com.sy.sypicture.infrastructure.exception.ErrorCode;
import com.sy.sypicture.infrastructure.exception.ThrowUtils;
import com.sy.sypicture.interfaces.dto.user.UserQueryRequest;
import com.sy.sypicture.interfaces.vo.user.LoginUserVO;
import com.sy.sypicture.interfaces.vo.user.UserVO;
import com.sy.sypicturebackend.manager.auth.StpKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
* @author 诺诺
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-12-18 10:35:27
*/
@Service
@Slf4j
public class UserDomainServiceImpl implements UserDomainService {

	@Resource
	private UserRepository userRepository;

	/**
	 * 用户注册
	 * @param userAccount 用户账号
	 * @param userPassword 用户密码
	 * @param checkPassword 检验密码
	 * @return 新用户id
	 */
	@Override
	public long userRegister(String userAccount, String userPassword, String checkPassword) {
		//检查是否重复
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount", userAccount); ;
		long count = userRepository.getBaseMapper().selectCount(queryWrapper);
		if (count > 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
		}
		//加密
		String encryptPassword = getEncryptPassword(userPassword);
		//插入数据
		User user = new User();
		user.setUserAccount(userAccount);
		user.setUserPassword(encryptPassword);
		user.setUserName("无名");
		user.setUserRole(UserRoleEnum.USER.getValue());
		boolean saveResult = userRepository.save(user);
        if(!saveResult){
        	throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }

		return user.getId();
	}

	@Override
	public User getLoginUser(HttpServletRequest request) {
		//先查询登录状态
		Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		User currentUser = (User)userObj;
		ThrowUtils.throwIf(currentUser == null || currentUser.getId() == null,ErrorCode.NOT_LOGIN_ERROR);
		//从数据库查找
		Long userId = currentUser.getId();
		currentUser = userRepository.getById(userId);
		ThrowUtils.throwIf(currentUser == null,ErrorCode.NOT_LOGIN_ERROR);

		return currentUser;
	}

	@Override
	public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
		//加密
		String encryptPassword = getEncryptPassword(userPassword);
		//查询用户是否存在
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount", userAccount);
		queryWrapper.eq("userPassword",encryptPassword);
		User user = userRepository.getBaseMapper().selectOne(queryWrapper);
		//用户不存在
		if(user == null){
			log.info("user login failed, userAccount cannot match userPassword");
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
		}
		//记录用户的登录态
		request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
		// 记录用户登录态到 Sa-token 便于空间鉴权时候使用 注意保证该用户信息与 SpringSession 中的信息过期时间一致
		StpKit.SPACE.login(user.getId());
		StpKit.SPACE.getSession().set(UserConstant.USER_LOGIN_STATE, user);
		return this.getLoginUserVO(user);
	}

	/**
	 * 获取脱敏后的数据
	 * @param user
	 * @return
	 */
	@Override
	public LoginUserVO getLoginUserVO(User user) {
		if (user == null){
			return null;
		}
		LoginUserVO loginUserVO = new LoginUserVO();
		BeanUtil.copyProperties(user,loginUserVO);
		return loginUserVO;
	}

	@Override
	public UserVO getUserVO(User user) {
		if (user == null){
			return null;
		}
		UserVO userVO = new UserVO();
		BeanUtil.copyProperties(user,userVO);
		return userVO;
	}

	@Override
	public List<UserVO> getUserVOList(List<User> userList) {
		if(CollUtil.isEmpty(userList)){
			return new ArrayList<>();
		}

		return userList.stream().map(this::getUserVO).collect(Collectors.toList());
	}

	/**
	 * 用户注销
	 * @param request request 请求
	 * @return
	 */
	@Override
	public boolean userLogout(HttpServletRequest request) {
		//判断是否已登录
		Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		ThrowUtils.throwIf(userObj == null, ErrorCode.OPERATION_ERROR, "未登录");

		//移除登录态
		request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);

		return true;
	}
	/**
	 * 加密密码
	 * @param userPassword 用户密码
	 * @return
	 */
	@Override
	public String getEncryptPassword(String userPassword) {
		// 盐值，混淆密码
		final String SALT = "sy";
		return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
	}

	@Override
	public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
		if (userQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
		}
		Long id = userQueryRequest.getId();
		String userAccount = userQueryRequest.getUserAccount();
		String userName = userQueryRequest.getUserName();
		String userProfile = userQueryRequest.getUserProfile();
		String userRole = userQueryRequest.getUserRole();
		String sortField = userQueryRequest.getSortField();
		String sortOrder = userQueryRequest.getSortOrder();
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
		queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
		queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
		queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
		queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
		queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
		return queryWrapper;
	}

	@Override
	public Long addUser(User user) {
		// 默认密码 12345678
		final String DEFAULT_PASSWORD = "12345678";
		String encryptPassword = this.getEncryptPassword(DEFAULT_PASSWORD);
		user.setUserPassword(encryptPassword);
		boolean result = userRepository.save(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return user.getId();
	}

	@Override
	public Boolean removeById(Long id) {
		return userRepository.removeById(id);
	}

	@Override
	public boolean updateById(User user) {
		return userRepository.updateById(user);
	}

	@Override
	public User getById(long id) {
		return userRepository.getById(id);
	}

	@Override
	public Page<User> page(Page<User> userPage, QueryWrapper<User> queryWrapper) {
		return userRepository.page(userPage, queryWrapper);
	}

	@Override
	public List<User> listByIds(Set<Long> userIdSet) {
		return userRepository.listByIds(userIdSet);
	}
}




