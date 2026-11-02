-- 학생 앱 홈 화면의 배너와 신청 섹션 노출 설정.
--
-- 관리자 쓰기 API는 후속 이슈라, 당분간 두 테이블 모두 DB에 직접 넣어 확인한다
-- (locker_section_layouts를 V15에서 만들 때와 같은 상황이다).

-- 배너. 노출 기간 안의 배너를 display_order 순으로 보여준다.
--
-- 랜딩은 notifications.target_id(V19)와 같은 방식이다 — 무엇의 ID인지는 landing_type이 정한다.
-- 외부 링크만 ID가 아니라 주소라서 link_url을 따로 둔다. 타입과 값의 짝은 DB가 아니라 도메인에서 지킨다.
CREATE TABLE home_banners (
    home_banner_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,                -- 줄바꿈(\n)을 포함할 수 있다
    subtitle VARCHAR(100) NULL,
    image_file_id BIGINT NOT NULL,              -- files.file_id. 공개 URL은 웹 계층에서 만든다
    landing_type VARCHAR(20) NOT NULL,          -- NOTICE / EVENT / EXTERNAL_LINK
    target_id BIGINT NULL,                      -- NOTICE면 notices.notice_id, EVENT면 events.event_id
    link_url VARCHAR(2048) NULL,                -- EXTERNAL_LINK일 때만 채운다
    display_order INT NOT NULL,
    exposure_start_at DATETIME NOT NULL,
    exposure_end_at DATETIME NOT NULL,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 신청 섹션의 노출 여부와 순서만 저장한다. 후보 자체는 진행 중인 행사·사물함 회차에서 파생되므로
-- 여기에 제목·기간 같은 값을 복사해두지 않는다.
--
-- 노출 기간을 따로 두지 않는 이유는 후보가 사라지면(마감·미게시) 자동으로 빠지기 때문이다.
CREATE TABLE home_apply_sections (
    home_apply_section_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_type VARCHAR(20) NOT NULL,           -- EVENT / LOCKER
    target_id BIGINT NOT NULL,                  -- EVENT면 events.event_id, LOCKER면 locker_periods.locker_period_id
    display_order INT NOT NULL,
    is_exposed TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 같은 대상을 두 번 설정할 수 없다. 삭제가 없는 테이블이라 활성 행 구분 없이 그대로 건다.
CREATE UNIQUE INDEX uk_home_apply_sections_target ON home_apply_sections (target_type, target_id);
