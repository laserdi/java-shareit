DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS feedbacks CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;


create table if not exists users
(
    id    bigint generated by default as identity primary key,
    name  varchar(255) not null,
    email varchar(512) not null,
    constraint UQ_USER_EMAIL unique (email)
);

create table if not exists requests
(
    id           bigint generated by default as identity primary key,
    description  varchar(512)             not null,
    requester_id bigint references users (id),
    created_date timestamp with time zone not null
);

create table if not exists items
(
    id           bigint generated by default as identity primary key,
    name         varchar(255) not null,
    description  varchar(512) not null,
    owner_id     bigint references users (id) on delete cascade,
    is_available boolean      not null,
    request_id   bigint references requests (id)
);

create table if not exists bookings
(
    id         bigint generated by default as identity primary key,
    item_id    integer references items (id) on delete cascade,
    booker_id  integer references users (id) on delete cascade,
    start_time timestamp without time zone not null,
    end_time   timestamp without time zone not null,
    status     varchar(16)                 not null
);

create table if not exists feedbacks
(
    id           bigint generated by default as identity primary key,
    content      varchar(512)                not null,
    item_id      bigint references items (id) on delete cascade,
    author_id    bigint references users (id) on delete cascade,
    created_date timestamp without time zone not null
)


