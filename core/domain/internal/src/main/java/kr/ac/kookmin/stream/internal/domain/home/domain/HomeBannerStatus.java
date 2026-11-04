package kr.ac.kookmin.stream.internal.domain.home.domain;

/**
 * 배너의 노출 상태. 컬럼으로 저장하지 않고 노출 기간과 현재 시각으로 그때그때 판정한다 —
 * 시각이 지나면 저절로 바뀌는 값이라 저장해두면 갱신 사이에 틀린 값이 남는다.
 */
public enum HomeBannerStatus {

    /** 노출 시작 전 */
    SCHEDULED,
    /** 노출 기간 안 */
    EXPOSED,
    /** 노출 기간이 지남 */
    ENDED
}
