-- 구역 상세 화면의 칸 배치 구조(layout)와 구역 사진을 구역별로 저장한다.
-- 서버는 layout 내용을 해석하지 않고 그대로 내려준다. 칸 상태(선택 가능·내 사물함)는 layout에 넣지 않고 조회 시점에 계산한다.
-- 관리자 등록 API가 아직 없어 행은 DB에 직접 넣는다.
CREATE TABLE locker_section_layouts (
    locker_section_layout_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    section_id BIGINT NOT NULL,                 -- locker_sections.locker_section_id
    layout JSON NOT NULL,                       -- root 블록 트리. {"type": "column", "children": [...]}
    version SMALLINT NOT NULL,                  -- layout 형식 버전. 블록 종류·필드가 바뀌면 올려 옛 앱이 모르는 형식을 알아채게 한다
    photo_file_id BIGINT NOT NULL,              -- 구역 실제 사진. files.file_id. 공개 URL은 웹 계층에서 만든다
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 구역마다 최신 형식 한 벌만 둔다. 구역 상세 조회(WHERE section_id = ?)도 이 인덱스를 탄다.
CREATE UNIQUE INDEX uk_locker_section_layouts_section_id ON locker_section_layouts (section_id);
