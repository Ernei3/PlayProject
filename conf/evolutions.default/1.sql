
# --- !Ups

create table CATEGORY (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" TEXT NOT NULL
    );




# --- !Downs

DROP TABLE CATEGORY;
