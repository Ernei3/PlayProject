
# --- !Ups

CREATE TABLE role(
 id   INTEGER NOT NULL PRIMARY KEY,
 name VARCHAR
);

INSERT INTO role (id, name) VALUES (1, 'user'), (2, 'admin');

CREATE TABLE user(
 id         VARCHAR    NOT NULL PRIMARY KEY,
 first_name VARCHAR,
 last_name  VARCHAR,
 email      VARCHAR,
 role_id    INTEGER     NOT NULL,
 avatar_url VARCHAR,
 CONSTRAINT auth_user_role_id_fk FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE login_info(
   id           INTEGER NOT NULL PRIMARY KEY,
   provider_id  VARCHAR,
   provider_key VARCHAR
);

CREATE TABLE user_login_info(
    user_id       VARCHAR   NOT NULL,
    login_info_id INTEGER NOT NULL,
    CONSTRAINT auth_user_login_info_user_id_fk FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT auth_user_login_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES login_info(id)
);

CREATE TABLE oauth2_info (
 id            INTEGER NOT NULL PRIMARY KEY,
 access_token  VARCHAR   NOT NULL,
 token_type    VARCHAR,
 expires_in    INTEGER,
 refresh_token VARCHAR,
 login_info_id INTEGER    NOT NULL,
 CONSTRAINT auth_oauth2_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES login_info(id)
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
  "user" VARCHAR,
  "quantity" INT NOT NULL,
  "product" INT NOT NULL,
  FOREIGN KEY("product") references product(id),
  FOREIGN KEY (user) REFERENCES user (id)
      ON UPDATE CASCADE
      ON DELETE RESTRICT
);

CREATE TABLE "basket" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "user" VARCHAR,
  "quantity" INT NOT NULL,
  "product" INT NOT NULL,
  FOREIGN KEY("product") references product(id),
  FOREIGN KEY (user) REFERENCES user (id)
      ON UPDATE CASCADE
      ON DELETE RESTRICT
);

CREATE TABLE "orderad" (
   "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   "country" VARCHAR NOT NULL,
   "city" VARCHAR NOT NULL,
   "street" VARCHAR NOT NULL,
   "number" VARCHAR NOT NULL
);

CREATE TABLE "order" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "user" VARCHAR,
  "status" TEXT NOT NULL,
  "address" INT NOT NULL,
  FOREIGN KEY (user) REFERENCES user (id)
      ON UPDATE CASCADE
      ON DELETE RESTRICT
);

CREATE TABLE "orderprod" (
   "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   "name" VARCHAR NOT NULL,
   "price" INT NOT NULL,
   "quantity" INT NOT NULL,
   "order" INT NOT NULL,
   FOREIGN KEY("order") references "order" (id)
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
DROP TABLE "orderprod";
DROP TABLE "order";
DROP TABLE "orderad";
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