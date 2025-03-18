
### 데이터베이스 계정 생성(postgres)
```postgresql
CREATE USER modoo_admin WITH PASSWORD '1234';

CREATE DATABASE stickybook;

ALTER DATABASE stickybook OWNER TO modoo_admin;

GRANT ALL PRIVILEGES ON DATABASE stickybook TO modoo_admin;
```