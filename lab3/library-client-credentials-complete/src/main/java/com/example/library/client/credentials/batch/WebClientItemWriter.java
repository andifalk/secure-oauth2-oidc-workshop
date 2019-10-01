package com.example.library.client.credentials.batch;

import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

public class WebClientItemWriter<T> extends AbstractItemStreamItemWriter<T> {

  private final WebClient webClient;
  private final String targetUrl;

  public WebClientItemWriter(WebClient webClient, String targetUrl) {
    this.webClient = webClient;
    this.targetUrl = targetUrl;
  }

  @Override
  public void write(List<? extends T> items) throws Exception {
    ClientResponse clientResponse = webClient.post().uri(targetUrl + "/books").syncBody(items.get(0)).exchange().log().block();
    System.out.println(Objects.requireNonNull(clientResponse).statusCode());
  }
}
