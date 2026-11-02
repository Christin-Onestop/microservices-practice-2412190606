package com.zjgsu.tyt.task.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserServiceClient(
            @Value("${services.user-service.url:http://localhost:8081}") String userServiceUrl) {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(3000);

        this.restTemplate = new RestTemplate(factory);
        this.userServiceUrl = userServiceUrl;
    }

    public void validateUser(Long userId) {
        try {
            restTemplate.getForEntity(
                    userServiceUrl + "/api/users/" + userId,
                    Object.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalArgumentException(
                        "用户不存在，ID：" + userId);
            }
            throw new IllegalStateException(
                    "用户服务返回错误：" + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new IllegalStateException(
                    "用户服务暂时不可用，请稍后重试", e);
        }
    }
}