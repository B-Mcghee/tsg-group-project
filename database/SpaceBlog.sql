DROP DATABASE IF EXISTS SpaceBlog;
CREATE DATABASE SpaceBlog;

USE SpaceBlog;

CREATE TABLE Users(
Id INT PRIMARY KEY AUTO_INCREMENT,
DisplayName VARCHAR (20) NOT NULL,
UserName VARCHAR (20) NOT NULL,
`Password` VARCHAR (20) NOT NULL,
Email VARCHAR (50) NOT NULL
);

CREATE TABLE Blogs(
Id INT PRIMARY KEY AUTO_INCREMENT,
UserId INT NOT NULL,
StatusId INT NOT NULL,
Title VARCHAR (50) NOT NULL,
Body TEXT NOT NULL,
PostedTime DATETIME NOT NULL,
ExpirationTime DATETIME NULL
);

CREATE TABLE Statuses(
Id INT PRIMARY KEY AUTO_INCREMENT,
`Name` VARCHAR(20) NOT NULL
);

CREATE TABLE Comments(
Id INT PRIMARY KEY AUTO_INCREMENT,
UserId INT NOT NULL,
BlogId INT NOT NULL,
Body VARCHAR(2000) NOT NULL,
PostedTime DATETIME NOT NULL
);

CREATE TABLE Hashtags(
Id INT PRIMARY KEY AUTO_INCREMENT,
`Name` VARCHAR (50) NOT NULL UNIQUE
);

CREATE TABLE BlogHash(
Id INT PRIMARY KEY AUTO_INCREMENT,
BlogId INT NOT NULL,
HashtagId INT NOT NULL
);

CREATE TABLE ImageURLs(
Id INT PRIMARY KEY AUTO_INCREMENT,
URL VARCHAR (200) NOT NULL,
`Type` VARCHAR (50) NOT NULL
);

CREATE TABLE BlogIMG(
Id INT PRIMARY KEY AUTO_INCREMENT,
BlogId INT NOT NULL,
ImgId INT NOT NULL
);

CREATE TABLE Categories(
Id INT PRIMARY KEY AUTO_INCREMENT,
`Name` VARCHAR (50) NOT NULL
);

CREATE TABLE BlogCat(
Id INT PRIMARY KEY AUTO_INCREMENT,
BlogId INT NOT NULL,
CatId INT NOT NULL
);

ALTER TABLE Blogs ADD CONSTRAINT fk_BlogToUser
FOREIGN KEY (UserId) REFERENCES Users(Id);

ALTER TABLE Blogs ADD CONSTRAINT fk_BlogToStatus
FOREIGN KEY (StatusId) REFERENCES Statuses(Id);

ALTER TABLE Comments ADD CONSTRAINT fk_CommentToUser
FOREIGN KEY (UserId) REFERENCES Users(Id);

ALTER TABLE Comments ADD CONSTRAINT fk_CommentToBlog
FOREIGN KEY (BlogId) REFERENCES Blogs(Id);

ALTER TABLE BlogHash ADD CONSTRAINT fk_BlogToHash
FOREIGN KEY (BlogId) REFERENCES Blogs(Id);

ALTER TABLE BlogHash ADD CONSTRAINT fk_HashToBlog
FOREIGN KEY (HashtagId) REFERENCES Hashtags(Id);

ALTER TABLE BlogImg ADD CONSTRAINT fk_BlogToImg
FOREIGN KEY (BlogId) REFERENCES Blogs(Id);

ALTER TABLE BlogImg ADD CONSTRAINT fk_ImgToBlog
FOREIGN KEY (ImgId) REFERENCES ImageURLs(Id);

ALTER TABLE BlogCat ADD CONSTRAINT fk_BlogToCat
FOREIGN KEY (BlogId) REFERENCES Blogs(Id);

ALTER TABLE BlogCat ADD CONSTRAINT fk_CatToBlog
FOREIGN KEY (CatId) REFERENCES Categories(Id);

INSERT INTO `SpaceBlog`.`Categories` (`Id`, `Name`) VALUES ('1', 'Centauri Diplomatic Reports');
INSERT INTO `SpaceBlog`.`Categories` (`Id`, `Name`) VALUES ('2', 'Terran Diplomatic Reports');
INSERT INTO `SpaceBlog`.`Categories` (`Id`, `Name`) VALUES ('3', 'Minbari Diplomatic Reports');
INSERT INTO `SpaceBlog`.`Categories` (`Id`, `Name`) VALUES ('4', 'Narn Diplomatic Reports');
INSERT INTO `SpaceBlog`.`Categories` (`Id`, `Name`) VALUES ('5', 'Vorlon Diplomatic Reports');
INSERT INTO `SpaceBlog`.`Categories` (`Id`, `Name`) VALUES ('6', 'Events');
INSERT INTO `SpaceBlog`.`Categories` (`Id`, `Name`) VALUES ('7', 'News');
INSERT INTO `SpaceBlog`.`Categories` (`Id`, `Name`) VALUES ('8', 'Job Postings');
INSERT INTO `SpaceBlog`.`Categories` (`Id`, `Name`) VALUES ('9', 'Arrivals/Departures');

INSERT INTO SpaceBlog.statuses (Id, `Name`) VALUES ('1', 'Approved');
INSERT INTO SpaceBlog.statuses (Id, `Name`) VALUES ('2', 'Staged');
INSERT INTO SpaceBlog.statuses (Id, `Name`) VALUES ('3', 'Submitted');

INSERT INTO statuses (Id, `Name`) VALUES ('-1', 'NA');
INSERT INTO users (Id, DisplayName, UserName, `Password`, Email) VALUES ('-1', 'NA', 'NA', '8675309', 'NA');