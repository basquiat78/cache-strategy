# cache-strategy
캐쉬 전략

# 얼마전 같이 일했던 동료가...

문득 연락해서는 이런 경우 어떻게 할 수 있을까라고 물어보길래 그 고민에 대한 전략을 한번 풀어보고자 한다.     
사실 이 친구의 고민은 얼마든지 인터넷에서 검색해서 해결할 수 있는 문제이지만 나에게 도움을 청할 정도로 내부 개발팀의 '어떤 사정'이 있었던든 싶다.

## Requirement

내가 그 친구에게 먼저 물어본 것은 현재 개발팀의 환경이었다.

다음과 같이 추릴 수 있다.

1. Redis
  - 세션 저장용
2. RabbitMQ
  - 메세지 브로커
3. MySql
  - 일반적으로 많이 사용하는 RDBMS

CI/CD와 툴들을 사용하고 있는 환경이지만 여기서는 그 친구의 고민에 대한 해결책에 필요한 저 3개만을 다루고자 한다.

## 상황

백오피스가 있고 코어 어플리케이션이 존재한다. 이중 코어 어플리케이션은 메타 데이터를 서버가 실행될 때 DB로부터 조회를 하고 코드 레벨에서 캐쉬로 사용하고 있다.

이 메타 데이터는 백오피스에서 수정 또는 추가가 가능한 상태이다.

### 문제 1

백 오피스에서 메타데이터가 추가/생성이 되더라도 어플리케이션에서는 알 수 없고 서버를 올리고 내릴때만 다시 DB로부터 조회를 해서 변경된 정보를 캐쉬로 사용하는 것이 문제.

나의 대답은 이것이였다.

"그럼 RabbitMQ를 사용하고 있으니 백 오피스에서 코드 변경이 생기면 큐를 통해 코어로 메세지를 보내고 코어에서 리스너를 통해 그 메세지를 받으면 DB에서 다시 조회해서 사용하면 되겠네?"

하지만 그 다음 문제를 제기한다.

### 문제 2

"형 그런데요. 그 추가/변경된 코드가 무조건 코어쪽에서 메타데이터로 반영되면 안되거든요. 그런 경우에는 서버가 죽지 않는다면 가능한데 이게 서버가 내려갔다 다시 올라가면 DB조회를 다시 해버리잖아요? 
이걸 방지할 수 있는 방법이 있을까요?"

나의 대답은 다음과 같았다.

"Redis를 사용하고 있으니 가장 쉬운 방법은 캐쉬를 사용하는 방법이다. 그렇게 되면 서버가 죽었다 살더라도 DB로부터 다시 읽지 않고 Redis의 캐쉬를 먼저 보고 코드레벨에서 캐쉬로 사용할 수 있겠다."

그랬더니 바로 오는 반문은

"그러면 Redis서버를 내렸다 올리게 되면 그 캐쉬도 다 사라지는 거 아니에요?"

다음 링크로 대답을 했다. 

[Redis Configuration](https://www.happykoo.net/@happykoo/posts/42)

## 개발 경력이 몇년인데 이 자슥아!

일단 메타 데이터에 대한 정의를 내가 모르니 스냅샷 또는 AOF - 위 링크에 따르면 그 환경에 어떻게 사용할 것인가는 내가 정할 수 있는 것이 아닌듯 -, 마스터/슬레이브, 센티널 세팅을 하는 것은 그 친구가 있는 회사의 환경과 역량이므로 여기서는 언급하지 않을 것이다.

# 코드로 증명해 보자

현재는 이 방식을 통해서 문제가 해결되었다고 한다.

사실 무언가 엄청 거창할 거 같지만 사실 코드 몇줄로 끝냈다. 

***Spring Boot와 함께라면~ 넌 모든 할 수 있기 때문에~***

작성해준 코드가 해당 회사의 코드들이 많아서 관련 내용만 정리해서 다시 돌아온다.~

그 전에 레디스 깔아보고 레디스 관련 GUI툴도 깔아보자.

윈도우의 경우에는 medis라는 넘을 썼는데 맥용으로는 좋은 툴이 하나 있어서 맥쓰시는 분들은 다음 걸 추천해 본다.

## Redis, rabbitmq, mysql 설지

```
$ brew install redis
# 설치 이후 레디스 시작
$ brew services start redis

$ brew install --cask another-redis-desktop-manager
// 설치하고 나면 아이콘 생긴다.

$ brew install rqbbitmq
# 설치 이후
$ brew services start rabbitmq

# latest version will be installed
$ brew install mysql
# 설치 이
$ brew services start mysql
```
macOS에서 설치하게 되면 터미널에 다 알려주기 때문에 그대로 하면 된다.
rabbitmq의 경우에는 자동으로 관리자 플러그인이 설치되고 접속 URL도 알려준다.
기본 ID/PW는 guest/guest 이다.

mysql 역시 설치하고 그냥 root로 사용한다.

자세한 것은 해당 공식 사이트에서 확인하면 될것이고 여기에서는 그냥 설치하는데 의의를 둔다.

## Prerequisites
OS: macOS Big Sur v11.4    
Java: OpenJDK(AdoptOpenJDK) 11.0.10    
IDE: IntelliJ IDEA 2020.3.3 (Community Edition)    
Plugin: Lombok    
Database: mySql v.8.0.26    
Redis: v6.2.5    
RabbitMQ: v3.9.5    
Spring Boot: 2.3.4.RELEASE    


## STEP 1

```
CREATE TABLE `meta_data` (
  `id` int NOT NULL AUTO_INCREMENT,
  `meta_code` varchar(4) NOT NULL,
  `created_at` datetime NULL DEFAULT NULL,
  `updated_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_meta_code` (`meta_code`) USING BTREE
)
```
테스트 데이터는 알아서....

기존의 코드는 다음과 같이 구성되어 있었다.

MetaDataCacheConfiguration.java

```
package io.basquiat.common.configuration;

import io.basquiat.cachestrategy.model.dto.MetaDataDTO;
import io.basquiat.cachestrategy.service.MetaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

import static io.basquiat.cachestrategy.model.MetaDataCache.initMetaDataCache;

/**
 * application이 올라올 때 캐시성의 메타데이터를 메모리에 올리기 위한 설정 파일
 * created by basquiat
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MetaDataCacheConfiguration {

    private final MetaDataService metaDataService;

    @PostConstruct
    public void initMetaData() {
        List<MetaDataDTO> metaDataList = metaDataService.fetchMetaDataList();
        initMetaDataCache(metaDataList);
    }

}
```

서버가 올라올때 DB로부터 정보를 가져와 ConcurrentHashMap 에 담는 코드였다.    

문제가 되는 코드는 아니지만 DB에 값 변경이 발생하면 알수 없수 서버를 내렸다 다시 올릴때 변경된 값이 그대로 반영이 된다.    

***디비에 값이 변경되더라도 기존 정책 정보는 유지해야 하기 때문에 (이벤트 관련 등등등) 이렇게 되면 변경된 코드값이 어플리케이션에 그대로 반영되기 때문에 문제가 된다는 것이 동생의 요구사항중 하나***

Redis관련 환경 설정은 다음 RedisConfiguration을 참조한다. 인터넷에서 흔하게 볼 수 있는 코드이다.

```
package io.basquiat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CacheStrategyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheStrategyApplication.class, args);
	}

}
```

main 클래스에 캐쉬를 사용하겠다는 어노테이션을 하나 달아준다. 쏘 심플~     

그리고 다음과 같이 기존 코드를 수정한다.

```
package io.basquiat.cachestrategy.service;

import io.basquiat.cachestrategy.model.dto.MetaDataDTO;
import io.basquiat.cachestrategy.repository.MetaDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Application에 필요한 메타데이터 정보를 가져온다.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaDataService {

    private final MetaDataRepository metaDataRepository;

    /**
     * meta data 정보를 반환한다.
     * @return List<MetaData>
     */
    @Cacheable(value = "MetaDataCache")
    public List<MetaDataDTO> fetchMetaDataList() {
        return metaDataRepository.findAll()
                                 .stream()
                                 .map(entity -> MetaDataDTO.builder()
                                                           .id(entity.getId())
                                                           .metaCode(entity.getMetaCode())
                                                           .build())
                                 .collect(toList());
    }

}
```

기존 메소드에 @Cacheable(value = "MetaDataCache”)를 통해 어떤 키값으로 Redis에 저장할지 어노테이션을 하나 달아준다.     

이렇게 하고 일단 서버를 올렸다 내리면 처음에는 레디스에는 캐쉬가 없기 때문에 DB를 통해 정보를 가져와서 레디스에 저장한다.     

어? 그런데 레디스에 데이터를 담지 못한다?     

그래서 서버를 내리고 올릴때마다 DB를 조회하넹???

### @PostConstruct vs @EventListener(ApplicationReadyEvent.class)

문제는 바로 저 두개의 차이이다.     

두 어노테이션의 차이가 무엇인지는 인터넷을 찾아보길 바란다.     

다만 간단하게 말하자면 레디스를 사용하기 때문에 관련 Configuration을 설정했는데 @PostConstruct는 스프링이 관련 설정들을 빈으로 등록하기 전에 실행되기 때문에 레디스가 적용되지 않았기 때문이다.     

실제로 저 두개의 어노테이션을 번갈아 가며서 서버를 실행하면 실행되는 시점이 다르다는 것을 콘솔창을 통해서 확인할 수 있다.     

따라서 @PostConstruct을 @EventListener(ApplicationReadyEvent.class)로 변경한다.      

이렇게 설정을 하면 서버를 내리고 올릴 때 처음에는 디비를 조회해서 레디스에 데이터를 담고 서버를 다시 내렸다 올리면 디비를 조회하지 않고 레디스를 통해 캐쉬 정보를 가져오는 것을 확인할 수 있다.    

## STEP 2

첫번째 제안은 이거였다.     

"기왕 cache를 사용하는거니깐 백오피스에서 코드값을 변경하고 레디스의 데이터를 변경한 후에 어플리케이션쪽으로 변경되었다는 메세지만 보내면 되지 않을까?"     

"형 그렇게 되면 백오피스와 어플리케이션 양쪽에 비슷한 코드가 들어가게 되서 그냥 백오피스에서는 변경 여부 메세지만 보내게 작업하고 싶어"     

일단 이건 개발팀 환경에 따른 상황이기 때문에 일단 관련 로직이 어플리케이션으로 집중하는 방식으로 간다.     

우선 코어 어플리케이션에서는 래빗엠큐 설정과 넘어온 메세지를 받아 처리할 리스너를 작성한다.     

관련 코드는 configuration패키지에 있으므로 참고하면 될듯 싶다. 물론 인터넷에서 흔하게 볼 수 있는 코드이다.    

```
package io.basquiat.cachestrategy.service;

import io.basquiat.cachestrategy.model.dto.MetaDataDTO;
import io.basquiat.cachestrategy.repository.MetaDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Application에 필요한 메타데이터 정보를 가져온다.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaDataService {

    private final MetaDataRepository metaDataRepository;

    /**
     * meta data 정보를 반환한다.
     * @return List<MetaData>
     */
    @Cacheable(value = "MetaDataCache")
    public List<MetaDataDTO> fetchMetaDataList() {
        return metaDataRepository.findAll()
                                 .stream()
                                 .map(entity -> MetaDataDTO.builder()
                                                           .id(entity.getId())
                                                           .metaCode(entity.getMetaCode())
                                                           .build())
                                 .collect(toList());
    }

    /**
     * 레디스 MetaDataCache정보를 지운다.
     */
    @CacheEvict(value = "MetaDataCache")
    public void cacheEvictMetaData() {
        log.info("remove redis cache: MetaDataCache");
    }

}
```

@CacheEvict라는 어노테이션을 통해서 기존의 데이터를 레디스로 부터 지울 수 있다.     

원래 처음에는 기존의 캐쉬를 전부 지우고 다시 전부 불러 와서 캐쉬를 생성하는 것보다는 변경된 부분만 변경하는 방식이었다.     

일단 가장 쉬운 방법부터 적용을 하고 그 이후 생각해 보겠다는 반응이어서 일단 이렇게 시나리오는 잡았다.     

```
(메세지 큐) -> 기존의 캐쉬를 지운다 -> 디비를 조회한다. -> 레디스 캐쉬
```

리스너를 등록하자.

```
package io.basquiat.common.listener;

import io.basquiat.cachestrategy.model.dto.MetaDataDTO;
import io.basquiat.cachestrategy.service.MetaDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.basquiat.cachestrategy.model.MetaDataCache.initMetaDataCache;

/**
 * Consume queue admin Listener
 * created by basquiat
 */
@Slf4j
public class QueueCacheListener {

    @Autowired
    private MetaDataService metaDataService;

    /**
     * cache queue listener
     * @param message
     */
    //public void queueCache(String message) {
    public void queueCache(byte[] message) {
        log.info("queue start");
        metaDataService.cacheEvictMetaData();
        List<MetaDataDTO> metaDataList = metaDataService.fetchMetaDataList();
        initMetaDataCache(metaDataList);
    }

}

```

테스트는 [RabbitMq Admin](http://localhost:15672/) 해당 콘솔에서 직접 날려서 테스트를 해본다.    

코드에서 보면 알겠지만 이렇게 직접 메세지를 publish하면 바이트로 받기 때문에 고려해서 리스너 코드를 작성했으며 여기서는 큐가 오면 실제로 작동을 하는지에 대한 테스트이기 때문에 코드는 간략하게만 작성한다.      

모든 코드는 다 올려놨기 때문에 소스를 참조하면 된다.

# At A Glance

사실 코드 자체는 별게 없다. 단지 메세지 큐를 통해서 변경 시점 이벤트를 발생시키고 레디스를 통해 캐쉬를 사용한다 정도이다.    

여러 바리에이션이 생길 수 있지만 이 예제를 통해서 충분히 그 가닥을 잡을 수 있을 거라 판단을 하며 관련 내용에 대해서 어느정도 도움이 됬으면 하는 바램이다.     





