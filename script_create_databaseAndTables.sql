-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema measurement_devices
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema measurement_devices
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `measurement_devices` DEFAULT CHARACTER SET utf8 ;
USE `measurement_devices` ;

-- -----------------------------------------------------
-- Table `measurement_devices`.`calibration_module`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`calibration_module` (
  `moduleId` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `calibrationType` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `condDesignation` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `email` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `employeeFullName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `isActive` BIT(1) NOT NULL DEFAULT b'1',
  `moduleNumber` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `moduleType` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `organizationCode` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `serialNumber` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `telephone` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `workDate` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`moduleId`))
ENGINE = InnoDB
AUTO_INCREMENT = 19
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`organization`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`organization` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `certificateDate` DATETIME NULL DEFAULT NULL,
  `certificateNumrAuthoriz` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `codeEDRPOU` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `subordination` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `building` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `district` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `flat` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `locality` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `region` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `street` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `buildingRegistered` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `districtRegistered` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `flatRegistered` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `localityRegistered` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `regionRegistered` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `streetRegistered` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `certificateGrantedDate` DATETIME NULL DEFAULT NULL,
  `certificateNumber` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `email` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `employeesCapacity` INT(11) NULL DEFAULT NULL,
  `maxProcessTime` INT(11) NULL DEFAULT NULL,
  `name` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `phone` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 53
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`user` (
  `username` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `building` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `district` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `flat` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `locality` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `region` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `street` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `email` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `firstName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `isAvailable` BIT(1) NULL DEFAULT NULL,
  `lastName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `middleName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `password` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `phone` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `secondPhone` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `organizationId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`username`),
  INDEX `FKggk4bs8kq7q3jti8itc1yhwvf` (`organizationId` ASC),
  CONSTRAINT `FKggk4bs8kq7q3jti8itc1yhwvf`
    FOREIGN KEY (`organizationId`)
    REFERENCES `measurement_devices`.`organization` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`disassembly_team`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`disassembly_team` (
  `id` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `effectiveTo` DATE NULL DEFAULT NULL,
  `leaderEmail` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `leaderFullName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `leaderPhone` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `name` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `calibratorId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKsf3002h7qhu1t4c6cqxgubkeu` (`calibratorId` ASC),
  CONSTRAINT `FKsf3002h7qhu1t4c6cqxgubkeu`
    FOREIGN KEY (`calibratorId`)
    REFERENCES `measurement_devices`.`organization` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`calibration_task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`calibration_task` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `createTaskDate` DATE NULL DEFAULT NULL,
  `dateOfTask` DATE NULL DEFAULT NULL,
  `status` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `moduleId` BIGINT(20) NULL DEFAULT NULL,
  `teamId` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `username` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK4e9c4hm2bswcwk1ec6g3m4i8c` (`moduleId` ASC),
  INDEX `FKyqjf5ail47tomhmbjh2vfvtt` (`teamId` ASC),
  INDEX `FKnxd1rhugluh05jd2malujag6d` (`username` ASC),
  CONSTRAINT `FK4e9c4hm2bswcwk1ec6g3m4i8c`
    FOREIGN KEY (`moduleId`)
    REFERENCES `measurement_devices`.`calibration_module` (`moduleId`),
  CONSTRAINT `FKnxd1rhugluh05jd2malujag6d`
    FOREIGN KEY (`username`)
    REFERENCES `measurement_devices`.`user` (`username`),
  CONSTRAINT `FKyqjf5ail47tomhmbjh2vfvtt`
    FOREIGN KEY (`teamId`)
    REFERENCES `measurement_devices`.`disassembly_team` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`manufacturer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`manufacturer` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`device`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`device` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `deviceName` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `deviceSign` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `deviceType` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `number` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `manufacturerId` BIGINT(20) NULL DEFAULT NULL,
  `providerId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKftg5a289klp0qe443qtsk64yw` (`manufacturerId` ASC),
  INDEX `FKu67v1sjdpt0o6nwaeq9f09vb` (`providerId` ASC),
  CONSTRAINT `FKftg5a289klp0qe443qtsk64yw`
    FOREIGN KEY (`manufacturerId`)
    REFERENCES `measurement_devices`.`manufacturer` (`id`),
  CONSTRAINT `FKu67v1sjdpt0o6nwaeq9f09vb`
    FOREIGN KEY (`providerId`)
    REFERENCES `measurement_devices`.`organization` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 65470
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`counter_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`counter_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `calibrationInterval` INT(11) NULL DEFAULT NULL,
  `gost` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `manufacturer` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `name` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `standardSize` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `symbol` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `yearIntroduction` INT(11) NULL DEFAULT NULL,
  `deviceId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKp5ur1qrnuhbp5l8f24tata6wg` (`deviceId` ASC),
  CONSTRAINT `FKp5ur1qrnuhbp5l8f24tata6wg`
    FOREIGN KEY (`deviceId`)
    REFERENCES `measurement_devices`.`device` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`counter`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`counter` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `dateOfDismantled` DATETIME NULL DEFAULT NULL,
  `dateOfMounted` DATETIME NULL DEFAULT NULL,
  `numberCounter` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `realiseYear` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `stamp` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `counterTypeId` BIGINT(20) NULL DEFAULT NULL,
  `verification_id` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKs5erj4tcwhhxq7qfdd6oxxwqe` (`counterTypeId` ASC),
  INDEX `FKf9rn3iydl8klt87m8cah2x1qj` (`verification_id` ASC),
  CONSTRAINT `FKf9rn3iydl8klt87m8cah2x1qj`
    FOREIGN KEY (`verification_id`)
    REFERENCES `measurement_devices`.`verification` (`id`),
  CONSTRAINT `FKs5erj4tcwhhxq7qfdd6oxxwqe`
    FOREIGN KEY (`counterTypeId`)
    REFERENCES `measurement_devices`.`counter_type` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 45
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`verification`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`verification` (
  `id` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `building` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `district` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `flat` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `locality` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `region` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `street` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `email` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `firstName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `lastName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `middleName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `phone` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `secondPhone` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `comment` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `counterStatus` TINYINT(1) NULL DEFAULT '0',
  `expirationDate` DATE NULL DEFAULT NULL,
  `initialDate` DATE NULL DEFAULT NULL,
  `isAddInfoExists` TINYINT(1) NULL DEFAULT '0',
  `isManual` BIT(1) NULL DEFAULT b'0',
  `processTimeExceeding` INT(11) NULL DEFAULT NULL,
  `readStatus` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `rejectedMessage` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `sealPresence` TINYINT(1) NULL DEFAULT '1',
  `sentToCalibratorDate` DATE NULL DEFAULT NULL,
  `status` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `taskStatus` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `moduleId` BIGINT(20) NULL DEFAULT NULL,
  `calibratorId` BIGINT(20) NULL DEFAULT NULL,
  `calibratorEmployeeUsername` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `counterId` BIGINT(20) NULL DEFAULT NULL,
  `deviceId` BIGINT(20) NULL DEFAULT NULL,
  `infoId` BIGINT(20) NULL DEFAULT NULL,
  `providerId` BIGINT(20) NULL DEFAULT NULL,
  `providerEmployeeUsername` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `stateVerificatorId` BIGINT(20) NULL DEFAULT NULL,
  `stateVerificatorEmployeeUsername` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `taskId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK78dilsm5nvt3ubnjjvou0g5d9` (`moduleId` ASC),
  INDEX `FKrs5dkbvl6aoc1y4e3fftyooq0` (`calibratorId` ASC),
  INDEX `FKsl0t31h26fco68gswjrkaliul` (`calibratorEmployeeUsername` ASC),
  INDEX `FKt0i6qoq1rdbke1auy52xg0u2c` (`counterId` ASC),
  INDEX `FKkdpyd2265y143tj6xq79bn8if` (`deviceId` ASC),
  INDEX `FKmb5vi9vchy4r4jp4vj1net2gb` (`infoId` ASC),
  INDEX `FKfyqyeqoou3kcg57xiytbh9dq9` (`providerId` ASC),
  INDEX `FKbqfhk4m2suuoseilkcudetup5` (`providerEmployeeUsername` ASC),
  INDEX `FKmtqk9cwlullt0kfalwnb2e1cv` (`stateVerificatorId` ASC),
  INDEX `FKb4wxtytxodyfby8xcw6h8bijh` (`stateVerificatorEmployeeUsername` ASC),
  INDEX `FKg9qucf4yasjwwhsvhsfhlexrj` (`taskId` ASC),
  CONSTRAINT `FK78dilsm5nvt3ubnjjvou0g5d9`
    FOREIGN KEY (`moduleId`)
    REFERENCES `measurement_devices`.`calibration_module` (`moduleId`),
  CONSTRAINT `FKb4wxtytxodyfby8xcw6h8bijh`
    FOREIGN KEY (`stateVerificatorEmployeeUsername`)
    REFERENCES `measurement_devices`.`user` (`username`),
  CONSTRAINT `FKbqfhk4m2suuoseilkcudetup5`
    FOREIGN KEY (`providerEmployeeUsername`)
    REFERENCES `measurement_devices`.`user` (`username`),
  CONSTRAINT `FKfyqyeqoou3kcg57xiytbh9dq9`
    FOREIGN KEY (`providerId`)
    REFERENCES `measurement_devices`.`organization` (`id`),
  CONSTRAINT `FKg9qucf4yasjwwhsvhsfhlexrj`
    FOREIGN KEY (`taskId`)
    REFERENCES `measurement_devices`.`calibration_task` (`id`),
  CONSTRAINT `FKkdpyd2265y143tj6xq79bn8if`
    FOREIGN KEY (`deviceId`)
    REFERENCES `measurement_devices`.`device` (`id`),
  CONSTRAINT `FKmb5vi9vchy4r4jp4vj1net2gb`
    FOREIGN KEY (`infoId`)
    REFERENCES `measurement_devices`.`additional_info` (`id`),
  CONSTRAINT `FKmtqk9cwlullt0kfalwnb2e1cv`
    FOREIGN KEY (`stateVerificatorId`)
    REFERENCES `measurement_devices`.`organization` (`id`),
  CONSTRAINT `FKrs5dkbvl6aoc1y4e3fftyooq0`
    FOREIGN KEY (`calibratorId`)
    REFERENCES `measurement_devices`.`organization` (`id`),
  CONSTRAINT `FKsl0t31h26fco68gswjrkaliul`
    FOREIGN KEY (`calibratorEmployeeUsername`)
    REFERENCES `measurement_devices`.`user` (`username`),
  CONSTRAINT `FKt0i6qoq1rdbke1auy52xg0u2c`
    FOREIGN KEY (`counterId`)
    REFERENCES `measurement_devices`.`counter` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`additional_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`additional_info` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `dateOfVerif` DATE NULL DEFAULT NULL,
  `doorCode` INT(11) NOT NULL,
  `entrance` INT(11) NOT NULL,
  `floor` INT(11) NOT NULL,
  `noWaterToDate` DATE NULL DEFAULT NULL,
  `notes` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `serviceability` BIT(1) NOT NULL,
  `timeFrom` TINYBLOB NULL DEFAULT NULL,
  `timeTo` TINYBLOB NULL DEFAULT NULL,
  `verification_id` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKawghniuhp8odlyq0tqr4c0uy1` (`verification_id` ASC),
  CONSTRAINT `FKawghniuhp8odlyq0tqr4c0uy1`
    FOREIGN KEY (`verification_id`)
    REFERENCES `measurement_devices`.`verification` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`agreement`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`agreement` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `date` DATE NULL DEFAULT NULL,
  `deviceCount` INT(11) NOT NULL,
  `deviceType` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `isAvailable` BIT(1) NULL DEFAULT NULL,
  `number` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `customerId` BIGINT(20) NULL DEFAULT NULL,
  `executorId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK3m55g6hbf63h8j7dur6fr3pg4` (`customerId` ASC),
  INDEX `FK6vtamd86vepbrfetq6imycr8u` (`executorId` ASC),
  CONSTRAINT `FK3m55g6hbf63h8j7dur6fr3pg4`
    FOREIGN KEY (`customerId`)
    REFERENCES `measurement_devices`.`organization` (`id`),
  CONSTRAINT `FK6vtamd86vepbrfetq6imycr8u`
    FOREIGN KEY (`executorId`)
    REFERENCES `measurement_devices`.`organization` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`bbi_protocol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`bbi_protocol` (
  `fileName` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `verificationId` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`fileName`),
  INDEX `FK6m93e8719ku68mckxbnmxj79d` (`verificationId` ASC),
  CONSTRAINT `FK6m93e8719ku68mckxbnmxj79d`
    FOREIGN KEY (`verificationId`)
    REFERENCES `measurement_devices`.`verification` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`region`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`region` (
  `id` BIGINT(20) NOT NULL,
  `designation` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`district`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`district` (
  `id` BIGINT(20) NOT NULL,
  `designation` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `regionId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKq9v3357dmmo0adqb2nhlromnd` (`regionId` ASC),
  CONSTRAINT `FKq9v3357dmmo0adqb2nhlromnd`
    FOREIGN KEY (`regionId`)
    REFERENCES `measurement_devices`.`region` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`locality`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`locality` (
  `id` BIGINT(20) NOT NULL,
  `designation` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `mailIndex` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `districtId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKgda2byq8urniv9hlnlcp6sgpm` (`districtId` ASC),
  CONSTRAINT `FKgda2byq8urniv9hlnlcp6sgpm`
    FOREIGN KEY (`districtId`)
    REFERENCES `measurement_devices`.`district` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`street_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`street_type` (
  `id` BIGINT(20) NOT NULL,
  `designation` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`street`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`street` (
  `id` BIGINT(20) NOT NULL,
  `designation` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `localityId` BIGINT(20) NOT NULL,
  `streetTypeId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK2805m6trvltd65ma2adlulq5m` (`localityId` ASC),
  INDEX `FKl4c7juxxwpvq6ts9dt6e6v9wp` (`streetTypeId` ASC),
  CONSTRAINT `FK2805m6trvltd65ma2adlulq5m`
    FOREIGN KEY (`localityId`)
    REFERENCES `measurement_devices`.`locality` (`id`),
  CONSTRAINT `FKl4c7juxxwpvq6ts9dt6e6v9wp`
    FOREIGN KEY (`streetTypeId`)
    REFERENCES `measurement_devices`.`street_type` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`building`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`building` (
  `id` BIGINT(20) NOT NULL,
  `designation` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `streetId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKdsnlucx17d0tvxt23slk8nlc3` (`streetId` ASC),
  CONSTRAINT `FKdsnlucx17d0tvxt23slk8nlc3`
    FOREIGN KEY (`streetId`)
    REFERENCES `measurement_devices`.`street` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`calibration_module_device_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`calibration_module_device_type` (
  `moduleId` BIGINT(20) NOT NULL,
  `deviceType` VARCHAR(20) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  INDEX `FK6o7okb1ue5bop3k63g19ayq4w` (`moduleId` ASC),
  CONSTRAINT `FK6o7okb1ue5bop3k63g19ayq4w`
    FOREIGN KEY (`moduleId`)
    REFERENCES `measurement_devices`.`calibration_module` (`moduleId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`calibration_test`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`calibration_test` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `capacity` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `consumptionStatus` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `dateTest` DATETIME NULL DEFAULT NULL,
  `latitude` DOUBLE NULL DEFAULT NULL,
  `longitude` DOUBLE NULL DEFAULT NULL,
  `documentName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `documentSign` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `name` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `photoPath` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `signed` TINYINT(1) NULL DEFAULT '0',
  `signedDocument` TINYBLOB NULL DEFAULT NULL,
  `temperature` INT(11) NULL DEFAULT NULL,
  `testResult` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `verificationId` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKglapl4whbv8eouc8i7s2ju1n4` (`verificationId` ASC),
  CONSTRAINT `FKglapl4whbv8eouc8i7s2ju1n4`
    FOREIGN KEY (`verificationId`)
    REFERENCES `measurement_devices`.`verification` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`calibration_test_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`calibration_test_data` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `acceptableError` BIGINT(20) NULL DEFAULT NULL,
  `actualConsumption` DOUBLE NULL DEFAULT NULL,
  `calculationError` DOUBLE NULL DEFAULT NULL,
  `consumptionStatus` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `duration` DOUBLE NULL DEFAULT NULL,
  `endValue` DOUBLE NULL DEFAULT NULL,
  `givenConsumption` DOUBLE NULL DEFAULT NULL,
  `initialValue` DOUBLE NULL DEFAULT NULL,
  `lowerConsumptionLimit` BIGINT(20) NULL DEFAULT NULL,
  `testPosition` INT(11) NULL DEFAULT NULL,
  `testResult` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `upperConsumptionLimit` BIGINT(20) NULL DEFAULT NULL,
  `volumeInDevice` DOUBLE NULL DEFAULT NULL,
  `volumeOfStandard` DOUBLE NULL DEFAULT NULL,
  `calibrationTestId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK5gs4a99e1be47r86v6ck1vb90` (`calibrationTestId` ASC),
  CONSTRAINT `FK5gs4a99e1be47r86v6ck1vb90`
    FOREIGN KEY (`calibrationTestId`)
    REFERENCES `measurement_devices`.`calibration_test` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`calibration_test_manual`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`calibration_test_manual` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `dateTest` DATETIME NULL DEFAULT NULL,
  `generateNumberTest` BIGINT(20) NULL DEFAULT NULL,
  `numberOfTest` INT(11) NULL DEFAULT NULL,
  `pathToScan` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `calibrationModuleId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKgtlfkaw3t8pbmcvv9lu37j4ip` (`calibrationModuleId` ASC),
  CONSTRAINT `FKgtlfkaw3t8pbmcvv9lu37j4ip`
    FOREIGN KEY (`calibrationModuleId`)
    REFERENCES `measurement_devices`.`calibration_module` (`moduleId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`calibration_test_data_manual`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`calibration_test_data_manual` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `statusCommon` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `statusTestFirst` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `statusTestSecond` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `statusTestThird` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `calibrationTestManualId` BIGINT(20) NULL DEFAULT NULL,
  `counterId` BIGINT(20) NULL DEFAULT NULL,
  `verificationId` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKkkqdqpeh2x6ma8wh4tiktc880` (`calibrationTestManualId` ASC),
  INDEX `FKbs3eh2le4lu74eqj5mv79ek5l` (`counterId` ASC),
  INDEX `FKe96dm7nm8pmqhfmpa12ixoak` (`verificationId` ASC),
  CONSTRAINT `FKbs3eh2le4lu74eqj5mv79ek5l`
    FOREIGN KEY (`counterId`)
    REFERENCES `measurement_devices`.`counter` (`id`),
  CONSTRAINT `FKe96dm7nm8pmqhfmpa12ixoak`
    FOREIGN KEY (`verificationId`)
    REFERENCES `measurement_devices`.`verification` (`id`),
  CONSTRAINT `FKkkqdqpeh2x6ma8wh4tiktc880`
    FOREIGN KEY (`calibrationTestManualId`)
    REFERENCES `measurement_devices`.`calibration_test_manual` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`calibration_test_img`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`calibration_test_img` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `imgName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `initialDate` DATE NULL DEFAULT NULL,
  `calibrationTestDataId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK98vsoq38nbyhgwye3w491e033` (`calibrationTestDataId` ASC),
  CONSTRAINT `FK98vsoq38nbyhgwye3w491e033`
    FOREIGN KEY (`calibrationTestDataId`)
    REFERENCES `measurement_devices`.`calibration_test_data` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`device_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`device_type` (
  `organizationId` BIGINT(20) NOT NULL,
  `value` VARCHAR(20) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  INDEX `FKyl23ya18vov062v3x4r7au8o` (`organizationId` ASC),
  CONSTRAINT `FKyl23ya18vov062v3x4r7au8o`
    FOREIGN KEY (`organizationId`)
    REFERENCES `measurement_devices`.`organization` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`disassembly_team_specialization`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`disassembly_team_specialization` (
  `disassemblyTeamId` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `value` VARCHAR(20) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  INDEX `FKq35u61nu4xi1lpk98ni6tb0ca` (`disassemblyTeamId` ASC),
  CONSTRAINT `FKq35u61nu4xi1lpk98ni6tb0ca`
    FOREIGN KEY (`disassemblyTeamId`)
    REFERENCES `measurement_devices`.`disassembly_team` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`hibernate_sequence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`hibernate_sequence` (
  `next_val` BIGINT(20) NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`measuring_equipment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`measuring_equipment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `deviceType` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `manufacturer` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `name` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `verificationInterval` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`organization_changes_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`organization_changes_history` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `building` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `district` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `flat` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `locality` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `region` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `street` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `adminName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `date` DATETIME NULL DEFAULT NULL,
  `email` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `employeesCapacity` INT(11) NULL DEFAULT NULL,
  `firstName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `lastName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `maxProcessTime` INT(11) NULL DEFAULT NULL,
  `middleName` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `name` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `phone` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `types` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `username` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `organizationId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKh1e76mgxuepug4397ieoxfjpf` (`organizationId` ASC),
  CONSTRAINT `FKh1e76mgxuepug4397ieoxfjpf`
    FOREIGN KEY (`organizationId`)
    REFERENCES `measurement_devices`.`organization` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`organization_locality`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`organization_locality` (
  `organizationId` BIGINT(20) NOT NULL,
  `localityId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`localityId`, `organizationId`),
  INDEX `FKa21lhul8ltef949rgbks2yngx` (`organizationId` ASC),
  CONSTRAINT `FKa21lhul8ltef949rgbks2yngx`
    FOREIGN KEY (`organizationId`)
    REFERENCES `measurement_devices`.`organization` (`id`),
  CONSTRAINT `FKlwhosmogm62y4wb0lvscpl34h`
    FOREIGN KEY (`localityId`)
    REFERENCES `measurement_devices`.`locality` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`organization_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`organization_type` (
  `organizationId` BIGINT(20) NOT NULL,
  `value` VARCHAR(20) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  INDEX `FKjpw4rgllfrks1e6gfp5kkn8ii` (`organizationId` ASC),
  CONSTRAINT `FKjpw4rgllfrks1e6gfp5kkn8ii`
    FOREIGN KEY (`organizationId`)
    REFERENCES `measurement_devices`.`organization` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`reasons_unsuitability`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`reasons_unsuitability` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `deviceId` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK9qw94ulwn0vd8tprqul46w5et` (`deviceId` ASC),
  CONSTRAINT `FK9qw94ulwn0vd8tprqul46w5et`
    FOREIGN KEY (`deviceId`)
    REFERENCES `measurement_devices`.`device` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`saved_filter`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`saved_filter` (
  `savedFilterId` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `filter` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `locationUrl` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `name` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `user` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`savedFilterId`),
  INDEX `FK6snm38vl93hwp8ogk49c370tt` (`user` ASC),
  CONSTRAINT `FK6snm38vl93hwp8ogk49c370tt`
    FOREIGN KEY (`user`)
    REFERENCES `measurement_devices`.`user` (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `measurement_devices`.`user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `measurement_devices`.`user_role` (
  `username` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `value` VARCHAR(30) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  INDEX `FK4lu7adaml4m6jiw1l8igk4evl` (`username` ASC),
  CONSTRAINT `FK4lu7adaml4m6jiw1l8igk4evl`
    FOREIGN KEY (`username`)
    REFERENCES `measurement_devices`.`user` (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
