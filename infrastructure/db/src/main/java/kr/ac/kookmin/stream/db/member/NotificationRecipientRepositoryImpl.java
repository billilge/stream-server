package kr.ac.kookmin.stream.db.member;

import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushTarget;
import kr.ac.kookmin.stream.member.domain.notification.repository.NotificationRecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRecipientRepositoryImpl implements NotificationRecipientRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public List<Long> findAllActiveMemberIds() {
        return memberJpaRepository.findAllActiveIds();
    }

    @Override
    public List<PushTarget> findPushTargets(Collection<Long> memberIds) {
        if (memberIds.isEmpty()) {
            return List.of();
        }
        return toPushTargets(memberJpaRepository.findPushTargetsByIdIn(memberIds));
    }

    @Override
    public List<PushTarget> findAllPushTargets() {
        return toPushTargets(memberJpaRepository.findAllPushTargets());
    }

    @Override
    public int clearFcmTokens(Collection<String> fcmTokens) {
        if (fcmTokens.isEmpty()) {
            return 0;
        }
        return memberJpaRepository.clearFcmTokens(fcmTokens);
    }

    private List<PushTarget> toPushTargets(List<PushTargetProjection> projections) {
        return projections.stream()
            .map(p -> new PushTarget(p.memberId(), p.fcmToken(), p.setting().toDomain()))
            .toList();
    }
}
