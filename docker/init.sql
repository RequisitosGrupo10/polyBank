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
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `surname` VARCHAR(45) NOT NULL,
  `creationDate` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `polyBank`.`Badge`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Badge` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `value` FLOAT NOT NULL,
  `name` VARCHAR(5) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `polyBank`.`BankAccount`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`BankAccount` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `client_id` INT UNSIGNED NOT NULL,
  `IBAN` VARCHAR(34) NOT NULL,
  `active` TINYINT NOT NULL,
  `balance` INT NOT NULL,
  `Badge_id` INT UNSIGNED NOT NULL,
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
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
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
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `initialAmount` FLOAT NOT NULL,
  `finalAmount` FLOAT NOT NULL,
  `initialBadge_id` INT UNSIGNED NOT NULL,
  `finalBadge_id` INT UNSIGNED NOT NULL,
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
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `BankAccount_id` INT UNSIGNED NOT NULL,
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
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `Benficiary_id` INT UNSIGNED NOT NULL,
  `CurrencyExchange_id` INT UNSIGNED NULL,
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
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `timestamp` TIMESTAMP NOT NULL,
  `Client_id` INT UNSIGNED NOT NULL,
  `BankAccount_id` INT UNSIGNED NOT NULL,
  `CurrencyExchange_id` INT UNSIGNED NOT NULL,
  `Payment_id` INT UNSIGNED NOT NULL,
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
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `type` ENUM('assistant', 'manager') NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `polyBank`.`Request`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Request` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `Client_id` INT UNSIGNED NOT NULL,
  `BankAccount_id` INT UNSIGNED NOT NULL,
  `Employee_id` INT UNSIGNED NOT NULL,
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
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `IBAN` VARCHAR(34) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `polyBank`.`Chat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`Chat` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `Client_id` INT UNSIGNED NOT NULL,
  `Assistant_id` INT UNSIGNED NOT NULL,
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
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `Chat_id` INT UNSIGNED NOT NULL,
  `content` VARCHAR(45) NOT NULL,
  `timestamp` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Message_Chat1_idx` (`Chat_id` ASC) VISIBLE,
  CONSTRAINT `fk_Message_Chat1`
    FOREIGN KEY (`Chat_id`)
    REFERENCES `polyBank`.`Chat` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `polyBank`.`AuthorizedAccount`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `polyBank`.`AuthorizedAccount` (
  `Client_id` INT UNSIGNED NOT NULL,
  `BankAccount_id` INT UNSIGNED NOT NULL,
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
