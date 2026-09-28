INSERT INTO categories (name, description) VALUES
('Programming', 'Software development and computer science'),
('Fiction', 'Novels and literary fiction'),
('Science', 'Popular science and research'),
('History', 'World history and biographies'),
('Business', 'Management, finance and productivity');

INSERT INTO books (title, author, isbn, price, description, cover_image) VALUES
('Clean Code', 'Robert C. Martin', '9780132350884', 39.99, 'A handbook of agile software craftsmanship', 'https://example.com/covers/clean-code.jpg'),
('Effective Java', 'Joshua Bloch', '9780134685991', 44.99, 'Best practices for the Java platform', 'https://example.com/covers/effective-java.jpg'),
('Spring in Action', 'Craig Walls', '9781617297571', 49.99, 'Covers Spring 5 and Spring Boot', 'https://example.com/covers/spring-in-action.jpg'),
('The Pragmatic Programmer', 'Andrew Hunt, David Thomas', '9780135957059', 42.50, 'Your journey to mastery', 'https://example.com/covers/pragmatic-programmer.jpg'),
('Designing Data-Intensive Applications', 'Martin Kleppmann', '9781449373320', 54.99, 'The big ideas behind reliable, scalable systems', 'https://example.com/covers/ddia.jpg'),
('Head First Design Patterns', 'Eric Freeman, Elisabeth Robson', '9781492078005', 47.99, 'A brain-friendly guide to design patterns', 'https://example.com/covers/head-first-patterns.jpg'),
('1984', 'George Orwell', '9780451524935', 12.99, 'A dystopian social science fiction novel', 'https://example.com/covers/1984.jpg'),
('The Master and Margarita', 'Mikhail Bulgakov', '9780143108276', 15.99, 'A fantastical satire of Soviet life', 'https://example.com/covers/master-margarita.jpg'),
('Dune', 'Frank Herbert', '9780441172719', 18.99, 'Epic science fiction on the desert planet Arrakis', 'https://example.com/covers/dune.jpg'),
('The Witcher: The Last Wish', 'Andrzej Sapkowski', '9780316029186', 16.50, 'Short stories introducing Geralt of Rivia', 'https://example.com/covers/last-wish.jpg'),
('Solaris', 'Stanislaw Lem', '9780156027601', 14.99, 'A classic of philosophical science fiction', 'https://example.com/covers/solaris.jpg'),
('A Brief History of Time', 'Stephen Hawking', '9780553380163', 17.99, 'From the Big Bang to black holes', 'https://example.com/covers/brief-history-time.jpg'),
('The Selfish Gene', 'Richard Dawkins', '9780198788607', 19.99, 'A gene-centred view of evolution', 'https://example.com/covers/selfish-gene.jpg'),
('Cosmos', 'Carl Sagan', '9780345539434', 18.50, 'A personal voyage through the universe', 'https://example.com/covers/cosmos.jpg'),
('Sapiens', 'Yuval Noah Harari', '9780062316097', 22.99, 'A brief history of humankind', 'https://example.com/covers/sapiens.jpg'),
('Guns, Germs, and Steel', 'Jared Diamond', '9780393354324', 21.50, 'The fates of human societies', 'https://example.com/covers/guns-germs-steel.jpg'),
('The Second World War', 'Antony Beevor', '9780316023757', 29.99, 'A comprehensive history of WWII', 'https://example.com/covers/second-world-war.jpg'),
('Thinking, Fast and Slow', 'Daniel Kahneman', '9780374533557', 20.99, 'How two systems of thought shape our decisions', 'https://example.com/covers/thinking-fast-slow.jpg'),
('The Lean Startup', 'Eric Ries', '9780307887894', 24.99, 'Continuous innovation for successful businesses', 'https://example.com/covers/lean-startup.jpg'),
('Atomic Habits', 'James Clear', '9780735211292', 19.50, 'Small changes, remarkable results', 'https://example.com/covers/atomic-habits.jpg');

INSERT INTO books_categories (book_id, category_id)
SELECT b.id, c.id FROM books b JOIN categories c ON
   (b.isbn IN ('9780132350884', '9780134685991', '9781617297571', '9780135957059', '9781449373320', '9781492078005') AND c.name = 'Programming')
OR (b.isbn IN ('9780451524935', '9780143108276', '9780441172719', '9780316029186', '9780156027601') AND c.name = 'Fiction')
OR (b.isbn IN ('9780441172719', '9780156027601', '9780553380163', '9780198788607', '9780345539434', '9780374533557') AND c.name = 'Science')
OR (b.isbn IN ('9780062316097', '9780393354324', '9780316023757') AND c.name = 'History')
OR (b.isbn IN ('9780307887894', '9780735211292', '9780374533557', '9781449373320') AND c.name = 'Business');
