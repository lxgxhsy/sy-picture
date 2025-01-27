package com.sy.sypicture.infrastructure.aop;

import com.sy.sypicture.infrastructure.annotation.AuthCheck;
import com.sy.sypicture.infrastructure.exception.BusinessException;
import com.sy.sypicture.infrastructure.exception.ErrorCode;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.domain.user.valueobject.UserRoleEnum;
import com.sy.sypicture.application.service.UserApplicationService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: sy
 * @CreateTime: 2024-12-19
 * @Description:
 * @Version: 1.0
 */


@Aspect
@Component
public class AuthInterceptor {

	@Resource
	private UserApplicationService userApplicationService;

	/**
	 * 执行拦截
	 * @param joinPoint
	 * @param authCheck
	 * @return
	 * @throws Throwable
	 */
	@Around("@annotation(authCheck)")
	public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
		String mustRole = authCheck.mustRole();
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		// 获取当前登录用户
		User loginUser = userApplicationService.getLoginUser(request);
		UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
		// 如果不需要权限，放行
		if (mustRoleEnum == null) {
			return joinPoint.proceed();
		}
		// 以下的代码：必须有权限，才会通过
		UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
		if (userRoleEnum == null) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 要求必须有管理员权限，但用户没有管理员权限，拒绝
		if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 通过权限校验，放行
		return joinPoint.proceed();
	}
}
