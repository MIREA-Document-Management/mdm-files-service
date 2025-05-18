package ru.mdm.files.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import ru.mdm.files.configuration.converter.JsonToMapConverter;
import ru.mdm.files.configuration.converter.MapToJsonConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableR2dbcAuditing
@RequiredArgsConstructor
public class R2dbcConfiguration {

    private static final String UNKNOWN_USER = "Неизвестный пользователь";

    private final ObjectMapper objectMapper;

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new JsonToMapConverter(objectMapper));
        converters.add(new MapToJsonConverter(objectMapper));
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactiveAuditorAware<String> auditorAware() {
        return () -> ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    var userLogin = UNKNOWN_USER;
                    if (securityContext.getAuthentication() != null
                            && securityContext.getAuthentication().isAuthenticated()) {
                        userLogin = securityContext.getAuthentication().getName();
                    }

                    return userLogin;
                })
                .defaultIfEmpty(UNKNOWN_USER);
    }
}
