package kr.ac.kookmin.stream.api.common.config;

import java.util.List;
import kr.ac.kookmin.stream.api.common.resolver.ApiUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 클라이언트 모듈이 등록한 {@link ApiUserArgumentResolver}를 일괄 등록한다.
 * <p>
 * 리졸버를 타입으로 지목하지 않고 주입받아, common-api가 app-api·admin-api를 컴파일 타임에 참조하지 않도록 한다.
 * 클라이언트 모듈이 추가돼도 이 클래스는 수정할 필요가 없다.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ObjectProvider<ApiUserArgumentResolver> apiUserArgumentResolvers;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 생성자가 아니라 이 시점에 펼쳐, WebMvcConfigurer 생성 단계에서 리졸버 빈을 앞당겨 만들지 않는다.
        // 리졸버 빈이 하나도 없는 컨텍스트에서도 orderedStream()은 빈 스트림이라 기동이 깨지지 않는다.
        resolvers.addAll(apiUserArgumentResolvers.orderedStream().toList());
    }
}
