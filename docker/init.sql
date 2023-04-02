-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema polyBank
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema polyBank
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `polyBank` DEFAULT CHARACTER SET utf8mb3 ;
USE `polyBank` ;

-- -----------------------------------------------------
-- Table `polyBank`.`Client`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Client` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `DNI` VARCHAR(45) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `salt` VARCHAR(32) NOT NULL,
  `surname` VARCHAR(45) NOT NULL,
  `creationDate` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `DNI_UNIQUE` (`DNI` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `polyBank`.`Badge`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Badge` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `value` FLOAT NOT NULL,
  `name` VARCHAR(5) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `polyBank`.`BankAccount`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`BankAccount` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `client_id` INT NOT NULL,
  `IBAN` VARCHAR(34) NOT NULL,
  `active` TINYINT NOT NULL,
  `balance` FLOAT NOT NULL,
  `Badge_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_BankAccount_Client_idx` (`client_id` ASC) VISIBLE,
  UNIQUE INDEX `IBAN_UNIQUE` (`IBAN` ASC) VISIBLE,
  INDEX `fk_BankAccount_Badge1_idx` (`Badge_id` ASC) VISIBLE,
  CONSTRAINT `fk_BankAccount_Client`
    FOREIGN KEY (`client_id`)
    REFERENCES `polyBank`.`Client` (`id`),
  CONSTRAINT `fk_BankAccount_Badge1`
    FOREIGN KEY (`Badge_id`)
    REFERENCES `polyBank`.`Badge` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `polyBank`.`Benficiary`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Benficiary` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `badge` VARCHAR(3) NOT NULL,
  `IBAN` VARCHAR(34) NOT NULL,
  `swift` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `polyBank`.`CurrencyExchange`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`CurrencyExchange` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `initialAmount` FLOAT NOT NULL,
  `finalAmount` FLOAT NOT NULL,
  `initialBadge_id` INT NOT NULL,
  `finalBadge_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_CurrencyExchange_Badge1_idx` (`initialBadge_id` ASC) VISIBLE,
  INDEX `fk_CurrencyExchange_Badge2_idx` (`finalBadge_id` ASC) VISIBLE,
  CONSTRAINT `fk_CurrencyExchange_Badge1`
    FOREIGN KEY (`initialBadge_id`)
    REFERENCES `polyBank`.`Badge` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_CurrencyExchange_Badge2`
    FOREIGN KEY (`finalBadge_id`)
    REFERENCES `polyBank`.`Badge` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `polyBank`.`Company`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Company` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `BankAccount_id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Enterprise_BankAccount1_idx` (`BankAccount_id` ASC) VISIBLE,
  CONSTRAINT `fk_Enterprise_BankAccount1`
    FOREIGN KEY (`BankAccount_id`)
    REFERENCES `polyBank`.`BankAccount` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `polyBank`.`Payment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Payment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `Benficiary_id` INT NOT NULL,
  `CurrencyExchange_id` INT NULL,
  `amount` FLOAT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Payment_CurrencyExchange1_idx` (`CurrencyExchange_id` ASC) VISIBLE,
  INDEX `fk_Payment_Benficiary1_idx` (`Benficiary_id` ASC) VISIBLE,
  CONSTRAINT `fk_Payment_Benficiary1`
    FOREIGN KEY (`Benficiary_id`)
    REFERENCES `polyBank`.`Benficiary` (`id`),
  CONSTRAINT `fk_Payment_CurrencyExchange1`
    FOREIGN KEY (`CurrencyExchange_id`)
    REFERENCES `polyBank`.`CurrencyExchange` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `polyBank`.`Transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Transaction` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `timestamp` TIMESTAMP NOT NULL,
  `Client_id` INT NOT NULL,
  `BankAccount_id` INT NOT NULL,
  `CurrencyExchange_id` INT NULL,
  `Payment_id` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Transaction_BankAccount2_idx` (`BankAccount_id` ASC) VISIBLE,
  INDEX `fk_Transaction_CurrencyExchange1_idx` (`CurrencyExchange_id` ASC) VISIBLE,
  INDEX `fk_Transaction_Payment1_idx` (`Payment_id` ASC) VISIBLE,
  INDEX `fk_Transaction_Client1_idx` (`Client_id` ASC) VISIBLE,
  CONSTRAINT `fk_Transaction_BankAccount2`
    FOREIGN KEY (`BankAccount_id`)
    REFERENCES `polyBank`.`BankAccount` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Transaction_CurrencyExchange1`
    FOREIGN KEY (`CurrencyExchange_id`)
    REFERENCES `polyBank`.`CurrencyExchange` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Transaction_Payment1`
    FOREIGN KEY (`Payment_id`)
    REFERENCES `polyBank`.`Payment` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Transaction_Client1`
    FOREIGN KEY (`Client_id`)
    REFERENCES `polyBank`.`Client` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `polyBank`.`Employee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Employee` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `DNI` VARCHAR(45) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `type` ENUM('assistant', 'manager') NOT NULL,
  `salt` VARCHAR(32) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `DNI_UNIQUE` (`DNI` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `polyBank`.`Request`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Request` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `Client_id` INT NOT NULL,
  `BankAccount_id` INT NOT NULL,
  `Employee_id` INT NOT NULL,
  `solved` TINYINT NOT NULL,
  `timestamp` TIMESTAMP NOT NULL,
  `type` ENUM('activation', 'other') NOT NULL,
  `description` VARCHAR(100) NOT NULL,
  `approved` TINYINT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Petition_BankAccount1_idx` (`BankAccount_id` ASC) VISIBLE,
  INDEX `fk_Request_Client1_idx` (`Client_id` ASC) VISIBLE,
  INDEX `fk_Request_Employee1_idx` (`Employee_id` ASC) VISIBLE,
  CONSTRAINT `fk_Petition_BankAccount1`
    FOREIGN KEY (`BankAccount_id`)
    REFERENCES `polyBank`.`BankAccount` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Request_Client1`
    FOREIGN KEY (`Client_id`)
    REFERENCES `polyBank`.`Client` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Request_Employee1`
    FOREIGN KEY (`Employee_id`)
    REFERENCES `polyBank`.`Employee` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `polyBank`.`SuspiciousAccount`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`SuspiciousAccount` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `IBAN` VARCHAR(34) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `polyBank`.`Chat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Chat` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `Client_id` INT NOT NULL,
  `Assistant_id` INT NOT NULL,
  `closed` TINYINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Chat_Client1_idx` (`Client_id` ASC) VISIBLE,
  INDEX `fk_Chat_Assistant1_idx` (`Assistant_id` ASC) VISIBLE,
  CONSTRAINT `fk_Chat_Client1`
    FOREIGN KEY (`Client_id`)
    REFERENCES `polyBank`.`Client` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Chat_Assistant1`
    FOREIGN KEY (`Assistant_id`)
    REFERENCES `polyBank`.`Employee` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `polyBank`.`Message`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Message` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `Chat_id` INT NOT NULL,
  `Employee_id` INT NOT NULL,
  `Client_id` INT NOT NULL,
  `content` VARCHAR(1000) NOT NULL,
  `timestamp` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Message_Chat1_idx` (`Chat_id` ASC) VISIBLE,
  INDEX `fk_Message_Employee1_idx` (`Employee_id` ASC) VISIBLE,
  INDEX `fk_Message_Client1_idx` (`Client_id` ASC) VISIBLE,
  CONSTRAINT `fk_Message_Chat1`
    FOREIGN KEY (`Chat_id`)
    REFERENCES `polyBank`.`Chat` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Message_Employee1`
    FOREIGN KEY (`Employee_id`)
    REFERENCES `polyBank`.`Employee` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Message_Client1`
    FOREIGN KEY (`Client_id`)
    REFERENCES `polyBank`.`Client` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `polyBank`.`AuthorizedAccount`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`AuthorizedAccount` (
  `Client_id` INT NOT NULL,
  `BankAccount_id` INT NOT NULL,
  `blocked` TINYINT NOT NULL,
  PRIMARY KEY (`Client_id`, `BankAccount_id`),
  INDEX `fk_Client_has_BankAccount_BankAccount1_idx` (`BankAccount_id` ASC) VISIBLE,
  INDEX `fk_Client_has_BankAccount_Client1_idx` (`Client_id` ASC) VISIBLE,
  CONSTRAINT `fk_Client_has_BankAccount_Client1`
    FOREIGN KEY (`Client_id`)
    REFERENCES `polyBank`.`Client` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Client_has_BankAccount_BankAccount1`
    FOREIGN KEY (`BankAccount_id`)
    REFERENCES `polyBank`.`BankAccount` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


insert into Client (`name`, dni, surname, `password`, `salt`, creationDate) values ('Debbie', '40397815V', 'Vasyutichev', 'password', '1234', '2018-04-24 14:11:09');
insert into Client (`name`, dni, surname, `password`, `salt`, creationDate) values ('Stacie', 'Y0192459D',  'Vasyutichev', 'password', '1234', '2018-04-21 14:11:09');
insert into Client (`name`, dni, surname, `password`, `salt`, creationDate) values ('Doralynne', '17080917J',  'Vasyutichev', 'password', '1234', '2018-04-21 14:11:09');
insert into Client (`name`, dni, surname, `password`, `salt`, creationDate) values ('Kristos',  '17080917A','Rotherforth', 'password', '1234', '2020-04-21 14:11:09');
insert into Client (`name`, dni, surname, `password`, `salt`, creationDate) values ('Keelby',  '17080917X','Giacovetti', 'password', '1234', '2018-04-1 14:11:09');
insert into Client (`name`, dni, surname, `password`, `salt`, creationDate) values ('Courtnay',  '17080917T','Candlin', 'password', '1234', '2018-04-21 14:11:09');
insert into Client (`name`, dni, surname, `password`, `salt`, creationDate) values ('Gloriane',  '17080917F','Brice', 'password', '1234', '2019-04-21 14:11:09');
insert into Client (`name`, dni, surname, `password`, `salt`, creationDate) values ('Roxie', '17080917L', 'Ivancevic', 'password', '1234', '2018-04-21 14:11:09');
insert into Client (`name`, dni, surname, `password`, `salt`, creationDate) values ('Lorilee', '17080917M', 'Leile', 'password', '1234', '2016-04-21 14:11:09');
insert into Client (`name`, dni, surname, `password`, `salt`, creationDate) values ('Abdul', '17080917R', 'Gauthorpp', 'password', '1234', '2018-04-21 14:11:09');

insert into Employee (`name`, dni, `password`, `salt`, `type`) values ('Kienan Benaine', '40397815A','TqbcDk', '1234', 'assistant');
insert into Employee (`name`, dni, `password`, `salt`, `type`) values ('Leah Aguirre', '40397815S','AHdUsN', '1234', 'assistant');
insert into Employee (`name`, dni, `password`, `salt`, `type`) values ('Levon Izak', '40397815T','iW5PcuT', '1234', 'assistant');
insert into Employee (`name`, dni, `password`, `salt`, `type`) values ('Martguerita Strase', '40397815P','6hUdplam76Wd', '1234', 'assistant');
insert into Employee (`name`, dni, `password`, `salt`, `type`) values ('Julissa Bernade', '40397815J','Si5yIaDK', '1234', 'manager');

insert into Badge (`name`, `value`) values ('RUB', 2059.1);
insert into Badge (`name`, `value`) values ('MXN', 6188.03);
insert into Badge (`name`, `value`) values ('DKK', 5774.94);
insert into Badge (`name`, `value`) values ('IRR', 3800.86);
insert into Badge (`name`, `value`) values ('XOF', 3465.79);
insert into Badge (`name`, `value`) values ('EUR', 9125.59);
insert into Badge (`name`, `value`) values ('IDR', 2122.9);
insert into Badge (`name`, `value`) values ('TJS', 6756.73);
insert into Badge (`name`, `value`) values ('USD', 7912.92);
insert into Badge (`name`, `value`) values ('IDR', 9580.17);
insert into Badge (`name`, `value`) values ('AFN', 2548.01);
insert into Badge (`name`, `value`) values ('CLP', 9513.29);
insert into Badge (`name`, `value`) values ('HNL', 6266.4);
insert into Badge (`name`, `value`) values ('CNY', 9078.98);
insert into Badge (`name`, `value`) values ('CAD', 7969.3);
insert into Badge (`name`, `value`) values ('ETB', 9203.5);
insert into Badge (`name`, `value`) values ('PEN', 1370.84);
insert into Badge (`name`, `value`) values ('CNY', 2132.18);
insert into Badge (`name`, `value`) values ('IDR', 4218.74);
insert into Badge (`name`, `value`) values ('EUR', 681.98);

insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('XQ52 3493 5842 02 6281016579', 1, 1033.41, 1, 9);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('LW85 5056 1367 71 5597786337', 0, 1889.7, 2, 8);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('RE73 8424 2517 02 2726764989', 1, 2898.91, 3, 7);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('EQ53 2965 5873 76 8550831055', 1, 7580.68, 4, 20);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('BG33 6067 7579 09 0578769649', 1, 1372.65, 5, 3);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('PE91 2168 9016 54 7246606933', 1, 1928.13, 6, 1);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('OV47 5795 0107 81 3232382316', 0, 6088.84, 1, 6);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('MO64 8237 3988 86 2354670412', 1, 2004.76, 1, 3);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('DU76 9522 8334 44 9282702541', 0, 4009.16, 7, 18);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('US97 8983 9210 61 4292047390', 1, 7216.86, 5, 18);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('AP89 8004 8050 89 2128300007', 1, 6501.55, 6, 20);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('XQ51 3580 7169 65 4884017513', 0, 9491.4, 7, 10);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('IW49 9245 0204 65 7939552987', 1, 2703.41, 6, 1);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('WA70 2433 2905 09 4866387675', 0, 8085.21, 10, 2);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('RH35 7727 2247 18 4691738863', 0, 8425.7, 8, 4);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('CQ92 6746 6922 96 8680676979', 1, 38.62, 9, 5);
insert into BankAccount (IBAN, `active`, balance, client_id, Badge_id) values ('DK32 2212 8929 74 2121292462', 1, 4221.52, 10, 20);

insert into SuspiciousAccount (IBAN) values ('RQ31 9139 4872 90 1035739992');
insert into SuspiciousAccount (IBAN) values ('FW44 5895 9732 48 8786444400');
insert into SuspiciousAccount (IBAN) values ('QS70 2017 9642 07 7406935610');
insert into SuspiciousAccount (IBAN) values ('NL86 2945 0417 98 0684333969');
insert into SuspiciousAccount (IBAN) values ('SB73 8593 7197 35 9921075506');

insert into Company (`name`, BankAccount_id) values ('Meeveo', 1);
insert into Company (`name`, BankAccount_id) values ('Lazz', 2);
insert into Company (`name`, BankAccount_id) values ('Browsecat', 3);
insert into Company (`name`, BankAccount_id) values ('Ooba', 4);
insert into Company (`name`, BankAccount_id) values ('Snaptags', 5);

insert into AuthorizedAccount (blocked, BankAccount_id, Client_id) values (0, 1, 9);
insert into AuthorizedAccount (blocked, BankAccount_id, Client_id) values (1, 2, 8);
insert into AuthorizedAccount (blocked, BankAccount_id, Client_id) values (0, 3, 7);
insert into AuthorizedAccount (blocked, BankAccount_id, Client_id) values (1, 4, 6);
insert into AuthorizedAccount (blocked, BankAccount_id, Client_id) values (0, 5, 10);
