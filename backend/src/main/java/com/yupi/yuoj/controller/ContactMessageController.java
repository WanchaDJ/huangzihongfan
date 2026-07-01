package com.yupi.yuoj.controller;

import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.dto.contact.ContactMessageAddRequest;
import com.yupi.yuoj.model.entity.ContactMessage;
import com.yupi.yuoj.service.ContactMessageService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 联系我们接口
 */
@RestController
@RequestMapping("/contact")
@Slf4j
public class ContactMessageController {

    @Resource
    private ContactMessageService contactMessageService;

    /**
     * 发送联系消息（公开接口，无需登录）
     */
    @PostMapping("/send")
    public BaseResponse<Boolean> sendMessage(@RequestBody ContactMessageAddRequest req) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ContactMessage message = new ContactMessage();
        BeanUtils.copyProperties(req, message);
        message.setIsRead(0);
        boolean result = contactMessageService.save(message);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}
