package likelion13th.SunShop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion13th.SunShop.DTO.response.UserInfoResponse;
import likelion13th.SunShop.login.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 정보", description = "사용자 정보 관련 API 입니다.")
@Slf4j
@RestController
@RequestMapping("/users/info")
@RequiredArgsConstructor
public class UserInfoController {
    private final UserService userService;

    @Operation(summary = "사용자 조회", description = "사용자의 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(new UserInfoResponse(userId, "홍길동"));
    }


}
