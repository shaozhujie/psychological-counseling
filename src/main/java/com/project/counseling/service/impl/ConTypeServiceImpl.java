package com.project.counseling.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.counseling.domain.ConType;
import com.project.counseling.mapper.ConTypeMapper;
import com.project.counseling.service.ConTypeService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 类型service实现类
 * @date 2025/11/15 10:03
 */
@Service
public class ConTypeServiceImpl extends ServiceImpl<ConTypeMapper, ConType> implements ConTypeService {
}
