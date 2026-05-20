-- =========================================
-- BASE DE DATOS KAMPETS — SCRIPT COMPLETO
-- Tablas + Datos + Funciones + Índices +
-- Constraints + Triggers + Vista + Permisos
-- =========================================


-- =========================================
-- TABLAS
-- =========================================

CREATE TABLE CLIENTES (
    id             SERIAL PRIMARY KEY,
    nombre_c       VARCHAR(100) NOT NULL,
    correo_c       VARCHAR(100),
    direccion_c    VARCHAR(150),
    telefono_c     VARCHAR(20),
    contrasena     VARCHAR(255),
    fecha_registro DATE DEFAULT CURRENT_DATE
);

CREATE TABLE ESPECIES (
    id              SERIAL PRIMARY KEY,
    nombre_especie  VARCHAR(50) NOT NULL
);

CREATE TABLE EMPLEADOS (
    id              SERIAL PRIMARY KEY,
    nombre_emp      VARCHAR(100) NOT NULL,
    apellido_emp    VARCHAR(100),
    telefono_emp    VARCHAR(20),
    cargo           VARCHAR(50),
    correo_emp      VARCHAR(100),
    contrasena_emp  VARCHAR(255)
);

CREATE TABLE SERVICIOS (
    id              SERIAL PRIMARY KEY,
    nombre_se       VARCHAR(100) NOT NULL,
    descripcion_se  TEXT,
    precio_se       DECIMAL(10,2),
    duracion_min    INT NOT NULL
);

CREATE TABLE PRODUCTOS (
    id          SERIAL PRIMARY KEY,
    nombre_pro  VARCHAR(100) NOT NULL,
    tipo_pro    VARCHAR(50),
    marca_pro   VARCHAR(50),
    precio_pro  DECIMAL(10,2) NOT NULL,
    stock_pro   INT NOT NULL
);

CREATE TABLE VACUNAS (
    id             SERIAL PRIMARY KEY,
    nombre_vacuna  VARCHAR(100) NOT NULL,
    descripcion    TEXT
);

CREATE TABLE MASCOTAS (
    id              SERIAL PRIMARY KEY,
    id_cliente      INT NOT NULL,
    id_especie      INT NOT NULL,
    nombre_m        VARCHAR(100) NOT NULL,
    fecha_nac       DATE,
    sexo            VARCHAR(10),
    caracteristica  VARCHAR(200),

    FOREIGN KEY (id_cliente) REFERENCES CLIENTES(id) ON DELETE CASCADE,
    FOREIGN KEY (id_especie) REFERENCES ESPECIES(id)
);

CREATE TABLE PEDIDOS (
    id             SERIAL PRIMARY KEY,
    id_cliente     INT NOT NULL,
    fecha_pedido   DATE DEFAULT CURRENT_DATE,
    total_pedido   DECIMAL(10,2),

    FOREIGN KEY (id_cliente) REFERENCES CLIENTES(id)
);

CREATE TABLE CITAS (
    id                  SERIAL PRIMARY KEY,
    id_mascota          INT NOT NULL,
    id_empleado         INT NOT NULL,
    fecha_cita          DATE NOT NULL,
    hora_cita           TIME NOT NULL,
    estado_cita         VARCHAR(50) DEFAULT 'PENDIENTE',
    direccion_domicilio VARCHAR(200),
    motivo              VARCHAR(200),

    FOREIGN KEY (id_mascota)  REFERENCES MASCOTAS(id),
    FOREIGN KEY (id_empleado) REFERENCES EMPLEADOS(id)
);

CREATE TABLE DETALLE_PEDIDO (
    id               SERIAL PRIMARY KEY,
    id_pedido        INT NOT NULL,
    id_producto      INT NOT NULL,
    cantidad         INT NOT NULL,
    precio_unitario  DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (id_pedido)   REFERENCES PEDIDOS(id)   ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES PRODUCTOS(id)
);

CREATE TABLE CONTROL_VACUNAS (
    id               SERIAL PRIMARY KEY,
    id_mascota       INT NOT NULL,
    id_vacuna        INT NOT NULL,
    fecha_aplicacion DATE NOT NULL,
    proxima_dosis    DATE,

    FOREIGN KEY (id_mascota) REFERENCES MASCOTAS(id) ON DELETE CASCADE,
    FOREIGN KEY (id_vacuna)  REFERENCES VACUNAS(id)
);

CREATE TABLE CITA_SERVICIO (
    id              SERIAL PRIMARY KEY,
    id_cita         INT NOT NULL,
    id_servicio     INT NOT NULL,
    precio_cobrado  DECIMAL(10,2),

    FOREIGN KEY (id_cita)     REFERENCES CITAS(id)     ON DELETE CASCADE,
    FOREIGN KEY (id_servicio) REFERENCES SERVICIOS(id)
);


-- =========================================
-- DATOS INICIALES
-- =========================================

INSERT INTO CLIENTES (id, nombre_c, correo_c, direccion_c, telefono_c, contrasena, fecha_registro) VALUES
(1, 'Oscar Vega',       'oscarvega1097@gmail.com', 'Manzana a casa 18', '3167181025', 'Oscarvega10.', '2026-05-13'),
(2, 'Brayan V',         'snayder071207@gmail.com', '',                  '2324343554', 'hola123456',  '2026-05-16'),
(4, 'Jaidivier Lombo G','JaidivierMT15@correo.com', NULL,               NULL,         'SoloMT15',    NULL),
(5, 'eliot',            'trpoo7368@gmail.com',     '',                  '3456546',    'hola234567',  '2026-05-19'),
(6, 'osito progra',     'aassds',                  '',                  '11122',      'aqwe',        '2026-05-19'),
(7, 'sadad sdada',      'sadd',                    '',                  '11',         '12345678',    '2026-05-19');

INSERT INTO ESPECIES (id, nombre_especie) VALUES
(1, 'Perro'),
(2, 'Gato'),
(3, 'Ave'),
(4, 'Conejo'),
(5, 'Hamster');

INSERT INTO EMPLEADOS (id, nombre_emp, apellido_emp, telefono_emp, cargo, correo_emp, contrasena_emp) VALUES
(1, 'Kampets', NULL,      NULL,         'Administrador', 'appkampets@gmail.com',     'Kampet12345'),
(2, 'Carlos',  'Ramírez', '3001112233', 'Veterinario',   'carlos.vet@kampets.com',   'vet123'),
(3, 'Laura',   'Torres',  '3009998877', 'Veterinario',   'laura.vet@kampets.com',    'vet456');

INSERT INTO VACUNAS (id, nombre_vacuna, descripcion) VALUES
(1, 'Rabia',       'Vacuna contra la rabia'),
(2, 'Moquillo',    'Vacuna contra el moquillo'),
(3, 'Triple Felina','Vacuna para gatos'),
(4, 'Conejos VHD', 'Vacuna para conejos'),
(5, 'Parvovirus',  'Vacuna contra parvovirus');

INSERT INTO MASCOTAS (id, id_cliente, id_especie, nombre_m, fecha_nac, sexo, caracteristica) VALUES
(1,  1, 1, 'Max',    '2020-05-10', 'Macho',  NULL),
(2,  1, 2, 'Luna',   '2022-03-15', 'Hembra', NULL),
(3,  2, 1, 'Rocky',  '2019-08-20', 'Macho',  NULL),
(4,  2, 2, 'Michi',  '2021-11-05', 'Macho',  NULL),
(5,  2, 4, 'Coco',   '2023-02-28', 'Hembra', NULL),
(6,  2, 2, 'gigi',   '2026-07-07', 'Hembra', NULL),
(7,  2, 4, 'gigi',   '2026-11-18', 'Hembra', NULL),
(9,  2, 4, 'daniel', '2026-08-04', 'Macho',  NULL),
(10, 5, 1, 'max',    '2026-05-04', 'Macho',  'es negro'),
(11, 2, 5, 'peter',  '2026-05-06', 'Macho',  NULL),
(12, 4, 2, 'pepe',   '2026-05-06', 'Macho',  NULL),
(13, 1, 1, 'oscar',  '2020-05-08', 'Hembra', NULL),
(14, 2, 3, 'oscar',  '2026-05-05', 'Hembra', 'Le falta un pie');

INSERT INTO CITAS (id, id_mascota, id_empleado, fecha_cita, hora_cita, estado_cita, direccion_domicilio, motivo) VALUES
(1,  1,  1, '2026-05-23', '11:15:00', 'CANCELADA',   NULL,      NULL),
(2,  1,  1, '2026-04-10', '10:30:00', 'COMPLETADA',  NULL,      NULL),
(3,  1,  1, '2026-03-05', '08:00:00', 'COMPLETADA',  NULL,      NULL),
(4,  2,  1, '2026-05-25', '14:00:00', 'CONFIRMADA',  NULL,      NULL),
(5,  2,  1, '2026-03-18', '11:00:00', 'COMPLETADA',  NULL,      NULL),
(6,  3,  1, '2026-06-02', '08:30:00', 'CONFIRMADA',  NULL,      NULL),
(7,  3,  1, '2026-04-22', '15:00:00', 'COMPLETADA',  NULL,      NULL),
(9,  4,  1, '2026-05-29', '12:00:00', 'CONFIRMADA',  NULL,      NULL),
(10, 4,  1, '2026-06-10', '09:00:00', 'CONFIRMADA',  NULL,      NULL),
(13, 9,  2, '2026-05-27', '09:00:00', 'CONFIRMADA',  NULL,      NULL),
(14, 5,  2, '2026-05-23', '09:45:00', 'CONFIRMADA',  NULL,      NULL),
(15, 10, 2, '2026-05-30', '10:00:00', 'CONFIRMADA',  NULL,      NULL),
(16, 5,  2, '2026-05-22', '09:45:00', 'CANCELADA',   NULL,      NULL),
(17, 10, 2, '2026-05-28', '09:30:00', 'CONFIRMADA',  NULL,      NULL),
(18, 11, 2, '2026-05-28', '11:00:00', 'CONFIRMADA',  NULL,      NULL),
(19, 12, 2, '2026-05-28', '09:00:00', 'CONFIRMADA',  NULL,      NULL),
(20, 13, 2, '2026-05-21', '15:00:00', 'CANCELADA',   NULL,      'Vacunación'),
(21, 4,  2, '2026-05-29', '09:30:00', 'CONFIRMADA',  NULL,      'Vacunación'),
(22, 14, 2, '2026-05-21', '14:30:00', 'PENDIENTE',   'mi casa', 'Consulta general');

INSERT INTO CONTROL_VACUNAS (id, id_mascota, id_vacuna, fecha_aplicacion, proxima_dosis) VALUES
(1,  1, 1, '2024-05-10', '2025-05-10'),
(2,  1, 2, '2024-05-10', '2025-05-10'),
(3,  1, 5, '2024-01-15', '2025-01-15'),
(4,  2, 3, '2024-03-20', '2025-03-20'),
(5,  2, 1, '2023-04-10', '2024-04-10'),
(6,  2, 4, '2025-04-20', '2026-06-01'),
(7,  3, 1, '2024-06-15', '2025-06-15'),
(8,  3, 2, '2024-06-15', '2025-06-15'),
(9,  3, 4, '2023-12-01', '2024-12-01'),
(10, 4, 3, '2024-04-10', '2025-04-10'),
(11, 4, 1, '2024-08-20', '2025-08-20');


-- =========================================
-- FUNCIONES
-- =========================================

-- Estado de vacuna: lógica que antes vivía en Java
CREATE OR REPLACE FUNCTION fn_estado_vacuna(proxima_dosis DATE)
RETURNS VARCHAR AS $$
BEGIN
    IF proxima_dosis IS NULL                                 THEN RETURN 'Al día';
    ELSIF proxima_dosis < CURRENT_DATE                       THEN RETURN 'Vencida';
    ELSIF proxima_dosis < CURRENT_DATE + INTERVAL '30 days' THEN RETURN 'Próxima';
    ELSE                                                          RETURN 'Al día';
    END IF;
END;
$$ LANGUAGE plpgsql STABLE;

-- Recalcula total_pedido al modificar detalles
CREATE OR REPLACE FUNCTION fn_actualizar_total_pedido()
RETURNS TRIGGER AS $$
DECLARE
    v_id_pedido INT;
BEGIN
    v_id_pedido := COALESCE(NEW.id_pedido, OLD.id_pedido);
    UPDATE PEDIDOS
    SET total_pedido = (
        SELECT COALESCE(SUM(cantidad * precio_unitario), 0)
        FROM DETALLE_PEDIDO
        WHERE id_pedido = v_id_pedido
    )
    WHERE id = v_id_pedido;
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;


-- =========================================
-- TRIGGERS
-- =========================================

DROP TRIGGER IF EXISTS trg_total_pedido ON DETALLE_PEDIDO;
CREATE TRIGGER trg_total_pedido
AFTER INSERT OR UPDATE OR DELETE ON DETALLE_PEDIDO
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_total_pedido();


-- =========================================
-- ÍNDICES DE RENDIMIENTO
-- =========================================

CREATE INDEX IF NOT EXISTS idx_mascotas_cliente    ON MASCOTAS(id_cliente);
CREATE INDEX IF NOT EXISTS idx_cv_mascota          ON CONTROL_VACUNAS(id_mascota);
CREATE INDEX IF NOT EXISTS idx_cv_vacuna           ON CONTROL_VACUNAS(id_vacuna);
CREATE INDEX IF NOT EXISTS idx_citas_mascota       ON CITAS(id_mascota);
CREATE INDEX IF NOT EXISTS idx_citas_fecha         ON CITAS(fecha_cita);
CREATE INDEX IF NOT EXISTS idx_citas_estado        ON CITAS(estado_cita);
CREATE INDEX IF NOT EXISTS idx_detalle_pedido      ON DETALLE_PEDIDO(id_pedido);
CREATE INDEX IF NOT EXISTS idx_pedidos_cliente     ON PEDIDOS(id_cliente);
CREATE INDEX IF NOT EXISTS idx_cita_servicio_cita  ON CITA_SERVICIO(id_cita);


-- =========================================
-- CONSTRAINTS DE VALIDACIÓN
-- =========================================

ALTER TABLE CITAS
    ADD CONSTRAINT chk_estado_cita
    CHECK (estado_cita IN ('PENDIENTE','CONFIRMADA','CANCELADA','COMPLETADA'));

ALTER TABLE MASCOTAS
    ADD CONSTRAINT chk_sexo_mascota
    CHECK (sexo IN ('Macho','Hembra') OR sexo IS NULL);

ALTER TABLE PRODUCTOS
    ADD CONSTRAINT chk_stock_positivo
    CHECK (stock_pro >= 0);

ALTER TABLE PRODUCTOS
    ADD CONSTRAINT chk_precio_producto
    CHECK (precio_pro > 0);

ALTER TABLE SERVICIOS
    ADD CONSTRAINT chk_precio_servicio
    CHECK (precio_se IS NULL OR precio_se > 0);

ALTER TABLE DETALLE_PEDIDO
    ADD CONSTRAINT chk_cantidad_positiva
    CHECK (cantidad > 0);

ALTER TABLE CONTROL_VACUNAS
    ADD CONSTRAINT chk_proxima_dosis_logica
    CHECK (proxima_dosis IS NULL OR proxima_dosis >= fecha_aplicacion);


-- =========================================
-- VISTA: VACUNAS COMPLETAS
-- =========================================

CREATE OR REPLACE VIEW v_vacunas_completas AS
SELECT
    cv.id,
    m.nombre_m                         AS mascota,
    c.nombre_c                         AS dueno,
    v.nombre_vacuna                    AS vacuna,
    cv.fecha_aplicacion,
    cv.proxima_dosis,
    fn_estado_vacuna(cv.proxima_dosis) AS estado
FROM CONTROL_VACUNAS cv
JOIN MASCOTAS m ON m.id = cv.id_mascota
JOIN CLIENTES c ON c.id = m.id_cliente
JOIN VACUNAS  v ON v.id = cv.id_vacuna
ORDER BY
    CASE fn_estado_vacuna(cv.proxima_dosis)
        WHEN 'Vencida' THEN 1
        WHEN 'Próxima' THEN 2
        ELSE 3
    END,
    cv.proxima_dosis;


-- =========================================
-- PERMISOS — ROL DE APLICACIÓN
-- =========================================

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'kampets_app') THEN
        CREATE ROLE kampets_app LOGIN PASSWORD 'kampets_app_2024';
    END IF;
END$$;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA public TO kampets_app;
GRANT USAGE, SELECT                  ON ALL SEQUENCES IN SCHEMA public TO kampets_app;
GRANT EXECUTE ON FUNCTION fn_estado_vacuna(DATE)      TO kampets_app;
