# --- !Ups

INSERT INTO "category"("name") VALUES("clothes");
INSERT INTO "category"("name") VALUES("books");

INSERT INTO "subcategory"("name", "category") VALUES ("trousers", 1);
INSERT INTO "subcategory"("name", "category") VALUES ("shirts", 1);
INSERT INTO "subcategory"("name", "category") VALUES ("SFF", 2);
INSERT INTO "subcategory"("name", "category") VALUES ("romance", 2);

INSERT INTO "product"("name", "description", "subcategory") VALUES ("The Way of Kings", "A book by Brandon Sanderson.", 3);
INSERT INTO "product"("name", "description", "subcategory") VALUES ("Words of Radiance", "A book by Brandon Sanderson.", 3);
INSERT INTO "product"("name", "description", "subcategory") VALUES ("jeans", "A pair of jeans.", 1);

INSERT INTO "review"("title", "content", "product") VALUES ("Brilliant!", "Amazing and outstanding!", 1);

INSERT INTO "wishlist"("user", "quantity", "product") VALUES (1, 1, 1);
INSERT INTO "wishlist"("user", "quantity", "product") VALUES (1, 1, 2);
INSERT INTO "wishlist"("user", "quantity", "product") VALUES (2, 3, 3);

INSERT INTO "basket"("user", "quantity", "product") VALUES (1, 4, 3);
INSERT INTO "basket"("user", "quantity", "product") VALUES (2, 1, 2);
INSERT INTO "basket"("user", "quantity", "product") VALUES (2, 2, 1);

# --- !Downs

DELETE FROM "category" WHERE name="clothes";
DELETE FROM "category" WHERE name="books";

DELETE FROM "subcategory" WHERE name="trousers";
DELETE FROM "subcategory" WHERE name="shirts";
DELETE FROM "subcategory" WHERE name="SFF";
DELETE FROM "subcategory" WHERE name="romance";

DELETE FROM "product" WHERE name="The Way of Kings";
DELETE FROM "product" WHERE name="Words of Radiance";
DELETE FROM "product" WHERE name="jeans";

DELETE FROM "review" WHERE name="Brilliant!";

DELETE FROM "wishlist" WHERE id=1;
DELETE FROM "wishlist" WHERE id=2;
DELETE FROM "wishlist" WHERE id=3;

DELETE FROM "basket" WHERE id=1;
DELETE FROM "basket" WHERE id=2;
DELETE FROM "basket" WHERE id=3;