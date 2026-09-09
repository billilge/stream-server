package kr.ac.kookmin.stream.internal.domain.file.service.impl;

import java.io.InputStream;
import java.util.UUID;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.internal.domain.file.domain.File;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileUploadUrlIssueCommand;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileUploadUrlIssueResult;
import kr.ac.kookmin.stream.internal.domain.file.domain.UploadUrl;
import kr.ac.kookmin.stream.internal.domain.file.client.FileStorageClient;
import kr.ac.kookmin.stream.internal.domain.file.repository.FileRepository;
import kr.ac.kookmin.stream.internal.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FileServiceImpl implements FileService {

    private static final String FILE_KEY_PREFIX = "files/";

    private final FileRepository fileRepository;
    private final FileStorageClient fileStorageClient;

    @Override
    @Transactional
    public FileUploadUrlIssueResult issuePresignedUrl(FileUploadUrlIssueCommand command) {
        FileUploadPolicy.validate(command.category(), command.originalName(), command.fileSize());

        String fileKey = generateFileKey(command.originalName());
        UploadUrl uploadUrl = fileStorageClient.issuePresignedUrl(fileKey, command.contentType());

        File file = File.of(
            null,
            fileKey,
            command.category(),
            command.originalName(),
            command.fileSize(),
            command.contentType(),
            command.uploaderId()
        );
        File saved = fileRepository.save(file);

        return new FileUploadUrlIssueResult(saved.getId(), uploadUrl.url(), fileKey, uploadUrl.expiresAt());
    }

    @Override
    @Transactional(readOnly = true)
    public void receiveUpload(String fileKey, InputStream content) {
        fileRepository.findByFileKey(fileKey)
            .orElseThrow(() -> new BusinessException(FileErrorCode.FILE_NOT_FOUND));
        fileStorageClient.write(fileKey, content);
    }

    @Override
    @Transactional
    public void delete(Long fileId) {
        File file = fileRepository.findById(fileId)
            .orElseThrow(() -> new BusinessException(FileErrorCode.FILE_NOT_FOUND));
        fileStorageClient.deleteObject(file.getFileKey());
        fileRepository.deleteById(fileId);
    }

    private String generateFileKey(String originalName) {
        String extension = FileUploadPolicy.extractExtension(originalName);
        String key = UUID.randomUUID().toString();
        return extension.isEmpty() ? FILE_KEY_PREFIX + key : FILE_KEY_PREFIX + key + "." + extension;
    }
}
