package io.basquiat.common.listener;

import io.basquiat.cachestrategy.model.dto.MetaDataDTO;
import io.basquiat.cachestrategy.service.MetaDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.basquiat.cachestrategy.model.MetaDataCache.initMetaDataCache;

/**
 * Consume queue cache Listener
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
    public void queueCache(byte[] message) {
        log.info("queue start");
        metaDataService.cacheEvictMetaData();
        List<MetaDataDTO> metaDataList = metaDataService.fetchMetaDataList();
        initMetaDataCache(metaDataList);
    }

}
