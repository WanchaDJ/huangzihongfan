package com.yupi.yuoj.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserPasswordUpdateRequest implements Serializable {

    private String oldPassword;

    private String newPassword;

    private String checkPassword;

    private static final long serialVersionUID = 1L;
}
