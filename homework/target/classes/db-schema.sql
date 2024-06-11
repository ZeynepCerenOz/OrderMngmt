create table if not exists sample(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    data text,
    value int default 0
);

create FUNCTION sample_trigger() RETURNS TRIGGER AS
'
    BEGIN
        IF (SELECT value FROM sample where id = NEW.id ) > 1000
           THEN
           RAISE SQLSTATE ''23503'';
           END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

create table  if not exists Product(
	product_id INT PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	description VARCHAR(50),
	band_name VARCHAR(50)
);

CREATE TABLE  if not exists company (
    name VARCHAR(200) PRIMARY KEY,
    zip INT,
    country VARCHAR(200),
    city VARCHAR(200),
    street_info VARCHAR(200),
    phoneNumber VARCHAR(200) UNIQUE NOT NULL
);
CREATE TABLE if not exists Email (
    email_id SERIAL PRIMARY KEY,
    email VARCHAR(200),
    company_name VARCHAR(200),
    FOREIGN KEY (company_name) REFERENCES Company(name)
);


CREATE TABLE if not exists Produce (
    produce_id SERIAL PRIMARY KEY,
    company_name VARCHAR(200),
    product_id INT,
    capacity INT
);

ALTER TABLE Produce
ADD FOREIGN KEY (company_name) REFERENCES Company(name);

ALTER TABLE Produce
ADD FOREIGN KEY (product_id) REFERENCES Product(product_id);

CREATE TABLE if not exists Transaction (
    transaction_id SERIAL PRIMARY KEY,
    company_name VARCHAR(200),
    product_id INT,
    amount INT,
    created_date DATE
);
ALTER TABLE Transaction
ADD FOREIGN KEY (company_name) REFERENCES Company(name);

ALTER TABLE Transaction
ADD FOREIGN KEY (product_id) REFERENCES Product(product_id);


create FUNCTION check_transaction_capacity() RETURNS TRIGGER AS
'
    BEGIN
        IF (SELECT sum(capacity) FROM Produce where product_id = NEW.product_id
                and company_name = NEW.company_name
                group by product_id, company_name
                ) < NEW.amount
           THEN
           RAISE SQLSTATE ''23503'';
           END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

create TRIGGER enforce_transaction_capacity AFTER insert ON transaction
    FOR EACH ROW EXECUTE PROCEDURE check_transaction_capacity();


create TRIGGER sample_value AFTER insert ON sample
    FOR EACH ROW EXECUTE PROCEDURE sample_trigger();

CREATE TABLE Transaction_history (
    transaction_history_id SERIAL PRIMARY KEY,
	transaction_id INT,
    company_name VARCHAR(200),
    product_id INT,
    amount INT,
    created_date DATE
);
