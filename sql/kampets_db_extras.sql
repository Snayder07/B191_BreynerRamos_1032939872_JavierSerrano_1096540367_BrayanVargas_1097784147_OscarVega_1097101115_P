-- =========================================
-- KAMPETS — FUNCIONES, ÍNDICES, CONSTRAINTS
-- Ejecutar en Supabase SQL Editor
-- =========================================


-- =========================================
-- 1. FUNCIÓN: ESTADO DE VACUNA
--    Centraliza la lógica que antes vivía en Java
-- =========================================
CREATE OR REPLACE FUNCTION fn_estado_vacuna(proxima_dosis DATE)
RETURNS VARCHAR AS $$
BEGIN
    IF proxima_dosis IS NULL                             THEN RETURN 'Al día';
    ELSIF proxima_dosis < CURRENT_DATE                  THEN RETURN 'Vencida';
    ELSIF proxima_dosis < CURRENT_DATE + INTERVAL '30 days' THEN RETURN 'Próxima';
    ELSE                                                      RETURN 'Al día';
    END IF;
END;
$$ LANGUAGE plpgsql STABLE;


-- =========================================
-- 2. ÍNDICES DE RENDIMIENTO
--    Aceleran las consultas por FK y columnas filtradas frecuentemente
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
-- 3. CONSTRAINTS DE VALIDACIÓN
--    La BD rechaza datos inválidos antes de llegar a Java
-- =========================================

-- Estado de cita solo puede ser uno de estos valores
ALTER TABLE CITAS
    DROP CONSTRAINT IF EXISTS chk_estado_cita,
    ADD  CONSTRAINT chk_estado_cita
    CHECK (estado_cita IN ('PENDIENTE','CONFIRMADA','CANCELADA','COMPLETADA'));

-- Sexo de mascota
ALTER TABLE MASCOTAS
    DROP CONSTRAINT IF EXISTS chk_sexo_mascota,
    ADD  CONSTRAINT chk_sexo_mascota
    CHECK (sexo IN ('Macho','Hembra') OR sexo IS NULL);

-- Stock nunca negativo
ALTER TABLE PRODUCTOS
    DROP CONSTRAINT IF EXISTS chk_stock_positivo,
    ADD  CONSTRAINT chk_stock_positivo
    CHECK (stock_pro >= 0);

-- Precios positivos
ALTER TABLE PRODUCTOS
    DROP CONSTRAINT IF EXISTS chk_precio_producto,
    ADD  CONSTRAINT chk_precio_producto
    CHECK (precio_pro > 0);

ALTER TABLE SERVICIOS
    DROP CONSTRAINT IF EXISTS chk_precio_servicio,
    ADD  CONSTRAINT chk_precio_servicio
    CHECK (precio_se IS NULL OR precio_se > 0);

-- Cantidad positiva en pedido
ALTER TABLE DETALLE_PEDIDO
    DROP CONSTRAINT IF EXISTS chk_cantidad_positiva,
    ADD  CONSTRAINT chk_cantidad_positiva
    CHECK (cantidad > 0);

-- Próxima dosis no puede ser antes de la fecha de aplicación
ALTER TABLE CONTROL_VACUNAS
    DROP CONSTRAINT IF EXISTS chk_proxima_dosis_logica,
    ADD  CONSTRAINT chk_proxima_dosis_logica
    CHECK (proxima_dosis IS NULL OR proxima_dosis >= fecha_aplicacion);


-- =========================================
-- 4. TRIGGER: TOTAL DE PEDIDO AUTOMÁTICO
--    Cada vez que se inserta/modifica/elimina un detalle,
--    recalcula el total del pedido. Ya no es necesario en Java.
-- =========================================
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

DROP TRIGGER IF EXISTS trg_total_pedido ON DETALLE_PEDIDO;
CREATE TRIGGER trg_total_pedido
AFTER INSERT OR UPDATE OR DELETE ON DETALLE_PEDIDO
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_total_pedido();


-- =========================================
-- 5. VISTA: VACUNAS COMPLETAS
--    Para reportes y consultas directas en Supabase
-- =========================================
CREATE OR REPLACE VIEW v_vacunas_completas AS
SELECT
    cv.id,
    m.nombre_m                          AS mascota,
    c.nombre_c                          AS dueno,
    v.nombre_vacuna                     AS vacuna,
    cv.fecha_aplicacion,
    cv.proxima_dosis,
    fn_estado_vacuna(cv.proxima_dosis)  AS estado
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
-- 6. PERMISOS (rol de aplicación de solo escritura/lectura)
--    Opcional: crear un rol dedicado para la app
-- =========================================
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'kampets_app') THEN
        CREATE ROLE kampets_app LOGIN PASSWORD 'kampets_app_2024';
    END IF;
END$$;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO kampets_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO kampets_app;
GRANT EXECUTE ON FUNCTION fn_estado_vacuna(DATE) TO kampets_app;
