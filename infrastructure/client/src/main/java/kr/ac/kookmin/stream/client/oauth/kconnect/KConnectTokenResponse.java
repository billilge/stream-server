package kr.ac.kookmin.stream.client.oauth.kconnect;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// 토큰 교환 응답. access token만 쓰고 refresh token 등 나머지는 받지 않는다
@JsonIgnoreProperties(ignoreUnknown = true)
record KConnectTokenResponse(
    @JsonProperty("access_token") String accessToken
) {}
