package kr.ac.kookmin.stream.welfare.domain.fee.domain;

/**
 * 학년(1~4)·학기(1~2)를 묶은 값 객체. 항상 유효한 상태로만 생성되도록 {@link #of}에서 검증한다.
 */
public record GradeSemester(int grade, int semester) {

    private static final int MIN_GRADE = 1;
    private static final int MAX_GRADE = 4;
    private static final int MIN_SEMESTER = 1;
    private static final int MAX_SEMESTER = 2;

    // 값이 없거나(null) 범위를 벗어나면 IllegalArgumentException을 던진다.
    // (웹 계층에서 파라미터로 받으므로 미입력까지 여기서 함께 걸러낸다.)
    public static GradeSemester of(Integer grade, Integer semester) {
        if (grade == null || grade < MIN_GRADE || grade > MAX_GRADE) {
            throw new IllegalArgumentException("학년은 " + MIN_GRADE + "~" + MAX_GRADE + " 사이여야 합니다.");
        }
        if (semester == null || semester < MIN_SEMESTER || semester > MAX_SEMESTER) {
            throw new IllegalArgumentException("학기는 " + MIN_SEMESTER + "~" + MAX_SEMESTER + " 사이여야 합니다.");
        }
        return new GradeSemester(grade, semester);
    }
}
