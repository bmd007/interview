package wonderland.interview.sb_ab.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean
    public WebClient slWebClient(SLApiProperties slApiProperties) {
        return WebClient.builder()
                .baseUrl(slApiProperties.getBaseUrl().toString())
                .codecs(codec -> codec.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
    }
}
