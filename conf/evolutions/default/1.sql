
# --- !Ups

create table "category" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" TEXT NOT NULL
    );

CREATE TABLE "subcategory"(
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" VARCHAR NOT NULL,
 "category" INT NOT NULL,
 FOREIGN KEY(category) references category(id)
);

CREATE TABLE "product" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" VARCHAR NOT NULL,
 "description" TEXT NOT NULL,
 "subcategory" INT NOT NULL,
 FOREIGN KEY(subcategory) references subcategory(id)
);


# --- !Downs

DROP TABLE "category";
DROP TABLE "subcategory";
DROP TABLE "product";
