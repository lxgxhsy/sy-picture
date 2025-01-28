package com.sy.sypicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sy.sypicture.domain.space.entity.SpaceUser;
import com.sy.sypicture.domain.space.repository.SpaceUserRepository;
import com.sy.sypicture.infrastructure.mapper.SpaceUserMapper;
import org.springframework.stereotype.Service;

/**
 * @author 诺诺
 * 空间用户仓储服务
 */
@Service
public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}