package com.sy.sypicture.interfaces.controller;

import com.sy.sypicture.infrastructure.annotation.AuthCheck;
import com.sy.sypicture.infrastructure.common.BaseResponse;
import com.sy.sypicture.infrastructure.common.ResultUtils;
import com.sy.sypicture.domain.user.constant.UserConstant;
import com.sy.sypicture.infrastructure.exception.BusinessException;
import com.sy.sypicture.infrastructure.exception.ErrorCode;
import com.sy.sypicture.infrastructure.api.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @Author: sy
 * @CreateTime: 2024-12-19
 * @Description: 文件
 * @Version: 1.0
 */


@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

	@Resource
	private CosManager cosManager;

	/**
	 * 测试文件上传
	 * @return
	 */
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@PostMapping("/test/upload")
	public BaseResponse<String> testUploadFile(@RequestPart("file")MultipartFile multipartFile){
		String filename = multipartFile.getOriginalFilename();
		String pathUrl = String.format("/test/%s",filename);
		log.info("sdsd{}",filename);
		File file = null;
		try {
			//上传文件
			file = File.createTempFile(pathUrl, null);
			multipartFile.transferTo(file);
			cosManager.putObject(pathUrl,file);
			return ResultUtils.success(pathUrl);
		} catch (Exception e) {
			log.error("file fail to upload |||");
			throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传失败");
		}finally {
			if (file != null) {
				// 删除临时文件
				boolean delete = file.delete();
				if (!delete) {
					log.error("file delete error, filepath = {}", pathUrl);
				}
			}
		}

	}


	/**
	 * 测试文件下载
	 * 使用Servlet 识别到返回值
	 * @return
	 */
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@GetMapping("/test/download")
	public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException{
		COSObjectInputStream cosObjectInput  = null;
		try {
			COSObject cosObject = cosManager.getObject(filepath);
			cosObjectInput = cosObject.getObjectContent();
			//处理下载下来的流
			byte[] bytes = IOUtils.toByteArray(cosObjectInput);
			//设置响应头
			response.setContentType("application/octet-stream;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            //写入响应
			response.getOutputStream().write(bytes);
			response.getOutputStream().flush();

		} catch (Exception e){
            log.error("file download error, filepath = " + filepath,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
		} finally {
			 if(cosObjectInput != null){
				 cosObjectInput.close();
			 }
		}

	}
}
