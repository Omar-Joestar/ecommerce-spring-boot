
CREATE TABLE inventory (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                           product_id UUID NOT NULL UNIQUE,

                           quantity INTEGER NOT NULL DEFAULT 0,

                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT chk_inventory_quantity_non_negative
                               CHECK (quantity >= 0)
);
