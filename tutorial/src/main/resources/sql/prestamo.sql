-- Esquema de referencia para la tabla de préstamos (JPA/Hibernate puede generarla con ddl-auto).
-- Relaciones: juego (game), cliente (client).

CREATE TABLE IF NOT EXISTS prestamo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    CONSTRAINT fk_prestamo_juego FOREIGN KEY (game_id) REFERENCES game (id),
    CONSTRAINT fk_prestamo_cliente FOREIGN KEY (client_id) REFERENCES client (id)
);
