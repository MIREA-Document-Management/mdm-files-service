package ru.mdm.files.client.configuration.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactivefeign.utils.MultiValueMapUtils;
import reactivefeign.webclient.WebReactiveOptions;
import ru.mdm.files.exception.BadRequestServerException;
import ru.mdm.files.exception.ServerException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Конфигурация Feign клиента.
 */
@RequiredArgsConstructor
public class FeignClientConfiguration {

    private static final String UNKNOWN_ERROR_MSG = "Неизвестная ошибка";

    private final ObjectMapper objectMapper;

    @Bean
    public WebReactiveOptions options() {
        var builder = new WebReactiveOptions.Builder();
        builder.setReadTimeoutMillis(600000);
        builder.setWriteTimeoutMillis(600000);
        builder.setConnectTimeoutMillis(5000);
        return builder.build();
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return (methodKey, response) -> {
            String message = UNKNOWN_ERROR_MSG;
            try {
                if (response.body() != null) {
                    var error = objectMapper.readTree(response.body().asReader(StandardCharsets.UTF_8));
                    var messageText = getNodeAsText(error, "message");
                    if (!messageText.isBlank()) {
                        message = messageText;
                    }
                }
            } catch (IOException ignored) {
                // ignore
            }

            var cause = FeignException.errorStatus(methodKey, response);
            return buildException(response.status(), message, cause);
        };
    }

    @Bean
    public ReactiveHttpRequestInterceptor auth() {
        return request -> ReactiveSecurityContextHolder.getContext()
                .doOnSuccess(securityContext -> {
                    if (securityContext != null && securityContext.getAuthentication() != null
                            && securityContext.getAuthentication().getCredentials() instanceof AbstractOAuth2Token) {
                        var token = "Bearer " + ((AbstractOAuth2Token) securityContext.getAuthentication()
                                .getCredentials()).getTokenValue();
                        MultiValueMapUtils.addOrdered(request.headers(), HttpHeaders.AUTHORIZATION, token);
                    }
                })
                .thenReturn(request);
    }

    private String getNodeAsText(JsonNode parentNode, String name) {
        return Optional.ofNullable(parentNode.get(name))
                .map(jsonNode -> jsonNode.asText(""))
                .orElse("");
    }

    private Exception buildException(int status, String message, Throwable cause) {
        Exception e;
        if (status == HttpStatus.BAD_REQUEST.value()) {
            e = new BadRequestServerException(message, cause);
        } else {
            e = new ServerException(message, cause);
        }
        return e;
    }
}
