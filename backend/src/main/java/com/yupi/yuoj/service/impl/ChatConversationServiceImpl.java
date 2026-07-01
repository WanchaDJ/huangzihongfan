package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.mapper.ChatConversationMapper;
import com.yupi.yuoj.model.entity.ChatConversation;
import com.yupi.yuoj.service.ChatConversationService;
import org.springframework.stereotype.Service;

@Service
public class ChatConversationServiceImpl extends ServiceImpl<ChatConversationMapper, ChatConversation> implements ChatConversationService {
}
