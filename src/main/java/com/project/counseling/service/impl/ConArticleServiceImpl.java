package com.project.counseling.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.counseling.domain.ConArticle;
import com.project.counseling.mapper.ConArticleMapper;
import com.project.counseling.service.ConArticleService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 文章service实现类
 * @date 2025/11/15 04:29
 */
@Service
public class ConArticleServiceImpl extends ServiceImpl<ConArticleMapper, ConArticle> implements ConArticleService {
}
