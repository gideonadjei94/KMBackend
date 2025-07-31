package com.gideon.knowmate.Service;

import com.gideon.knowmate.Dto.UserDto;


import java.util.List;

public interface UserService {
    List<UserDto> getTopCreators();
}
