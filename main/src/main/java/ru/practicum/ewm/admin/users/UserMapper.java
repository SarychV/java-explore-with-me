package ru.practicum.ewm.admin.users;

import ru.practicum.ewm.admin.users.dto.UserDto;
import ru.practicum.ewm.admin.users.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    protected static User toUser(UserDto dto) {
        User result = new User();
        result.setName(dto.getName());
        result.setEmail(dto.getEmail());
        return result;
    }

    protected static UserDto toUserDto(User user) {
        UserDto result = new UserDto();
        result.setId(user.getId());
        result.setName(user.getName());
        result.setEmail(user.getEmail());
        return result;
    }

    protected static List<UserDto> toUserDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
