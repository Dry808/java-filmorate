CREATE TABLE IF NOT EXISTS films_rating (
  id INT PRIMARY KEY,
  name VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS films (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  description VARCHAR(200),
  release_date DATE,
  duration INT,
  rating_id INT,
  FOREIGN KEY (rating_id) REFERENCES films_rating(id)
);

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(64),
  login VARCHAR(200) NOT NULL,
  name VARCHAR(200),
  birthday DATE
);

CREATE TABLE IF NOT EXISTS films_like (
  film_id INT,
  user_id INT,
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

CREATE TABLE IF NOT EXISTS reviews (
  id INT AUTO_INCREMENT PRIMARY KEY,
  film_id INT,
  user_id INT,
  content TEXT,
  is_positive BOOLEAN,
  FOREIGN KEY (film_id) REFERENCES films(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS review_likes (
  review_id INT,
  user_id INT,
  is_like BOOLEAN,
  PRIMARY KEY (review_id, user_id),
  FOREIGN KEY (review_id) REFERENCES reviews(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS directors (
  director_id INT AUTO_INCREMENT PRIMARY KEY,
  director_name VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS film_director (
  film_id INT NOT NULL,
  director_id INT NOT NULL,
  PRIMARY KEY (film_id, director_id),
  FOREIGN KEY (film_id) REFERENCES films(id),
  FOREIGN KEY (director_id) REFERENCES directors(director_id)
);

CREATE TABLE IF NOT EXISTS event_feed (
  event_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  times_tamp TIMESTAMP,
  event_type VARCHAR(200),
  operation VARCHAR(200),
  entity_id INT,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

