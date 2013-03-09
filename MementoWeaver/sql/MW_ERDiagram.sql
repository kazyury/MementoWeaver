
/* Drop Tables */

DROP TABLE MW.MEMENTO_CONTENTS;
DROP TABLE MW.MEMENTO;
DROP TABLE MW.PREDEFINED_TAG;
DROP TABLE MW.TAGGED_MATERIAL;
DROP TABLE MW.MATERIAL;




/* Create Tables */

CREATE TABLE MW.MEMENTO
(
	MEMENTO_ID CHAR(8) NOT NULL,
	PRIMARY KEY (MEMENTO_ID)
);


CREATE TABLE MW.MEMENTO_CONTENTS
(
	MATERIAL_ID CHAR(14) NOT NULL,
	TAG CHAR(8) NOT NULL,
	MEMENTO_ID CHAR(8) NOT NULL
);


CREATE TABLE MW.PREDEFINED_TAG
(
	TAG CHAR(8) NOT NULL,
	FQCN VARCHAR(256) NOT NULL,
	PRIMARY KEY (TAG)
);


CREATE TABLE MW.TAGGED_MATERIAL
(
	MATERIAL_ID CHAR(14) NOT NULL,
	TAG CHAR(8) NOT NULL,
	MEMO VARCHAR(256),
	TAG_STATE CHAR,
	PRIMARY KEY (MATERIAL_ID, TAG)
);


CREATE TABLE MW.MATERIAL
(
	MATERIAL_ID CHAR(14) NOT NULL,
	CREATED_YEAR INTEGER NOT NULL,
	CREATED_MONTH INTEGER NOT NULL,
	MATERIAL_TYPE CHAR(1) NOT NULL,
	MATERIAL_STATE CHAR NOT NULL,
	PRIMARY KEY (MATERIAL_ID)
);



/* Create Foreign Keys */

ALTER TABLE MW.MEMENTO_CONTENTS
	ADD FOREIGN KEY (MEMENTO_ID)
	REFERENCES MW.MEMENTO (MEMENTO_ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MW.MEMENTO_CONTENTS
	ADD FOREIGN KEY (MATERIAL_ID, TAG)
	REFERENCES MW.TAGGED_MATERIAL (MATERIAL_ID, TAG)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MW.TAGGED_MATERIAL
	ADD FOREIGN KEY (MATERIAL_ID)
	REFERENCES MW.MATERIAL (MATERIAL_ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;



