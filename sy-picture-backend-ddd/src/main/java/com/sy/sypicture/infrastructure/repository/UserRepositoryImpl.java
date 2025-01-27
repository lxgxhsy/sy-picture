package com.sy.sypicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.domain.user.repository.UserRepository;
import com.sy.sypicture.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author 诺诺
 */
@Service
public class UserRepositoryImpl extends ServiceImpl<UserMapper, User> implements UserRepository {
}