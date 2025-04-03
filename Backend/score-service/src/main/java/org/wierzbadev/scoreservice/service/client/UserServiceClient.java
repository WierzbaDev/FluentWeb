package org.wierzbadev.scoreservice.service.client;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.wierzbadev.scoreservice.model.dto.user.UserDto;

import java.util.List;

@FeignClient(name = "user-service", url = "${app.current-api}" + "/api/system/users")
public interface UserServiceClient {
    @Cacheable(value = "userCache", key = "'#userId'")
    @GetMapping("/dto/{id}")
     UserDto readUserById(@PathVariable("id") long id, @RequestHeader("Authorization") String token);


    @GetMapping("/dto/list")
    List<UserDto> readUsersByIds(@RequestHeader("Authorization") String token, @RequestParam("ids") List<Long> ids);
}
