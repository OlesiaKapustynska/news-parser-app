# news-parser-app

#create DB
CREATE DATABASE newsdb;

USE newsdb;

CREATE TABLE news (
id INT AUTO_INCREMENT PRIMARY KEY,
headline VARCHAR(255) NOT NULL,
description TEXT,
publication_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
