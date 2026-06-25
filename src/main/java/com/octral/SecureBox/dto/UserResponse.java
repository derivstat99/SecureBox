package com.octral.SecureBox.dto;

import com.octral.SecureBox.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername());
    }
}
