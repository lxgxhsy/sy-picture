package com.sy.sypicturebackend.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.sy.sypicturebackend.manager.auth.model.SpaceUserAuthConfig;
import com.sy.sypicturebackend.manager.auth.model.SpaceUserRole;
import com.sy.sypicture.domain.space.entity.Space;
import com.sy.sypicture.domain.space.entity.SpaceUser;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.domain.space.valueobject.SpaceRoleEnum;
import com.sy.sypicture.domain.space.valueobject.SpaceTypeEnum;
import com.sy.sypicture.application.service.SpaceUserApplicationService;
import com.sy.sypicture.application.service.UserApplicationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shiyong
 * @CreateTime: 2025-01-05
 * @Description: 空间成员权限管理
 * @Version: 1.0
 */

@Component
public class SpaceUserAuthManager {

	@Resource
	private SpaceUserApplicationService spaceUserApplicationService;

	@Resource
	private UserApplicationService userApplicationService;

	public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

	static {
		String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
		SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
	}

	/**
	 * 根据用户获取权限列表
	 * @param spaceUserRole
	 * @return
	 */
	public List<String> getPermissionsByRole(String spaceUserRole){
		if(StrUtil.isBlank(spaceUserRole)){
			return new ArrayList<>();
		}
		// 找到所有的匹配的角色
		SpaceUserRole role = SPACE_USER_AUTH_CONFIG.getRoles().stream()
				.filter(r -> spaceUserRole.equals(r.getKey()))
				.findFirst()
				.orElse(null);
		if(role == null){
			return new ArrayList<>();
		}
		return role.getPermissions();
	}

	/**
	 * 获取权限列表
	 * @param space
	 * @param loginUser
	 * @return
	 */
	public List<String> getPermissionList(Space space, User loginUser) {
		if (loginUser == null) {
			return new ArrayList<>();
		}
		// 管理员权限
		List<String> ADMIN_PERMISSIONS = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
		// 公共图库
		if (space == null) {
			if (loginUser.isAdmin()) {
				return ADMIN_PERMISSIONS;
			}
			return new ArrayList<>();
		}
		SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
		if (spaceTypeEnum == null) {
			return new ArrayList<>();
		}
		// 根据空间获取对应的权限
		switch (spaceTypeEnum) {
			case PRIVATE:
				// 私有空间，仅本人或管理员有所有权限
				if (space.getUserId().equals(loginUser.getId()) || loginUser.isAdmin()) {
					return ADMIN_PERMISSIONS;
				} else {
					return new ArrayList<>();
				}
			case TEAM:
				// 团队空间，查询 SpaceUser 并获取角色和权限
				SpaceUser spaceUser = spaceUserApplicationService.lambdaQuery()
						.eq(SpaceUser::getSpaceId, space.getId())
						.eq(SpaceUser::getUserId, loginUser.getId())
						.one();
				if (spaceUser == null) {
					return new ArrayList<>();
				} else {
					return getPermissionsByRole(spaceUser.getSpaceRole());
				}
		}
		return new ArrayList<>();
	}

}
