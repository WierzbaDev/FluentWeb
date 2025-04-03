package org.wierzbadev.userservice.controller.system;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wierzbadev.userservice.dto.publish.UserDto;
import org.wierzbadev.userservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/system/users")
public class SystemUsersController {

    private final UserService service;

    public SystemUsersController(UserService service) {
        this.service = service;
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<UserDto> readUserDtoById(@PathVariable("id") long id, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.readUserForRanking(id));
    }

    @GetMapping("/dto/list")
    public ResponseEntity<List<UserDto>> readUsersDtoByList(@RequestParam("ids") List<Long> userIds, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.readUsersForRanking(userIds));
    }
}
