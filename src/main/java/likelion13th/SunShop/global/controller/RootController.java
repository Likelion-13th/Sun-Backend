package likelion13th.SunShop.global.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//헬스체크 등

@Tag(name = "health", description = "health check 관련 api 입니다.")
@RestController
public class RootController {

    @Operation(summary = "cicd 확인", description = "cicd가 정상적으로 작동하고 있는지 확인하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON_200", description = "Success"),
    })
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";  // 간단한 응답으로 서버 상태 확인
    }
}
