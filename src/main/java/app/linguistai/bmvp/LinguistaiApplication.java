package app.linguistai.bmvp;

import app.linguistai.bmvp.configs.XPConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(XPConfiguration.class)
public class LinguistaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinguistaiApplication.class, args);
	}

}
