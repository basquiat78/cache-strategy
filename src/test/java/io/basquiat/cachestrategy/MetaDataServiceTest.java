package io.basquiat.cachestrategy;

import io.basquiat.cachestrategy.model.dto.MetaDataDTO;
import io.basquiat.cachestrategy.service.MetaDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MetaDataServiceTest {

    @Autowired
    private MetaDataService metaDataService;

    @Test
    @DisplayName("메타데이터 정보 가져오기")
    public void findByIdTest() {
        List<MetaDataDTO> results = metaDataService.fetchMetaDataList();
        assertThat(results.size()).isEqualTo(2);
    }

}
