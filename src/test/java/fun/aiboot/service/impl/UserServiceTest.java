package fun.aiboot.service.impl;

import fun.aiboot.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void login() {
        String login = userService.login("admin", "123456");
        System.out.println(login);
    }

    @Test
    void register() {
        userService.register("admin", "123456", "");
    }

}