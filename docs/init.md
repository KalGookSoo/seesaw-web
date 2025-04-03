
### 데이터베이스 계정 생성(postgres)
```postgresql
CREATE USER modoo_admin WITH PASSWORD '1234';

CREATE DATABASE stickybook;

ALTER DATABASE stickybook OWNER TO modoo_admin;

GRANT ALL PRIVILEGES ON DATABASE stickybook TO modoo_admin;
```

### 테이블 생성
```postgresql
CREATE TABLE tb_attachment
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    mime_type VARCHAR(255),
    name VARCHAR(255),
    original_name VARCHAR(255),
    path_name VARCHAR(255),
    size BIGINT NOT NULL
);

COMMENT ON TABLE tb_attachment IS '첨부파일';

COMMENT ON COLUMN tb_attachment.id IS '식별자';

COMMENT ON COLUMN tb_attachment.created_by IS '생성자';

COMMENT ON COLUMN tb_attachment.created_date IS '생성일시';

COMMENT ON COLUMN tb_attachment.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_attachment.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_attachment.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_attachment.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_attachment.version IS '버전';

COMMENT ON COLUMN tb_attachment.mime_type IS 'MIME 타입';

COMMENT ON COLUMN tb_attachment.name IS '이름';

COMMENT ON COLUMN tb_attachment.original_name IS '원본이름';

COMMENT ON COLUMN tb_attachment.path_name IS '경로명';

COMMENT ON COLUMN tb_attachment.size IS '크기';

ALTER TABLE tb_attachment
    OWNER TO modoo_admin;

CREATE TABLE tb_role
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    alias VARCHAR(255),
    name VARCHAR(255)
        CONSTRAINT tb_role_name_check
            CHECK ((name)::TEXT = ANY ((ARRAY ['ROLE_ADMIN'::CHARACTER VARYING, 'ROLE_MANAGER'::CHARACTER VARYING, 'ROLE_USER'::CHARACTER VARYING])::TEXT[]))
);

COMMENT ON TABLE tb_role IS '역할';

COMMENT ON COLUMN tb_role.id IS '식별자';

COMMENT ON COLUMN tb_role.created_by IS '생성자';

COMMENT ON COLUMN tb_role.created_date IS '생성일시';

COMMENT ON COLUMN tb_role.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_role.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_role.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_role.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_role.version IS '버전';

COMMENT ON COLUMN tb_role.alias IS '별칭';

COMMENT ON COLUMN tb_role.name IS '이름';

ALTER TABLE tb_role
    OWNER TO modoo_admin;

CREATE TABLE tb_code
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    parent_id VARCHAR(36)
        CONSTRAINT fk_tb_code_parent
            REFERENCES tb_code,
    description VARCHAR(255),
    name VARCHAR(255),
    sequence INTEGER
);

COMMENT ON TABLE tb_code IS '코드';

COMMENT ON COLUMN tb_code.id IS '식별자';

COMMENT ON COLUMN tb_code.created_by IS '생성자';

COMMENT ON COLUMN tb_code.created_date IS '생성일시';

COMMENT ON COLUMN tb_code.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_code.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_code.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_code.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_code.version IS '버전';

COMMENT ON COLUMN tb_code.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_code.description IS '설명';

COMMENT ON COLUMN tb_code.name IS '이름';

COMMENT ON COLUMN tb_code.sequence IS '순서';

ALTER TABLE tb_code
    OWNER TO modoo_admin;

CREATE TABLE tb_menu
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    parent_id VARCHAR(36)
        CONSTRAINT fk_tb_menu_parent
            REFERENCES tb_menu,
    name VARCHAR(255),
    sequence INTEGER,
    uri VARCHAR(255)
);

COMMENT ON TABLE tb_menu IS '메뉴';

COMMENT ON COLUMN tb_menu.id IS '식별자';

COMMENT ON COLUMN tb_menu.created_by IS '생성자';

COMMENT ON COLUMN tb_menu.created_date IS '생성일시';

COMMENT ON COLUMN tb_menu.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_menu.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_menu.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_menu.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_menu.version IS '버전';

COMMENT ON COLUMN tb_menu.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_menu.name IS '이름';

COMMENT ON COLUMN tb_menu.sequence IS '순번';

COMMENT ON COLUMN tb_menu.uri IS 'URI';

ALTER TABLE tb_menu
    OWNER TO modoo_admin;

CREATE TABLE tb_menu_role
(
    menu_id VARCHAR(36) NOT NULL
        CONSTRAINT fk4nbotupucr3ymefuw3ls9ft5v
            REFERENCES tb_menu,
    role_id VARCHAR(36) NOT NULL
        CONSTRAINT fk9ksiagbksl3tyjq12sxa5c5tm
            REFERENCES tb_role,
    PRIMARY KEY (menu_id, role_id)
);

ALTER TABLE tb_menu_role
    OWNER TO modoo_admin;

CREATE TABLE tb_remember_me_token
(
    series VARCHAR(255) NOT NULL
        PRIMARY KEY,
    last_used TIMESTAMP(6) NOT NULL,
    token VARCHAR(64) NOT NULL,
    username VARCHAR(64) NOT NULL
);

ALTER TABLE tb_remember_me_token
    OWNER TO modoo_admin;

CREATE TABLE tb_site
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    parent_id VARCHAR(36)
        CONSTRAINT fk_tb_site_parent
            REFERENCES tb_site,
    address VARCHAR(255),
    zipcode VARCHAR(255),
    contact_number VARCHAR(255),
    description VARCHAR(255),
    distribution_code VARCHAR(255),
    domain_name VARCHAR(255),
    image_exposed BOOLEAN NOT NULL,
    name VARCHAR(255),
    search_engine_exposed BOOLEAN NOT NULL,
    tags VARCHAR(255),
    profile_image_id VARCHAR(36)
        CONSTRAINT uk80c2eo4umamt2bjlsxq58fcbh
            UNIQUE
        CONSTRAINT fkceppo0k0wek7eymop5ynkuvg3
            REFERENCES tb_attachment
);

COMMENT ON TABLE tb_site IS '사이트';

COMMENT ON COLUMN tb_site.id IS '식별자';

COMMENT ON COLUMN tb_site.created_by IS '생성자';

COMMENT ON COLUMN tb_site.created_date IS '생성일시';

COMMENT ON COLUMN tb_site.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_site.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_site.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_site.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_site.version IS '버전';

COMMENT ON COLUMN tb_site.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_site.address IS '주소';

COMMENT ON COLUMN tb_site.contact_number IS '연락처';

COMMENT ON COLUMN tb_site.description IS '설명';

COMMENT ON COLUMN tb_site.distribution_code IS '분류코드';

COMMENT ON COLUMN tb_site.domain_name IS '도메인이름';

COMMENT ON COLUMN tb_site.image_exposed IS '이미지 노출여부';

COMMENT ON COLUMN tb_site.name IS '이름';

COMMENT ON COLUMN tb_site.search_engine_exposed IS '검색엔진 노출여부';

COMMENT ON COLUMN tb_site.tags IS '태그';

COMMENT ON COLUMN tb_site.profile_image_id IS '프로필이미지 식별자';

ALTER TABLE tb_site
    OWNER TO modoo_admin;

CREATE TABLE tb_category
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    parent_id VARCHAR(36)
        CONSTRAINT fk_tb_category_parent
            REFERENCES tb_category,
    description VARCHAR(255),
    is_public BOOLEAN NOT NULL,
    name VARCHAR(255),
    sequence INTEGER,
    type VARCHAR(255)
        CONSTRAINT tb_category_type_check
            CHECK ((type)::TEXT = ANY ((ARRAY ['STATIC_CONTENT'::CHARACTER VARYING, 'BOARD'::CHARACTER VARYING, 'QNA'::CHARACTER VARYING, 'SCHEDULE'::CHARACTER VARYING, 'STORE'::CHARACTER VARYING, 'BUSINESS'::CHARACTER VARYING])::TEXT[])),
    site_id VARCHAR(36)
        CONSTRAINT fkfimlfw9i9ouminiv7n2lcxwiu
            REFERENCES tb_site
);

COMMENT ON TABLE tb_category IS '카테고리';

COMMENT ON COLUMN tb_category.id IS '식별자';

COMMENT ON COLUMN tb_category.created_by IS '생성자';

COMMENT ON COLUMN tb_category.created_date IS '생성일시';

COMMENT ON COLUMN tb_category.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_category.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_category.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_category.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_category.version IS '버전';

COMMENT ON COLUMN tb_category.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_category.description IS '설명';

COMMENT ON COLUMN tb_category.is_public IS '공개여부';

COMMENT ON COLUMN tb_category.name IS '이름';

COMMENT ON COLUMN tb_category.sequence IS '순서';

COMMENT ON COLUMN tb_category.type IS '타입';

COMMENT ON COLUMN tb_category.site_id IS '사이트 식별자';

ALTER TABLE tb_category
    OWNER TO modoo_admin;

CREATE TABLE tb_article
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    parent_id VARCHAR(36)
        CONSTRAINT fk_tb_article_parent
            REFERENCES tb_article,
    content TEXT,
    fixed_order INTEGER,
    is_fixed BOOLEAN NOT NULL,
    is_public BOOLEAN NOT NULL,
    title VARCHAR(255),
    type VARCHAR(255)
        CONSTRAINT tb_article_type_check
            CHECK ((type)::TEXT = ANY ((ARRAY ['MAP'::CHARACTER VARYING, 'HTML'::CHARACTER VARYING, 'CAROUSEL'::CHARACTER VARYING, 'BUTTON_GROUP'::CHARACTER VARYING, 'IMAGE'::CHARACTER VARYING, 'TABLE'::CHARACTER VARYING, 'VIDEO'::CHARACTER VARYING])::TEXT[])),
    category_id VARCHAR(36)
        CONSTRAINT fkjl0ctedle9lho18jeg61brkqv
            REFERENCES tb_category,
    thumbnail_id VARCHAR(36)
        CONSTRAINT ukb8692hyhjxug1x7qt20npqikl
            UNIQUE
        CONSTRAINT fk4lgppuocodlx5a4wmxc07jp4n
            REFERENCES tb_attachment
);

COMMENT ON TABLE tb_article IS '게시글';

COMMENT ON COLUMN tb_article.id IS '식별자';

COMMENT ON COLUMN tb_article.created_by IS '생성자';

COMMENT ON COLUMN tb_article.created_date IS '생성일시';

COMMENT ON COLUMN tb_article.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_article.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_article.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_article.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_article.version IS '버전';

COMMENT ON COLUMN tb_article.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_article.content IS '본문';

COMMENT ON COLUMN tb_article.fixed_order IS '고정순서';

COMMENT ON COLUMN tb_article.is_fixed IS '고정여부';

COMMENT ON COLUMN tb_article.is_public IS '공개여부';

COMMENT ON COLUMN tb_article.title IS '제목';

COMMENT ON COLUMN tb_article.type IS '타입';

COMMENT ON COLUMN tb_article.category_id IS '카테고리 식별자';

COMMENT ON COLUMN tb_article.thumbnail_id IS '썸네일 식별자';

ALTER TABLE tb_article
    OWNER TO modoo_admin;

CREATE TABLE tb_article_attachment
(
    article_id VARCHAR(36) NOT NULL
        CONSTRAINT fka7f419uqx065gcurr8j6g7rhu
            REFERENCES tb_article,
    attachment_id VARCHAR(36) NOT NULL
        CONSTRAINT fk9kvdmo1dyvq8feh4kjl12kfm5
            REFERENCES tb_attachment,
    PRIMARY KEY (article_id, attachment_id)
);

ALTER TABLE tb_article_attachment
    OWNER TO modoo_admin;

CREATE TABLE tb_notification
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    type VARCHAR(255)
        CONSTRAINT tb_notification_type_check
            CHECK ((type)::TEXT = ANY ((ARRAY ['TOKTOK'::CHARACTER VARYING, 'SMS'::CHARACTER VARYING, 'EMAIL'::CHARACTER VARYING])::TEXT[])),
    category_id VARCHAR(36)
        CONSTRAINT fk2kcoxgk2mh9qxqvwdfpgkpuxn
            REFERENCES tb_category
);

COMMENT ON TABLE tb_notification IS '알림';

COMMENT ON COLUMN tb_notification.id IS '식별자';

COMMENT ON COLUMN tb_notification.created_by IS '생성자';

COMMENT ON COLUMN tb_notification.created_date IS '생성일시';

COMMENT ON COLUMN tb_notification.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_notification.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_notification.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_notification.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_notification.version IS '버전';

COMMENT ON COLUMN tb_notification.type IS '유형';

COMMENT ON COLUMN tb_notification.category_id IS '카테고리 식별자';

ALTER TABLE tb_notification
    OWNER TO modoo_admin;

CREATE TABLE tb_reply
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    parent_id VARCHAR(36)
        CONSTRAINT fk_tb_reply_parent
            REFERENCES tb_reply,
    content TEXT,
    is_public BOOLEAN NOT NULL,
    article_id VARCHAR(36)
        CONSTRAINT fkhww2jr5mxv1gllrramxu6qx7l
            REFERENCES tb_article
);

COMMENT ON TABLE tb_reply IS '댓글';

COMMENT ON COLUMN tb_reply.id IS '식별자';

COMMENT ON COLUMN tb_reply.created_by IS '생성자';

COMMENT ON COLUMN tb_reply.created_date IS '생성일시';

COMMENT ON COLUMN tb_reply.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_reply.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_reply.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_reply.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_reply.version IS '버전';

COMMENT ON COLUMN tb_reply.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_reply.content IS '본문';

COMMENT ON COLUMN tb_reply.is_public IS '공개여부';

COMMENT ON COLUMN tb_reply.article_id IS '게시글 식별자';

ALTER TABLE tb_reply
    OWNER TO modoo_admin;

CREATE TABLE tb_reply_attachment
(
    reply_id VARCHAR(36) NOT NULL
        CONSTRAINT fkpq67elr2agb7vo831er1dat7m
            REFERENCES tb_reply,
    attachment_id VARCHAR(36) NOT NULL
        CONSTRAINT fkicavqvh7ebno91q5vf6dyrk1q
            REFERENCES tb_attachment,
    PRIMARY KEY (reply_id, attachment_id)
);

ALTER TABLE tb_reply_attachment
    OWNER TO modoo_admin;

CREATE TABLE tb_user
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    contact_number VARCHAR(255),
    credentials_expired_date TIMESTAMP(6),
    email_domain VARCHAR(255),
    email_id VARCHAR(255),
    expired_date TIMESTAMP(6),
    locked_date TIMESTAMP(6),
    name VARCHAR(255),
    password VARCHAR(255),
    username VARCHAR(255) NOT NULL
        CONSTRAINT uk4wv83hfajry5tdoamn8wsqa6x
            UNIQUE
);

COMMENT ON TABLE tb_user IS '계정';

COMMENT ON COLUMN tb_user.id IS '식별자';

COMMENT ON COLUMN tb_user.created_by IS '생성자';

COMMENT ON COLUMN tb_user.created_date IS '생성일시';

COMMENT ON COLUMN tb_user.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_user.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_user.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_user.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_user.version IS '버전';

COMMENT ON COLUMN tb_user.contact_number IS '연락처';

COMMENT ON COLUMN tb_user.credentials_expired_date IS '패스워드 만료 일시';

COMMENT ON COLUMN tb_user.email_domain IS '이메일';

COMMENT ON COLUMN tb_user.expired_date IS '만료 일시';

COMMENT ON COLUMN tb_user.locked_date IS '잠금 일시';

COMMENT ON COLUMN tb_user.name IS '이름';

COMMENT ON COLUMN tb_user.password IS '패스워드';

COMMENT ON COLUMN tb_user.username IS '계정명';

ALTER TABLE tb_user
    OWNER TO modoo_admin;

CREATE TABLE tb_user_role
(
    user_id VARCHAR(36) NOT NULL
        CONSTRAINT fkc01a0eof42wgqi1osr6i13g20
            REFERENCES tb_user,
    role_id VARCHAR(36) NOT NULL
        CONSTRAINT fkpti0ht0f3s33ddpeoqqyey0am
            REFERENCES tb_role,
    PRIMARY KEY (user_id, role_id)
);

ALTER TABLE tb_user_role
    OWNER TO modoo_admin;

CREATE TABLE tb_view
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    article_id VARCHAR(36)
        CONSTRAINT fkpul2cbopi5fdutkif11oj9l0p
            REFERENCES tb_article
);

COMMENT ON COLUMN tb_view.id IS '식별자';

COMMENT ON COLUMN tb_view.created_by IS '생성자';

COMMENT ON COLUMN tb_view.created_date IS '생성일시';

COMMENT ON COLUMN tb_view.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_view.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_view.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_view.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_view.version IS '버전';

ALTER TABLE tb_view
    OWNER TO modoo_admin;

CREATE TABLE tb_vote
(
    id VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by VARCHAR(255),
    created_date TIMESTAMP(6),
    created_ip VARCHAR(45),
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip VARCHAR(45),
    version INTEGER NOT NULL,
    type VARCHAR(255)
        CONSTRAINT tb_vote_type_check
            CHECK ((type)::TEXT = ANY ((ARRAY ['APPROVE'::CHARACTER VARYING, 'DISAPPROVE'::CHARACTER VARYING])::TEXT[]))
);

COMMENT ON COLUMN tb_vote.id IS '식별자';

COMMENT ON COLUMN tb_vote.created_by IS '생성자';

COMMENT ON COLUMN tb_vote.created_date IS '생성일시';

COMMENT ON COLUMN tb_vote.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_vote.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_vote.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_vote.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_vote.version IS '버전';

COMMENT ON COLUMN tb_vote.type IS '타입';

ALTER TABLE tb_vote
    OWNER TO modoo_admin;

CREATE TABLE tb_article_vote
(
    article_id VARCHAR(36) NOT NULL
        CONSTRAINT fkroupxarwyu63jd8slgymq6lrt
            REFERENCES tb_article,
    vote_id VARCHAR(36) NOT NULL
        CONSTRAINT fk5v042xebfoadkyq91hjc8y0rf
            REFERENCES tb_vote,
    PRIMARY KEY (article_id, vote_id)
);

ALTER TABLE tb_article_vote
    OWNER TO modoo_admin;

CREATE TABLE tb_reply_vote
(
    reply_id VARCHAR(36) NOT NULL
        CONSTRAINT fk6w3o0jmds6tukupow7x0axrgn
            REFERENCES tb_reply,
    vote_id VARCHAR(36) NOT NULL
        CONSTRAINT fkic5jhpmyhmh4adxophg2id02
            REFERENCES tb_vote,
    PRIMARY KEY (reply_id, vote_id)
);

ALTER TABLE tb_reply_vote
    OWNER TO modoo_admin;


```