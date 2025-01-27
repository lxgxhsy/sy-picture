package com.sy.sypicture.interfaces.assember;

import com.sy.sypicture.domain.user.entity.User;
import com.sy.sypicture.interfaces.dto.user.UserAddRequest;
import com.sy.sypicture.interfaces.dto.user.UserUpdateRequest;
import org.springframework.beans.BeanUtils;

/**
 * 用户对象转换
 * @author 诺诺
 */
public class UserAssembler {

    public static User toUserEntity(UserAddRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return user;
    }

    public static User toUserEntity(UserUpdateRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return user;
    }
}