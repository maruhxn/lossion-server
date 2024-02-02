create table auth_token
(
    created_at datetime(6),
    expired_at datetime(6),
    id         bigint not null auto_increment,
    member_id  bigint,
    updated_at datetime(6),
    payload    varchar(255),
    primary key (id)
) engine = InnoDB;

create table refresh_token
(
    created_at    datetime(6),
    id            bigint not null auto_increment,
    updated_at    datetime(6),
    account_id    varchar(255),
    refresh_token varchar(255),
    primary key (id)
) engine = InnoDB;

create table category
(
    created_at datetime(6),
    id         bigint      not null auto_increment,
    updated_at datetime(6),
    name       varchar(30) not null,
    primary key (id)
) engine = InnoDB;

create table comment
(
    created_at  datetime(6),
    id          bigint not null auto_increment,
    member_id   bigint,
    reply_to_id bigint,
    topic_id    bigint,
    updated_at  datetime(6),
    group_id    varchar(255),
    text        TEXT   not null,
    primary key (id)
) engine = InnoDB;

create table comment_favorite
(
    comment_id bigint,
    created_at datetime(6),
    id         bigint not null auto_increment,
    member_id  bigint,
    updated_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table topic_favorite
(
    created_at datetime(6),
    id         bigint not null auto_increment,
    member_id  bigint,
    topic_id   bigint,
    updated_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table member
(
    is_verified   bit                                     default 0 not null,
    created_at    datetime(6),
    id            bigint                                            not null auto_increment,
    updated_at    datetime(6),
    username      varchar(10)                                       not null,
    email         varchar(30)                                       not null,
    account_id    varchar(255)                                      not null,
    password      varchar(255),
    profile_image TEXT                                              not null,
    sns_id        varchar(255),
    provider      enum ('GOOGLE','KAKAO','NAVER','LOCAL') default 'LOCAL',
    role          enum ('ROLE_USER','ROLE_ADMIN')         default 'ROLE_USER',
    primary key (id)
) engine = InnoDB;

create table topic
(
    is_closed     bit    default 0 not null,
    author_id     bigint,
    category_id   bigint,
    closed_at     datetime(6)      not null,
    created_at    datetime(6),
    id            bigint           not null auto_increment,
    updated_at    datetime(6),
    view_count    bigint default 0 not null,
    description   TEXT             not null,
    first_choice  varchar(255)     not null,
    second_choice varchar(255)     not null,
    title         varchar(255)     not null,
    primary key (id)
) engine = InnoDB;

create table topic_image
(
    created_at    datetime(6),
    id            bigint       not null auto_increment,
    topic_id      bigint,
    updated_at    datetime(6),
    original_name varchar(255) not null,
    stored_name   TEXT         not null,
    primary key (id)
) engine = InnoDB;

create table vote
(
    vote_type  tinyint check (vote_type between 0 and 1),
    created_at datetime(6),
    id         bigint not null auto_increment,
    topic_id   bigint,
    updated_at datetime(6),
    voter_id   bigint,
    primary key (id)
) engine = InnoDB;

alter table category
    add constraint UK_46ccwnsi9409t36lurvtyljak unique (name);

alter table member
    add constraint UK_gc3jmn7c2abyo3wf6syln5t2i unique (username);

alter table member
    add constraint UK_mbmcqelty0fbrvxp1q58dn57t unique (email);

alter table member
    add constraint UK_i54h1gvvnejys85e9d9qo9f2u unique (account_id);

alter table member
    add constraint UK_l1qg4hpywwijprtn2b2wi5n4s unique (sns_id);

alter table auth_token
    add constraint FKc9g8oe0e9suanoar3rk9soadn
        foreign key (member_id)
            references member (id);

alter table comment
    add constraint FKmrrrpi513ssu63i2783jyiv9m
        foreign key (member_id)
            references member (id);

alter table comment
    add constraint FKqys9mdo2xp2p1848yk002bw8e
        foreign key (reply_to_id)
            references comment (id);

alter table comment
    add constraint FKo3bvevu9ua4w6f8qu2b177f16
        foreign key (topic_id)
            references topic (id);

alter table comment_favorite
    add constraint FKs2npfy5t6bgtubiheih8t2rn4
        foreign key (comment_id)
            references comment (id);

alter table comment_favorite
    add constraint FKtenaa3eebahgwdt5w80kpdvb7
        foreign key (member_id)
            references member (id);

alter table topic
    add constraint FK4skdqjud85qq0152o7flnc36w
        foreign key (author_id)
            references member (id);

alter table topic
    add constraint FK8n7r9utm8sjpdfstb4wcqd7qj
        foreign key (category_id)
            references category (id);

alter table topic_favorite
    add constraint FKbe4fjnywqi9mksydis6gwo0b6
        foreign key (member_id)
            references member (id);

alter table topic_favorite
    add constraint FKamsaocwa5q7sggfytlj2a438y
        foreign key (topic_id)
            references topic (id);

alter table topic_image
    add constraint FKpl6wf86575gyvqosor76lc7f
        foreign key (topic_id)
            references topic (id);

alter table vote
    add constraint FK9kvbfyud6x07p795f1r7iynq3
        foreign key (topic_id)
            references topic (id);

alter table vote
    add constraint FKkqlx1pm06wic6l5bvniuh1dmd
        foreign key (voter_id)
            references member (id);