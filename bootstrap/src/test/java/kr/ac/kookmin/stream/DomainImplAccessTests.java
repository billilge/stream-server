package kr.ac.kookmin.stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.conditions.ArchConditions;

/**
 * 도메인 모듈은 OPEN 모듈이라 Modulith verify()가 service.impl을 가리지 않는다.
 * 대신 여기서 impl 패키지의 외부 참조와 public 노출을 막는다.
 */
@AnalyzeClasses(packages = "kr.ac.kookmin.stream", importOptions = ImportOption.DoNotIncludeTests.class)
class DomainImplAccessTests {

    private static final String IMPL_PACKAGE = "..service.impl..";

    @ArchTest
    static final ArchRule implIsOnlyReferencedFromItsOwnPackage = classes()
        .that().resideInAPackage(IMPL_PACKAGE)
        .should(ArchConditions.onlyHaveDependentsWhere(originInSamePackageAsTarget()))
        .because("service.impl 패키지는 같은 패키지 안에서만 참조한다");

    @ArchTest
    static final ArchRule implHasNoPublicClasses = noClasses()
        .that().resideInAPackage(IMPL_PACKAGE)
        .should().bePublic()
        .because("service.impl 패키지의 클래스는 package-private으로 둔다");

    private static DescribedPredicate<Dependency> originInSamePackageAsTarget() {
        return new DescribedPredicate<>("origin is in the same package as target") {
            @Override
            public boolean test(Dependency dependency) {
                return dependency.getOriginClass().getPackageName()
                    .equals(dependency.getTargetClass().getPackageName());
            }
        };
    }
}
