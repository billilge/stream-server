package kr.ac.kookmin.stream.file.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.file.client.FileStorageClient;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueCommand;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueResult;
import kr.ac.kookmin.stream.file.domain.UploadUrl;
import kr.ac.kookmin.stream.file.repository.FileRepository;
import kr.ac.kookmin.stream.file.service.FileService;
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

        File file = File.create(
            fileKey,
            command.category(),
            command.originalName(),
            command.fileSize(),
            command.contentType(),
            command.uploaderId()
        );
        File saved = fileRepository.save(file);

        return FileUploadUrlIssueResult.of(saved, uploadUrl);
    }

    @Override
    @Transactional
    public void delete(Long fileId) {
        File file = fileRepository.findById(fileId)
            .orElseThrow(() -> new BusinessException(FileErrorCode.FILE_NOT_FOUND));
        fileRepository.deleteById(fileId);
        fileStorageClient.deleteObject(file.getFileKey());
    }

    @Override
    public Optional<File> findById(Long fileId) {
        return fileRepository.findById(fileId);
    }

    @Override
    public Map<Long, File> findAllByIdIn(List<Long> fileIds) {
        if (fileIds.isEmpty()) {
            return Map.of();
        }
        return fileRepository.findAllByIdIn(fileIds).stream()
            .collect(Collectors.toMap(File::getId, Function.identity()));
    }

    private String generateFileKey(String originalName) {
        String extension = FileUploadPolicy.extractExtension(originalName);
        String key = UUID.randomUUID().toString();
        return extension.isEmpty() ? FILE_KEY_PREFIX + key : FILE_KEY_PREFIX + key + "." + extension;
    }
}
