
CREATE TABLE carts (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       user_id BIGINT NOT NULL UNIQUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_cart_user
                           FOREIGN KEY (user_id)
                               REFERENCES users(id)
                               ON DELETE CASCADE
);

CREATE TABLE cart_items (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            cart_id UUID NOT NULL,
                            product_id UUID NOT NULL,
                            quantity INTEGER NOT NULL,

                            CONSTRAINT fk_cart_item_cart
                                FOREIGN KEY (cart_id)
                                    REFERENCES carts(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT chk_cart_item_quantity
                                CHECK (quantity >= 1),

                            CONSTRAINT uk_cart_item_product
                                UNIQUE (cart_id, product_id)
);

CREATE INDEX idx_cart_items_product_id
    ON cart_items(product_id);
