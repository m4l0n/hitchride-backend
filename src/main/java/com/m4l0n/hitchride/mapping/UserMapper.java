package com.m4l0n.hitchride.mapping;

import com.m4l0n.hitchride.dto.UserDTO;
import com.m4l0n.hitchride.pojos.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDTO>{
}
