package kr.ac.kookmin.stream.common.event;

/**
 * 다른 도메인이 반응할 수 있는 도메인의 사실. 아웃박스에 기록되어 릴레이가 다시 발행한다.
 * <p>
 * 아웃박스에 JSON으로 저장되므로 ID와 원시값만 담는 record로 만든다. 클래스 이름이 역직렬화 키라서,
 * 이름이나 패키지를 바꿀 때는 아직 발행되지 않은 아웃박스 행이 없는지 확인한다.
 */
public interface DomainEvent {
}
