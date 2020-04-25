
# --- !Ups

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
 FOREIGN KEY("product") references product(id)
);

CREATE TABLE "basket" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "user" INTEGER,
 "quantity" INT NOT NULL,
 "product" INT NOT NULL,
 FOREIGN KEY("product") references product(id)
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
   "code" INT NOT NULL,
   "order" INT NOT NULL,
   FOREIGN KEY("order") references "order"(id)
);


# --- !Downs

DROP TABLE "category";
DROP TABLE "subcategory";
DROP TABLE "product";
DROP TABLE "review";
DROP TABLE "wishlist";
DROP TABLE "basket";
DROP TABLE "order";
DROP TABLE "orderprod";
DROP TABLE "orderad";
DROP TABLE "payment";