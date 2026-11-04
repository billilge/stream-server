package kr.ac.kookmin.stream.api.admin.event.locker.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * 구역의 칸 배치 구조를 바꾼다.
 * <p>
 * 배치 형식 버전은 받지 않는다. 형식 버전은 서버와 앱 사이의 약속이라 운영진이 화면에서 고를 값이 아니고,
 * 잘못 올리면 앱이 멀쩡한 배치를 모르는 형식으로 보고 그리지 않는다. 저장할 때 서버가 박는다.
 */
public record LockerSectionLayoutUpdateRequest(
    @NotNull(message = "칸 배치를 입력해 주세요.")
    @Schema(description = "root 블록 트리. 칸 번호는 구역 상세의 lockers[].lockerNumber와 같다")
    ObjectNode root
) {

    /**
     * 저장할 JSON 원문.
     * <p>
     * 블록을 들여다보지 않고 공백만 걷어낸 문자열로 바꾼다. {@code String}으로 받으면 깨진 JSON이 DB까지 내려가
     * 500이 되고, 블록 타입으로 받으면 서버가 형식을 알아야 한다. {@code ObjectNode}는 Jackson이 파싱해
     * 유효한 JSON 객체임만 보장하고 내용은 모른 채 넘긴다.
     */
    public String layout() {
        return root.toString();
    }

    /**
     * 빈 객체를 거른다. 편집기를 비우고 저장하면 {@code {}}가 올라오는데 {@code @NotNull}은 이를 막지 못하고
     * ({@code @NotEmpty}는 {@code ObjectNode}를 지원하지 않는다), 저장되면 앱이 아무것도 그리지 않는다.
     * 배치 화면에는 배치를 지우는 수단이 없어 빈 배치는 운영진의 선택이 아니다.
     * <p>
     * 필드가 하나라도 있는지만 보므로 블록 형식은 알지 못한다. 그래서 {@code children}이 빈 블록처럼
     * 비어 있지 않으면서 아무것도 그리지 않는 배치는 걸러내지 못한다.
     */
    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "칸 배치가 비어 있습니다.")
    public boolean isRootNotEmpty() {
        // null은 @NotNull이 따로 알린다. 여기서 false를 내면 메시지가 둘 붙는다
        return root == null || !root.isEmpty();
    }
}
