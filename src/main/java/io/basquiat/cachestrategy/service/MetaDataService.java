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
