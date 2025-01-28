package com.sy.sypicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sy.sypicture.domain.space.entity.Space;
import com.sy.sypicture.domain.space.repository.SpaceRepository;
import com.sy.sypicture.infrastructure.mapper.SpaceMapper;
import org.springframework.stereotype.Service;

/**
 * @author 诺诺
 */
@Service
public class SpaceRepositoryImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceRepository {
}