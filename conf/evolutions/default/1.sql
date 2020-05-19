
# --- !Ups

CREATE TABLE role (
   id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   name VARCHAR
);

INSERT INTO role(name)
VALUES ('user'),
       ('admin');

CREATE TABLE "user" (
 id INTEGER PRIMARY KEY,
 email TEXT NOT NULL UNIQUE,
 first_name TEXT NOT NULL,
 last_name TEXT NOT NULL,
 password TEXT NOT NULL,
 role_id INT NOT NULL,
 FOREIGN KEY (role_id) REFERENCES role (id)
     ON UPDATE CASCADE
     ON DELETE RESTRICT
);

CREATE TABLE "login_info" (
  id           INTEGER PRIMARY KEY,
  provider_id  TEXT,
  provider_key TEXT
);

CREATE TABLE "user_login_info" (
   user_id       INTEGER NOT NULL,
   login_info_id INTEGER NOT NULL,
   FOREIGN KEY (user_id)
       REFERENCES user (id)
       ON UPDATE CASCADE
       ON DELETE RESTRICT,
   FOREIGN KEY (login_info_id)
       REFERENCES login_info (id)
       ON UPDATE CASCADE
       ON DELETE RESTRICT
);

CREATE TABLE "oauth2_info" (
   id            INTEGER PRIMARY KEY,
   access_token  TEXT NOT NULL,
   token_type    TEXT,
   expires_in    INTEGER,
   refresh_token TEXT,
   login_info_id INTEGER NOT NULL,
   FOREIGN KEY (login_info_id)
       REFERENCES login_info (id)
       ON UPDATE CASCADE
       ON DELETE RESTRICT
);



create table "category" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" TEXT NOT NULL
);

CREATE TABLE "subcategory"(
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "name" VARCHAR NOT NULL,
  "category" INT NOT NULL,
  FOREIGN KEY("category") references category(id)
);

CREATE TABLE "product" (
   "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   "name" VARCHAR NOT NULL,
   "description" TEXT NOT NULL,
   "price" INT NOT NULL,
   "subcategory" INT NOT NULL,
   FOREIGN KEY("subcategory") references subcategory(id)
);

CREATE TABLE "review" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "title" VARCHAR NOT NULL,
  "content" TEXT NOT NULL,
  "product" INT NOT NULL,
  FOREIGN KEY("product") references product(id)
);

CREATE TABLE "wishlist" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "user" INTEGER,
  "quantity" INT NOT NULL,
  "product" INT NOT NULL,
  FOREIGN KEY("product") references product(id),
  FOREIGN KEY (user)
      REFERENCES user (id)
      ON UPDATE CASCADE
      ON DELETE RESTRICT
);

CREATE TABLE "basket" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "user" INTEGER,
  "quantity" INT NOT NULL,
  "product" INT NOT NULL,
  FOREIGN KEY("product") references product(id),
  FOREIGN KEY (user)
      REFERENCES user (id)
      ON UPDATE CASCADE
      ON DELETE RESTRICT
);

CREATE TABLE "order" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "user" INTEGER,
  "status" TEXT NOT NULL,
  "address" INT NOT NULL,
  FOREIGN KEY("address") references orderad(id)
);

CREATE TABLE "orderprod" (
   "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   "name" VARCHAR NOT NULL,
   "price" INT NOT NULL,
   "quantity" INT NOT NULL,
   "order" INT NOT NULL,
   FOREIGN KEY("order") references "order" (id)
);

CREATE TABLE "orderad" (
   "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   "country" VARCHAR NOT NULL,
   "city" VARCHAR NOT NULL,
   "street" VARCHAR NOT NULL,
   "number" VARCHAR NOT NULL
);

CREATE TABLE "payment" (
   "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   "number" VARCHAR NOT NULL,
   "name" VARCHAR NOT NULL,
   "date" VARCHAR NOT NULL,
   "code" VARCHAR NOT NULL,
   "order" INT NOT NULL,
   FOREIGN KEY("order") references "order"(id)
);


# --- !Downs




DROP TABLE "payment";
DROP TABLE "orderad";
DROP TABLE "orderprod";
DROP TABLE "order";
DROP TABLE "basket";
DROP TABLE "wishlist";
DROP TABLE "review";
DROP TABLE "product";
DROP TABLE "subcategory";
DROP TABLE "category";


DROP TABLE oauth2_info;
DROP TABLE user_login_info;
DROP TABLE login_info;
DROP TABLE user;
DROP TABLE role;