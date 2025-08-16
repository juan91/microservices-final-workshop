package org.example.apigateway.config;

import org.example.apigateway.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Value("${bank-service.url}") private String bankServiceUrl;
    @Value("${bank-service.id}") private String bankServiceId;
    @Value("${bank-service.path}") private String bankServicePath;

    @Value("${account-service.url}") private String accountsServiceUrl;
    @Value("${account-service.id}") private String accountsServiceId;
    @Value("${account-service.path}") private String accountsServicePath;

    @Value("${transaction-service.url}") private String transacctionServiceUrl;
    @Value("${transaction-service.id}") private String transacctionServiceId;
    @Value("${transaction-service.path}") private String transacctionServicePath;

    private final JwtAuthenticationFilter filter;

    public RouteConfig(JwtAuthenticationFilter filter) {
        this.filter = filter;
    }

  @Bean
  public RouteLocator createRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
      .route(bankServiceId, route -> route.path(bankServicePath).filters(gtf -> gtf.filter(filter)).uri(bankServiceUrl))
      .route(accountsServiceId, route -> route.path(accountsServicePath).filters(gtf -> gtf.filter(filter)).uri(accountsServiceUrl))
      .route(transacctionServiceId, route -> route.path(transacctionServicePath).filters(gtf -> gtf.filter(filter)).uri(transacctionServiceUrl))
      .build();
  }
}
