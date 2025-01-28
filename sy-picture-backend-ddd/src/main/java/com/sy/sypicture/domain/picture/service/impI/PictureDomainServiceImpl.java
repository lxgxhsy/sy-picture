package com.sy.sypicture.domain.picture.service.impI;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sy.sypicture.application.service.UserApplicationService;
import com.sy.sypicture.domain.picture.entity.Picture;
import com.sy.sypicture.domain.picture.repository.PictureRepository;
import com.sy.sypicture.domain.picture.service.PictureDomainService;
import com.sy.sypicture.domain.picture.valueobject.PictureReviewStatusEnum;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.infrastructure.api.CosManager;
import com.sy.sypicture.infrastructure.api.aliyunai.AliYunAiApi;
import com.sy.sypicture.infrastructure.api.aliyunai.model.CreateOutPaintingTaskRequest;
import com.sy.sypicture.infrastructure.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.sy.sypicture.infrastructure.exception.BusinessException;
import com.sy.sypicture.infrastructure.exception.ErrorCode;
import com.sy.sypicture.infrastructure.exception.ThrowUtils;
import com.sy.sypicture.infrastructure.utils.ColorSimilarUtils;
import com.sy.sypicture.infrastructure.utils.ColorTransformUtils;
import com.sy.sypicture.interfaces.dto.picture.*;
import com.sy.sypicture.interfaces.vo.picture.PictureVO;
import com.sy.sypicturebackend.manager.upload.FilePictureUpload;
import com.sy.sypicturebackend.manager.upload.PictureUploadTemplate;
import com.sy.sypicturebackend.manager.upload.UrlPictureUpload;
import com.sy.sypicturebackend.model.dto.file.UploadPictureResult;
import com.sy.sypicturebackend.model.entity.Space;
import com.sy.sypicturebackend.service.SpaceService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 诺诺
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2024-12-19 15:54:34
*/
@Service
@Slf4j
public class PictureDomainServiceImpl
    implements PictureDomainService {

	@Resource
	private PictureRepository pictureRepository;

	@Resource
	private FilePictureUpload filePictureUpload;

	@Resource
	private UrlPictureUpload urlPictureUpload;

	@Resource
	private AliYunAiApi aliYunAiApi;

	@Resource
	private SpaceService spaceService;

	@Resource
	private CosManager cosManager;

	@Resource
	private TransactionTemplate transactionTemplate;



	/**
	 * @param pictureQueryRequest 前端对于图片查询类
	 * @return
	 */
	@Override
	public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
		QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
		if (pictureQueryRequest == null) {
			return queryWrapper;
		}
		// 从对象中取值
		Long id = pictureQueryRequest.getId();
		String name = pictureQueryRequest.getName();
		String introduction = pictureQueryRequest.getIntroduction();
		String category = pictureQueryRequest.getCategory();
		List<String> tags = pictureQueryRequest.getTags();
		Long picSize = pictureQueryRequest.getPicSize();
		Integer picWidth = pictureQueryRequest.getPicWidth();
		Integer picHeight = pictureQueryRequest.getPicHeight();
		Double picScale = pictureQueryRequest.getPicScale();
		String picFormat = pictureQueryRequest.getPicFormat();
		String searchText = pictureQueryRequest.getSearchText();
		Long userId = pictureQueryRequest.getUserId();
		Integer reviewStatus = pictureQueryRequest.getReviewStatus();
		Long spaceId = pictureQueryRequest.getSpaceId();
		boolean nullSpaceId = pictureQueryRequest.isNullSpaceId();
		String reviewMessage = pictureQueryRequest.getReviewMessage();
		Long reviewerId = pictureQueryRequest.getReviewerId();
		String sortField = pictureQueryRequest.getSortField();
		String sortOrder = pictureQueryRequest.getSortOrder();
		Date startEditTime = pictureQueryRequest.getStartEditTime();
		Date endEditTime = pictureQueryRequest.getEndEditTime();

		// 从多字段中搜索
		if (StrUtil.isNotBlank(searchText)) {
			// 需要拼接查询条件
			// and (name like "%xxx%" or introduction like "%xxx%")
			queryWrapper.and(
					qw -> qw.like("name", searchText)
							.or()
							.like("introduction", searchText)
			);
		}
		queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
		queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
		queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
		queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
		queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
		queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
		queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
		queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
		queryWrapper.isNull(nullSpaceId, "spaceId");
		queryWrapper.ge(ObjUtil.isNotEmpty(startEditTime), "editTime", startEditTime);
		queryWrapper.le(ObjUtil.isNotEmpty(endEditTime), "editTime", endEditTime);
		queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
		queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
		queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
		queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);


		// JSON 数组查询
		if (CollUtil.isNotEmpty(tags)) {
			/* and (tag like "%\"Java\"%" and like "%\"Python\"%") */
			for (String tag : tags) {
				queryWrapper.like("tags", "\"" + tag + "\"");
			}
		}
		// 排序
		queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
		return queryWrapper;
	}


	@Override
	public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
		// 校验参数
		ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
		// 校验空间是否存在
		Long spaceId = pictureUploadRequest.getSpaceId();
		if (spaceId != null) {
			Space space = spaceService.getById(spaceId);
			ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
			//校验是否有空间的权限 仅空间管理员才能上传
			if (!loginUser.getId().equals(space.getUserId())) {
				throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
			}
			// 校验额度
			if (space.getTotalCount() >= space.getMaxCount()) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间条数不足");
			}
			if (space.getTotalSize() >= space.getMaxSize()) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间大小不足");
			}
		}
		//判断是新增还是删除
		Long pictureId = null;
		if (pictureUploadRequest != null) {
			pictureId = pictureUploadRequest.getId();
		}
		//如果是更新 判断是否存在
		if (pictureId != null) {
			Picture oldPicture = pictureRepository.getById(pictureId);
			ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
			//只能本人还有管理员才能编辑
			if (!oldPicture.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
				throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
			}
			// 校验空间是否一致
			// 没传 spaceId，则复用原有图片的 spaceId（这样也兼容了公共图库）
			if (spaceId == null) {
				if (oldPicture.getSpaceId() != null) {
					spaceId = oldPicture.getSpaceId();
				}
			} else {
				// 传了 spaceId，必须和原图片的空间 id 一致
				if (ObjUtil.notEqual(spaceId, oldPicture.getSpaceId())) {
					throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间 id 不一致");
				}
			}
		}
		//上传图片 得到图片信息
		// 按照用户 id 划分目录 => 按照空间划分目录
		String uploadPathPrefix;
		if (spaceId == null) {
			// 公共图库
			uploadPathPrefix = String.format("public/%s", loginUser.getId());
		} else {
			// 空间
			uploadPathPrefix = String.format("space/%s", spaceId);
		}
		// 根据 inputSource 类型区分上传文件
		PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
		if (inputSource instanceof String) {
			pictureUploadTemplate = urlPictureUpload;
		}
		UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
		// 构造要入库的图片信息
		Picture picture = new Picture();
		/**
		 *  指定空间 id
		 */
		picture.setSpaceId(spaceId);
		picture.setUrl(uploadPictureResult.getUrl());
		picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
		String picName = uploadPictureResult.getPicName();
		//支持外层传递名称
		if (pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPicName())) {
			picName = pictureUploadRequest.getPicName();
		}
		picture.setName(picName);
		picture.setPicSize(uploadPictureResult.getPicSize());
		picture.setPicWidth(uploadPictureResult.getPicWidth());
		picture.setPicHeight(uploadPictureResult.getPicHeight());
		picture.setPicColor(ColorTransformUtils.getStandardColor(uploadPictureResult.getPicColor()));
		picture.setPicScale(uploadPictureResult.getPicScale());
		picture.setPicFormat(uploadPictureResult.getPicFormat());
		picture.setUserId(loginUser.getId());
		//补充审核参数
		this.fillReviewParams(picture, loginUser);
		//操作数据库
		//如果pictureId不为空 那就是更新 否则就是新增
		if (pictureId != null) {
			//如果是更新 那么更新 id  还有编辑时间
			picture.setId(pictureId);
			picture.setEditTime(new Date());

		}

//		boolean result  = this.saveOrUpdate(picture);
		// 开启事务
		Long finalSpaceId = spaceId;
		transactionTemplate.execute(status -> {
			// 插入数据
			boolean result = pictureRepository.saveOrUpdate(picture);
			ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败 数据库操作失败");
			if (finalSpaceId != null) {
				// 更新空间的使用额度

				boolean update = spaceService.lambdaUpdate()
						.eq(Space::getId, finalSpaceId)
						.setSql("totalSize = totalSize + " + picture.getPicSize())
						.setSql("totalCount = totalCount + 1")
						.update();
				ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
			}
			return picture;
		});

		return PictureVO.objToVo(picture);
	}



	@Override
	public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
		//校验参数
		ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
		Long id = pictureReviewRequest.getId();
		Integer reviewStatus = pictureReviewRequest.getReviewStatus();
		PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
		String reviewMessage = pictureReviewRequest.getReviewMessage();
		if (reviewStatusEnum == null || id == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		//判断图片是否存在
		Picture oldPicture = pictureRepository.getById(id);
		ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
		//判断审核状态是否重复 已经是该状态
		if (oldPicture.getReviewStatus().equals(reviewStatus)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复审核");
		}
		//数据库操作
		Picture updatePicture = new Picture();
		BeanUtil.copyProperties(pictureReviewRequest, updatePicture);
		updatePicture.setReviewTime(new Date());
		updatePicture.setReviewerId(loginUser.getId());
		boolean result = pictureRepository.updateById(updatePicture);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
	}

	/**
	 * 填充审核参数
	 *
	 * @param picture
	 * @param loginUser
	 */
	@Override
	public void fillReviewParams(Picture picture, User loginUser) {
		if (loginUser.isAdmin()) {
			//管理员过审
			picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
			picture.setReviewerId(loginUser.getId());
			picture.setReviewMessage("管理员自动过审");
			picture.setReviewTime(new Date());
		} else {
			//非管理员， 创建 编辑默认都是待审核
			picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
		}
	}


	@Override
	public int uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
		String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
		String searchText = pictureUploadByBatchRequest.getSearchText();
		if (StrUtil.isBlank(namePrefix)) {
			namePrefix = searchText;
		}
		// 格式化数量
		Integer count = pictureUploadByBatchRequest.getCount();
		ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "最多 30 条");
		// 要抓取的地址
		String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
		Document document;
		try {
			document = Jsoup.connect(fetchUrl).get();
		} catch (IOException e) {
			log.error("获取页面失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
		}
		Element div = document.getElementsByClass("dgControl").first();
		if (ObjUtil.isNull(div)) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");
		}
		Elements imgElementList = div.select("img.mimg");
		int uploadCount = 0;
		for (Element imgElement : imgElementList) {
			String fileUrl = imgElement.attr("src");
			if (StrUtil.isBlank(fileUrl)) {
				log.info("当前链接为空，已跳过: {}", fileUrl);
				continue;
			}
			// 处理图片上传地址，防止出现转义问题
			int questionMarkIndex = fileUrl.indexOf("?");
			if (questionMarkIndex > -1) {
				fileUrl = fileUrl.substring(0, questionMarkIndex);
			}
			// 上传图片
			PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
			pictureUploadRequest.setFileUrl(fileUrl);
			pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
			try {
				PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
				log.info("图片上传成功, id = {}", pictureVO.getId());
				uploadCount++;
			} catch (Exception e) {
				log.error("图片上传失败", e);
				continue;
			}
			if (uploadCount >= count) {
				break;
			}
		}
		return uploadCount;
	}

	@Async
	@Override
	public void clearPictureFile(Picture oldPicture) {
		// 判断该图片是否被多条记录所使用
		String pictureUrl = oldPicture.getUrl();
		Long count = pictureRepository.lambdaQuery()
				.eq(Picture::getUrl, pictureUrl)
				.count();
		// 有不止一条记录用到了该图片，不清理
		if (count > 1) {
			return;
		}
		// 删除图片
		cosManager.deleteObject(pictureUrl);
		// 删除缩略图
		String thumbnailUrl = oldPicture.getThumbnailUrl();
		if (StrUtil.isNotBlank(thumbnailUrl)) {
			cosManager.deleteObject(thumbnailUrl);
		}
	}

	@Override
	public void checkPictureAuth(User loginUser, Picture picture) {
		Long spaceId = picture.getSpaceId();
		if (spaceId == null) {
			// 公共图库，仅本人或管理员可操作
			if (!picture.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
				throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
			}
		} else {
			// 私有空间，仅空间管理员可操作
			if (!picture.getUserId().equals(loginUser.getId())) {
				throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
			}
		}
	}

	@Override
	public void deletePicture(long pictureId, User loginUser) {
		ThrowUtils.throwIf(pictureId <= 0, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
		// 判断是否存在
		Picture oldPicture = pictureRepository.getById(pictureId);
		ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
		// 改为使用注解鉴权

//		checkPictureAuth(loginUser, oldPicture);
		// 开启事务
		transactionTemplate.execute(status -> {
			// 操作数据库
			boolean result = pictureRepository.removeById(pictureId);
			ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
			// 更新空间的使用额度，释放额度
			boolean update = spaceService.lambdaUpdate()
					.eq(Space::getId, oldPicture.getSpaceId())
					.setSql("totalSize = totalSize - " + oldPicture.getPicSize())
					.setSql("totalCount = totalCount - 1")
					.update();
			ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
			return true;
		});
		// 异步清理文件
		this.clearPictureFile(oldPicture);
	}

	@Override
	public void editPicture(Picture picture, User loginUser) {
		// 设置编辑时间
		picture.setEditTime(new Date());
		// 数据校验
		picture.validPicture();
		// 判断是否存在
		long id = picture.getId();
		Picture oldPicture = pictureRepository.getById(id);
		ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
		// 校验权限
		// 改为使用注解鉴权
//		checkPictureAuth(loginUser, oldPicture);
		// 补充审核参数
		this.fillReviewParams(picture, loginUser);
		// 操作数据库
		boolean result = pictureRepository.updateById(picture);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
	}

	@Override
	public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser) {
		//校验参数
		ThrowUtils.throwIf(spaceId == null || StrUtil.isBlank(picColor), ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
		//校验空间权限
		Space space = spaceService.getById(spaceId);
		ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
		if (!loginUser.getId().equals(space.getUserId())) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间访问权限");
		}
		//查询空间的所有图片 必须要有主色调
		List<Picture> pictureList = pictureRepository.lambdaQuery()
				.eq(Picture::getSpaceId, spaceId)
				.isNotNull(Picture::getPicColor)
				.list();
		//如果没有图片 返回空列表
		if (CollUtil.isEmpty(pictureList)) {
			return Collections.emptyList();
		}
		//将颜色字符串转换为主色调
		Color targetColor = Color.decode(picColor);
		//计算相似度排序
		List<Picture> sortedPictures = pictureList.stream()
				.sorted(Comparator.comparingDouble(picture -> {
					//提取主色调
					String hexColor = picture.getPicColor();
					//没有主色调的图片放在最后
					if (StrUtil.isBlank(hexColor)) {
						return Double.MAX_VALUE;
					}
					Color pictureColor = Color.decode(hexColor);
					// 越大越相似  
					return -ColorSimilarUtils.calculateSimilarity(targetColor, pictureColor);
				})).limit(12).collect(Collectors.toList());
		// 转换为 PictureVO
		return sortedPictures.stream()
				.map(PictureVO::objToVo)
				.collect(Collectors.toList());
	}

	@Override
	public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
		// 参数校验
		List<Long> pictureIdList = pictureEditByBatchRequest.getPictureIdList();
		Long spaceId = pictureEditByBatchRequest.getSpaceId();
		String category = pictureEditByBatchRequest.getCategory();
		List<String> tags = pictureEditByBatchRequest.getTags();
		ThrowUtils.throwIf(CollUtil.isEmpty(pictureIdList) || spaceId == null || loginUser == null, ErrorCode.PARAMS_ERROR);
		// 检验空间权限
		Space space = spaceService.getById(spaceId);
		ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
		if(!space.getUserId().equals(loginUser.getId())){
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间访问权限");
		}
		// 查询指定图片 仅选择需要的字段
		List<Picture> pictureList = pictureRepository.lambdaQuery()
				.select(Picture::getId, Picture::getSpaceId)
				.eq(Picture::getSpaceId, spaceId)
				.in(Picture::getId, pictureIdList)
				.list();
		// 更新分类和标签
		pictureList.forEach(picture -> {
			if(StrUtil.isNotBlank(category)){
				picture.setCategory(category);
			}
			if(CollUtil.isNotEmpty(tags)){
				picture.setTags(JSONUtil.toJsonStr(tags));
			}
		});
		// 批量重命名
         String nameRule = pictureEditByBatchRequest.getNameRule();
         fillPictureWithNameRule(pictureList, nameRule);
		// 操作数据库进行批量更新
		boolean result = pictureRepository.updateBatchById(pictureList);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "批量编辑失败");
	}

	@Override
	public CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser) {
		// 获取图片信息
		Long pictureId = createPictureOutPaintingTaskRequest.getPictureId();
		Picture picture = Optional.ofNullable(pictureRepository.getById(pictureId))
				.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在"));
		// 校验权限
		// 改为使用注解鉴权
//		checkPictureAuth(loginUser, picture);
		// 创建扩图任务
		CreateOutPaintingTaskRequest createOutPaintingTaskRequest = new CreateOutPaintingTaskRequest();
		CreateOutPaintingTaskRequest.Input input = new CreateOutPaintingTaskRequest.Input();
		input.setImageUrl(picture.getUrl());
		createOutPaintingTaskRequest.setInput(input);
		createOutPaintingTaskRequest.setParameters(createPictureOutPaintingTaskRequest.getParameters());
		// 创建任务
		return aliYunAiApi.createOutPaintingTask(createOutPaintingTaskRequest);
	}

	/**
	 * nameRule 格式：图片{序号}
	 * @param pictureList 图片列表
	 * @param nameRule 命名规则
	 */
	private void fillPictureWithNameRule(List<Picture> pictureList, String nameRule) {
		if(StrUtil.isBlank(nameRule) || CollUtil.isEmpty(pictureList)){
			return;
		}
		long count = 1;
		try {
			for(Picture picture : pictureList){
				String pictureName = nameRule.replaceAll("\\{序号}", String.valueOf(count++));
				picture.setName(pictureName);
			}
		} catch (Exception e) {
			log.error("名称解析错误", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "名称解析错误");
		}
	}
}




