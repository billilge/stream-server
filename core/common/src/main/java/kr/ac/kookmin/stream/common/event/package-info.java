/**
 * 크로스 도메인 이벤트 타입. 발행 도메인과 구독 도메인이 서로를 모른 채 이 타입만 공유한다.
 * Modulith는 모듈의 하위 패키지를 비공개로 보므로 named interface로 열어 둔다.
 */
@NamedInterface("event")
package kr.ac.kookmin.stream.common.event;

import org.springframework.modulith.NamedInterface;
