package wonderland.interview.sb_ab.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "sl")
@Getter
@Builder
public class SLApiProperties {
    @NotNull
    URI baseUrl;
    @NotBlank
    String apiKey;
}
