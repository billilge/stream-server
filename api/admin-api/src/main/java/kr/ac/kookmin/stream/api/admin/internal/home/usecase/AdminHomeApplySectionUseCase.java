package kr.ac.kookmin.stream.api.admin.internal.home.usecase;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeApplySectionListItemResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeApplySectionListResponse;
import kr.ac.kookmin.stream.event.domain.event.service.AdminEventService;
import kr.ac.kookmin.stream.event.domain.locker.service.AdminLockerService;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTarget;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;
import kr.ac.kookmin.stream.internal.domain.home.service.AdminHomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 홈 신청 섹션 후보. 후보는 지금 신청을 받는 행사·사물함 회차이고 노출 여부·순서는 홈이 들고 있어,
 * 세 도메인을 가로지르는 조합을 여기서 한다({@code architecture.md} 6-1절).
 * <p>
 * 후보 목록을 읽기만 한다 — 설정이 없는 후보도 저장하지 않고 미노출로 채워 내려준다.
 */
@Component
@RequiredArgsConstructor
public class AdminHomeApplySectionUseCase {

    private final AdminHomeService adminHomeService;
    private final AdminEventService adminEventService;
    private final AdminLockerService adminLockerService;

    public AdminHomeApplySectionListResponse getApplySections(Boolean exposed) {
        List<Candidate> candidates = loadCandidates();
        Map<HomeApplyTarget, Candidate> candidatesByTarget = candidates.stream()
            .collect(Collectors.toMap(Candidate::target, Function.identity()));

        List<AdminHomeApplySectionListItemResponse> content = adminHomeService
            .getApplySections(candidates.stream().map(Candidate::target).toList(), exposed).stream()
            .map(section -> toResponse(section, candidatesByTarget.get(section.target())))
            .toList();

        return new AdminHomeApplySectionListResponse(content);
    }

    /**
     * 지금 신청을 받는 행사와 사물함 회차를 신청 마감 임박순으로 섞는다. 설정이 아직 없는 후보가
     * 이 순서로 목록 뒤에 붙는다.
     */
    private List<Candidate> loadCandidates() {
        return Arrays.stream(HomeApplyTargetType.values())
            .flatMap(this::candidatesOf)
            .sorted(Comparator.comparing(Candidate::applyEndAt)
                .thenComparing(candidate -> candidate.target().targetType())
                .thenComparing(candidate -> candidate.target().targetId()))
            .toList();
    }

    // 대상 종류를 switch 식으로 모두 받는다. 값이 늘면 컴파일이 막으므로 새 종류가 후보에서 조용히 빠지지 않는다
    private Stream<Candidate> candidatesOf(HomeApplyTargetType targetType) {
        return switch (targetType) {
            case EVENT -> adminEventService.getAllApplicableEvents().stream()
                .map(event -> new Candidate(
                    new HomeApplyTarget(HomeApplyTargetType.EVENT, event.getId()),
                    event.getTitle(),
                    event.getApplyStartAt(),
                    event.getApplyEndAt()
                ));
            case LOCKER -> adminLockerService.getAllApplicablePeriods().stream()
                .map(period -> new Candidate(
                    new HomeApplyTarget(HomeApplyTargetType.LOCKER, period.getId()),
                    period.getName(),
                    period.getApplyStartAt(),
                    period.getApplyEndAt()
                ));
        };
    }

    private AdminHomeApplySectionListItemResponse toResponse(HomeApplySection section, Candidate candidate) {
        return new AdminHomeApplySectionListItemResponse(
            section.getTargetType(),
            section.getTargetId(),
            candidate.title(),
            candidate.applyStartAt(),
            candidate.applyEndAt(),
            section.isExposed()
        );
    }

    /** 후보 하나. 제목과 신청 기간은 행사·회차에서 읽어 온 값이다. */
    private record Candidate(
        HomeApplyTarget target,
        String title,
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt
    ) {
    }
}
