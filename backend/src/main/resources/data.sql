INSERT INTO "product" ("id","name","price") VALUES ('0c244bd2-5ad5-487b-8eb6-099e665b0f69','product 1', 0.99);
INSERT INTO "product" ("id","name","price") VALUES ('96470a61-6b99-40f8-a45a-98516e7175d8','product 2', 20.99);

INSERT INTO "order" ("id","orderDateTime","product_id")  VALUES ('20f0a02c-9b6b-4c1c-8859-c70a0b78ae9a',TIMESTAMP WITH TIME ZONE '2024-03-01 23:55:54+01:00', '0c244bd2-5ad5-487b-8eb6-099e665b0f69');
INSERT INTO "order" ("id","orderDateTime","product_id")  VALUES ('21e113e8-906a-487b-adcb-849de0beb398',TIMESTAMP WITH TIME ZONE '2024-02-29 11:00:00+01:00','96470a61-6b99-40f8-a45a-98516e7175d8');