package com.sy.sypicture.application.service.impI;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sy.sypicture.domain.picture.service.PictureDomainService;
import com.sy.sypicture.infrastructure.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.sy.sypicture.infrastructure.exception.ErrorCode;
import com.sy.sypicture.infrastructure.exception.ThrowUtils;
import com.sy.sypicture.interfaces.dto.picture.*;
import com.sy.sypicture.domain.picture.entity.Picture;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.interfaces.vo.picture.PictureVO;
import com.sy.sypicture.application.service.PictureApplicationService;
import com.sy.sypicture.infrastructure.mapper.PictureMapper;
import com.sy.sypicture.application.service.UserApplicationService;
import com.sy.sypicture.interfaces.vo.user.UserVO;
import com.sy.sypicture.infrastructure.manager.upload.FilePictureUpload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 诺诺
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2024-12-19 15:54:34
*/
@Service
@Slf4j
public class PictureApplicationServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureApplicationService {

	@Resource
	private PictureDomainService pictureDomainService;

	@Resource
	private UserApplicationService userApplicationService;

	@Resource
	private FilePictureUpload filePictureUpload;




	/**
	 * 分页获取图片封装
	 */
	@Override
	public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
		List<Picture> pictureList = picturePage.getRecords();
		Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
		if (CollUtil.isEmpty(pictureList)) {
			return pictureVOPage;
		}
		// 对象列表 => 封装对象列表
		List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
		// 1. 关联查询用户信息
		Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		// 2. 填充信息
		pictureVOList.forEach(pictureVO -> {
			Long userId = pictureVO.getUserId();
			User user = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			pictureVO.setUser(userApplicationService.getUserVO(user));
		});
		pictureVOPage.setRecords(pictureVOList);
		return pictureVOPage;
	}

	/**
	 * @param pictureQueryRequest 前端对于图片查询类
	 * @return
	 */
	@Override
	public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
		return pictureDomainService.getQueryWrapper(pictureQueryRequest);
	}


	@Override
	public void validPicture(Picture picture) {

		ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR, "图片不能为空");
		picture.validPicture();
	}

	@Override
	public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
		return pictureDomainService.uploadPicture(inputSource, pictureUploadRequest, loginUser);
	}

	@Override
	public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {

		// 对象转封装类
		PictureVO pictureVO = PictureVO.objToVo(picture);
		// 关联查询用户信息
		Long userId = picture.getUserId();
		if (userId != null && userId > 0) {
			User user = userApplicationService.getUserById(userId);
			UserVO userVO = userApplicationService.getUserVO(user);
			pictureVO.setUser(userVO);
		}
		return pictureVO;
	}

	@Override
	public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
	        pictureDomainService.doPictureReview(pictureReviewRequest, loginUser);
	}

	/**
	 * 填充审核参数
	 *
	 * @param picture
	 * @param loginUser
	 */
	@Override
	public void fillReviewParams(Picture picture, User loginUser) {
		pictureDomainService.fillReviewParams(picture, loginUser);
	}


	@Override
	public int uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
		return pictureDomainService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
	}

	@Async
	@Override
	public void clearPictureFile(Picture oldPicture) {
	   pictureDomainService.clearPictureFile(oldPicture);
	}

	@Override
	public void checkPictureAuth(User loginUser, Picture picture) {
		pictureDomainService.checkPictureAuth(loginUser, picture);
	}

	@Override
	public void deletePicture(long pictureId, User loginUser) {
		pictureDomainService.deletePicture(pictureId, loginUser);
	}

	@Override
	public void editPicture(Picture picture, User loginUser) {
		pictureDomainService.editPicture(picture, loginUser);
	}

	@Override
	public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser) {
		return pictureDomainService.searchPictureByColor(spaceId, picColor, loginUser);
	}

	@Override
	public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
		pictureDomainService.editPictureByBatch(pictureEditByBatchRequest, loginUser);
	}

	@Override
	public CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser) {
		return pictureDomainService.createPictureOutPaintingTask(createPictureOutPaintingTaskRequest, loginUser);
	}


}




