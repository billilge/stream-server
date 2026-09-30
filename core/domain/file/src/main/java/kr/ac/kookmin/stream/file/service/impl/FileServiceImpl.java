package kr.ac.kookmin.stream.file.service.impl;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.file.client.FileStorageClient;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.file.domain.FileInfo;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueCommand;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueResult;
import kr.ac.kookmin.stream.file.domain.FileUrl;
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

    @Override
    public Optional<FileUrl> resolveUrl(Long fileId) {
        return fileRepository.findById(fileId)
            .map(file -> fileStorageClient.publicUrl(file.getFileKey()));
    }

    @Override
    public Map<Long, FileUrl> resolveUrls(List<Long> fileIds) {
        if (fileIds.isEmpty()) {
            return Map.of();
        }

        List<File> files = fileRepository.findAllByIdIn(fileIds);
        Map<String, FileUrl> urlsByKey = fileStorageClient.publicUrls(files.stream().map(File::getFileKey).toList());

        Map<Long, FileUrl> result = new LinkedHashMap<>();
        for (File file : files) {
            result.put(file.getId(), urlsByKey.get(file.getFileKey()));
        }
        return result;
    }

    @Override
    public Optional<FileInfo> resolveInfo(Long fileId) {
        return fileRepository.findById(fileId)
            .map(file -> new FileInfo(file.getOriginalName(), fileStorageClient.publicUrl(file.getFileKey())));
    }

    @Override
    public Map<Long, FileInfo> resolveInfos(List<Long> fileIds) {
        if (fileIds.isEmpty()) {
            return Map.of();
        }

        List<File> files = fileRepository.findAllByIdIn(fileIds);
        Map<String, FileUrl> urlsByKey = fileStorageClient.publicUrls(files.stream().map(File::getFileKey).toList());

        Map<Long, FileInfo> result = new LinkedHashMap<>();
        for (File file : files) {
            result.put(file.getId(), new FileInfo(file.getOriginalName(), urlsByKey.get(file.getFileKey())));
        }
        return result;
    }

    @Override
    public FileUrl publicUrl(String fileKey) {
        return fileStorageClient.publicUrl(fileKey);
    }

    @Override
    public Map<String, FileUrl> publicUrls(List<String> fileKeys) {
        return fileStorageClient.publicUrls(fileKeys);
    }

    private String generateFileKey(String originalName) {
        String extension = FileUploadPolicy.extractExtension(originalName);
        String key = UUID.randomUUID().toString();
        return extension.isEmpty() ? FILE_KEY_PREFIX + key : FILE_KEY_PREFIX + key + "." + extension;
    }
}
