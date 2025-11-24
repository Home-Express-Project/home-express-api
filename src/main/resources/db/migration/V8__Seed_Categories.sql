-- ============================================================================
-- V8: Seed Categories and Sizes
-- ============================================================================
-- This migration adds default furniture categories and their sizes
-- to enable the transport pricing system to function properly.

-- ============================================================================
-- 1. CATEGORIES
-- ============================================================================

INSERT IGNORE INTO
    `categories` (
        `name`,
        `name_en`,
        `description`,
        `icon`,
        `default_weight_kg`,
        `default_volume_m3`,
        `default_length_cm`,
        `default_width_cm`,
        `default_height_cm`,
        `is_fragile_default`,
        `requires_disassembly_default`,
        `display_order`,
        `is_active`
    )
VALUES
    -- Furniture Categories
    (
        'Ghế sofa',
        'Sofa',
        'Ghế sofa các loại: 1 chỗ, 2 chỗ, 3 chỗ, góc L',
        '🛋️',
        50.00,
        1.5000,
        200,
        90,
        85,
        FALSE,
        FALSE,
        1,
        TRUE
    ),
    (
        'Giường',
        'Bed',
        'Giường ngủ các loại: đơn, đôi, king, queen',
        '🛏️',
        80.00,
        2.0000,
        200,
        160,
        50,
        FALSE,
        TRUE,
        2,
        TRUE
    ),
    (
        'Tủ quần áo',
        'Wardrobe',
        'Tủ quần áo, tủ âm tường',
        '🚪',
        100.00,
        2.5000,
        200,
        120,
        200,
        FALSE,
        TRUE,
        3,
        TRUE
    ),
    (
        'Bàn làm việc',
        'Desk',
        'Bàn làm việc, bàn học',
        '🪑',
        30.00,
        0.8000,
        120,
        60,
        75,
        FALSE,
        FALSE,
        4,
        TRUE
    ),
    (
        'Bàn ăn',
        'Dining Table',
        'Bàn ăn 4-8 chỗ',
        '🍽️',
        40.00,
        1.2000,
        150,
        90,
        75,
        FALSE,
        FALSE,
        5,
        TRUE
    ),
    (
        'Tủ bếp',
        'Kitchen Cabinet',
        'Tủ bếp trên, tủ bếp dưới',
        '🗄️',
        60.00,
        1.5000,
        100,
        60,
        200,
        FALSE,
        TRUE,
        6,
        TRUE
    ),
    (
        'Tủ lạnh',
        'Refrigerator',
        'Tủ lạnh các loại',
        '❄️',
        70.00,
        0.8000,
        70,
        70,
        170,
        TRUE,
        FALSE,
        7,
        TRUE
    ),
    (
        'Máy giặt',
        'Washing Machine',
        'Máy giặt cửa trên, cửa ngang',
        '🌀',
        60.00,
        0.6000,
        60,
        60,
        85,
        TRUE,
        FALSE,
        8,
        TRUE
    ),
    (
        'TV',
        'Television',
        'TV màn hình phẳng các kích cỡ',
        '📺',
        15.00,
        0.3000,
        120,
        10,
        70,
        TRUE,
        FALSE,
        9,
        TRUE
    ),
    (
        'Kệ sách',
        'Bookshelf',
        'Kệ sách, giá đỡ',
        '📚',
        40.00,
        1.0000,
        100,
        30,
        180,
        FALSE,
        FALSE,
        10,
        TRUE
    ),
    (
        'Ghế văn phòng',
        'Office Chair',
        'Ghế văn phòng có bánh xe',
        '💺',
        15.00,
        0.4000,
        60,
        60,
        100,
        FALSE,
        FALSE,
        11,
        TRUE
    ),
    (
        'Tủ giày',
        'Shoe Cabinet',
        'Tủ đựng giày dép',
        '👞',
        25.00,
        0.6000,
        100,
        40,
        100,
        FALSE,
        FALSE,
        12,
        TRUE
    ),
    (
        'Bàn trang điểm',
        'Vanity Table',
        'Bàn trang điểm có gương',
        '💄',
        35.00,
        0.7000,
        100,
        50,
        150,
        TRUE,
        FALSE,
        13,
        TRUE
    ),
    (
        'Tủ tivi',
        'TV Stand',
        'Kệ tivi, tủ tivi',
        '📱',
        30.00,
        0.8000,
        150,
        40,
        50,
        FALSE,
        FALSE,
        14,
        TRUE
    ),
    (
        'Hộp đồ',
        'Storage Box',
        'Hộp đựng đồ, thùng carton',
        '📦',
        10.00,
        0.2000,
        50,
        40,
        40,
        FALSE,
        FALSE,
        15,
        TRUE
    );

-- ============================================================================
-- 2. SIZES (for categories that have size variations)
-- ============================================================================

-- Sofa sizes
SET
    @sofa_id = (
        SELECT `category_id`
        FROM `categories`
        WHERE
            `name` = 'Ghế sofa'
    );

INSERT IGNORE INTO
    `sizes` (
        `category_id`,
        `name`,
        `weight_kg`,
        `height_cm`,
        `width_cm`,
        `depth_cm`,
        `price_multiplier`
    )
VALUES (
        @sofa_id,
        '1 chỗ ngồi',
        30.00,
        85,
        90,
        90,
        0.80
    ),
    (
        @sofa_id,
        '2 chỗ ngồi',
        50.00,
        85,
        150,
        90,
        1.00
    ),
    (
        @sofa_id,
        '3 chỗ ngồi',
        70.00,
        85,
        200,
        90,
        1.30
    ),
    (
        @sofa_id,
        'Góc L',
        100.00,
        85,
        250,
        180,
        1.80
    );

-- Bed sizes
SET
    @bed_id = (
        SELECT `category_id`
        FROM `categories`
        WHERE
            `name` = 'Giường'
    );

INSERT IGNORE INTO
    `sizes` (
        `category_id`,
        `name`,
        `weight_kg`,
        `height_cm`,
        `width_cm`,
        `depth_cm`,
        `price_multiplier`
    )
VALUES (
        @bed_id,
        'Đơn (Single)',
        50.00,
        50,
        100,
        200,
        0.70
    ),
    (
        @bed_id,
        'Đôi (Double)',
        70.00,
        50,
        140,
        200,
        1.00
    ),
    (
        @bed_id,
        'Queen',
        90.00,
        50,
        160,
        200,
        1.20
    ),
    (
        @bed_id,
        'King',
        110.00,
        50,
        180,
        200,
        1.50
    );

-- Wardrobe sizes
SET
    @wardrobe_id = (
        SELECT `category_id`
        FROM `categories`
        WHERE
            `name` = 'Tủ quần áo'
    );

INSERT IGNORE INTO
    `sizes` (
        `category_id`,
        `name`,
        `weight_kg`,
        `height_cm`,
        `width_cm`,
        `depth_cm`,
        `price_multiplier`
    )
VALUES (
        @wardrobe_id,
        '2 cánh',
        80.00,
        200,
        100,
        60,
        0.80
    ),
    (
        @wardrobe_id,
        '3 cánh',
        120.00,
        200,
        150,
        60,
        1.00
    ),
    (
        @wardrobe_id,
        '4 cánh',
        150.00,
        200,
        200,
        60,
        1.30
    );

-- Refrigerator sizes
SET
    @fridge_id = (
        SELECT `category_id`
        FROM `categories`
        WHERE
            `name` = 'Tủ lạnh'
    );

INSERT IGNORE INTO
    `sizes` (
        `category_id`,
        `name`,
        `weight_kg`,
        `height_cm`,
        `width_cm`,
        `depth_cm`,
        `price_multiplier`
    )
VALUES (
        @fridge_id,
        'Mini (< 150L)',
        40.00,
        100,
        50,
        50,
        0.70
    ),
    (
        @fridge_id,
        'Trung (150-300L)',
        60.00,
        150,
        60,
        60,
        1.00
    ),
    (
        @fridge_id,
        'Lớn (300-500L)',
        80.00,
        170,
        70,
        70,
        1.30
    ),
    (
        @fridge_id,
        'Side-by-side (> 500L)',
        120.00,
        180,
        90,
        80,
        1.80
    );

-- TV sizes
SET
    @tv_id = (
        SELECT `category_id`
        FROM `categories`
        WHERE
            `name` = 'TV'
    );

INSERT IGNORE INTO
    `sizes` (
        `category_id`,
        `name`,
        `weight_kg`,
        `height_cm`,
        `width_cm`,
        `depth_cm`,
        `price_multiplier`
    )
VALUES (
        @tv_id,
        '32-43 inch',
        10.00,
        60,
        100,
        10,
        0.70
    ),
    (
        @tv_id,
        '50-55 inch',
        15.00,
        75,
        125,
        10,
        1.00
    ),
    (
        @tv_id,
        '65-75 inch',
        25.00,
        95,
        165,
        10,
        1.50
    ),
    (
        @tv_id,
        '> 75 inch',
        35.00,
        110,
        180,
        10,
        2.00
    );

-- Dining Table sizes
SET
    @dining_id = (
        SELECT `category_id`
        FROM `categories`
        WHERE
            `name` = 'Bàn ăn'
    );

INSERT IGNORE INTO
    `sizes` (
        `category_id`,
        `name`,
        `weight_kg`,
        `height_cm`,
        `width_cm`,
        `depth_cm`,
        `price_multiplier`
    )
VALUES (
        @dining_id,
        '4 chỗ',
        30.00,
        75,
        120,
        80,
        0.80
    ),
    (
        @dining_id,
        '6 chỗ',
        40.00,
        75,
        150,
        90,
        1.00
    ),
    (
        @dining_id,
        '8 chỗ',
        55.00,
        75,
        200,
        100,
        1.40
    );

-- Storage Box sizes
SET
    @box_id = (
        SELECT `category_id`
        FROM `categories`
        WHERE
            `name` = 'Hộp đồ'
    );

INSERT IGNORE INTO
    `sizes` (
        `category_id`,
        `name`,
        `weight_kg`,
        `height_cm`,
        `width_cm`,
        `depth_cm`,
        `price_multiplier`
    )
VALUES (
        @box_id,
        'Nhỏ',
        5.00,
        30,
        40,
        30,
        0.50
    ),
    (
        @box_id,
        'Trung',
        10.00,
        40,
        50,
        40,
        1.00
    ),
    (
        @box_id,
        'Lớn',
        15.00,
        50,
        60,
        50,
        1.50
    );