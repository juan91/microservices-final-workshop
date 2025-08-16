
CREATE TABLE bank.accounts (
	id serial4 NOT NULL,
	numero varchar(50) NOT NULL,
	titular varchar(100) NOT NULL,
	saldo numeric(15, 2) NOT NULL,
	banco_id int4 NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT accounts_pkey PRIMARY KEY (id),
	CONSTRAINT unique_numero UNIQUE (numero)
);
CREATE INDEX accounts_numero_idx ON bank.accounts USING btree (numero);

ALTER TABLE bank.accounts ADD CONSTRAINT accounts_banco_id_fkey FOREIGN KEY (banco_id) REFERENCES bank.banks(id);