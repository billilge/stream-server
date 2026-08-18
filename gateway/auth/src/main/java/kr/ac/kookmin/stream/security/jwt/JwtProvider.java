package kr.ac.kookmin.stream.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private static final String ROLES_CLAIM = "roles";
    private static final String COUNCIL_CLAIM = "council";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, Set<Role> roles, Set<CouncilDepartment> councilDepartments) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + jwtProperties.accessTokenExpiry());

        return Jwts.builder()
            .issuer(jwtProperties.issuer())
            .subject(String.valueOf(userId))
            .issuedAt(issuedAt)
            .expiration(expiration)
            .claim(ROLES_CLAIM, names(roles))
            .claim(COUNCIL_CLAIM, names(councilDepartments))
            .signWith(secretKey)
            .compact();
    }

    public JwtPayload parse(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(jwtProperties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

            return new JwtPayload(
                Long.valueOf(claims.getSubject()),
                toEnumSet(claims, ROLES_CLAIM, Role.class),
                toEnumSet(claims, COUNCIL_CLAIM, CouncilDepartment.class)
            );
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
    }

    private List<String> names(Collection<? extends Enum<?>> values) {
        return values.stream().map(Enum::name).toList();
    }

    private <E extends Enum<E>> Set<E> toEnumSet(Claims claims, String claimName, Class<E> type) {
        List<?> values = claims.get(claimName, List.class);
        if (values == null) {
            return EnumSet.noneOf(type);
        }
        return values.stream()
            .map(value -> Enum.valueOf(type, String.valueOf(value)))
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(type)));
    }
}
