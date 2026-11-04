---
name: flyway-check
description: PR을 올리기 직전에 현재 브랜치가 추가한 Flyway 마이그레이션 파일의 버전 번호를 main과 열린 PR들의 최대 버전 다음 번호로 다시 매기고 커밋하는 스킬. /flyway-check로 호출하며, git 스킬의 PR 생성 단계에서도 먼저 실행한다.
---

# Flyway 버전 점검 워크플로우

PR을 올리기 직전, 현재 브랜치가 **새로 추가한** Flyway 파일의 버전이 main과 다른 열린 PR들의 버전과 겹치지 않도록
**가장 큰 버전 다음 번호부터** 다시 매긴다(`flyway-migration.md` 2절 "버전 충돌 주의").

- 마이그레이션 위치: `infrastructure/db/src/main/resources/db/migration` (이하 `{MIG_DIR}`)
- 파일명: `V{버전}__{설명}.sql`, 버전은 정수다. Flyway는 버전을 **숫자로** 비교하므로 정렬도 숫자 기준(`sort -n`)으로 한다.

> **모든 GitHub 조회는 `gh api`(REST)로만 한다.** `gh pr list`·`gh pr view --json`은 내부적으로 GraphQL을 호출하는데, 이 저장소에서 GraphQL 오류가 난 적이 있어 사용하지 않는다.
> **`gh api`에는 `-r`(raw) 플래그가 없다.** `--jq` 결과가 문자열이면 이미 raw로 출력된다.

## Step 0 — 저장소·브랜치 정보 확보

```bash
git fetch origin
gh repo view --json nameWithOwner --jq .nameWithOwner   # → {owner}/{repo}
git rev-parse --abbrev-ref HEAD                          # → {branch}
```

PR의 base 브랜치(`{base}`)를 정한다.

- 현재 브랜치에 열린 PR이 이미 있으면 그 PR의 `.base.ref`를 쓴다.
  **`head={owner}:{branch}` 필터는 쓰지 않는다** — 브랜치명의 `#`(이슈번호) 때문에 URL이 잘려 빈 결과가 나온다. 열린 PR을 모두 받아 jq로 매칭한다.

  ```bash
  gh api "repos/{owner}/{repo}/pulls?state=open&per_page=100" --paginate \
    --jq '.[] | select(.head.ref=="{branch}") | {number, base: .base.ref}'
  ```

- PR이 없으면 앞으로 올릴 PR의 base를 쓴다. 기본은 `main`이고, 다른 기능 브랜치 위에 쌓는 PR이면 그 부모 브랜치다.
  base가 `main`이 아닐 수 있는데 확실하지 않으면 사용자에게 묻는다.

## Step 1 — 내 Flyway 파일 찾기

base 대비 **추가(A)된** 마이그레이션 파일만 대상이다. 이미 머지된 파일은 수정하지 않는 규칙(3-1절)이 있으므로 수정(M)·삭제(D)는 보지 않는다.

```bash
git diff --name-only --no-renames --diff-filter=A "origin/{base}...HEAD" -- {MIG_DIR}
```

- 결과가 비어 있으면 "추가한 Flyway 파일 없음"을 알리고 종료한다.
- base를 `main`으로 잘못 잡으면 스택 PR에서 부모 PR의 파일까지 내 파일로 잡혀 번호가 바뀐다. base를 정확히 잡는다.
- `git status --porcelain -- {MIG_DIR}`에 커밋하지 않은 변경이 있으면 멈추고 사용자에게 알린다. 이 스킬은 마지막에 커밋까지 하므로 작업 중인 변경과 섞이면 안 된다.

## Step 2 — main과 열린 PR의 최대 버전 찾기

**(1) main에 이미 있는 버전**

```bash
git ls-tree --name-only origin/main {MIG_DIR}/ \
  | sed -nE 's#.*/V([0-9]+)__.*#\1#p' | sort -n | tail -1
```

**(2) 열린 PR들이 추가한 버전** — 현재 브랜치의 PR은 제외한다.

```bash
# 열린 PR 번호 (내 PR 제외)
gh api "repos/{owner}/{repo}/pulls?state=open&per_page=100" --paginate \
  --jq '.[] | select(.head.ref != "{branch}") | .number'

# PR마다 Flyway 파일명
gh api "repos/{owner}/{repo}/pulls/{번호}/files?per_page=100" --paginate \
  --jq '.[] | select(.status != "removed") | .filename
        | select(startswith("{MIG_DIR}/")) | split("/") | last'
```

- 파일명에서 `^V([0-9]+)__`로 버전을 뽑는다. 이 형식이 아닌 파일은 무시한다.
- PR의 `files`는 그 PR의 base 대비 변경분이다. 스택 PR은 자기 파일만 나오고 부모 PR의 파일은 부모 PR에서 잡히므로 따로 처리하지 않는다.
- 머지되지 않은 오래된 PR이 main보다 낮은 번호를 갖고 있어도 최댓값에는 영향이 없으므로 그대로 둔다.

`MAX = max(main 최대 버전, 열린 PR 최대 버전)`

## Step 3 — 새 번호 계산

내 파일을 현재 버전 오름차순으로 정렬하고, 원래 순서를 유지한 채 `MAX+1`, `MAX+2`, ... 를 차례로 붙인다. 설명(`__` 뒤)은 바꾸지 않는다.

```
MAX = 22
V20__add_fee_submission_columns.sql                → V23__add_fee_submission_columns.sql
V21__insert_default_student_transfer_statuses.sql  → V24__insert_default_student_transfer_statuses.sql
V23__rename_needs_check_to_rejected.sql            → V25__rename_needs_check_to_rejected.sql
```

- 모든 파일이 이미 목표 번호와 같으면 "변경 없음"을 알리고 Step 6으로 간다.
- 중간에 빈 번호가 있어도(예: V21 다음 V23) 연속 번호로 다시 매긴다.

## Step 4 — 파일 이름 변경과 참조 수정

```bash
git mv "{MIG_DIR}/V20__add_fee_submission_columns.sql" "{MIG_DIR}/V23__add_fee_submission_columns.sql"
```

- 목표 파일명이 이미 있으면 멈추고 사용자에게 알린다.
- 옛 파일명을 가리키는 곳을 찾아 새 이름으로 고친다. 확장자 없이 `V{옛 버전}__{설명}`으로 검색해야 `.sql` 유무와 상관없이 잡힌다.

  ```bash
  git grep -n "V20__add_fee_submission_columns"
  ```

  SQL 파일 머리 주석, `docs/` 문서, 계획 문서(`docs/plans/`) 등에서 나올 수 있다. `V20`처럼 버전만 적은 곳은 다른 파일을 가리킬 수도 있으므로 자동으로 바꾸지 말고, 이 브랜치가 바꾼 파일에서 나온 것만 문맥을 보고 고친다.

## Step 5 — 커밋

이름 변경과 참조 수정을 커밋 하나로 묶는다. **push는 하지 않는다.**

```bash
git add {참조를 고친 파일들}   # git mv 한 파일은 이미 스테이징돼 있다
git commit -F - <<'EOF'
chore: Flyway 마이그레이션 버전 조정

- V20 → V23 add_fee_submission_columns
- V21 → V24 insert_default_student_transfer_statuses
- V23 → V25 rename_needs_check_to_rejected
EOF
```

- 커밋 메시지 형식은 `git-convention.md`를 따른다. `Co-Authored-By` 같은 AI 트레일러를 넣지 않는다.
- `git add -A`·`git add .`로 다른 변경을 섞지 않는다. 바꾼 파일만 지정한다.

## Step 6 — 결과 출력

```
## Flyway 버전 점검

- main 최대: V19
- 열린 PR: #129 V20 · #134 V17, V18 · #136 V19 · #140 V22 · #141 V21
- MAX: V22

| 현재 | 변경 |
| --- | --- |
| V20__add_fee_submission_columns.sql | V23__add_fee_submission_columns.sql |

참조 수정: docs/plans/xxx.md
커밋: chore: Flyway 마이그레이션 버전 조정 (push 안 함)
```

- 변경이 없으면 표 대신 "변경 없음"만 적는다.
- 현재 브랜치에 이미 PR이 있으면 push해야 PR에 반영된다고 알린다.
