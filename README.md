# 🌐 SEESAW WEB

> **SEESAW CMS 기반의 다목적 포털 웹 어플리케이션**

SEESAW WEB은 SEESAW 엔진을 활용하여 구축된 사용자 지향 포털 서비스입니다. 게시글 관리, 정적 콘텐츠, 일정 관리 등 다양한 모듈을 조합하여 완성도 높은 커뮤니티 및 정보 포털 서비스를 제공합니다.

---

## ✨ Key Features

### 1. Modular Content Management
- **게시글 모듈**: 계층형 댓글, 첨부파일, 검색 기능을 포함한 강력한 게시판 시스템
- **정적 콘텐츠**: 페이지 기반의 공지사항 및 정보 안내 페이지 관리
- **이벤트 캘린더**: TUI Calendar 연동을 통한 일정 시각화 및 관리 (진행 중)
- **주문/예약/상품**: 커머스 기능을 위한 확장 모듈 설계 (준비 중)

### 2. Rich User Experience
- **첨부파일 미리보기**: 별도 다운로드 없이 이미지, PDF, HTML, 소스 코드 등을 브라우저에서 즉시 확인 가능
- **반응형 디자인**: Bootstrap 기반의 UI로 데스크톱, 태블릿, 모바일 등 모든 기기에 최적화된 레이아웃 제공
- **직관적인 에디터**: Toast UI Editor를 통한 WYSIWYG 편집 및 마크다운 지원

### 3. Performance & Stability
- **Server-Side Rendering**: Thymeleaf를 활용한 빠른 초기 로딩 및 SEO 최적화
- **Fragment-based UI**: 반복되는 UI 요소를 프래그먼트화하여 코드 재사용성 및 유지보수성 향상

---

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.4.x (MVC)
- **Template Engine**: Thymeleaf
- **Frontend Framework**: Bootstrap 5
- **Icons**: Bootstrap Icons
- **Scripting**: JavaScript (ES6+), jQuery
- **Plugins**: Toast UI (Editor, Calendar, Chart), Interact.js

---

## 🏗 Project Structure

```text
seesaw-web/
├── src/main/java/kr/me/seesaw/
│   ├── controller/      # MVC 컨트롤러
│   └── ...              # 웹 레이어 전용 로직
└── src/main/resources/
    ├── static/          # CSS, JS, 이미지 등 정적 자원
    └── templates/       # Thymeleaf HTML 템플릿
        ├── articles/    # 게시글 관련 페이지
        ├── events/      # 일정 관리 관련 페이지
        ├── components/  # 공통 UI 프래그먼트
        └── layout.html  # 전역 레이아웃
```

---

## 📖 Documentation & Guides

프로젝트 개발 및 운영에 필요한 상세 가이드는 아래 문서를 참고하세요.

- [🛠 개발 환경 설정 가이드](docs/DEVELOPMENT_SETUP.md)
- [🏷️ 소프트웨어 버전 관리 가이드](docs/versioning.md)
- [📝 GitHub 템플릿 가이드](docs/GITHUB_TEMPLATES.md)
- [🚢 Production Release 워크플로우](docs/PROD_RELEASE_WORKFLOW.md)

---

## 🚢 Deployment Info

현재 다음 URL에서 운영 배포 중입니다:
- [대전포스트잇 포털](https://daejeonstickybook.seesaw.me.kr)

---

## 🔗 Related Modules
- [seesaw-core](../seesaw-core/README.md): 핵심 도메인 및 비즈니스 모듈
- [seesaw-api](../seesaw-api/README.md): REST API 서버 모듈
