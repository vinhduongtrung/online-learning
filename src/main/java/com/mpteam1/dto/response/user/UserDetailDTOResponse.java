package com.mpteam1.dto.response.user;

import com.mpteam1.entities.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDetailDTOResponse {
    private Long id;
    private String email;
    private String fullName;
    private String accountName;
    private String avatarUrl;

    public static UserDetailDTOResponse fromUser(User user) {
        UserDetailDTOResponse userDetailDTOResponse = new UserDetailDTOResponse();
        userDetailDTOResponse.setId(user.getId());
        userDetailDTOResponse.setEmail(user.getEmail());
        userDetailDTOResponse.setFullName(user.getFullName());
        userDetailDTOResponse.setAccountName(user.getAccountName());
        if(user.getAvatarName() != null && !user.getAvatarName().isEmpty()) userDetailDTOResponse.setAvatarUrl("https://storage.googleapis.com/test-firebase-storage-d1b61.appspot.com/" + user.getAvatarName());
        return userDetailDTOResponse;
    }
}
