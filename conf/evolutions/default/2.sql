# --- !Ups

INSERT INTO "category"("name") VALUES("clothes");
INSERT INTO "category"("name") VALUES("books");

INSERT INTO "subcategory"("name", "category") VALUES ("trousers", 1);
INSERT INTO "subcategory"("name", "category") VALUES ("shirts", 1);
INSERT INTO "subcategory"("name", "category") VALUES ("SFF", 2);
INSERT INTO "subcategory"("name", "category") VALUES ("romance", 2);

INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("The Way of Kings", "A book by Brandon Sanderson.", 31, 3);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("Words of Radiance", "A book by Brandon Sanderson.", 32, 3);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("jeans", "A pair of jeans.", 20, 1);

INSERT INTO "review"("title", "content", "product") VALUES ("Brilliant!", "Amazing and outstanding!", 1);

INSERT INTO "wishlist"("user", "quantity", "product") VALUES (1, 1, 1);
INSERT INTO "wishlist"("user", "quantity", "product") VALUES (1, 1, 2);
INSERT INTO "wishlist"("user", "quantity", "product") VALUES (2, 3, 3);

INSERT INTO "basket"("user", "quantity", "product") VALUES (1, 4, 3);
INSERT INTO "basket"("user", "quantity", "product") VALUES (2, 1, 2);
INSERT INTO "basket"("user", "quantity", "product") VALUES (2, 2, 1);


INSERT INTO "order"("user","status") VALUES (1, "Delivered");
INSERT INTO "order"("user","status") VALUES (2, "Accepted");


INSERT INTO "orderprod"("name","price", "quantity", "order") VALUES ("Twilight", 28, 1, 1);
INSERT INTO "orderprod"("name","price", "quantity", "order") VALUES ("Roge One", 23, 1, 2);

INSERT INTO "orderad"("country","city", "street", "number", "order") VALUES ("Poland", "Krakow", "Wielka", "22b", 1);
INSERT INTO "orderad"("country","city", "street", "number", "order") VALUES ("Poland", "Warsaw", "Lwowska", "21/3", 2);


INSERT INTO "payment"("number","name", "date", "code", "order") VALUES ("1111111111111111", "Ala Makota", "2/25", 111, 1);
INSERT INTO "payment"("number","name", "date", "code", "order") VALUES ("2222222222222222", "Jan Kowalski", "2/25", 222, 2);

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

DELETE "order" WHERE id=1;
DELETE "order" WHERE id=2;



DELETE "orderprod" WHERE id=1;
DELETE "orderad" WHERE id=1;