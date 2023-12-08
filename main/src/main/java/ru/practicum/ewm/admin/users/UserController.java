package ru.practicum.ewm.admin.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.users.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        log.info("userService.addUser() was invoked with arguments userDto={}", userDto);
        return userService.addUser(userDto);
    }

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(name = "ids", required = false) @Positive long[] ids,
            @RequestParam(name = "from", defaultValue = "0") @Positive int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("userService.getUserList() was invoked with arguments ids={}, from={}, size={}", ids, from, size);
        return userService.getUserList(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Positive long userId) {
        log.info("userService.deleteUser() was invoked with userId={}", userId);
        userService.deleteUser(userId);
    }
}
