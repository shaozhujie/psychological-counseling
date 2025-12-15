package com.project.counseling.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.counseling.domain.ConAppointment;
import com.project.counseling.mapper.ConAppointmentMapper;
import com.project.counseling.service.ConAppointmentService;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 咨询预约service实现类
 * @date 2025/11/19 03:49
 */
@Service
public class ConAppointmentServiceImpl extends ServiceImpl<ConAppointmentMapper, ConAppointment> implements ConAppointmentService {
}