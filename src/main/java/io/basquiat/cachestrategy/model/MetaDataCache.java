package io.basquiat.cachestrategy.model;

import io.basquiat.cachestrategy.model.dto.MetaDataDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MetaDataDTO 정보를 메모리에 올린다.
 * created by basquiat
 */
@Slf4j
public class MetaDataCache {

    private static final Map<String, MetaDataDTO> META_DATA_CACHE = new ConcurrentHashMap<>();

    /**
     * init META_DATA_CACHE
     * @param MetaDataDTOs
     */
    public static void initMetaDataCache(List<MetaDataDTO> MetaDataDTOs) {
        META_DATA_CACHE.clear();
        Map<String, MetaDataDTO> initMap = MetaDataDTOs.stream()
                                                       .collect(Collectors.toMap(MetaDataDTO::getMetaCode, Function.identity()));
        META_DATA_CACHE.putAll(initMap);
    }

    /**
     * cache map으로부터 MetaDataDTO 리스트를 가져온다.
     * @return Collection<MetaDataDTO>
     */
    public static Collection<MetaDataDTO> fetchCMetaDataList() {
        return META_DATA_CACHE.values();
    }

    /**
     * metaCode에 해당하는 metaData 객체를 가져온다.
     * @param metaCode
     * @return MetaDataDTO
     */
    public static MetaDataDTO fetchMetaDataByCode(String metaCode) {
        return META_DATA_CACHE.get(metaCode);
    }

}
