package com.sy.sypicture.interfaces.assember;

import com.sy.sypicture.domain.space.entity.SpaceUser;
import com.sy.sypicture.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.sy.sypicture.interfaces.dto.spaceuser.SpaceUserEditRequest;
import org.springframework.beans.BeanUtils;

/**
 * 空间用户转换
 * @author 诺诺
 */
public class SpaceUserAssembler {

    public static SpaceUser toSpaceUserEntity(SpaceUserAddRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }

    public static SpaceUser toSpaceUserEntity(SpaceUserEditRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }
}
