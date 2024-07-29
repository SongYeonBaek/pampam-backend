INSERT INTO category (idx, category_type)
VALUES
    (1, 'Vegetable'),
    (2, 'Nut'),
    (3, 'Grain'),
    (4, 'Mushroom'),
    (5, 'Fruit'),
    (6, 'SeaFood'),
    (7, 'DriedFish'),
    (8, 'Meat'),
    (9, 'MilkProducts'),
    (10, 'Drink')
    ON DUPLICATE KEY UPDATE idx = idx;
