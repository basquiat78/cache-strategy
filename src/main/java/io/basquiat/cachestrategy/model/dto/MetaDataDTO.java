package io.basquiat.cachestrategy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MetaData DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaDataDTO {

    private Long id;
    private String metaCode;

}
