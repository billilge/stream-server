package kr.ac.kookmin.stream.api.admin.core.member;

import jakarta.validation.Valid;
import java.util.Set;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.core.member.response.AdminMemberResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.Department;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.AdminMemberService;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/members")
@RequiredArgsConstructor
public class AdminMemberController implements AdminMemberApi {

    private final AdminMemberService adminMemberService;
    private final MemberService memberService;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<AdminMemberResponse>> getMembers(
        AdminApiUser apiUser,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String department,
        @RequestParam(required = false) String role,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        PageResult<Member> result = adminMemberService.search(
            keyword, Department.from(department), Role.from(role), pageParams.toOffset()
        );
        Set<Long> signedUpIds = memberService.findSignedUpIds(result.content().stream().map(Member::getId).toList());
        return ApiResponse.success(
            PageResponse.from(result, member -> AdminMemberResponse.from(member, signedUpIds.contains(member.getId())))
        );
    }
}
