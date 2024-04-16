package pl.futurecollars.invoicing.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfiguration {

  @Bean
  public Docket docket() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("pl.futurecollars.invoicing"))
        .paths(PathSelectors.any())
        .build()
        .tags(
            new Tag("invoice-controller", "This controller is used to: [add] [update] [delete] [get] invoices from the database")
        )
        .apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .description("Application for invoice management")
        .license("No licence available - private!")
        .version("v1.0.0")
        .title("INVOICING APPLICATION")
        .contact(
            new Contact(
                "Piotr Farbaniec",
                "https://github.com/PiotrFarbaniec",
                "n/a")
        )
        .build();
  }
}
