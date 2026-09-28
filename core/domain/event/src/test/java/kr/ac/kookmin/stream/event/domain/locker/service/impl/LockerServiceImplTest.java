package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.SectionAvailabilityStatus;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerApplicationRepository;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 구역·사물함·신청을 각각 조회해 서비스가 구역별로 묶어 센다. 그 집계가 어긋나지 않는지 확인한다.
 * <p>
 * 선택 가능 여부와 내 사물함 표시는 표현 계층이 조회 결과를 맞춰봐서 만든다. 판정 규칙 자체는
 * {@code LockerTest}가 덮으므로 여기서는 조회가 무엇을 돌려주는지만 본다.
 */
class LockerServiceImplTest {

    private static final Long PERIOD_ID = 1L;
    private static final Long MEMBER_ID = 100L;

    private static LockerSection section(Long id, String label) {
        return LockerSection.of(id, label);
    }

    private static Locker locker(Long id, Long sectionId, LockerStatus status) {
        return Locker.of(id, sectionId, "A-" + id, id.intValue(), 1, id.intValue(), status);
    }

    private static LockerServiceImpl service(FakeLockerRepository repository) {
        return new LockerServiceImpl(repository, repository.applications);
    }

    private static Locker usable(Long id, Long sectionId) {
        return locker(id, sectionId, LockerStatus.AVAILABLE);
    }

    @Nested
    @DisplayName("구역 목록")
    class GetSections {

        @Test
        @DisplayName("구역마다 자기 사물함만 세고, 사용 중지·신청된 것은 선택 가능 수에서 빠진다")
        void countsPerSection() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withSections(section(1L, "A-1"), section(2L, "A-2"))
                .withAllLockers(
                    usable(11L, 1L),
                    locker(12L, 1L, LockerStatus.DISABLED),
                    usable(13L, 1L),
                    usable(21L, 2L))
                .withAppliedLockerIds(13L);

            List<LockerSectionSummary> sections = service(repository).getSections(PERIOD_ID);

            assertEquals(List.of(1L, 2L), sections.stream().map(LockerSectionSummary::sectionId).toList());
            assertEquals(3, sections.get(0).totalCount());
            assertEquals(1, sections.get(0).availableCount());   // 12는 사용 중지, 13은 신청됨
            assertEquals(1, sections.get(1).totalCount());
            assertEquals(1, sections.get(1).availableCount());
        }

        @Test
        @DisplayName("사물함이 한 건도 없는 구역도 0으로 채워 목록에서 빠지지 않는다")
        void keepsSectionsWithoutLockers() {
            // 구역별로 묶으면 사물함이 없는 2번 구역은 키 자체가 생기지 않는다
            FakeLockerRepository repository = new FakeLockerRepository()
                .withSections(section(1L, "A-1"), section(2L, "A-2"))
                .withAllLockers(usable(11L, 1L));

            List<LockerSectionSummary> sections = service(repository).getSections(PERIOD_ID);

            assertEquals(2, sections.size());
            assertEquals(0, sections.get(1).totalCount());
            assertEquals(SectionAvailabilityStatus.FULL, sections.get(1).availabilityStatus());
        }

        @Test
        @DisplayName("게시되지 않은 회차는 찾을 수 없다")
        void unpublishedPeriod() {
            FakeLockerRepository repository = new FakeLockerRepository().withUnpublishedPeriod();

            BusinessException e = assertThrows(BusinessException.class,
                () -> service(repository).getSections(PERIOD_ID));

            assertEquals(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND, e.getErrorCode());
        }
    }

    @Nested
    @DisplayName("구역 상세")
    class GetSectionLockers {

        @Test
        @DisplayName("구역의 사물함을 레포지토리가 준 순서대로 돌려준다")
        void returnsSectionLockers() {
            // 선택 가능 판정은 담지 않는다. 판정 자체는 LockerTest가 덮는다
            FakeLockerRepository repository = new FakeLockerRepository()
                .withSectionLockers(
                    usable(11L, 1L),
                    locker(12L, 1L, LockerStatus.DISABLED),
                    usable(13L, 1L));

            List<Locker> lockers = service(repository).getSectionLockers(PERIOD_ID, 1L);

            assertEquals(List.of(11L, 12L, 13L), lockers.stream().map(Locker::getId).toList());
        }

        @Test
        @DisplayName("게시되지 않은 회차는 찾을 수 없다")
        void unpublishedPeriod() {
            FakeLockerRepository repository = new FakeLockerRepository().withUnpublishedPeriod();

            BusinessException e = assertThrows(BusinessException.class,
                () -> service(repository).getSectionLockers(PERIOD_ID, 1L));

            assertEquals(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND, e.getErrorCode());
        }

        @Test
        @DisplayName("없는 구역은 찾을 수 없다")
        void unknownSection() {
            FakeLockerRepository repository = new FakeLockerRepository().withMissingSection();

            BusinessException e = assertThrows(BusinessException.class,
                () -> service(repository).getSectionLockers(PERIOD_ID, 99L));

            assertEquals(LockerErrorCode.LOCKER_SECTION_NOT_FOUND, e.getErrorCode());
        }
    }

    @Nested
    @DisplayName("회원이 신청한 사물함")
    class GetLockerByMemberId {

        @Test
        @DisplayName("신청한 사물함을 구역까지 담아 돌려준다")
        void returnsAppliedLocker() {
            Locker mine = usable(21L, 2L);
            FakeLockerRepository repository = new FakeLockerRepository()
                .withAllLockers(usable(11L, 1L), mine)
                .withMyLocker(mine);

            Optional<Locker> found = service(repository).getLockerByMemberId(PERIOD_ID, MEMBER_ID);

            assertTrue(found.isPresent());
            assertEquals(21L, found.get().getId());
            assertEquals(2L, found.get().getSectionId());
        }

        @Test
        @DisplayName("신청하지 않았으면 비어 있다")
        void emptyWhenNotApplied() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withAllLockers(usable(11L, 1L));

            assertTrue(service(repository).getLockerByMemberId(PERIOD_ID, MEMBER_ID).isEmpty());
        }

        @Test
        @DisplayName("신청한 사물함이 삭제됐으면 비어 있다")
        void emptyWhenLockerRemoved() {
            // 신청 행은 남아 있는데 사물함이 삭제된 경우. 사물함 조회가 삭제된 건을 거르므로 여기서도 없는 것이 된다
            FakeLockerRepository repository = new FakeLockerRepository()
                .withAllLockers(usable(11L, 1L))
                .withMyLockerId(99L);

            assertTrue(service(repository).getLockerByMemberId(PERIOD_ID, MEMBER_ID).isEmpty());
        }
    }

    /**
     * 조회 결과만 답하고 집계는 서비스가 하는지 보기 위한 가짜 레포지토리.
     * <p>
     * 신청 조회는 {@link LockerApplicationRepository}로 나뉘어 있지만, 한 번에 준비할 수 있도록 그 가짜를 함께 들고 있다.
     */
    private static final class FakeLockerRepository implements LockerRepository {

        private final FakeLockerApplicationRepository applications = new FakeLockerApplicationRepository();

        private boolean publishedPeriod = true;
        private boolean sectionExists = true;
        private List<LockerSection> sections = List.of();
        private List<Locker> allLockers = List.of();
        private List<Locker> sectionLockers = List.of();

        FakeLockerRepository withSections(LockerSection... values) {
            this.sections = List.of(values);
            return this;
        }

        FakeLockerRepository withAllLockers(Locker... values) {
            this.allLockers = List.of(values);
            return this;
        }

        FakeLockerRepository withSectionLockers(Locker... values) {
            this.sectionLockers = List.of(values);
            return this;
        }

        FakeLockerRepository withAppliedLockerIds(Long... values) {
            applications.appliedLockerIds = Set.of(values);
            return this;
        }

        FakeLockerRepository withMyLocker(Locker value) {
            return withMyLockerId(value.getId());
        }

        /** 사물함 목록에 없는 식별자를 넣으면 신청 행만 남고 사물함은 삭제된 상황이 된다. */
        FakeLockerRepository withMyLockerId(Long value) {
            applications.myLockerId = value;
            return this;
        }

        FakeLockerRepository withUnpublishedPeriod() {
            this.publishedPeriod = false;
            return this;
        }

        FakeLockerRepository withMissingSection() {
            this.sectionExists = false;
            return this;
        }

        @Override
        public boolean existsPublishedPeriod(Long lockerPeriodId) {
            return publishedPeriod;
        }

        @Override
        public boolean existsSection(Long sectionId) {
            return sectionExists;
        }

        @Override
        public List<LockerSection> findAllSections() {
            return sections;
        }

        @Override
        public List<Locker> findAllLockers() {
            return allLockers;
        }

        @Override
        public List<Locker> findLockersBySectionId(Long sectionId) {
            return sectionLockers;
        }

        @Override
        public Optional<Locker> findLockerById(Long lockerId) {
            return Stream.concat(allLockers.stream(), sectionLockers.stream())
                .filter(locker -> locker.getId().equals(lockerId))
                .findFirst();
        }

        // 사물함 신청(LockerApplicationService)용 메서드라 이 테스트에서는 쓰지 않는다
        @Override
        public Optional<LockerPeriod> findPublishedPeriodById(Long lockerPeriodId) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class FakeLockerApplicationRepository implements LockerApplicationRepository {

        private Set<Long> appliedLockerIds = Set.of();
        private Long myLockerId;

        @Override
        public Set<Long> findAppliedLockerIds(Long lockerPeriodId) {
            return appliedLockerIds;
        }

        @Override
        public Optional<Long> findAppliedLockerId(Long lockerPeriodId, Long memberId) {
            return Optional.ofNullable(myLockerId);
        }

        // 아래는 사물함 신청(LockerApplicationService)용 메서드라 이 테스트에서는 쓰지 않는다

        @Override
        public boolean existsByLocker(Long lockerPeriodId, Long lockerId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LockerApplication save(LockerApplication application) {
            throw new UnsupportedOperationException();
        }
    }
}
