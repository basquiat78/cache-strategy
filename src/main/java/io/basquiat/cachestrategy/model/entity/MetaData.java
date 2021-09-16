package io.basquiat.cachestrategy.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * meta data entity
 */
@Data
@Entity
@Table(name = "meta_data", catalog = "basquiat")
public class MetaData {

    /** unique id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 메타 코드 */
    @Column(name = "meta_code", length = 4)
    private String metaCode;

    /** 등록일 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** 변경일 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
