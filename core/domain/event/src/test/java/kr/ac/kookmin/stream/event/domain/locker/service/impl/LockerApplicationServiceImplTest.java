package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplyCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerStatus;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerApplicationRepository;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 사물함 신청 규칙을 확인한다. 동시 신청을 막는 유니크 제약은 DB가 걸기 때문에, 여기서는 저장소가 그 위반을
 * {@code LOCKER_ALREADY_ASSIGNED}로 알렸을 때 서비스가 그대로 전파하는지만 본다.
 */
class LockerApplicationServiceImplTest {

    private static final Long PERIOD_ID = 1L;
    private static final Long MEMBER_ID = 100L;
    private static final Long LOCKER_ID = 11L;

    private static LockerPeriod period() {
        return LockerPeriod.of(
            PERIOD_ID,
            "2026-2학기",
            LocalDateTime.of(2026, 8, 20, 10, 0),
            LocalDateTime.of(2026, 8, 25, 18, 0),
            LocalDate.of(2026, 9, 1),
            LocalDate.of(2026, 12, 15),
            true
        );
    }

    private static Locker locker(LockerStatus status) {
        return Locker.of(LOCKER_ID, 1L, "B-25", 25, 1, 1, status);
    }

    private static LockerApplicationServiceImpl service(FakeLockerRepository repository) {
        return new LockerApplicationServiceImpl(repository, repository.applications);
    }

    private static LockerApplyCommand command() {
        return new LockerApplyCommand(PERIOD_ID, LOCKER_ID);
    }

    @Nested
    @DisplayName("신청 성공")
    class Success {

        @Test
        @DisplayName("요청한 회차·회원·사물함으로 신청 시각을 채워 저장하고, 배정된 사물함과 회차를 돌려준다")
        void savesAndReturnsAssignment() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withPeriod(period())
                .withLocker(locker(LockerStatus.AVAILABLE));

            LocalDateTime before = LocalDateTime.now();
            LockerApplicationResult result = service(repository).apply(MEMBER_ID, command());
            LocalDateTime after = LocalDateTime.now();

            LockerApplication saved = repository.applications.saved.getFirst();
            assertNull(saved.getId());   // 식별자는 저장소가 부여한다
            assertEquals(PERIOD_ID, saved.getLockerPeriodId());
            assertEquals(MEMBER_ID, saved.getMemberId());
            assertEquals(LOCKER_ID, saved.getLockerId());
            assertNotNull(saved.getAppliedAt());
            assertFalse(saved.getAppliedAt().isBefore(before));
            assertFalse(saved.getAppliedAt().isAfter(after));

            assertEquals(FakeLockerApplicationRepository.SAVED_ID, result.application().getId());
            assertSame(repository.locker, result.locker());
            assertSame(repository.period, result.period());
        }

        @Test
        @DisplayName("다른 회차에 신청된 사물함은 이 회차에서 신청할 수 있다")
        void appliedInOtherPeriod() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withPeriod(period())
                .withLocker(locker(LockerStatus.AVAILABLE))
                .withAppliedLocker(PERIOD_ID + 1, LOCKER_ID);

            service(repository).apply(MEMBER_ID, command());

            assertEquals(1, repository.applications.saved.size());
        }
    }

    @Nested
    @DisplayName("신청 거절")
    class Rejected {

        @Test
        @DisplayName("게시되지 않은(없는) 회차면 LOCKER_PERIOD_NOT_FOUND로 막고 저장하지 않는다")
        void unpublishedPeriod() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withLocker(locker(LockerStatus.AVAILABLE));

            assertErrorCode(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND, repository);
            assertEquals(0, repository.applications.saved.size());
        }

        @Test
        @DisplayName("이 회차에 이미 신청된 사물함이면 LOCKER_ALREADY_ASSIGNED로 막고 저장하지 않는다")
        void alreadyApplied() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withPeriod(period())
                .withLocker(locker(LockerStatus.AVAILABLE))
                .withAppliedLocker(PERIOD_ID, LOCKER_ID);

            assertErrorCode(LockerErrorCode.LOCKER_ALREADY_ASSIGNED, repository);
            assertEquals(0, repository.applications.saved.size());
        }

        @Test
        @DisplayName("사용 중지된 사물함도 선택할 수 없는 사물함이라 LOCKER_ALREADY_ASSIGNED로 막는다")
        void disabledLocker() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withPeriod(period())
                .withLocker(locker(LockerStatus.DISABLED));

            assertErrorCode(LockerErrorCode.LOCKER_ALREADY_ASSIGNED, repository);
            assertEquals(0, repository.applications.saved.size());
        }

        @Test
        @DisplayName("없는 사물함은 정상 흐름에서 들어올 수 없어 전용 에러 코드 없이 끊는다")
        void missingLocker() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withPeriod(period());

            assertThrows(
                IllegalArgumentException.class,
                () -> service(repository).apply(MEMBER_ID, command())
            );
            assertEquals(0, repository.applications.saved.size());
        }

        @Test
        @DisplayName("사전 검사를 통과했어도 저장 시 먼저 들어온 신청에 밀리면 LOCKER_ALREADY_ASSIGNED가 그대로 나간다")
        void lostConcurrentApplication() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withPeriod(period())
                .withLocker(locker(LockerStatus.AVAILABLE))
                .withLosingSave();

            assertErrorCode(LockerErrorCode.LOCKER_ALREADY_ASSIGNED, repository);
        }

        private void assertErrorCode(LockerErrorCode expected, FakeLockerRepository repository) {
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service(repository).apply(MEMBER_ID, command())
            );
            assertEquals(expected, exception.getErrorCode());
        }
    }

    /**
     * 신청에 쓰는 회차·사물함 조회만 답하는 가짜 레포지토리.
     * <p>
     * 신청 저장·조회는 {@link LockerApplicationRepository}로 나뉘어 있지만, 한 번에 준비할 수 있도록 그 가짜를 함께 들고 있다.
     */
    private static final class FakeLockerRepository implements LockerRepository {

        private final FakeLockerApplicationRepository applications = new FakeLockerApplicationRepository();
        private LockerPeriod period;
        private Locker locker;

        FakeLockerRepository withPeriod(LockerPeriod value) {
            this.period = value;
            return this;
        }

        FakeLockerRepository withLocker(Locker value) {
            this.locker = value;
            return this;
        }

        FakeLockerRepository withAppliedLocker(Long lockerPeriodId, Long lockerId) {
            applications.appliedPeriodLockerIds.add(List.of(lockerPeriodId, lockerId));
            return this;
        }

        /** 사전 검사와 저장 사이에 다른 신청이 먼저 커밋되어 유니크 제약에 걸린 상황. */
        FakeLockerRepository withLosingSave() {
            applications.losingSave = true;
            return this;
        }

        @Override
        public Optional<LockerPeriod> findPublishedPeriodById(Long lockerPeriodId) {
            return Optional.ofNullable(period).filter(value -> value.getId().equals(lockerPeriodId));
        }

        @Override
        public Optional<Locker> findLockerById(Long lockerId) {
            return Optional.ofNullable(locker).filter(value -> value.getId().equals(lockerId));
        }

        // 아래는 구역·배치 조회(LockerService)용 메서드라 이 테스트에서는 쓰지 않는다

        @Override
        public boolean existsPublishedPeriod(Long lockerPeriodId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existsSection(Long sectionId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<LockerSection> findAllSections() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Locker> findAllLockers() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Locker> findLockersBySectionId(Long sectionId) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class FakeLockerApplicationRepository implements LockerApplicationRepository {

        static final Long SAVED_ID = 25L;

        private final Set<List<Long>> appliedPeriodLockerIds = new HashSet<>();
        private boolean losingSave;
        private final List<LockerApplication> saved = new ArrayList<>();

        @Override
        public boolean existsByLocker(Long lockerPeriodId, Long lockerId) {
            return appliedPeriodLockerIds.contains(List.of(lockerPeriodId, lockerId));
        }

        @Override
        public LockerApplication save(LockerApplication application) {
            if (losingSave) {
                throw new BusinessException(LockerErrorCode.LOCKER_ALREADY_ASSIGNED);
            }
            saved.add(application);
            return LockerApplication.of(
                SAVED_ID,
                application.getLockerPeriodId(),
                application.getMemberId(),
                application.getLockerId(),
                application.getAppliedAt()
            );
        }

        // 아래는 구역·배치 조회(LockerService)용 메서드라 이 테스트에서는 쓰지 않는다

        @Override
        public Set<Long> findAppliedLockerIds(Long lockerPeriodId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Long> findAppliedLockerId(Long lockerPeriodId, Long memberId) {
            throw new UnsupportedOperationException();
        }
    }
}
