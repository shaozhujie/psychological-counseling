package com.project.counseling.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.counseling.domain.ConSet;
import com.project.counseling.mapper.ConSetMapper;
import com.project.counseling.service.ConSetService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 咨询师个人设置service实现类
 * @date 2025/11/14 05:27
 */
@Service
public class ConSetServiceImpl extends ServiceImpl<ConSetMapper, ConSet> implements ConSetService {
}
