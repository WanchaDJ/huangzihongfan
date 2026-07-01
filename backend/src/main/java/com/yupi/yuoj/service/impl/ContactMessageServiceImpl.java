package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.mapper.ContactMessageMapper;
import com.yupi.yuoj.model.entity.ContactMessage;
import com.yupi.yuoj.service.ContactMessageService;
import org.springframework.stereotype.Service;

@Service
public class ContactMessageServiceImpl extends ServiceImpl<ContactMessageMapper, ContactMessage> implements ContactMessageService {
}
