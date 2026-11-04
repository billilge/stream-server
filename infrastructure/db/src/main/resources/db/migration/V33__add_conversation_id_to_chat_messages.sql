-- 챗봇 이력을 회원 단위가 아니라 대화(채팅방 입장 한 번) 단위로 끊는다.
-- 앱은 채팅방에 들어올 때마다 새 대화로 보여주는데, 회원 기준으로 최근 10개를 붙이면 화면은 비어 있어도 봇이 어제 대화를 기억한다.
--
-- conversation_id: 프론트가 입장할 때 만든 UUID(36자). 기록은 지우지 않고 이력을 붙일 때만 이 값으로 거른다.
--   기존 행이 있을 수 있어 DEFAULT로 채운 뒤 DEFAULT를 제거한다(flyway-migration.md 3-3절).
--   기존 행은 빈 문자열이 되어 어느 대화의 이력에도 붙지 않는다.
ALTER TABLE chat_messages
    ADD COLUMN conversation_id CHAR(36) NOT NULL DEFAULT '' AFTER member_id;

ALTER TABLE chat_messages
    ALTER COLUMN conversation_id DROP DEFAULT;

-- 이력은 WHERE member_id = ? AND conversation_id = ? ORDER BY message_id DESC 로 조회한다.
-- member_id를 같이 거는 이유는 다른 회원의 conversationId를 보내도 그 대화가 붙지 않게 하기 위해서다.
-- InnoDB가 세컨더리 인덱스 끝에 PK를 붙이므로 message_id 정렬까지 인덱스로 커버된다.
-- 기존 idx_chat_messages_member_id는 새 인덱스의 좌측 prefix라 완전히 포함되므로 제거한다.
DROP INDEX idx_chat_messages_member_id ON chat_messages;

CREATE INDEX idx_chat_messages_member_id_conversation_id
    ON chat_messages (member_id, conversation_id);
