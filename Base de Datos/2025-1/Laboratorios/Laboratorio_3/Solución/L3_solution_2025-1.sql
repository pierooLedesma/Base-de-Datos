
-- Pregunta 1
--(Solución de la pregunta 1)

CREATE OR REPLACE FUNCTION FN_ES_FAMILIAR(p_doc_pasajero_1 VARCHAR2, p_doc_pasajero_2 VARCHAR2)
RETURN VARCHAR2
IS  
    -- Variables que almacenarán los identificadores (ID) de cada pasajero.
    v_id_pasajero_1 AP_PASAJEROS.PASAJERO_ID%TYPE;
    v_id_pasajero_2 AP_PASAJEROS.PASAJERO_ID%TYPE;
    
    -- Variable que cuenta si existe relación familiar entre ambos pasajeros.
    p_contador NUMBER;
    
    -- Excepción personalizada para validar parámetros nulos.
    parametro_nulo EXCEPTION;
    
BEGIN
    -- Validación de si algún parámetro ingresado es nulo.
    IF (p_doc_pasajero_1 IS NULL OR p_doc_pasajero_2 IS NULL) THEN
        RAISE parametro_nulo;
    END IF;
    
    
    /*
      En los 2 siguientes "SELECT ... INTO" podría lanzar la excepción "NO_DATA_FOUND"
      si no existe un pasajero cuyo "NRO_DOCUMENTO" coincida con los parámetros ingresados.
    */
    SELECT PASAJERO_ID INTO v_id_pasajero_1
    FROM AP_PASAJEROS
    WHERE NRO_DOCUMENTO = p_doc_pasajero_1;

    SELECT PASAJERO_ID INTO v_id_pasajero_2
    FROM AP_PASAJEROS
    WHERE NRO_DOCUMENTO = p_doc_pasajero_2;
    
    
    /*
      Se verifica si existe relación familiar entre ambos pasajeros.
      Se busca en ambas direcciones (pasajero-pariente o pariente-pasajero).
    */
    SELECT COUNT(*) INTO p_contador
    FROM AP_PARENTESCOS P
    WHERE ( (P.PASAJERO_ID = v_id_pasajero_1 AND P.PARIENTE_ID = v_id_pasajero_2)
           OR (P.PASAJERO_ID = v_id_pasajero_2 AND P.PARIENTE_ID = v_id_pasajero_1) );
    
    
    -- Retorna 'SI' si se encontró parentesco; caso contrario, 'NO'.
    IF (p_contador > 0) THEN
        RETURN 'SI';
    ELSE
        RETURN 'NO';
    END IF;

EXCEPTION
    -- Excepción cuando no se encuentra el documento de algún pasajero.
    WHEN NO_DATA_FOUND THEN
        RETURN 'ERROR: No se encontró el documento de un pasajero.';
    
    -- Excepción cuando uno de los parámetros enviados es nulo.
    WHEN parametro_nulo THEN
        RETURN 'ERROR: Uno de los parámetros ingresados es nulo.';
END;


-- Casos de prueba
SELECT FN_ES_FAMILIAR('45678901','10293847') FROM DUAL; -- Jorge y Ana -> 'SI'
SELECT FN_ES_FAMILIAR('USA123456','23456789') FROM DUAL; -- Robert y Javier -> 'NO'









-- Pregunta 2
--(Solución de la pregunta 2)

SET SERVEROUTPUT ON;

CREATE OR REPLACE PROCEDURE SP_VALIDAR_TRIPULACION_VUELO(p_codigo_vuelo CHAR)
IS
    v_verificar_codVuelo NUMBER;
    v_pilotos NUMBER;
    v_tripulantes_cabina NUMBER;
    parametro_nulo EXCEPTION;
BEGIN
    -- Validación de si el parámetro del código de vuelo es nulo.
    IF (p_codigo_vuelo IS NULL) THEN
        RAISE parametro_nulo;
    END IF;
    
    -- Verificar existencia del vuelo (puede ocurrir 'NO_DATA_FOUND').
    SELECT 1 INTO v_verificar_codVuelo
    FROM AP_VUELOS
    WHERE VUELO_ID = p_codigo_vuelo;
    
    -- Contar los pilotos en el vuelo solicitado.
    SELECT COUNT(*) INTO v_pilotos
    FROM AP_EMPLEADOS_VUELOS EV, AP_EMPLEADOS E, AP_CARGOS C
    WHERE EV.EMPLEADO_ID = E.EMPLEADO_ID AND E.CARGO_ID = C.CARGO_ID
          AND EV.VUELO_ID = p_codigo_vuelo AND C.DETALLE_CARGO = 'Piloto';
    
    -- Contar los tripulantes de cabina en el vuelo solicitado.
    SELECT COUNT(*) INTO v_tripulantes_cabina
    FROM AP_EMPLEADOS_VUELOS EV, AP_EMPLEADOS E, AP_CARGOS C
    WHERE EV.EMPLEADO_ID = E.EMPLEADO_ID AND E.CARGO_ID = C.CARGO_ID
          AND EV.VUELO_ID = p_codigo_vuelo AND
          C.DETALLE_CARGO IN ('Jefe de Cabina', 'Aeromozo', 'Aeromoza');
    
    IF (v_pilotos >= 1 AND v_tripulantes_cabina > 0) THEN
        dbms_output.put_line('Vuelo ' || p_codigo_vuelo || ' cuenta con tripulación mínima.');
    ELSE
        dbms_output.put_line('Vuelo ' || p_codigo_vuelo || ' NO cumple con los requisitos mínimos:');
        IF (v_pilotos = 0) THEN 
            dbms_output.put_line('- Falta PILOTO');
        ELSE
            dbms_output.put_line('- Falta un tripulante de cabina');
        END IF;
    END IF;
EXCEPTION
    WHEN parametro_nulo THEN
        dbms_output.put_line('El parámetro ingresado es nulo.');
    WHEN NO_DATA_FOUND THEN
        dbms_output.put_line('El código de vuelo ' || p_codigo_vuelo || ' no existe.');
END;


-- Casos de prueba
EXEC SP_VALIDAR_TRIPULACION_VUELO('LA2201');
EXEC SP_VALIDAR_TRIPULACION_VUELO('UA4501');










-- Pregunta 3
--(Solución de la pregunta 3)

SET SERVEROUTPUT ON;

CREATE OR REPLACE PROCEDURE SP_DATOS_VUELO(p_codigo_vuelo CHAR)
IS
    v_verificar_codigo NUMBER;
    v_regVuelo AP_VUELOS%ROWTYPE;
    v_placa AP_AVIONES.NRO_PLACA%TYPE;
    v_modelo AP_AVIONES.MODELO%TYPE;
    v_marca_id AP_AVIONES.MARCA_ID%TYPE;
    v_marca AP_MARCAS.DESCRIPCION%TYPE;
    v_nombre_aeropuerto AP_AEROPUERTOS.NOMBRE%TYPE;
    v_ciudad_id AP_AEROPUERTOS.CIUDAD_ID%TYPE;
    v_ciudad AP_CIUDADES.NOMBRE_CIUDAD%TYPE;
    parametro_nulo EXCEPTION;
BEGIN
    -- Validación de si el parámetro del código de vuelo es nulo.
    IF (p_codigo_vuelo IS NULL) THEN
        RAISE parametro_nulo;
    END IF;
    
    -- Verificar la existencia del vuelo (puede ocurrir la excepción de 'NO_DATA_FOUND').
    SELECT 1 INTO v_verificar_codigo
    FROM AP_VUELOS WHERE AP_VUELOS.VUELO_ID = p_codigo_vuelo;
    
    dbms_output.put_line('Código de vuelo: ' || p_codigo_vuelo);
    dbms_output.put_line('Datos del avión');
    dbms_output.put_line('---------------');
    
    -- Obtener el registro del vuelo del avión mediante la tabla de 'AP_VUELOS'.
    SELECT * INTO v_regVuelo
    FROM AP_VUELOS
    WHERE VUELO_ID = p_codigo_vuelo;
    
    -- Obtener el número de placa del avión, el modelo del avión y el identificador de la marca mediante la tabla 'AP_AVIONES'.
    SELECT NRO_PLACA, MODELO, MARCA_ID INTO v_placa, v_modelo, v_marca_id
    FROM AP_AVIONES
    WHERE AVION_ID = v_regVuelo.AVION_ID;
    
    -- Obtener la marca del avión mediante la tabla de 'AP_MARCAS'.
    SELECT DESCRIPCION INTO v_marca
    FROM AP_MARCAS
    WHERE MARCA_ID = v_marca_id;
    
    dbms_output.put_line('Marca: ' || v_marca);
    dbms_output.put_line('Placa: ' || v_placa);
    dbms_output.put_line('Modelo: ' || v_modelo);
    dbms_output.put_line('Datos del vuelo');
    dbms_output.put_line('---------------');
    
    
    /*
       Proceso para hallar el nombre del aeropueto y ciudad de partida.
    */
    
    -- Obtener el nombre del aeropuerto de partida y del identificador de la ciudad de partida mediante la tabla 'AP_AEROPUERTOS'.
    SELECT NOMBRE, CIUDAD_ID INTO v_nombre_aeropuerto, v_ciudad_id
    FROM AP_AEROPUERTOS
    WHERE AEROPUERTO_ID = v_regVuelo.AEROPUERTO_PARTIDA_ID;
    
    -- Obtener el nombre de la ciudad de partida mediante la tabla 'AP_CIUDADES'.
    SELECT NOMBRE_CIUDAD INTO v_ciudad
    FROM AP_CIUDADES
    WHERE CIUDAD_ID = v_ciudad_id;
    
    dbms_output.put_line('Lugar de origen: ' || v_ciudad || ' - ' || v_nombre_aeropuerto);
    dbms_output.put_line('Fecha partida: ' || TO_CHAR(v_regVuelo.FECHA_HORA_PARTIDA, 'DD/MM/YYYY HH24:MI:SS'));
    
    
    /*
       Proceso para hallar el nombre del aeropueto y ciudad de llegada.
    */
    
    -- Obtener el nombre del aeropuerto de llegada y del identificador de la ciudad de llegada mediante la tabla 'AP_AEROPUERTOS'.
    SELECT NOMBRE, CIUDAD_ID INTO v_nombre_aeropuerto, v_ciudad_id
    FROM AP_AEROPUERTOS
    WHERE AEROPUERTO_ID = v_regVuelo.AEROPUERTO_LLEGADA_ID;
    
    -- Obtener el nombre de la ciudad de llegada mediante la tabla 'AP_CIUDADES'.
    SELECT NOMBRE_CIUDAD INTO v_ciudad
    FROM AP_CIUDADES
    WHERE CIUDAD_ID = v_ciudad_id;
    
    dbms_output.put_line('Lugar de origen: ' || v_ciudad || ' - ' || v_nombre_aeropuerto);
    dbms_output.put_line('Fecha partida: ' || TO_CHAR(v_regVuelo.FECHA_HORA_LLEGADA, 'DD/MM/YYYY HH24:MI:SS'));
EXCEPTION
    -- Excepción cuando no se encuentra el código de vuelo.
    WHEN NO_DATA_FOUND THEN
        dbms_output.put_line('No se encontró el código de vuelo ' || p_codigo_vuelo);
        
    -- Excepción cuando el parámetro ingresado es nulo.
    WHEN parametro_nulo THEN
        dbms_output.put_line('El parámetro ingresado es nulo.');
END;


-- Casos de prueba
EXEC SP_DATOS_VUELO('AV3002');
EXEC SP_DATOS_VUELO('CM1001');










-- Pregunta 4
--(Solución de la pregunta 4)

CREATE OR REPLACE FUNCTION FN_OBTENER_MANTENIMIENTO(p_nro_placa NUMBER DEFAULT NULL)
RETURN VARCHAR2
IS
    v_avion_id NUMBER;
    v_tipo_mant_id NUMBER;
    v_nombre AP_TIPOS_MANTENIMIENTO.DESCRIPCION%TYPE;
    parametro_nulo EXCEPTION;
BEGIN
    -- Validación de si el parámetro del número de placa es nulo.
    IF (p_nro_placa IS NULL) THEN
        RAISE parametro_nulo;
    END IF;
    
    -- Obtener el identificador de la placa mediante la tabla 'AP_AVIONES'.
    SELECT AVION_ID INTO v_avion_id
    FROM AP_AVIONES
    WHERE NRO_PLACA = p_nro_placa;
    
    -- Obtener el identificador del tipo de mantenimiento mediante la tabla 'AP_MANT_AVIONES'.
    SELECT TIPO_MANTENIMIENTO_ID INTO v_tipo_mant_id
    FROM AP_MANT_AVIONES
    WHERE AVION_ID = v_avion_id;
    
    -- Obtener el nombre del mantenimiento mediante la tabla 'AP_TIPOS_MANTENIMIENTO'.
    SELECT DESCRIPCION INTO v_nombre
    FROM AP_TIPOS_MANTENIMIENTO
    WHERE TIPO_MANTENIMIENTO_ID = v_tipo_mant_id;
    
    RETURN 'Se realizó un mantenimiento ' || v_nombre || ' al avion de placa ' || p_nro_placa;

EXCEPTION
    -- Excepción cuando no se encuentra el número de placa.
    WHEN NO_DATA_FOUND THEN
        RETURN 'No se encontró mantenimiento para la placa ' || p_nro_placa;
        
    -- Excepción cuando el parámetro ingresado es nulo o no se ingresó ningún parámetro en la función.
    WHEN parametro_nulo THEN
        RETURN 'NINGUNO';
END;


-- Casos de prueba
select FN_OBTENER_MANTENIMIENTO(44556)from dual;
select FN_OBTENER_MANTENIMIENTO(44356)from dual;
select FN_OBTENER_MANTENIMIENTO()from dual;


