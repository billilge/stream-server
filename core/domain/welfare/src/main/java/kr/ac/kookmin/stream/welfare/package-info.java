/**
 * 계층 패키지(domain/{도메인}/{domain|repository|service})를 그대로 공개하기 위해 OPEN으로 둔다.
 * service.impl 접근 차단은 bootstrap의 DomainImplAccessTests(ArchUnit)가 담당한다.
 */
@ApplicationModule(type = ApplicationModule.Type.OPEN)
package kr.ac.kookmin.stream.welfare;

import org.springframework.modulith.ApplicationModule;
