package ru.practicum.ewm.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        log.info("userRepository.save() was invoked with arguments user={}", user);
        User returnedUser = userRepository.save(user);
        UserDto result = UserMapper.toUserDto(returnedUser);
        log.info("To the AdminUsersController was returned: userDto={}", result);
        return result;
    }

    @Override
    public List<UserDto> getUserList(long[] ids, int from, int size) {
        List<UserDto> userDtoList;
        if (ids != null && ids.length != 0) { //подготовка списка по указанным идентификаторам
            List<User> users = userRepository.findByIdIn(ids);
            userDtoList = UserMapper.toUserDtoList(users);
        } else {
            // подготовка списка всех пользователей с учетом параметров ограничения выборки
            Pageable page = PageRequest.of(from, size);
            Page<User> users = userRepository.findAll(page);
            userDtoList = UserMapper.toUserDtoList(users.stream().collect(Collectors.toList()));
        }
        log.info("To the AdminUsersController was returned: userDtoList={}", userDtoList);
        return userDtoList;
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User with id=%d was not found", userId)));
        userRepository.deleteById(userId);
    }
}
