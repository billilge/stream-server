package kr.ac.kookmin.stream.api.app.welfare.fee.usecase;

import java.util.Objects;
import kr.ac.kookmin.stream.api.app.welfare.fee.FeeAccountProvider;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.domain.FileCategory;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeAccount;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeSubmitCommand;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferGuide;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 학생회비 납부 요청. 학기 인증(남은 학기 수·학점이수현황 캡처)과 송금 완료를 한 번에 받는다.
 * {@code welfare}(납부 요청)와 {@code file}(인증 이미지) 두 도메인을 조합한다.
 * <p>
 * 메서드에 {@code @Transactional}을 붙이지 않는다. 쓰기는 {@code StudentTransferStatusService.submit} 하나뿐이고
 * 그 트랜잭션 안에서 끝난다. 인증 이미지는 조회해서 검증만 한다.
 * <p>
 * 다시 내도 이전 인증 이미지를 지우지 않는다. 반려된 이전 요청이 이력으로 남아 그 이미지를 계속 가리킨다.
 */
@Component
@RequiredArgsConstructor
public class FeeSubmitUseCase {

    private final StudentTransferStatusService studentTransferStatusService;
    private final FeeAccountProvider feeAccountProvider;
    private final FileService fileService;

    public FeeResponse submit(Long memberId, FeeSubmitCommand command) {
        validateProofImage(command.proofImageKey(), memberId);

        // 계좌가 없으면 송금 자체가 불가능하므로, 저장하기 전에 먼저 막는다
        FeeAccount account = feeAccountProvider.getAccount();
        long amount = account.calculateTransferAmount(command.remainingSemesters());

        StudentTransferStatus saved = studentTransferStatusService.submit(memberId, command, amount);
        return FeeResponse.of(saved, account, TransferGuide.notCalculated());
    }

    // 실재하는 키인지, 본인이 올린 것인지, 학점이수현황 캡처로 올린 것인지 본다.
    // 카테고리까지 보는 이유는 학생이 다른 용도로 올려둔 파일의 키를 넘기는 걸 막기 위해서다.
    private void validateProofImage(String proofImageKey, Long memberId) {
        File proofImage = fileService.findByFileKey(proofImageKey)
            .orElseThrow(() -> new BusinessException(FeeErrorCode.INVALID_PROOF_IMAGE));

        if (!Objects.equals(proofImage.getUploaderId(), memberId)
            || proofImage.getCategory() != FileCategory.FEE_TRANSCRIPT_IMAGE) {
            throw new BusinessException(FeeErrorCode.INVALID_PROOF_IMAGE);
        }
    }
}
