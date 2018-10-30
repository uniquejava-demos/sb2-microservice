package cyper.demo.shoppingcartui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import cyper.demo.shoppingcartui.filter.AuthHeaderFilter;

@EnableZuulProxy
@SpringBootApplication
public class ShoppingcartUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingcartUiApplication.class, args);
	}

	@Bean
	AuthHeaderFilter authHeaderFilter() {
		return new AuthHeaderFilter();
	}
}
