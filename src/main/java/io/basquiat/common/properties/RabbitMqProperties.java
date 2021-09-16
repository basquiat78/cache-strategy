package io.basquiat.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * application.yml로부터 rabbitmq 프로퍼티 속성을 객체로 변환한다.
 * created by basquiat
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMqProperties {

    /** application.yml rabbitmq properties */
    private String host;
    private int port;
    private String virtualHost;
    private String userId;
    private String password;
    private String exchange;
    /** cache queue name */
    private String cache;

}