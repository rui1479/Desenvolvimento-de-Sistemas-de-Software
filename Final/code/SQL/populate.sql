INSERT IGNORE INTO Diretores (id, nome, email, password)
VALUES  ('d1001', 'Carlos Pereira', 'carlos@gmail.com', 'admin');

INSERT IGNORE INTO sala(referencia, capacidade)
VALUES
-- Salas do tipo 1 (capacidade 130)
(1, 130),
(2, 130),
(3, 130),
(15, 130),
-- Salas do tipo 2 (capacidade 30)
(4, 30),
(5, 30),
(6, 30),
(7, 30),
(8, 30),
(9, 30),
(10, 30),
(11, 30),
(12, 30),
(13, 30),
(14, 30);
