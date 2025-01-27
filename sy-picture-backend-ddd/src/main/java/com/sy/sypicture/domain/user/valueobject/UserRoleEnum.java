package com.sy.sypicture.domain.user.valueobject;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @Author: sy
 * @CreateTime: 2024-12-18
 * @Description: 枚举类
 * @Version: 1.0
 */


@Getter
public enum UserRoleEnum {

	USER("用户","user"),
	ADMIN("管理员","admin");


	private  final String text;

	private final String value;

	/**
	 * 构造函数
	 * @param text
	 * @param value
	 * @return
	 */

	UserRoleEnum(String text, String value){
		this.text = text;
		this.value = value;
	}

	/**
	 *根据 value 获取枚举
	 * @param value 枚举值的value
	 * @return 枚举值
	 */
	public static UserRoleEnum getEnumByValue(String value){
		if(ObjUtil.isEmpty(value)){
			return null;
		}
		for(UserRoleEnum anEnum : UserRoleEnum.values()){
               if(anEnum.value.equals(value)){
               	return anEnum;
               }
		}
		return null;
	}
}
