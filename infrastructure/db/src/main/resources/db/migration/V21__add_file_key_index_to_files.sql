-- 학생회비 납부 요청은 인증 이미지를 업로드 URL 발급 때 받은 file_key로 가리킨다(WHERE file_key = ?).
-- file_key가 TEXT라 prefix 길이를 지정한다. 키는 files/{uuid}.{확장자} 형태라 255자 안에 모두 들어간다.
CREATE INDEX idx_files_file_key ON files (file_key(255));
