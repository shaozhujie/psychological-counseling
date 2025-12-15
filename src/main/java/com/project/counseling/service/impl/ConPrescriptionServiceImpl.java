package com.project.counseling.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.counseling.domain.ConPrescription;
import com.project.counseling.mapper.ConPrescriptionMapper;
import com.project.counseling.service.ConPrescriptionService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 处方service实现类
 * @date 2025/11/20 10:06
 */
@Service
public class ConPrescriptionServiceImpl extends ServiceImpl<ConPrescriptionMapper, ConPrescription> implements ConPrescriptionService {
}
