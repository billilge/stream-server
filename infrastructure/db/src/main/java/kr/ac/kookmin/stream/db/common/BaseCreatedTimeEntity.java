package kr.ac.kookmin.stream.db.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseCreatedTimeEntity {

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
