package com.sy.sypicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sy.sypicture.domain.space.entity.SpaceUser;
import com.sy.sypicture.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.sy.sypicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import com.sy.sypicture.interfaces.vo.space.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 诺诺
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-01-04 23:09:41
*/
public interface SpaceUserDomainService  {







	/**
	 * 获取查询对象
	 *
	 * @param spaceUserQueryRequest
	 * @return
	 */
	QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);
}
