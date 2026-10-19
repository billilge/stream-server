package kr.ac.kookmin.stream.common.event;

/** 운영진이 새 공지를 등록했다. 공지는 저장하는 즉시 공개된다. */
public record NoticeCreated(Long noticeId) implements DomainEvent {}
