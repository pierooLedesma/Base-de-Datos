
-- Pregunta 1
-- (Solución de la pregunta 1)

CREATE OR REPLACE PROCEDURE sp_actualiza_precio_venta
IS
    -- Declaración del cursor de la tabla "DETALLE_ORD_PEDIDO"
    CURSOR c_detalle_ord_pedido IS
    SELECT * FROM DETALLE_ORD_PEDIDO
    ORDER BY TO_NUMBER(ID_DETALLE_PEDIDO);
    
    -- Declaración del registro del cursor.
    v_reg c_detalle_ord_pedido%ROWTYPE;
    
    v_precio_venta_unitario DETALLE_ORD_PEDIDO.PRECIO_VENTA_UNITARIO%TYPE;
BEGIN
    OPEN c_detalle_ord_pedido;
    LOOP
        FETCH c_detalle_ord_pedido INTO v_reg;
        EXIT WHEN c_detalle_ord_pedido%NOTFOUND;
        
        -- Obtener el costo de producción unitario
        SELECT COSTO_PRODUCCION_UNITARIO INTO v_precio_venta_unitario
        FROM ORDEN_PRODUCCION
        WHERE ID_ORDEN_PRODUCCION = v_reg.ID_ORDEN_PRODUCCION;
        
        -- Actualizar el precio de venta unitario
        UPDATE DETALLE_ORD_PEDIDO
        SET PRECIO_VENTA_UNITARIO = v_precio_venta_unitario * 1.30
        WHERE ID_DETALLE_PEDIDO = v_reg.ID_DETALLE_PEDIDO;
        
    END LOOP;
    CLOSE c_detalle_ord_pedido;
END;

-- Ejecución del procedimiento "sp_actualiza_precio_venta"
BEGIN
    sp_actualiza_precio_venta();
END;

-- Mostrar la tabla actualizada "DETALLE_ORD_PEDIDO"
SELECT * FROM DETALLE_ORD_PEDIDO
ORDER BY TO_NUMBER(ID_DETALLE_PEDIDO);





-- Pregunta 2
-- (Solución de la pregunta 2)
SET SERVEROUTPUT ON;
CREATE OR REPLACE PROCEDURE sp_imprimir_detalle_pedido(
    p_id_pedido ORDEN_PEDIDO.ID_ORDEN_PEDIDO%TYPE
)
IS
    CURSOR c_detalle_ord_pedido IS
    SELECT DOP.ID_ORDEN_PEDIDO, DOP.CANTIDAD, DOP.PRECIO_VENTA_UNITARIO,
           DOP.ID_TIPO_BUS, B.NOMBRE
    FROM DETALLE_ORD_PEDIDO DOP
    JOIN TIPO_BUS B ON B.ID_TIPO_BUS = DOP.ID_TIPO_BUS
    ORDER BY ID_TIPO_BUS;
    
    v_fecha_entrega ORDEN_PEDIDO.FECHA_ENTREGA%TYPE;
    v_fecha_registro ORDEN_PEDIDO.FECHA_REGISTRO%TYPE;
BEGIN
    -- Obtener la fecha de entrega y la fecha de registro del pedido solicitado.
    -- (Aquí puede haber lanzar la excepción de "NO_DATA_FOUND" si el "id" ingresado no existe).
    SELECT FECHA_ENTREGA, FECHA_REGISTRO
    INTO v_fecha_entrega, v_fecha_registro
    FROM ORDEN_PEDIDO
    WHERE ID_ORDEN_PEDIDO = p_id_pedido;
    
    DBMS_OUTPUT.PUT_LINE('Pedido Nro: ' || p_id_pedido);
    DBMS_OUTPUT.PUT_LINE('Fecha Registro: ' || v_fecha_registro);
    DBMS_OUTPUT.PUT_LINE('Fecha Entrega: ' || v_fecha_entrega);
    DBMS_OUTPUT.PUT_LINE('Detalle de Articulos');
    DBMS_OUTPUT.PUT_LINE('****************************************************************************************************');
    DBMS_OUTPUT.PUT_LINE('ID TIPO BUS - NOMBRE TIPO BUS - CANTIDAD - PRECIO VENTA UNITARIO');
    DBMS_OUTPUT.PUT_LINE('****************************************************************************************************');
    
    FOR v_reg IN c_detalle_ord_pedido LOOP
        IF (v_reg.ID_ORDEN_PEDIDO = p_id_pedido) THEN
            DBMS_OUTPUT.PUT_LINE(v_reg.ID_TIPO_BUS||'- '|| v_reg.NOMBRE||'- '||v_reg.CANTIDAD||' - '||v_reg.PRECIO_VENTA_UNITARIO);
        END IF;
    END LOOP;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('El pedido no existe');
END;


-- Ejecución del caso de prueba correcto
BEGIN
    sp_imprimir_detalle_pedido(10);
END;

-- Ejecución del caso de prueba incorrecto
BEGIN
    sp_imprimir_detalle_pedido(89);
END;






-- Pregunta 3
-- (Solución de la pregunta 3)
CREATE OR REPLACE TRIGGER TRG_ACTUALIZA_FECHA
BEFORE INSERT OR UPDATE ON SEDE
FOR EACH ROW
BEGIN
    -- Si se está actualizando una fila de la tabla "SEDE",
    -- entonces solo actualizar el campo "FECHA_ULTIMA_MODIFICACION"
    IF updating THEN
        :NEW.FECHA_ULTIMA_MODIFICACION := SYSDATE;
    END IF;
    
    -- Si se está insertando una fila de la tabla "SEDE",
    -- entonces actualizar el campo "FECHA_CREACION"
    -- y el campo "FECHA_ULTIMA_MODIFICACION"
    IF inserting THEN
        :NEW.FECHA_CREACION := SYSDATE;
        :NEW.FECHA_ULTIMA_MODIFICACION := SYSDATE;
    END IF;
END;

-- Caso de prueba:
-- Inserción de una fila de "ID_SEDE" igual a 9.
INSERT INTO SEDE (ID_SEDE, NOMBRE_SEDE, DISTRITO, PROVINCIA, AREA_SEDE, DIRECCION, TELEFONO)
VALUES (9, 'Almacen Lima Centro', 'Cercado de Lima', 'Lima', 1200, 'Av Andahuaylas 1258', '999654123');

-- Verificación de la inserción de la sede de "ID_SEDE" igual a 9.
SELECT * FROM SEDE WHERE ID_SEDE = 9;






-- Pregunta 4
-- (Solución de la pregunta 4)
CREATE OR REPLACE TRIGGER TRG_ACTUALIZA_PRECIO
BEFORE UPDATE OF COSTO_PRODUCCION_UNITARIO ON ORDEN_PRODUCCION
FOR EACH ROW
BEGIN
    UPDATE DETALLE_ORD_PEDIDO
    SET PRECIO_VENTA_UNITARIO = :NEW.COSTO_PRODUCCION_UNITARIO * 1.30
    WHERE ID_ORDEN_PRODUCCION = :NEW.ID_ORDEN_PRODUCCION;
END;

-- Caso de prueba:
-- Actualizar el costo de producción unitario cuyo "ID_ORDEN_PRODUCCION" es 5.
UPDATE ORDEN_PRODUCCION SET COSTO_PRODUCCION_UNITARIO = 160000 WHERE ID_ORDEN_PRODUCCION = 5;

-- Mostrar las filas de la tabla "DETALLE_ORD_PEDIDO" cuyo "ID_ORDEN_PRODUCCION" es 5.
SELECT * FROM DETALLE_ORD_PEDIDO
WHERE ID_ORDEN_PRODUCCION = 5
ORDER BY TO_NUMBER(ID_DETALLE_PEDIDO);

