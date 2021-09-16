package io.basquiat.common.configuration;

import io.basquiat.cachestrategy.model.dto.MetaDataDTO;
import io.basquiat.cachestrategy.service.MetaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

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

    @EventListener(ApplicationReadyEvent.class)
    public void initMetaData() {
        List<MetaDataDTO> metaDataList = metaDataService.fetchMetaDataList();
        initMetaDataCache(metaDataList);
    }

}