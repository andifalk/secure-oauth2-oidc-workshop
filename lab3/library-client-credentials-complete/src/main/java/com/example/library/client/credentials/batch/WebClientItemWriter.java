package com.example.library.client.credentials.batch;

import com.example.library.client.credentials.web.BookResource;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class WebClientItemWriter<T> extends AbstractItemStreamItemWriter<T> {

  private final WebClient webClient;
  private final String targetUrl;

  public WebClientItemWriter(WebClient webClient, String targetUrl) {
    this.webClient = webClient;
    this.targetUrl = targetUrl;
  }

  @Override
  public void write(List<? extends T> items) throws Exception {
    webClient.post().uri(targetUrl + "/books").bodyValue(items.get(0))
            .retrieve()
            .onStatus(
                    s -> s.equals(HttpStatus.UNAUTHORIZED),
                    cr -> Mono.just(new BadCredentialsException("Not authenticated")))
            .onStatus(
                    HttpStatus::is4xxClientError,
                    cr -> Mono.just(new IllegalArgumentException(cr.statusCode().getReasonPhrase())))
            .onStatus(
                    HttpStatus::is5xxServerError,
                    cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())))
            .bodyToMono(BookResource.class).log().subscribe();
  }
}
