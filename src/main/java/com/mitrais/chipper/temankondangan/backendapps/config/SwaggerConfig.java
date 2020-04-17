package com.mitrais.chipper.temankondangan.backendapps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mitrais.chipper.temankondangan.backendapps"))
                .paths(PathSelectors.any())
                .build().apiInfo(apiEndPointsInfo());
    }

    private ApiInfo apiEndPointsInfo() {
        String description = String.format("Description of TemenKondangan REST API " +
                "%nFor the documentation about **Authentication with Gmail** please find [here](https://github.com/chipper-dev/temankondangan-backend/wiki/Gmail-Authentication-Flow).");

        return new ApiInfoBuilder().title("TemenKondangan REST API")
                .description(description)
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0.0")
                .termsOfServiceUrl("Terms of service")
                .build();
    }
}
