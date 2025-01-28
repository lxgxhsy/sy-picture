package com.sy.sypicture.domain.space.service.impI;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sy.sypicture.application.service.SpaceApplicationService;
import com.sy.sypicture.application.service.UserApplicationService;
import com.sy.sypicture.domain.space.entity.Space;
import com.sy.sypicture.domain.space.entity.SpaceUser;
import com.sy.sypicture.domain.space.service.SpaceUserDomainService;
import com.sy.sypicture.domain.space.valueobject.SpaceRoleEnum;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.infrastructure.exception.BusinessException;
import com.sy.sypicture.infrastructure.exception.ErrorCode;
import com.sy.sypicture.infrastructure.exception.ThrowUtils;
import com.sy.sypicture.infrastructure.mapper.SpaceUserMapper;
import com.sy.sypicture.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.sy.sypicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import com.sy.sypicture.interfaces.vo.space.SpaceUserVO;
import com.sy.sypicture.interfaces.vo.space.SpaceVO;
import com.sy.sypicture.interfaces.vo.user.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 诺诺
* @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
* @createDate 2025-01-04 23:09:41
*/
@Service
public class SpaceUserDomainServiceImpl implements SpaceUserDomainService {

	@Resource
	@Lazy
	private SpaceApplicationService spaceApplicationService;

	@Resource
    private UserApplicationService userApplicationService;



	@Override
	public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
		QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
		if (spaceUserQueryRequest == null) {
			return queryWrapper;
		}
		// 从对象中取值
		Long id = spaceUserQueryRequest.getId();
		Long spaceId = spaceUserQueryRequest.getSpaceId();
		Long userId = spaceUserQueryRequest.getUserId();
		String spaceRole = spaceUserQueryRequest.getSpaceRole();
		queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
		queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "spaceRole", spaceRole);
		return queryWrapper;
	}
}




