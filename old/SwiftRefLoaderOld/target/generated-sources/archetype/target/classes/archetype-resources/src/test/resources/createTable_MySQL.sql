SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `RP_SWIFT` ;
CREATE SCHEMA IF NOT EXISTS `RP_SWIFT` DEFAULT CHARACTER SET utf8 ;
USE `RP_SWIFT` ;


-- -----------------------------------------------------
-- Table `RP_SWIFT`.`RNDIBBC`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RP_SWIFT`.`RNDIBBC` ;

CREATE TABLE IF NOT EXISTS `RP_SWIFT`.`RNDIBBC`
    (`NO_INTERNE`                       INTEGER             NOT NULL,
     `LI_NOM_BANQUE`                    CHAR(105)            NOT NULL,
     `LI_NOM_PAYS`                      CHAR(70)             NOT NULL,
     `CD_PAYS_ISO`                      CHAR(2)              NOT NULL,
     `CD_IBAN_PAYS_ISO`                 CHAR(2)              NOT NULL,
     `CD_IBAN_BIC`                      CHAR(11)             NOT NULL,
     `CD_ROUTING_BIC`                   CHAR(11)             NOT NULL,
     `CD_IBAN_NATIONAL_ID`              CHAR(15)             NOT NULL,
     `LI_SER_CTXT`                      CHAR(8)              ,
     `DT_DEBUT_APPLI`                   DATE                 NOT NULL,
     `DT_FIN_APPLI`                     DATE                 ,
     `NO_ID_BATCH`                      SMALLINT             NOT NULL,
     `CD_ETAT`                          CHAR(1)              NOT NULL,
     `DT_CREATION`                      DATE                 NOT NULL,
     `HR_CREATION`                      TIME                 NOT NULL,
     `NI_CREATION`                      CHAR(8)              NOT NULL,
     `DT_MODIF`                         DATE                 ,
     `HR_MODIF`                         TIME                 ,
     `NI_MODIF`                         CHAR(8)              ,
     `DT_ANNULATION`                    DATE                 ,
     `HR_ANNULATION`                    TIME                 ,
     `NI_ANNULATION`                    CHAR(8)              ,
    PRIMARY KEY
      (`NO_INTERNE`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `RP_SWIFT`.`RNDIBST`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RP_SWIFT`.`RNDIBST` ;

CREATE TABLE IF NOT EXISTS `RP_SWIFT`.`RNDIBST`
	(`NO_INTERNE`                       INTEGER              NOT NULL,
     `CD_IBAN_PAYS_ISO`                 CHAR(2)              NOT NULL,
     `NO_POS_CD_IBAN_PAYS`              SMALLINT             NOT NULL,
     `NO_LGR_CD_IBAN_PAYS`              SMALLINT             ,
     `NO_POS_CLE_IBAN`                  SMALLINT             NOT NULL,
     `NO_LGR_CLE_IBAN`                  SMALLINT             NOT NULL,
     `NO_POS_CD_BANQUE`                 SMALLINT             NOT NULL,
     `NO_LGR_CD_BANQUE`                 SMALLINT             NOT NULL,
     `NO_POS_CD_GUICHET`                SMALLINT             ,
     `NO_LGR_CD_GUICHET`                SMALLINT             NOT NULL,
     `NO_LGR_IBAN_NATIONAL_ID`          SMALLINT             NOT NULL,
     `NO_POS_NUM_CPT`                   SMALLINT             NOT NULL,
     `NO_LGR_NUM_CPT`                   SMALLINT             NOT NULL,
     `NO_LGR_IBAN_TOTAL`                SMALLINT             NOT NULL,
     `CD_SEPA_STC`                      CHAR(1)              NOT NULL,
     `NO_ID_BATCH`                      SMALLINT             NOT NULL,
     `CD_ACTIVITE`                      CHAR(2)              ,
     `DT_CREATION`                      DATE                 NOT NULL,
     `HR_CREATION`                      TIME                 NOT NULL,
     `NI_CREATION`                      CHAR(8)              NOT NULL,
     `DT_MODIF`                         DATE                 ,
     `HR_MODIF`                         TIME                 ,
     `NI_MODIF`                         CHAR(8)              ,
     `DT_ANNULATION`                    DATE                 ,
     `HR_ANNULATION`                    TIME                 ,
     `NI_ANNULATION`                    CHAR(8)              ,
    PRIMARY KEY
      (`NO_INTERNE`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `RP_SWIFT`.`RNDIBEX`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RP_SWIFT`.`RNDIBEX` ;

CREATE TABLE IF NOT EXISTS `RP_SWIFT`.`RNDIBEX`
    (`NO_INTERNE`                       INTEGER              NOT NULL,
     `CD_IBAN_PAYS_ISO`                 CHAR(2)              NOT NULL,
     `CD_IBAN_NATIONAL_ID`              CHAR(15)             NOT NULL,
     `CD_IBAN_BIC`                      CHAR(11)             ,
     `NO_ID_BATCH`                      SMALLINT             NOT NULL,
     `CD_ACTIVITE`                      CHAR(2)              ,
     `DT_CREATION`                      DATE                 NOT NULL,
     `HR_CREATION`                      TIME                 NOT NULL,
     `NI_CREATION`                      CHAR(8)              NOT NULL,
     `DT_MODIF`                         DATE                 ,
     `HR_MODIF`                         TIME                 ,
     `NI_MODIF`                         CHAR(8)              ,
     `DT_ANNULATION`                    DATE                 ,
     `HR_ANNULATION`                    TIME                 ,
     `NI_ANNULATION`                    CHAR(8)              ,
    PRIMARY KEY
      (`NO_INTERNE`))
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;