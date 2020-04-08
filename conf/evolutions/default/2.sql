# --- !Ups

INSERT INTO "category"("name") VALUES("sample1");
INSERT INTO "category"("name") VALUES("sample2");

INSERT INTO "subcategory"("name", "category") VALUES ("sub1", 1);

# --- !Downs

DELETE FROM "category" WHERE name="sample1"";
DELETE FROM "category" WHERE name="sample2"";
DELETE FROM "subcategory" WHERE name="sub1";