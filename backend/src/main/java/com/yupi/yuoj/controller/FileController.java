package com.yupi.yuoj.controller;

import cn.hutool.core.io.FileUtil;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.model.dto.file.UploadFileRequest;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.model.enums.FileUploadBizEnum;
import com.yupi.yuoj.service.UserService;
import com.yupi.yuoj.utils.UploadPathUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    private static final long ONE_MB = 1024 * 1024L;
    private static final long IMAGE_MAX_SIZE = 10 * ONE_MB;

    @Resource
    private UserService userService;

    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           UploadFileRequest uploadFileRequest,
                                           HttpServletRequest request) {
        if (multipartFile == null || multipartFile.isEmpty() || uploadFileRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        FileUploadBizEnum bizEnum = FileUploadBizEnum.getEnumByValue(uploadFileRequest.getBiz());
        if (bizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, bizEnum);

        User loginUser = userService.getLoginUser(request);
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        String safeName = buildSafeName(originalFilename, suffix);
        String relativePath = String.format("%s/%s/%s", bizEnum.getValue(), loginUser.getId(), safeName);
        Path uploadRoot = UploadPathUtils.getUploadRoot();
        Path targetPath = uploadRoot.resolve(relativePath).normalize();
        if (!targetPath.startsWith(uploadRoot.normalize())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(multipartFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("file upload error, relativePath={}", relativePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "upload failed");
        }

        String normalizedRelativePath = relativePath.replace("\\", "/");
        String fileUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath() + "/uploads/" + normalizedRelativePath;
        return ResultUtils.success(fileUrl);
    }

    private String buildSafeName(String originalFilename, String suffix) {
        String mainName = FileUtil.mainName(StringUtils.defaultString(originalFilename, "image"));
        String safeMainName = mainName.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (StringUtils.isBlank(safeMainName)) {
            safeMainName = "image";
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        if (StringUtils.isBlank(suffix)) {
            return uuid + "-" + safeMainName;
        }
        return uuid + "-" + safeMainName + "." + suffix.toLowerCase(Locale.ROOT);
    }

    private void validFile(MultipartFile multipartFile, FileUploadBizEnum bizEnum) {
        long fileSize = multipartFile.getSize();
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        String suffix = StringUtils.defaultString(fileSuffix).toLowerCase(Locale.ROOT);
        if (!Arrays.asList("jpeg", "jpg", "png", "webp", "gif", "svg").contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "only image files are supported");
        }
        if (fileSize > IMAGE_MAX_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "image size must be less than 10MB");
        }
        if (FileUploadBizEnum.USER_AVATAR.equals(bizEnum) && fileSize > 5 * ONE_MB) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "avatar size must be less than 5MB");
        }
    }
}
