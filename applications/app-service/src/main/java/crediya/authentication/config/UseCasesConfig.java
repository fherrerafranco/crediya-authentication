package crediya.authentication.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "crediya.authentication.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UserUseCase$"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+LoginUseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {
}
