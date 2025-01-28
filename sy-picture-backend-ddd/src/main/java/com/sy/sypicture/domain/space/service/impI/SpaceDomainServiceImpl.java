package com.sy.sypicture.domain.space.service.impI;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sy.sypicture.domain.space.entity.Space;
import com.sy.sypicture.domain.space.repository.SpaceRepository;
import com.sy.sypicture.domain.space.service.SpaceDomainService;
import com.sy.sypicture.domain.space.valueobject.SpaceLevelEnum;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.infrastructure.exception.BusinessException;
import com.sy.sypicture.infrastructure.exception.ErrorCode;
import com.sy.sypicture.interfaces.dto.space.SpaceQueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author 诺诺
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2024-12-23 00:01:00
*/
@Service
public class SpaceDomainServiceImpl implements SpaceDomainService {

	@Resource
	private SpaceRepository spaceRepository;

	/**
	* 编程式事务处理器
	 */
	@Resource
	private TransactionTemplate transactionTemplate;

	Map<Long, Object> lockMap = new ConcurrentHashMap<>();




	@Override
	public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
		QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
		if(spaceQueryRequest != null){
			return queryWrapper;
		}
		Long id = spaceQueryRequest.getId();
		Long userId = spaceQueryRequest.getUserId();
		String spaceName = spaceQueryRequest.getSpaceName();
		Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
		String sortField = spaceQueryRequest.getSortField();
		String sortOrder = spaceQueryRequest.getSortOrder();
		Integer spaceType = spaceQueryRequest.getSpaceType();
		//拼接查询条件
		queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
		queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
		queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);
		queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "spaceType", spaceType);
		// 排序
		queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
		return queryWrapper;
	}

	@Override
	public void fillSpaceBySpaceLevel(Space space) {
		SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if(spaceLevelEnum != null){
	        long maxSize = spaceLevelEnum.getMaxSize();
	        if (space.getMaxSize() == null) {
		        space.setMaxSize(maxSize);
	        }
	        long maxCount = spaceLevelEnum.getMaxCount();
	        if (space.getMaxCount() == null) {
		        space.setMaxCount(maxCount);
	        }
        }
	}

	@Override
	public void checkSpaceAuth(User loginUser, Space space) {
		if(!space.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()){
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
	}


}




