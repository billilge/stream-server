package kr.ac.kookmin.stream.db.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseCreatedTimeEntity;
import kr.ac.kookmin.stream.internal.domain.file.domain.File;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileCategory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileJpaEntity extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "file_key", nullable = false, columnDefinition = "TEXT")
    private String fileKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FileCategory category;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;

    private FileJpaEntity(File file) {
        this.id = file.getId();
        this.fileKey = file.getFileKey();
        this.category = file.getCategory();
        this.originalName = file.getOriginalName();
        this.fileSize = file.getFileSize();
        this.contentType = file.getContentType();
        this.uploaderId = file.getUploaderId();
    }

    public static FileJpaEntity from(File file) {
        return new FileJpaEntity(file);
    }

    public File toDomain() {
        return File.of(id, fileKey, category, originalName, fileSize, contentType, uploaderId);
    }
}
