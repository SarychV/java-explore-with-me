package ru.practicum.ewm.users;

import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserShortDto;
import ru.practicum.ewm.users.model.User;

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

    public static UserShortDto toUserShortDto(User user) {
        UserShortDto result = new UserShortDto();
        result.setId(user.getId());
        result.setName(user.getName());
        return result;
    }
}
