package kr.ac.kookmin.stream.outbox;

public enum OutboxStatus {
    PENDING,    // 아직 발행하지 못함. 릴레이가 다음 폴링에서 다시 발행한다
    PUBLISHED,  // 리스너까지 예외 없이 끝남
    FAILED      // 발행 실패가 한도에 닿아 더는 재시도하지 않음. 원인을 고친 뒤 PENDING으로 되돌리면 다시 발행된다
}
