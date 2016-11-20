CREATE TABLE tb1 (
    uuid UUID,
    name VARCHAR(25),
    PRIMARY KEY (uuid)
);

CREATE TABLE tb1_tb1 (
    tb1_a UUID,
    tb1_b UUID,
    FOREIGN KEY (tb1_a) REFERENCES tb1(uuid),
    FOREIGN KEY (tb1_b) REFERENCES tb1(uuid),
    PRIMARY KEY (tb1_a, tb1_b)
);
