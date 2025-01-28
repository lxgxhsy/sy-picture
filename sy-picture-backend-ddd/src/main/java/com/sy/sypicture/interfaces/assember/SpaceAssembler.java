package com.sy.sypicture.interfaces.assember;

import com.sy.sypicture.domain.space.entity.Space;
import com.sy.sypicture.interfaces.dto.space.SpaceAddRequest;
import com.sy.sypicture.interfaces.dto.space.SpaceEditRequest;
import com.sy.sypicture.interfaces.dto.space.SpaceUpdateRequest;
import org.springframework.beans.BeanUtils;

/**
 * @author 诺诺
 * 空间对象转换
 */
public class SpaceAssembler {

    public static Space toSpaceEntity(SpaceAddRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceUpdateRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceEditRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }
}
