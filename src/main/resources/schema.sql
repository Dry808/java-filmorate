CREATE TABLE IF NOT EXISTS films (
  id INT AUTO_INCREMENT PRIMARY KEY ,
  name VARCHAR(200) NOT NULL,
  description VARCHAR(200),
  release_date DATE,
  duration INT,
  genre_id INT,
  rating_id INT
);

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(64),
  login VARCHAR(20) NOT NULL,
  name VARCHAR(20),
  birthday DATE
);

CREATE TABLE IF NOT EXISTS films_like (
  film_id INT,
  user_id INT,
  PRIMARY KEY (film_id, user_id),
  FOREIGN KEY (film_id) REFERENCES films(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS user_friends (
  user_id INT,
  friend_id INT,
  friendship_status ENUM('unconfirmed', 'confirmed') DEFAULT 'unconfirmed',
  PRIMARY KEY (user_id, friend_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (friend_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS genre (
  id INT PRIMARY KEY,
  genre_name VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id INT,
  genre_id INT,
  PRIMARY KEY (film_id, genre_id),
  FOREIGN KEY (film_id) REFERENCES films(id),
  FOREIGN KEY (genre_id) REFERENCES genre(id)
);

CREATE TABLE IF NOT EXISTS films_rating (
  id INT PRIMARY KEY,
  rating_mpa_name VARCHAR(10)
);