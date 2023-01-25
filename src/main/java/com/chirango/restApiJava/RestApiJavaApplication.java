package com.chirango.restApiJava;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class RestApiJavaApplication {

  public static void main(String[] args) throws Exception {

//    		SpringApplication.run(RestApiJavaApplication.class, args);

    Transcript transcript = new Transcript();
    transcript.setAudio_url(Constants.AUDIO_URL);
    System.out.println(transcript);
    Gson gson = new Gson();
    String jsonRequest = gson.toJson(transcript);
    System.out.println(jsonRequest);

    // POST Request
    HttpRequest postRequest =
        HttpRequest.newBuilder()
            .uri(new URI(Constants.URI))
            .header("Authorization", Constants.API_KEY)
            .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
            .build();

    HttpClient httpClient = HttpClient.newHttpClient();
    HttpResponse<String> postResponse =
        httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
    System.out.println(postResponse.body());

    transcript = gson.fromJson(postResponse.body(), Transcript.class);
    System.out.println(transcript.getId());

    // GET Request
    HttpRequest getRequest =
        HttpRequest.newBuilder()
            .uri(new URI(Constants.URI + "/" + transcript.getId()))
            .header("Authorization", Constants.API_KEY)
            .build();

    while (true) {
      HttpResponse<String> getResponse =
          httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
      transcript = gson.fromJson(getResponse.body(), Transcript.class);

      System.out.println(transcript.getStatus());

      if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
        break;
      }
      Thread.sleep(1000);
    }

    System.out.println("Transcription Completed");
    System.out.println(transcript.getText());
      }

}
