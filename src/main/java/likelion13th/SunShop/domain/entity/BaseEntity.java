package likelion13th.SunShop.domain.entity;

//공통 필드 (생성일, 수정일)


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false) // 수정시 관여 X
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false) // 삽입시 관여 X
    private LocalDateTime updatedAt;
}