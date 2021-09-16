package io.basquiat.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * application.yml로부터 redis 프로퍼티 속성을 객체로 변환한다.
 * created by basquiat
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    /** application.yml redis properties */
    private String host;
    private String password;
    private int port;

}