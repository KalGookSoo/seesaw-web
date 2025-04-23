## MODOO

<!-- TOC -->
  * [MODOO](#modoo)
    * [SETTINGS](#settings)
    * [환경변수 설정](#환경변수-설정)
    * [로컬환경 프로그램 인자(IntelliJ 기준)](#로컬환경-프로그램-인자intellij-기준)
<!-- TOC -->

### SETTINGS

1. [데이터베이스 계정 생성 및 테이블 생성](./docs/database/init.sql)
2. [테이블 데이터 삽입](./docs/database/initial_data.md)
3. [파일 다운로드(첨부파일)](https://nas.nxcoa.com)
    - 첨부파일을 받지않아도 실행은 되지만 기존 데이터 중 파일데이터를 찾을 수 없습니다.

### 환경변수 설정

[application.properties](./src/main/resources/application-local.properties)

spring.profiles.active 의 값은 local, test, prod 등으로 나누어 놓았습니다.
dev, stage 혹은 개인에 맞게 커스텀하려고한다면 application.properties, datasource.properties 파일을 쌍으로 추가하여 확장 가능합니다.

### 로컬환경 프로그램 인자(IntelliJ 기준)

```
--spring.thymeleaf.prefix=file:///$PROJECT_DIR$/src/main/resources/templates/
--spring.web.resources.static-locations=file:///$PROJECT_DIR$/src/main/resources/static/
--spring.messages.basename=file:///$PROJECT_DIR$/src/main/resources/messages/
```

