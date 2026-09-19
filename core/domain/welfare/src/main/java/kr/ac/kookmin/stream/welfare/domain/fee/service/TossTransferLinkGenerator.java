package kr.ac.kookmin.stream.welfare.domain.fee.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// 토스 앱을 여는 송금 딥링크(supertoss://send)를 만든다.
public final class TossTransferLinkGenerator {

    private static final String BASE_URL = "supertoss://send";

    private TossTransferLinkGenerator() {}

    // amount는 null이면 링크에서 뺀다 — 토스 앱에서 사용자가 직접 금액을 입력하게 된다.
    public static String generate(String bank, String accountNo, Long amount) {
        StringBuilder query = new StringBuilder();
        appendParam(query, "bank", bank);
        appendParam(query, "accountNo", accountNo == null ? null : accountNo.replace("-", ""));
        appendParam(query, "amount", amount == null ? null : String.valueOf(amount));
        return BASE_URL + "?" + query;
    }

    private static void appendParam(StringBuilder query, String key, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (!query.isEmpty()) {
            query.append('&');
        }
        query.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8));
    }
}
