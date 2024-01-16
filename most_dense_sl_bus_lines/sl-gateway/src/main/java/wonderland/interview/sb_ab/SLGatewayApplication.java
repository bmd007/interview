package wonderland.interview.sb_ab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SLGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SLGatewayApplication.class, args);
    }

}
