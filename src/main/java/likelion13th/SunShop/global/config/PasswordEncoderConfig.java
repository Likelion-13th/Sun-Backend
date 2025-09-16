package likelion13th.SunShop.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//비밀번호 암호화

@Configuration
public class PasswordEncoderConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 최고 수준의 단방향 암호화
    }
    public static class PasswordEncorderConfig {
    }
}

//bean 으로 등록 안하면 스프링에서 인식을 못함. 필수!