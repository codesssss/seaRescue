package com.sxh.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxh.common.core.domain.Response;
import com.sxh.common.core.domain.ResponseEnum;
import com.sxh.common.core.domain.entity.LighthouseEntity;
import com.sxh.common.core.domain.entity.ShipEntity;
import com.sxh.system.mapper.ILighthouseDao;
import com.sxh.system.service.MPLighthouseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sxh
 * @since 2022-02-25
 */
@Service
public class LighthouseServiceImpl extends ServiceImpl<ILighthouseDao, LighthouseEntity> implements MPLighthouseService {

    public Response addLighthouse(LighthouseEntity req) {
        if (req.getLatitude() != null) {
            LighthouseEntity lighthouse = new LighthouseEntity();
            BeanUtils.copyProperties(req, lighthouse);
            baseMapper.insert(lighthouse);
            return Response.success();
        }
        return Response.error(ResponseEnum.ERROR);
    }

    public Response deleteLighthouse(Long[] codes) {
        int row = 0;
        for (Long code : codes) {
            row += baseMapper.deleteById(code);
        }
        if (row > 0) {
            return Response.success();
        }
        return Response.error();
    }

    public Response updateLighthouse(LighthouseEntity req) {
        if (req.getId() != null) {
            LighthouseEntity lighthouse = new LighthouseEntity();
            BeanUtils.copyProperties(req, lighthouse);
            baseMapper.updateById(req);
            return Response.success();
        }
        return Response.error(ResponseEnum.ERROR);
    }

    public List<LighthouseEntity> getAllLighthouse() {
        Map<String,Object> map=new HashMap<>();
        List<LighthouseEntity> lighthouseEntities = baseMapper.selectByMap(map);
        return lighthouseEntities;
    }

    public LighthouseEntity getLighthouseInfo(Long id){
        return baseMapper.selectById(id);
    }
}
