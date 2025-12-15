package com.project.counseling.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.counseling.domain.ConBook;
import com.project.counseling.mapper.ConBookMapper;
import com.project.counseling.service.ConBookService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 证书类别service实现类
 * @date 2025/11/14 03:48
 */
@Service
public class ConBookServiceImpl extends ServiceImpl<ConBookMapper, ConBook> implements ConBookService {
}