create table public.tb_attachment
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    mime_type          varchar(255),
    name               varchar(255),
    original_name      varchar(255),
    path_name          varchar(255),
    reference_id       varchar(36),
    size               bigint      not null
);

comment on table public.tb_attachment is '첨부파일';

comment on column public.tb_attachment.id is '식별자';

comment on column public.tb_attachment.created_by is '생성자';

comment on column public.tb_attachment.created_date is '생성일시';

comment on column public.tb_attachment.created_ip is '생성 IP';

comment on column public.tb_attachment.last_modified_by is '수정자';

comment on column public.tb_attachment.last_modified_date is '수정일시';

comment on column public.tb_attachment.last_modified_ip is '수정 IP';

comment on column public.tb_attachment.version is '버전';

comment on column public.tb_attachment.mime_type is 'MIME 타입';

comment on column public.tb_attachment.name is '이름';

comment on column public.tb_attachment.original_name is '원본이름';

comment on column public.tb_attachment.path_name is '경로명';

comment on column public.tb_attachment.reference_id is '참조 식별자';

comment on column public.tb_attachment.size is '크기';

alter table public.tb_attachment
    owner to seesaw_admin;

create table public.tb_code
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    parent_id          varchar(255)
        constraint fki9nleldk9xadc7bwld6xejtse
            references public.tb_code
        constraint fk_tb_code_parent
            references public.tb_code,
    description        varchar(255),
    name               varchar(255),
    sequence           integer
);

comment on table public.tb_code is '코드';

comment on column public.tb_code.id is '식별자';

comment on column public.tb_code.created_by is '생성자';

comment on column public.tb_code.created_date is '생성일시';

comment on column public.tb_code.created_ip is '생성 IP';

comment on column public.tb_code.last_modified_by is '수정자';

comment on column public.tb_code.last_modified_date is '수정일시';

comment on column public.tb_code.last_modified_ip is '수정 IP';

comment on column public.tb_code.version is '버전';

comment on column public.tb_code.parent_id is '부모 식별자';

comment on column public.tb_code.description is '설명';

comment on column public.tb_code.name is '이름';

comment on column public.tb_code.sequence is '순서';

alter table public.tb_code
    owner to seesaw_admin;

create index idx58lcpwy3xs74vptvofy4utqfs
    on public.tb_code (parent_id);

create table public.tb_menu
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    parent_id          varchar(255)
        constraint fkf6cync8rfoqw0tl3s3q8s1les
            references public.tb_menu
        constraint fk_tb_menu_parent
            references public.tb_menu,
    name               varchar(255),
    sequence           integer,
    uri                varchar(255)
);

comment on table public.tb_menu is '메뉴';

comment on column public.tb_menu.id is '식별자';

comment on column public.tb_menu.created_by is '생성자';

comment on column public.tb_menu.created_date is '생성일시';

comment on column public.tb_menu.created_ip is '생성 IP';

comment on column public.tb_menu.last_modified_by is '수정자';

comment on column public.tb_menu.last_modified_date is '수정일시';

comment on column public.tb_menu.last_modified_ip is '수정 IP';

comment on column public.tb_menu.version is '버전';

comment on column public.tb_menu.parent_id is '부모 식별자';

comment on column public.tb_menu.name is '이름';

comment on column public.tb_menu.sequence is '순번';

comment on column public.tb_menu.uri is 'URI';

alter table public.tb_menu
    owner to seesaw_admin;

create index idx1gdkb9wq1hturnwh8s9iv5tre
    on public.tb_menu (parent_id);

create table public.tb_remember_me_token
(
    series    varchar(255) not null
        primary key,
    last_used timestamp(6) not null,
    token     varchar(64)  not null,
    username  varchar(64)  not null
);

alter table public.tb_remember_me_token
    owner to seesaw_admin;

create table public.tb_role
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    alias              varchar(255),
    name               varchar(255)
        constraint uq_tb_role_name
            unique
);

comment on table public.tb_role is '역할';

comment on column public.tb_role.id is '식별자';

comment on column public.tb_role.created_by is '생성자';

comment on column public.tb_role.created_date is '생성일시';

comment on column public.tb_role.created_ip is '생성 IP';

comment on column public.tb_role.last_modified_by is '수정자';

comment on column public.tb_role.last_modified_date is '수정일시';

comment on column public.tb_role.last_modified_ip is '수정 IP';

comment on column public.tb_role.version is '버전';

comment on column public.tb_role.alias is '별칭';

comment on column public.tb_role.name is '이름';

alter table public.tb_role
    owner to seesaw_admin;

create table public.tb_menu_role
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    menu_id            varchar(255)
        constraint fknqcdwb2ao2sjwscxjecanokv2
            references public.tb_menu
        constraint fk_tb_menu_role
            references public.tb_menu,
    role_id            varchar(255)
        constraint fkinu5og730ohayhatwqu3x2cju
            references public.tb_role
        constraint fk_tb_role_menu
            references public.tb_role
);

comment on table public.tb_menu_role is '메뉴 역할 매핑';

comment on column public.tb_menu_role.id is '식별자';

comment on column public.tb_menu_role.created_by is '생성자';

comment on column public.tb_menu_role.created_date is '생성일시';

comment on column public.tb_menu_role.created_ip is '생성 IP';

comment on column public.tb_menu_role.last_modified_by is '수정자';

comment on column public.tb_menu_role.last_modified_date is '수정일시';

comment on column public.tb_menu_role.last_modified_ip is '수정 IP';

comment on column public.tb_menu_role.version is '버전';

comment on column public.tb_menu_role.menu_id is '메뉴';

comment on column public.tb_menu_role.role_id is '역할';

alter table public.tb_menu_role
    owner to seesaw_admin;

create index idxmkcqoe7x5yqjfyegr8jjkre80
    on public.tb_menu_role (menu_id);

create index idxk6trg0ehpbdv2d57poob8acyy
    on public.tb_menu_role (role_id);

create table public.tb_site
(
    id                    varchar(36) not null
        primary key,
    created_by            varchar(255),
    created_date          timestamp(6),
    created_ip            varchar(45),
    last_modified_by      varchar(255),
    last_modified_date    timestamp(6),
    last_modified_ip      varchar(45),
    version               integer     not null,
    parent_id             varchar(255)
        constraint fkp36e7ojksygy0juaptdvnfonv
            references public.tb_site
        constraint fk_tb_site_parent
            references public.tb_site,
    address               varchar(255),
    zipcode               varchar(255),
    contact_number        varchar(255),
    content               text,
    description           varchar(255),
    distribution_code     varchar(255),
    domain_name           varchar(255)
        constraint ukhn1qotpfshhu6cuu0seckgfu0
            unique,
    image_exposed         boolean     not null,
    intro                 varchar(255),
    name                  varchar(255),
    search_engine_exposed boolean     not null,
    tags                  varchar(255)
);

comment on table public.tb_site is '사이트';

comment on column public.tb_site.id is '식별자';

comment on column public.tb_site.created_by is '생성자';

comment on column public.tb_site.created_date is '생성일시';

comment on column public.tb_site.created_ip is '생성 IP';

comment on column public.tb_site.last_modified_by is '수정자';

comment on column public.tb_site.last_modified_date is '수정일시';

comment on column public.tb_site.last_modified_ip is '수정 IP';

comment on column public.tb_site.version is '버전';

comment on column public.tb_site.parent_id is '부모 식별자';

comment on column public.tb_site.address is '주소';

comment on column public.tb_site.contact_number is '연락처';

comment on column public.tb_site.content is '본문';

comment on column public.tb_site.description is '설명';

comment on column public.tb_site.distribution_code is '분류코드';

comment on column public.tb_site.domain_name is '도메인이름';

comment on column public.tb_site.image_exposed is '이미지 노출여부';

comment on column public.tb_site.intro is '소개글';

comment on column public.tb_site.name is '이름';

comment on column public.tb_site.search_engine_exposed is '검색엔진 노출여부';

comment on column public.tb_site.tags is '태그';

alter table public.tb_site
    owner to seesaw_admin;

create index idxlhb3xbfqhyt9eiaauwuw60qfk
    on public.tb_site (parent_id);

create table public.tb_category
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    parent_id          varchar(255)
        constraint fk26cfrw5y75d43809fv18hx313
            references public.tb_category
        constraint fk_tb_category_parent
            references public.tb_category,
    description        varchar(255),
    exposed            boolean     not null,
    name               varchar(255),
    sequence           integer,
    site_exposed       boolean     not null,
    site_exposed_order integer     not null,
    site_id            varchar(255)
        constraint fkfimlfw9i9ouminiv7n2lcxwiu
            references public.tb_site
        constraint fk_tb_category_site
            references public.tb_site,
    type               varchar(255)
        constraint tb_category_type_check
            check ((type)::text = ANY
        (ARRAY [('NONE'::character varying)::text, ('STATIC_CONTENT'::character varying)::text, ('BOARD'::character varying)::text, ('QNA'::character varying)::text, ('SCHEDULE'::character varying)::text, ('STORE'::character varying)::text, ('BUSINESS'::character varying)::text]))
    );

comment on table public.tb_category is '카테고리';

comment on column public.tb_category.id is '식별자';

comment on column public.tb_category.created_by is '생성자';

comment on column public.tb_category.created_date is '생성일시';

comment on column public.tb_category.created_ip is '생성 IP';

comment on column public.tb_category.last_modified_by is '수정자';

comment on column public.tb_category.last_modified_date is '수정일시';

comment on column public.tb_category.last_modified_ip is '수정 IP';

comment on column public.tb_category.version is '버전';

comment on column public.tb_category.parent_id is '부모 식별자';

comment on column public.tb_category.description is '설명';

comment on column public.tb_category.exposed is '노출여부';

comment on column public.tb_category.name is '이름';

comment on column public.tb_category.sequence is '순서';

comment on column public.tb_category.site_exposed is '사이트 노출여부';

comment on column public.tb_category.site_exposed_order is '사이트 노출순서';

comment on column public.tb_category.site_id is '사이트 식별자';

comment on column public.tb_category.type is '타입';

alter table public.tb_category
    owner to seesaw_admin;

create index idxboklbhalsd7ctvf6n2h2juqvw
    on public.tb_category (parent_id);

create index idxoffi6acwtkwnp7ia1nmk1dq1l
    on public.tb_category (site_id);

create table public.tb_article
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    parent_id          varchar(255)
        constraint fkln2e0b6gt16fmnn2nvjy1sivi
            references public.tb_article
        constraint fk_tb_article_parent
            references public.tb_article,
    category_id        varchar(255)
        constraint fkjl0ctedle9lho18jeg61brkqv
            references public.tb_category
        constraint fk_tb_article_category
            references public.tb_category,
    content            text,
    exposed            boolean     not null,
    fixed              boolean     not null,
    fixed_order        integer,
    title              varchar(255),
    type               varchar(255)
        constraint tb_article_type_check
            check ((type)::text = ANY
        (ARRAY [('MAP'::character varying)::text, ('HTML'::character varying)::text, ('CAROUSEL'::character varying)::text, ('BUTTON_GROUP'::character varying)::text, ('IMAGE'::character varying)::text, ('TABLE'::character varying)::text, ('VIDEO'::character varying)::text]))
    );

comment on table public.tb_article is '게시글';

comment on column public.tb_article.id is '식별자';

comment on column public.tb_article.created_by is '생성자';

comment on column public.tb_article.created_date is '생성일시';

comment on column public.tb_article.created_ip is '생성 IP';

comment on column public.tb_article.last_modified_by is '수정자';

comment on column public.tb_article.last_modified_date is '수정일시';

comment on column public.tb_article.last_modified_ip is '수정 IP';

comment on column public.tb_article.version is '버전';

comment on column public.tb_article.parent_id is '부모 식별자';

comment on column public.tb_article.category_id is '카테고리 식별자';

comment on column public.tb_article.content is '본문';

comment on column public.tb_article.exposed is '노출여부';

comment on column public.tb_article.fixed is '고정여부';

comment on column public.tb_article.fixed_order is '고정순서';

comment on column public.tb_article.title is '제목';

comment on column public.tb_article.type is '타입';

alter table public.tb_article
    owner to seesaw_admin;

create index idx3ro5v7u7ygn6wsl83pcxsm6a7
    on public.tb_article (parent_id);

create index idx7agj6x250qwntq68jyksh6323
    on public.tb_article (category_id);

create table public.tb_notification
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    category_id        varchar(36)
        constraint fk_tb_notification_category
            references public.tb_category,
    type               varchar(255)
        constraint tb_notification_type_check
            check ((type)::text = ANY
        (ARRAY [('TOKTOK'::character varying)::text, ('SMS'::character varying)::text, ('EMAIL'::character varying)::text]))
    );

comment on table public.tb_notification is '알림';

comment on column public.tb_notification.id is '식별자';

comment on column public.tb_notification.created_by is '생성자';

comment on column public.tb_notification.created_date is '생성일시';

comment on column public.tb_notification.created_ip is '생성 IP';

comment on column public.tb_notification.last_modified_by is '수정자';

comment on column public.tb_notification.last_modified_date is '수정일시';

comment on column public.tb_notification.last_modified_ip is '수정 IP';

comment on column public.tb_notification.version is '버전';

comment on column public.tb_notification.category_id is '카테고리 식별자';

comment on column public.tb_notification.type is '유형';

alter table public.tb_notification
    owner to seesaw_admin;

create table public.tb_reply
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    parent_id          varchar(255)
        constraint fklv3fdrd0o4p3pjfw95xe9mshm
            references public.tb_reply
        constraint fk_tb_reply_parent
            references public.tb_reply,
    article_id         varchar(255)
        constraint fkhww2jr5mxv1gllrramxu6qx7l
            references public.tb_article
        constraint fk_tb_reply_article
            references public.tb_article,
    content            text,
    exposed            boolean     not null
);

comment on table public.tb_reply is '댓글';

comment on column public.tb_reply.id is '식별자';

comment on column public.tb_reply.created_by is '생성자';

comment on column public.tb_reply.created_date is '생성일시';

comment on column public.tb_reply.created_ip is '생성 IP';

comment on column public.tb_reply.last_modified_by is '수정자';

comment on column public.tb_reply.last_modified_date is '수정일시';

comment on column public.tb_reply.last_modified_ip is '수정 IP';

comment on column public.tb_reply.version is '버전';

comment on column public.tb_reply.parent_id is '부모 식별자';

comment on column public.tb_reply.article_id is '게시글';

comment on column public.tb_reply.content is '본문';

comment on column public.tb_reply.exposed is '노출여부';

alter table public.tb_reply
    owner to seesaw_admin;

create index idxp4re47n8emupok70ofqvb0e6j
    on public.tb_reply (parent_id);

create index idxo1vb6kreru79mo2unx8gdtxpa
    on public.tb_reply (article_id);

create table public.tb_user
(
    id                       varchar(36)  not null
        primary key,
    created_by               varchar(255),
    created_date             timestamp(6),
    created_ip               varchar(45),
    last_modified_by         varchar(255),
    last_modified_date       timestamp(6),
    last_modified_ip         varchar(45),
    version                  integer      not null,
    contact_number           varchar(255),
    credentials_expired_date timestamp(6),
    email_domain             varchar(255),
    email_id                 varchar(255),
    expired_date             timestamp(6),
    locked_date              timestamp(6),
    name                     varchar(255),
    password                 varchar(255),
    username                 varchar(255) not null
        constraint uk4wv83hfajry5tdoamn8wsqa6x
            unique
);

comment on table public.tb_user is '계정';

comment on column public.tb_user.id is '식별자';

comment on column public.tb_user.created_by is '생성자';

comment on column public.tb_user.created_date is '생성일시';

comment on column public.tb_user.created_ip is '생성 IP';

comment on column public.tb_user.last_modified_by is '수정자';

comment on column public.tb_user.last_modified_date is '수정일시';

comment on column public.tb_user.last_modified_ip is '수정 IP';

comment on column public.tb_user.version is '버전';

comment on column public.tb_user.contact_number is '연락처';

comment on column public.tb_user.credentials_expired_date is '패스워드 만료 일시';

comment on column public.tb_user.email_domain is '이메일';

comment on column public.tb_user.expired_date is '만료 일시';

comment on column public.tb_user.locked_date is '잠금 일시';

comment on column public.tb_user.name is '이름';

comment on column public.tb_user.password is '패스워드';

comment on column public.tb_user.username is '계정명';

alter table public.tb_user
    owner to seesaw_admin;

create table public.tb_role_mapping
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    role_id            varchar(255)
        constraint fkfgkvixw8comuimlxxawnx7py4
            references public.tb_role
        constraint fk_tb_mapping_role
            references public.tb_role,
    site_id            varchar(255)
        constraint fkla3gxx07kkum7pmjf93gsvovy
            references public.tb_site
        constraint fk_tb_mapping_site
            references public.tb_site,
    user_id            varchar(255)
        constraint fkg0hvhycik7bc00nr052bqp2xi
            references public.tb_user
        constraint fk_tb_mapping_user
            references public.tb_user
);

comment on table public.tb_role_mapping is '역할 매핑';

comment on column public.tb_role_mapping.id is '식별자';

comment on column public.tb_role_mapping.created_by is '생성자';

comment on column public.tb_role_mapping.created_date is '생성일시';

comment on column public.tb_role_mapping.created_ip is '생성 IP';

comment on column public.tb_role_mapping.last_modified_by is '수정자';

comment on column public.tb_role_mapping.last_modified_date is '수정일시';

comment on column public.tb_role_mapping.last_modified_ip is '수정 IP';

comment on column public.tb_role_mapping.version is '버전';

comment on column public.tb_role_mapping.role_id is '역할 식별자';

comment on column public.tb_role_mapping.site_id is '사이트 식별자';

comment on column public.tb_role_mapping.user_id is '계정 식별자';

alter table public.tb_role_mapping
    owner to seesaw_admin;

create index idxg4r4ulxvs9st8hpsyq4byfk7e
    on public.tb_role_mapping (role_id);

create index idxql518i4x1xu4cdt1jv1wjb4b9
    on public.tb_role_mapping (user_id, site_id);

create table public.tb_vote
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    approved           boolean     not null,
    reference_id       varchar(36)
);

comment on table public.tb_vote is '투표';

comment on column public.tb_vote.id is '식별자';

comment on column public.tb_vote.created_by is '생성자';

comment on column public.tb_vote.created_date is '생성일시';

comment on column public.tb_vote.created_ip is '생성 IP';

comment on column public.tb_vote.last_modified_by is '수정자';

comment on column public.tb_vote.last_modified_date is '수정일시';

comment on column public.tb_vote.last_modified_ip is '수정 IP';

comment on column public.tb_vote.version is '버전';

comment on column public.tb_vote.approved is '찬성여부';

comment on column public.tb_vote.reference_id is '참조 식별자';

alter table public.tb_vote
    owner to seesaw_admin;

create table public.tb_permission
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    mask               integer     not null,
    role_id            varchar(36),
    target_id          varchar(36)
);

comment on table public.tb_permission is '권한';

comment on column public.tb_permission.id is '식별자';

comment on column public.tb_permission.created_by is '생성자';

comment on column public.tb_permission.created_date is '생성일시';

comment on column public.tb_permission.created_ip is '생성 IP';

comment on column public.tb_permission.last_modified_by is '수정자';

comment on column public.tb_permission.last_modified_date is '수정일시';

comment on column public.tb_permission.last_modified_ip is '수정 IP';

comment on column public.tb_permission.version is '버전';

comment on column public.tb_permission.mask is '비트마스크';

comment on column public.tb_permission.role_id is '역할 식별자';

comment on column public.tb_permission.target_id is '대상 식별자';

alter table public.tb_permission
    owner to seesaw_admin;

create table public.tb_view
(
    id                 varchar(36) not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer     not null,
    article_id         varchar(255)
        constraint fkpul2cbopi5fdutkif11oj9l0p
            references public.tb_article
);

comment on table public.tb_view is '뷰';

comment on column public.tb_view.id is '식별자';

comment on column public.tb_view.created_by is '생성자';

comment on column public.tb_view.created_date is '생성일시';

comment on column public.tb_view.created_ip is '생성 IP';

comment on column public.tb_view.last_modified_by is '수정자';

comment on column public.tb_view.last_modified_date is '수정일시';

comment on column public.tb_view.last_modified_ip is '수정 IP';

comment on column public.tb_view.version is '버전';

comment on column public.tb_view.article_id is '게시글 식별자';

alter table public.tb_view
    owner to seesaw_admin;

create index idx9u9mod1bbs09l13v08wu8xopo
    on public.tb_view (article_id);

create table public.tb_event
(
    id                 varchar(36)                 not null
        primary key
        constraint fkdsblxnufwd2ygf0h83nmoo3bd
            references public.tb_article,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer                     not null,
    dt_stamp           timestamp(6) with time zone not null,
    sequence           integer                     not null,
    description        text,
    dt_end             timestamp(6),
    dt_start           timestamp(6)                not null,
    duration           varchar(255),
    location           varchar(255),
    status             varchar(255)
        constraint tb_event_status_check
            check ((status)::text = ANY
        ((ARRAY ['TENTATIVE'::character varying, 'CONFIRMED'::character varying, 'CANCELLED'::character varying])::text[])),
    summary            text,
    tzid               varchar(255)
);

comment on table public.tb_event is '캘린더 이벤트';

comment on column public.tb_event.id is '식별자';

comment on column public.tb_event.created_by is '생성자';

comment on column public.tb_event.created_date is '생성일시';

comment on column public.tb_event.created_ip is '생성 IP';

comment on column public.tb_event.last_modified_by is '수정자';

comment on column public.tb_event.last_modified_date is '수정일시';

comment on column public.tb_event.last_modified_ip is '수정 IP';

comment on column public.tb_event.version is '버전';

comment on column public.tb_event.dt_stamp is '데이터 생성/수정 시점 기록';

comment on column public.tb_event.sequence is '변경 횟수 순서';

comment on column public.tb_event.description is '상세 설명';

comment on column public.tb_event.dt_end is '종료 일시';

comment on column public.tb_event.dt_start is '시작 일시';

comment on column public.tb_event.duration is '기간';

comment on column public.tb_event.location is '장소';

comment on column public.tb_event.status is '상태';

comment on column public.tb_event.summary is '요약/제목';

comment on column public.tb_event.tzid is '시간대 식별자';

alter table public.tb_event
    owner to seesaw_admin;

create table public.tb_event_attendees
(
    event_id          varchar(36) not null
        constraint fk72mdvycw7u6ugmcdsbj3hfe7n
            references public.tb_event,
    attendee_cn       varchar(255),
    attendee_email    varchar(255),
    attendee_role     varchar(255)
        constraint tb_event_attendees_attendee_role_check
            check ((attendee_role)::text = ANY
        ((ARRAY ['CHAIR'::character varying, 'REQ_PARTICIPANT'::character varying, 'OPT_PARTICIPANT'::character varying, 'NON_PARTICIPANT'::character varying])::text[])),
    attendee_partstat varchar(255)
        constraint tb_event_attendees_attendee_partstat_check
            check ((attendee_partstat)::text = ANY
                   ((ARRAY ['NEEDS_ACTION'::character varying, 'ACCEPTED'::character varying, 'DECLINED'::character varying, 'TENTATIVE'::character varying, 'DELEGATED'::character varying, 'COMPLETED'::character varying, 'IN_PROCESS'::character varying])::text[]))
);

alter table public.tb_event_attendees
    owner to seesaw_admin;

create table public.tb_journal
(
    id                 varchar(36)                 not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer                     not null,
    dt_stamp           timestamp(6) with time zone not null,
    sequence           integer                     not null,
    dt_start           timestamp(6),
    status             varchar(255)
        constraint tb_journal_status_check
            check ((status)::text = ANY
        ((ARRAY ['DRAFT'::character varying, 'FINAL'::character varying, 'CANCELLED'::character varying])::text[])),
    summary            text
);

comment on table public.tb_journal is '저널(Journal)';

comment on column public.tb_journal.id is '식별자';

comment on column public.tb_journal.created_by is '생성자';

comment on column public.tb_journal.created_date is '생성일시';

comment on column public.tb_journal.created_ip is '생성 IP';

comment on column public.tb_journal.last_modified_by is '수정자';

comment on column public.tb_journal.last_modified_date is '수정일시';

comment on column public.tb_journal.last_modified_ip is '수정 IP';

comment on column public.tb_journal.version is '버전';

comment on column public.tb_journal.dt_stamp is '데이터 생성/수정 시점 기록';

comment on column public.tb_journal.sequence is '변경 횟수 순서';

comment on column public.tb_journal.dt_start is '기준 일시';

comment on column public.tb_journal.status is '상태';

comment on column public.tb_journal.summary is '요약';

alter table public.tb_journal
    owner to seesaw_admin;

create table public.tb_journal_descriptions
(
    journal_id  varchar(36) not null
        constraint fkjd6h8oveh2aivhpanhjl9h370
            references public.tb_journal,
    description text
);

alter table public.tb_journal_descriptions
    owner to seesaw_admin;

create table public.tb_todo
(
    id                 varchar(36)                 not null
        primary key,
    created_by         varchar(255),
    created_date       timestamp(6),
    created_ip         varchar(45),
    last_modified_by   varchar(255),
    last_modified_date timestamp(6),
    last_modified_ip   varchar(45),
    version            integer                     not null,
    dt_stamp           timestamp(6) with time zone not null,
    sequence           integer                     not null,
    completed          timestamp(6),
    dt_start           timestamp(6),
    due                timestamp(6),
    percent_complete   integer,
    priority           integer,
    status             varchar(255)
        constraint tb_todo_status_check
            check ((status)::text = ANY
        ((ARRAY ['NEEDS_ACTION'::character varying, 'COMPLETED'::character varying, 'IN_PROCESS'::character varying, 'CANCELLED'::character varying])::text[])),
    summary            text
);

comment on table public.tb_todo is '할 일(Todo)';

comment on column public.tb_todo.id is '식별자';

comment on column public.tb_todo.created_by is '생성자';

comment on column public.tb_todo.created_date is '생성일시';

comment on column public.tb_todo.created_ip is '생성 IP';

comment on column public.tb_todo.last_modified_by is '수정자';

comment on column public.tb_todo.last_modified_date is '수정일시';

comment on column public.tb_todo.last_modified_ip is '수정 IP';

comment on column public.tb_todo.version is '버전';

comment on column public.tb_todo.dt_stamp is '데이터 생성/수정 시점 기록';

comment on column public.tb_todo.sequence is '변경 횟수 순서';

comment on column public.tb_todo.completed is '완료 일시';

comment on column public.tb_todo.dt_start is '시작 일시';

comment on column public.tb_todo.due is '마감 기한';

comment on column public.tb_todo.percent_complete is '진행률 (0-100)';

comment on column public.tb_todo.priority is '우선순위 (0-9)';

comment on column public.tb_todo.status is '상태';

comment on column public.tb_todo.summary is '요약';

alter table public.tb_todo
    owner to seesaw_admin;

create table public.tb_event_attendee
(
    event_id          varchar(36) not null
        constraint fkohhpc5prcuyb8sls3kvb9294k
            references public.tb_event,
    attendee_cn       varchar(255),
    attendee_email    varchar(255),
    attendee_role     varchar(255)
        constraint tb_event_attendee_attendee_role_check
            check ((attendee_role)::text = ANY
        ((ARRAY ['CHAIR'::character varying, 'REQ_PARTICIPANT'::character varying, 'OPT_PARTICIPANT'::character varying, 'NON_PARTICIPANT'::character varying])::text[])),
    attendee_partstat varchar(255)
        constraint tb_event_attendee_attendee_partstat_check
            check ((attendee_partstat)::text = ANY
                   ((ARRAY ['NEEDS_ACTION'::character varying, 'ACCEPTED'::character varying, 'DECLINED'::character varying, 'TENTATIVE'::character varying, 'DELEGATED'::character varying, 'COMPLETED'::character varying, 'IN_PROCESS'::character varying])::text[]))
);

alter table public.tb_event_attendee
    owner to seesaw_admin;

create table public.tb_journal_description
(
    journal_id  varchar(36) not null
        constraint fkmlqwsc66169idwqk34otclfd7
            references public.tb_journal,
    description text
);

alter table public.tb_journal_description
    owner to seesaw_admin;

