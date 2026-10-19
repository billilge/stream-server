package kr.ac.kookmin.stream.api.app.welfare.fee.usecase;

import java.util.Objects;
import kr.ac.kookmin.stream.api.app.welfare.fee.FeeAccount;
import kr.ac.kookmin.stream.api.app.welfare.fee.FeeAccountProvider;
import kr.ac.kookmin.stream.api.app.welfare.fee.request.FeeSubmitRequest;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.domain.FileCategory;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeAmountCalculator;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 학생회비 납부 요청. 학기 인증(남은 학기 수·학점이수현황 캡처)과 송금 완료를 한 번에 받는다.
 * {@code welfare}(납부 요청)와 {@code file}(인증 이미지) 두 도메인을 조합한다.
 * <p>
 * 메서드에 {@code @Transactional}을 붙이지 않는다. 저장은 {@code StudentTransferStatusService.request}가
 * 자기 트랜잭션에서 끝내고, 이전 이미지 삭제는 R2 호출이라 롤백되지 않아 그 커밋 뒤에 해야 하기 때문이다
 * ({@code architecture.md} 6-1절 "롤백 불가한 외부 부수효과").
 */
@Component
@RequiredArgsConstructor
public class FeeSubmitUseCase {

    private static final Logger log = LoggerFactory.getLogger(FeeSubmitUseCase.class);

    private final StudentTransferStatusService studentTransferStatusService;
    private final FeeAccountProvider feeAccountProvider;
    private final FileService fileService;

    public FeeResponse submit(Long memberId, FeeSubmitRequest request) {
        int remainingSemesters = FeeAmountCalculator.validateRemainingSemesters(request.remainingSemesters());
        validateProofImage(request.proofImageKey(), memberId);

        // 계좌가 없으면 송금 자체가 불가능하므로, 저장하기 전에 먼저 막는다
        FeeAccount account = feeAccountProvider.getAccount();
        long amount = account.calculateTransferAmount(remainingSemesters);

        // 반려 후 다시 내면 이전 요청을 덮어쓰므로, 교체될 이전 키를 알려고 한 번 더 읽는다. request가 돌려주게 하면
        // 반환 타입에 값을 하나 더 얹어야 해서, 유니크 인덱스 조회 한 번을 받아들였다. 같은 학생의 동시 요청이 겹치면
        // 낡은 키를 볼 수 있는데, 그때 생기는 건 고아 파일 하나뿐이라 파일 정리 배치가 걷어간다.
        String previousProofImageKey = studentTransferStatusService.findLatestByMemberId(memberId)
            .map(StudentTransferStatus::getProofImageKey)
            .orElse(null);
        StudentTransferStatus saved =
            studentTransferStatusService.request(memberId, remainingSemesters, request.proofImageKey(), amount);

        deletePreviousProofImage(previousProofImageKey, saved.getProofImageKey());

        return FeeResponse.of(saved, account, null);
    }

    // 실재하는 키인지, 본인이 올린 것인지, 학점이수현황 캡처로 올린 것인지 본다.
    // 카테고리까지 보는 이유는 학생이 다른 용도로 올려둔 파일의 키를 넘기는 걸 막기 위해서다.
    private void validateProofImage(String proofImageKey, Long memberId) {
        if (proofImageKey == null || proofImageKey.isBlank()) {
            throw new BusinessException(FeeErrorCode.INVALID_PROOF_IMAGE);
        }

        File proofImage = fileService.findByFileKey(proofImageKey)
            .orElseThrow(() -> new BusinessException(FeeErrorCode.INVALID_PROOF_IMAGE));

        if (!Objects.equals(proofImage.getUploaderId(), memberId)
            || proofImage.getCategory() != FileCategory.FEE_TRANSCRIPT_IMAGE) {
            throw new BusinessException(FeeErrorCode.INVALID_PROOF_IMAGE);
        }
    }

    /**
     * 다시 내면서 교체된 이전 이미지를 지운다. 아무도 가리키지 않게 된 데다 학번·이름이 찍혀 있어 남겨둘 이유가 없다.
     * <p>
     * 삭제가 실패해도 요청은 성공으로 둔다 — 고아 파일 하나가 남는 편이 요청 실패보다 낫다.
     * 남은 것은 파일 정리 배치가 걷어간다.
     */
    private void deletePreviousProofImage(String previousKey, String currentKey) {
        if (previousKey == null || previousKey.equals(currentKey)) {
            return;
        }
        try {
            fileService.findByFileKey(previousKey).ifPresent(file -> fileService.delete(file.getId()));
        } catch (RuntimeException e) {
            log.warn("이전 인증 이미지를 지우지 못했습니다. fileKey={}", previousKey, e);
        }
    }
}
