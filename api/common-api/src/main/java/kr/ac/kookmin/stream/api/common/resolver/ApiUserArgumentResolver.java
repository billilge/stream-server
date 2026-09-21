package kr.ac.kookmin.stream.api.common.resolver;

import kr.ac.kookmin.stream.api.common.ApiUser;
import kr.ac.kookmin.stream.api.common.config.WebMvcConfig;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

/**
 * {@link ApiUser} 구현체를 컨트롤러 파라미터로 주입하는 리졸버.
 * <p>
 * {@link WebMvcConfig}가 이 타입의 빈만 수집해 등록한다. {@code HandlerMethodArgumentResolver}를 그대로 수집하면
 * 컨텍스트에 등록된 무관한 리졸버까지 딸려오므로 전용 타입으로 좁힌다.
 */
public interface ApiUserArgumentResolver extends HandlerMethodArgumentResolver {
}
