package com.sy.sypicture.interfaces.controller;

import cn.hutool.core.util.ObjectUtil;
import com.sy.sypicture.infrastructure.common.BaseResponse;
import com.sy.sypicture.infrastructure.common.DeleteRequest;
import com.sy.sypicture.infrastructure.common.ResultUtils;
import com.sy.sypicture.infrastructure.exception.BusinessException;
import com.sy.sypicture.infrastructure.exception.ErrorCode;
import com.sy.sypicture.infrastructure.exception.ThrowUtils;
import com.sy.sypicture.interfaces.assember.SpaceUserAssembler;
import com.sy.sypicture.shared.auth.annotation.SaSpaceCheckPermission;
import com.sy.sypicture.shared.auth.model.SpaceUserPermissionConstant;
import com.sy.sypicture.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.sy.sypicture.interfaces.dto.spaceuser.SpaceUserEditRequest;
import com.sy.sypicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import com.sy.sypicture.domain.space.entity.SpaceUser;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.interfaces.vo.space.SpaceUserVO;
import com.sy.sypicture.application.service.SpaceUserApplicationService;
import com.sy.sypicture.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: shiyong
 * @CreateTime: 2025-01-04
 * @Description:
 * @Version: 1.0
 */

@RestController
@Slf4j
@RequestMapping("/spaceUser")
public class SpaceUserController {
	@Resource
	private UserApplicationService userApplicationService;

	@Resource
	private SpaceUserApplicationService spaceUserApplicationService;


	/**
	 * 添加成员到空间
	 */
	@PostMapping("/add")
	@SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
	public BaseResponse<Long> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
		long id = spaceUserApplicationService.addSpaceUser(spaceUserAddRequest);
		return ResultUtils.success(id);
	}

	/**
	 * 从空间移除成员
	 */
	@PostMapping("/delete")
	@SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
	public BaseResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest,
	                                             HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long id = deleteRequest.getId();
		// 判断是否存在
		SpaceUser oldSpaceUser = spaceUserApplicationService.getById(id);
		ThrowUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = spaceUserApplicationService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}

	/**
	 * 查询某个成员在某个空间的信息
	 */
	@PostMapping("/get")
	@SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
	public BaseResponse<SpaceUser> getSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest) {
		// 参数校验
		ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
		Long spaceId = spaceUserQueryRequest.getSpaceId();
		Long userId = spaceUserQueryRequest.getUserId();
		ThrowUtils.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
		// 查询数据库
		SpaceUser spaceUser = spaceUserApplicationService.getOne(spaceUserApplicationService.getQueryWrapper(spaceUserQueryRequest));
		ThrowUtils.throwIf(spaceUser == null, ErrorCode.NOT_FOUND_ERROR);
		return ResultUtils.success(spaceUser);
	}

	/**
	 * 查询成员信息列表
	 */
	@PostMapping("/list")
	@SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
	public BaseResponse<List<SpaceUserVO>> listSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest,
	                                                     HttpServletRequest request) {
		ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
		List<SpaceUser> spaceUserList = spaceUserApplicationService.list(
				spaceUserApplicationService.getQueryWrapper(spaceUserQueryRequest)
		);
		return ResultUtils.success(spaceUserApplicationService.getSpaceUserVOList(spaceUserList));
	}

	/**
	 * 编辑成员信息（设置权限）
	 */
	@PostMapping("/edit")
	@SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
	public BaseResponse<Boolean> editSpaceUser(@RequestBody SpaceUserEditRequest spaceUserEditRequest,
	                                           HttpServletRequest request) {
		if (spaceUserEditRequest == null || spaceUserEditRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// 将实体类和 DTO 进行转换
		SpaceUser spaceUser = SpaceUserAssembler.toSpaceUserEntity(spaceUserEditRequest);
		// 数据校验
		spaceUserApplicationService.validSpaceUser(spaceUser, false);
		// 判断是否存在
		long id = spaceUserEditRequest.getId();
		SpaceUser oldSpaceUser = spaceUserApplicationService.getById(id);
		ThrowUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = spaceUserApplicationService.updateById(spaceUser);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}

	/**
	 * 查询我加入的团队空间列表
	 */
	@PostMapping("/list/my")
	public BaseResponse<List<SpaceUserVO>> listMyTeamSpace(HttpServletRequest request) {
		User loginUser = userApplicationService.getLoginUser(request);
		SpaceUserQueryRequest spaceUserQueryRequest = new SpaceUserQueryRequest();
		spaceUserQueryRequest.setUserId(loginUser.getId());
		List<SpaceUser> spaceUserList = spaceUserApplicationService.list(
				spaceUserApplicationService.getQueryWrapper(spaceUserQueryRequest)
		);
		return ResultUtils.success(spaceUserApplicationService.getSpaceUserVOList(spaceUserList));
	}
}
