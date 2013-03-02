
-- Table Material --------------------------
DROP TABLE MW.Material;
CREATE TABLE MW.Material
(
MATERIALID CHAR (14) NOT NULL,
CREATED_YEAR INTEGER  NOT NULL,
CREATED_MONTH INTEGER  NOT NULL,
MATERIALTYPE CHAR (1) NOT NULL,
MATERIALSTATE CHAR (1) NOT NULL,
CONSTRAINT PK_Material PRIMARY KEY (MATERIALID)
);

-- Table TaggedMaterial --------------------------
DROP TABLE MW.TaggedMaterial;
CREATE TABLE MW.TaggedMaterial
(
MATERIALID CHAR (14) NOT NULL,
TAG CHAR (8) NOT NULL,
MEMO VARCHAR (256) ,
TAGSTATE CHAR (1) NOT NULL ,
CONSTRAINT PK_TaggedMaterial PRIMARY KEY (MATERIALID, TAG)
);


-- Table PreDefinedTag --------------------------
DROP TABLE MW.PreDefinedTag;
CREATE TABLE MW.PreDefinedTag
(
TAG CHAR (8) NOT NULL,
FQCN VARCHAR (256) NOT NULL ,
CONSTRAINT PK_PreDefinedTag PRIMARY KEY (TAG)
);

-- Table Memento --------------------------

DROP TABLE MW.Memento;
CREATE TABLE MW.Memento
(
MEMENTOID CHAR (8) NOT NULL ,
CONSTRAINT PK_Memento PRIMARY KEY (MEMENTOID)
);

-- Table MementoContents --------------------------

DROP TABLE MW.MementoContents;
CREATE TABLE MW.MementoContents
(
MEMENTOID CHAR (8) NOT NULL,
MATERIALID CHAR (14) NOT NULL,
TAG CHAR (8) NOT NULL ,
CONSTRAINT PK_MementoContents PRIMARY KEY (MEMENTOID, MATERIALID, TAG)
);

