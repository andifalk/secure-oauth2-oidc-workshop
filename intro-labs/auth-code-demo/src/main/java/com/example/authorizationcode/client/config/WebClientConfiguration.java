package com.example.authorizationcode.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

@Configuration
public class WebClientConfiguration {

  @Profile("!proxy")
  @Bean
  WebClient webClient() {
    return WebClient.builder().build();
  }

  @Profile("proxy")
  @Bean
  WebClient webClientWithProxy() {
    HttpClient httpClient = HttpClient.create().followRedirect(false)
            .tcpConfiguration(tcpClient ->
                    tcpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP).host("localhost").port(8085)));
    ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
    return WebClient.builder().clientConnector(connector).build();
  }
}
