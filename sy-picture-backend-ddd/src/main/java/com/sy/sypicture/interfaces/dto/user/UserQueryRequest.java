package com.sy.sypicture.interfaces.dto.user;

import com.sy.sypicture.infrastructure.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author: sy
 * @CreateTime: 2024-12-19
 * @Description: 分页
 * @Version: 1.0
 */


@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

	/**
	 * id
	 */
	private Long id;

	/**
	 * 用户昵称
	 */
	private String userName;

	/**
	 * 账号
	 */
	private String userAccount;

	/**
	 * 简介
	 */
	private String userProfile;

	/**
	 * 用户角色：user/admin/ban
	 */
	private String userRole;

	private static final long serialVersionUID = 1L;
}

