package likelion13th.SunShop.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//API 문서화

@Configuration
public class SwaggerConfig {

@Bean
public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shop API")                    // API 제목
                        .version("1.0.0")                     // 버전
                        .description("Shop API 명세서"))      // 설명

                // JWT 인증 방식 설정
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))

                .schemaRequirement("Authorization", new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.APIKEY)     // API Key 방식
                        .in(SecurityScheme.In.HEADER)         // 헤더에 포함
                        .description("Access Token을 입력하세요."));
    }

    @Bean
    public GroupedOpenApi allGroup() {
        return GroupedOpenApi.builder()
                .group("All")                    // 그룹명
                .pathsToMatch("/**")             // 모든 경로 포함
                .build();
    }
}
