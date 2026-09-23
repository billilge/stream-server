package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerAvailability;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.SectionAvailabilityStatus;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 구역·사물함·신청을 각각 조회해 서비스가 구역별로 묶어 센다. 그 집계와 조합이 어긋나지 않는지 확인한다.
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

            List<LockerSectionSummary> sections =
                new LockerServiceImpl(repository).getSections(PERIOD_ID, MEMBER_ID);

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

            List<LockerSectionSummary> sections =
                new LockerServiceImpl(repository).getSections(PERIOD_ID, MEMBER_ID);

            assertEquals(2, sections.size());
            assertEquals(0, sections.get(1).totalCount());
            assertEquals(SectionAvailabilityStatus.FULL, sections.get(1).availabilityStatus());
        }

        @Test
        @DisplayName("내 사물함이 있는 구역에만 hasMine이 붙는다")
        void marksOnlyMySection() {
            Locker mine = usable(21L, 2L);
            FakeLockerRepository repository = new FakeLockerRepository()
                .withSections(section(1L, "A-1"), section(2L, "A-2"))
                .withAllLockers(usable(11L, 1L), mine)
                .withAppliedLockerIds(21L)
                .withMyLocker(mine);

            List<LockerSectionSummary> sections =
                new LockerServiceImpl(repository).getSections(PERIOD_ID, MEMBER_ID);

            assertFalse(sections.get(0).hasMine());
            assertTrue(sections.get(1).hasMine());
        }

        @Test
        @DisplayName("신청이 없으면 어느 구역에도 hasMine이 붙지 않는다")
        void noneWhenNotApplied() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withSections(section(1L, "A-1"))
                .withAllLockers(usable(11L, 1L));

            List<LockerSectionSummary> sections =
                new LockerServiceImpl(repository).getSections(PERIOD_ID, MEMBER_ID);

            assertFalse(sections.get(0).hasMine());
        }

        @Test
        @DisplayName("게시되지 않은 회차는 찾을 수 없다")
        void unpublishedPeriod() {
            FakeLockerRepository repository = new FakeLockerRepository().withUnpublishedPeriod();

            BusinessException e = assertThrows(BusinessException.class,
                () -> new LockerServiceImpl(repository).getSections(PERIOD_ID, MEMBER_ID));

            assertEquals(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND, e.getErrorCode());
        }
    }

    @Nested
    @DisplayName("구역 상세")
    class GetSectionLockers {

        @Test
        @DisplayName("사용 중지됐거나 이미 신청된 사물함은 선택할 수 없다")
        void availability() {
            FakeLockerRepository repository = new FakeLockerRepository()
                .withSectionLockers(
                    usable(11L, 1L),
                    locker(12L, 1L, LockerStatus.DISABLED),
                    usable(13L, 1L))
                .withAppliedLockerIds(13L);

            List<LockerAvailability> lockers =
                new LockerServiceImpl(repository).getSectionLockers(PERIOD_ID, 1L, MEMBER_ID);

            assertTrue(lockers.get(0).available());     // 사용 가능 + 미신청
            assertFalse(lockers.get(1).available());    // 사용 중지
            assertFalse(lockers.get(2).available());    // 이미 신청됨
        }

        @Test
        @DisplayName("내 사물함에만 isMine이 붙고, 그 사물함은 선택 가능하지 않다")
        void marksOnlyMyLocker() {
            Locker mine = usable(13L, 1L);
            FakeLockerRepository repository = new FakeLockerRepository()
                .withSectionLockers(usable(11L, 1L), mine)
                .withAppliedLockerIds(13L)
                .withMyLocker(mine);

            List<LockerAvailability> lockers =
                new LockerServiceImpl(repository).getSectionLockers(PERIOD_ID, 1L, MEMBER_ID);

            assertFalse(lockers.get(0).mine());
            assertTrue(lockers.get(1).mine());
            assertFalse(lockers.get(1).available());
        }

        @Test
        @DisplayName("없는 구역은 찾을 수 없다")
        void unknownSection() {
            FakeLockerRepository repository = new FakeLockerRepository().withMissingSection();

            BusinessException e = assertThrows(BusinessException.class,
                () -> new LockerServiceImpl(repository).getSectionLockers(PERIOD_ID, 99L, MEMBER_ID));

            assertEquals(LockerErrorCode.LOCKER_SECTION_NOT_FOUND, e.getErrorCode());
        }
    }

    /** 조회 결과만 답하고 집계·조합은 서비스가 하는지 보기 위한 가짜 레포지토리. */
    private static final class FakeLockerRepository implements LockerRepository {

        private boolean publishedPeriod = true;
        private boolean sectionExists = true;
        private List<LockerSection> sections = List.of();
        private List<Locker> allLockers = List.of();
        private List<Locker> sectionLockers = List.of();
        private Set<Long> appliedLockerIds = Set.of();
        private Locker myLocker;

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
            this.appliedLockerIds = Set.of(values);
            return this;
        }

        FakeLockerRepository withMyLocker(Locker value) {
            this.myLocker = value;
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
        public Set<Long> findAppliedLockerIds(Long lockerPeriodId) {
            return appliedLockerIds;
        }

        @Override
        public Optional<Long> findAppliedLockerId(Long lockerPeriodId, Long memberId) {
            return Optional.ofNullable(myLocker).map(Locker::getId);
        }
    }
}
