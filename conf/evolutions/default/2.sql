# --- !Ups

INSERT INTO "category"("name") VALUES("clothes");
INSERT INTO "category"("name") VALUES("books");

INSERT INTO "subcategory"("name", "category") VALUES ("trousers", 1);
INSERT INTO "subcategory"("name", "category") VALUES ("shirts", 1);
INSERT INTO "subcategory"("name", "category") VALUES ("SFF", 2);
INSERT INTO "subcategory"("name", "category") VALUES ("romance", 2);

INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("The Way of Kings", "A book by Brandon Sanderson.", 31, 3);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("Words of Radiance", "A book by Brandon Sanderson.", 32, 3);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("Oathbringer", "A book by Brandon Sanderson.", 32, 3);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("blue jeans", "A pair of blue jeans.", 20, 1);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("black jeans", "A pair of black jeans.", 21, 1);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("white shirt", "A white shirt.", 20, 2);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("red shirt", "A red shirt.", 22, 2);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("flower shirt", "A shirt with a flower pattern.", 23, 2);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("The Shadows Between Us", "A book by Tricia Levenseller.", 29, 4);
INSERT INTO "product"("name", "description", "price", "subcategory") VALUES ("Pride and Prejudice", "A book by Jane Austin.", 22, 4);

INSERT INTO "review"("title", "content", "product") VALUES ("Brilliant!", "Amazing and outstanding!", 1);
INSERT INTO "review"("title", "content", "product") VALUES ("Best book ever!", "I've never read anything better!", 1);

INSERT INTO "wishlist"("user", "quantity", "product") VALUES (1, 1, 1);
INSERT INTO "wishlist"("user", "quantity", "product") VALUES (1, 1, 2);
INSERT INTO "wishlist"("user", "quantity", "product") VALUES (2, 3, 3);

INSERT INTO "basket"("user", "quantity", "product") VALUES (1, 4, 4);
INSERT INTO "basket"("user", "quantity", "product") VALUES (2, 1, 2);
INSERT INTO "basket"("user", "quantity", "product") VALUES (2, 2, 1);
INSERT INTO "basket"("user", "quantity", "product") VALUES (3, 1, 1);
INSERT INTO "basket"("user", "quantity", "product") VALUES (3, 2, 4);
INSERT INTO "basket"("user", "quantity", "product") VALUES (3, 2, 5);


INSERT INTO "order"("user","status", "address") VALUES (1, "Delivered", 1);
INSERT INTO "order"("user","status", "address") VALUES (1, "Delivered", 1);
INSERT INTO "order"("user","status", "address") VALUES (2, "Accepted", 2);


INSERT INTO "orderprod"("name","price", "quantity", "order") VALUES ("Twilight", 28, 1, 1);
INSERT INTO "orderprod"("name","price", "quantity", "order") VALUES ("New Moon", 30, 1, 2);
INSERT INTO "orderprod"("name","price", "quantity", "order") VALUES ("Host", 31, 1, 2);
INSERT INTO "orderprod"("name","price", "quantity", "order") VALUES ("Roge One", 23, 1, 3);

INSERT INTO "orderad"("country","city", "street", "number") VALUES ("Poland", "Krakow", "Wielka", "22b");
INSERT INTO "orderad"("country","city", "street", "number") VALUES ("Poland", "Warsaw", "Lwowska", "21/3");


INSERT INTO "payment"("number","name", "date", "code", "order") VALUES ("1111111111111111", "Ala Makota", "2/25", "011", 1);
INSERT INTO "payment"("number","name", "date", "code", "order") VALUES ("1111111111111111", "Ala Makota", "2/25", "011", 2);
INSERT INTO "payment"("number","name", "date", "code", "order") VALUES ("2222222222222222", "Jan Kowalski", "2/25", "222", 3);

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

DELETE FROM "order" WHERE id=1;
DELETE FROM "order" WHERE id=2;



DELETE FROM "orderprod" WHERE id=1;
DELETE FROM "orderad" WHERE id=1;