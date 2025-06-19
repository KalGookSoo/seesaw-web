# SEESAW WEB

## 프로젝트 소개
SEESAW WEB는 SEESAW CMS로 만들어낸 포털 웹사이트입니다. 게시글, 정적 콘텐츠, 캘린더, 주문, 예약, 상품 등록 등 모듈을 추가하여 게시할 수 있습니다. 현재는 게시글, 정적 콘텐츠 모듈만 구현된 상태입니다.

## 배포 정보
현재 프로젝트는 다음 URL에서 운영 배포 중입니다:
- [대전포스트잇 포털](https://daejeonstickybook.seesaw.me.kr)

### 배포 절차
- [Production Release 워크플로우](./docs/PROD_RELEASE_WORKFLOW.md) - GitHub Actions를 사용한 배포 절차 시각화

## 개발 환경 설정

### 데이터베이스 설정
1. [데이터베이스 계정 생성 및 테이블 생성](./docs/database/init.sql)
2. [테이블 데이터 삽입](docs/database/initial_data-1.0.0.sql)
3. [파일 다운로드(첨부파일)](https://nas.nxcoa.com)
   - 첨부파일을 받지않아도 실행은 되지만 기존 데이터 중 파일데이터를 찾을 수 없습니다.

### 환경변수 설정
- [application.properties](./src/main/resources/application-local.properties)
- [datasource.properties](src/main/resources/datasource.properties)
- [datasource-local.properties](src/main/resources/datasource-local.properties)
- [datasource-prod.properties](src/main/resources/datasource-prod.properties)
- [datasource-test.properties](src/main/resources/datasource-test.properties)

spring.profiles.active 의 값은 local, test, prod 등으로 나누어 놓았습니다.
dev, stage 혹은 개인에 맞게 커스텀하려고한다면 application.properties, datasource.properties 파일을 쌍으로 추가하여 확장 가능합니다.

### 로컬환경 프로그램 인자(IntelliJ 기준)
```
--spring.thymeleaf.prefix=file:///$PROJECT_DIR$/src/main/resources/templates/
--spring.web.resources.static-locations=file:///$PROJECT_DIR$/src/main/resources/static/
--spring.messages.basename=file:///$PROJECT_DIR$/src/main/resources/messages/
```

### 빌드 명령어
```shell
gradlew clean bootJar -PappVersion=1.0.0
```

### 버저닝
[소프트웨어 버전 관리 가이드](./docs/versioning.md)

## GitHub 템플릿 가이드

이 디렉토리에는 GitHub 저장소에서 사용되는 다양한 템플릿이 포함되어 있습니다. 이 템플릿들은 이슈 보고, 기능 요청, 풀 리퀘스트 등의 프로세스를 표준화하고 효율적으로 만들기 위해 설계되었습니다.

### 템플릿 구조

```
.github/
├── ISSUE_TEMPLATE/
│   ├── bug_report.md      # 버그 리포트 템플릿
│   ├── feature_request.md # 기능 요청 템플릿
│   ├── general.md         # 일반 이슈 템플릿
│   └── config.yml         # 이슈 템플릿 설정
├── PULL_REQUEST_TEMPLATE.md # PR 템플릿
└── RELEASE_TEMPLATE.md    # 릴리스 노트 템플릿
```

### 이슈 템플릿

#### 버그 리포트 (`bug_report.md`)
버그를 보고할 때 사용하는 템플릿입니다. 다음 정보를 포함해야 합니다:
- 버그 설명
- 재현 방법
- 기대한 동작
- 실제 동작
- 스크린샷 (가능한 경우)
- 환경 정보
- 추가 정보
- 가능한 해결책

#### 기능 요청 (`feature_request.md`)
새로운 기능이나 개선 사항을 제안할 때 사용하는 템플릿입니다. 다음 정보를 포함해야 합니다:
- 기능 요청 설명
- 관련 문제
- 원하는 해결책
- 대안 고려사항
- 사용 사례
- 추가 정보
- 구현 아이디어 (선택사항)

#### 일반 이슈 (`general.md`)
버그나 기능 요청이 아닌 일반적인 이슈를 생성할 때 사용하는 템플릿입니다. 다음 정보를 포함해야 합니다:
- 이슈 설명
- 현재 상황
- 기대하는 결과
- 해결 방안 (선택사항)
- 추가 정보
- 체크리스트

### 풀 리퀘스트 템플릿

풀 리퀘스트를 생성할 때 사용하는 템플릿입니다. 다음 정보를 포함해야 합니다:
- PR 유형
- 변경 사항 설명
- 관련 이슈
- 테스트 정보
- 체크리스트
- 배포 고려사항
- 스크린샷 (선택사항)
- 추가 정보

### 릴리스 노트 템플릿

릴리스 노트를 작성할 때 사용하는 템플릿입니다. 다음 정보를 포함해야 합니다:
- 버전 정보 (버전 번호, 릴리스 날짜)
- 주요 변경 사항 (새로운 기능, 개선 사항, 버그 수정)
- 상세 변경 내역 (기술적 변경 사항, API 변경 사항)
- 관련 이슈 및 PR
- 배포 정보 (배포 요구사항, 배포 단계)
- 알려진 이슈
- 사용 가이드
- 테스트 결과
- 추가 정보

### 템플릿 사용 방법

1. **이슈 생성**: GitHub 저장소의 "Issues" 탭에서 "New issue" 버튼을 클릭하면 사용 가능한 템플릿 목록이 표시됩니다.
2. **PR 생성**: "Pull requests" 탭에서 "New pull request" 버튼을 클릭하고 PR을 생성하면 자동으로 PR 템플릿이 로드됩니다.
3. **릴리스 노트 작성**: 
   - GitHub 저장소의 "Releases" 탭에서 "Publish release" 버튼을 클릭합니다.
   - 태그 버전(예: v1.0.1)을 입력하고, 타겟 브랜치(예: prod)를 선택합니다.
   - 릴리스 제목을 입력합니다.
   - 릴리스 설명 부분에 `.github/RELEASE_TEMPLATE.md` 파일의 내용을 복사하여 붙여넣고 필요한 정보를 작성합니다.
   - 작성이 완료되면 "Publish release" 버튼을 클릭하여 릴리스를 게시합니다.

### 템플릿 수정

템플릿을 수정하려면 해당 파일을 직접 편집하면 됩니다. 모든 템플릿은 마크다운 형식으로 작성되어 있으며, 프로젝트의 요구사항에 맞게 자유롭게 수정할 수 있습니다.
