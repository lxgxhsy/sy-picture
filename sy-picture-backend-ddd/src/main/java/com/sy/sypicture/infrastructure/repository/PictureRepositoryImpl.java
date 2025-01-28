package com.sy.sypicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sy.sypicture.domain.picture.entity.Picture;
import com.sy.sypicture.domain.picture.repository.PictureRepository;
import com.sy.sypicture.infrastructure.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
 * @author 诺诺
 * 图片仓储
 */
@Service
public class PictureRepositoryImpl extends ServiceImpl<PictureMapper, Picture> implements PictureRepository {
}