package ru.practicum.ewm.users;

import ru.practicum.ewm.users.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto user);

    List<UserDto> getUserList(long[] ids, int from, int size);

    void deleteUser(long userId);
}
