package com.example.sypicturebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sypicturebackend.model.dto.space.analyze.*;
import com.example.sypicturebackend.model.entity.Space;
import com.example.sypicturebackend.model.entity.User;
import com.example.sypicturebackend.model.vo.analyze.*;

import java.util.List;

/**
 * @Author: shiyong
 * @CreateTime: 2024-12-28
 * @Description:
 */
public interface SpaceAnalyzeService extends IService<Space> {


	/**
	 * 获取空间使用情况分析
	 *
	 * @param spaceUsageAnalyzeRequest
	 * @param loginUser
	 * @return
	 */
	SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);
	/**
	 * 获取空间图片分类分析
	 *
	 * @param spaceCategoryAnalyzeRequest
	 * @param loginUser
	 * @return
	 */
	List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);
	/**
	 * 获取空间图片标签分析
	 *
	 * @param spaceTagAnalyzeRequest
	 * @param loginUser
	 * @return
	 */
	List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);
	/**
	 * 获取空间图片大小分析
	 *
	 * @param spaceSizeAnalyzeRequest
	 * @param loginUser
	 * @return
	 */
	List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);
	/**
	 * 获取空间用户上传行为分析
	 *
	 * @param spaceUserAnalyzeRequest
	 * @param loginUser
	 * @return
	 */
	List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);
	/**
	 * 空间使用排行分析（仅管理员）
	 *
	 * @param spaceRankAnalyzeRequest
	 * @param loginUser
	 * @return
	 */
	List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);

}
