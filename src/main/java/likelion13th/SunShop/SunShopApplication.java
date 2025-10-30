package likelion13th.SunShop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SunShopApplication {
	public static void main(String[] args) {
		SpringApplication.run(SunShopApplication.class, args);
	}
}
