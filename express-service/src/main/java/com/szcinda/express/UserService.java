package com.szcinda.express;

import com.szcinda.express.dto.*;
import com.szcinda.express.params.QueryUserParams;
import com.szcinda.express.persistence.User;

public interface UserService {
    PageResult<User> query(QueryUserParams params);

    UserIdentity getUserIdentity(String account, String password);

    void createUser(UserCreateDto createDto);

    String generatePwd(String password);

    void updateUser(UserUpdateDto updateDto);

    void updatePassword(UpdatePasswordDto updatePasswordDto);

    void delete(String userId);

    String getToken(String userId, String password);

    User findUserById(String userId);
}
