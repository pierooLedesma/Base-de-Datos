;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;DEFINITIONS;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

%define LOAD_ADDRESS 0x00020000 ; pretty much any number >0 works
%define CODE_SIZE END-(LOAD_ADDRESS+0x78) ; everything beyond the HEADER is code

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;HEADER;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

BITS 64
org LOAD_ADDRESS
ELF_HEADER:
	db 0x7F,"ELF" ; magic number to indicate ELF file
	db 0x02 ; 0x1 for 32-bit, 0x2 for 64-bit
	db 0x01 ; 0x1 for little endian, 0x2 for big endian
	db 0x01 ; 0x1 for current version of ELF
	db 0x09 ; 0x9 for FreeBSD, 0x3 for Linux (doesn't seem to matter)
	db 0x00 ; ABI version (ignored?)
	times 7 db 0x00 ; 7 padding bytes
	dw 0x0002 ; executable file
	dw 0x003E ; AMD x86-64 
	dd 0x00000001 ; version 1
	dq START ; entry point for our program
	dq 0x0000000000000040 ; 0x40 offset from ELF_HEADER to PROGRAM_HEADER
	dq 0x0000000000000000 ; section header offset (we don't have this)
	dd 0x00000000 ; unused flags
	dw 0x0040 ; 64-byte size of ELF_HEADER
	dw 0x0038 ; 56-byte size of each program header entry
	dw 0x0001 ; number of program header entries (we have one)
	dw 0x0000 ; size of each section header entry (none)
	dw 0x0000 ; number of section header entries (none)
	dw 0x0000 ; index in section header table for section names (waste)
PROGRAM_HEADER:
	dd 0x00000001 ; 0x1 for loadable program segment
	dd 0x00000007 ; read/write/execute flags
	dq 0x0000000000000078 ; offset of code start in file image (0x40+0x38)
	dq LOAD_ADDRESS+0x78 ; virtual address of segment in memory
	dq 0x0000000000000000 ; physical address of segment in memory (ignored?)
	dq CODE_SIZE ; size (bytes) of segment in file image
	dq CODE_SIZE ; size (bytes) of segment in memory
	dq 0x0000000000000000 ; alignment (doesn't matter, only 1 segment)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;INCLUDES;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

	; external assembly files will be included here
	%include "letras.txt"

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;INSTRUCTIONS;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

START:	; address label representing the entry point of our program

	cmp [rsp], 2
	jne error
		
	mov rdi, [rsp+16] ; Empieza en el inicio de la cadena 
			  ; Posteriormente sera incrementado para poder leer la cadena
	bucle:
		xor rax, rax 		; limpia rax
		mov al, byte[rdi] 	; Obtiene el caracter

		cmp al, 0 		; Verifica que no es el final de la cadena
		je finbucle

		push rdi 		; Se salva la posicion actual de la cadena 
		
		cmp al, ' '		; Comparamos si es un espacio vacio
		je imprimir		; De serlo, no es necesario rellenar nada, solo imprimir

		xor rdi, rdi		; Se carga rdi con 0, para luego cargar la posicion del caracter y poder usar 
					; registro de menor tamanho
		mov dil, al		; Pasamos como primer parametro el caracter de la posicion actual de la cadena
		sub dil, 'A'		; Se resta 'A', para saber que posicion en el alfabeto respecto a la letra 'A'
					; se tiene. Por ejemplo, 'A' es 0, 'B' es 1, 'C' es 2, ..., 'Z' es 26

		call rellenar		; Se prepara la letra para ser impreso
	imprimir:
	
		mov rax, 1		; Se escribe en pantalla la letra
		mov rdi, 1
		mov rsi, letra
		mov rdx, finletra - letra
		syscall


		call vaciar		; Se limpia la letra, de tal manera que la proxima letra pueda usar el espacio para ser pintado

		pop rdi			; Se recupera el valor de rdi que contenia la posicion actual de la cadena

		inc rdi			; Aumenta en 1 la posicion actual de la cadena
		jmp bucle
	finbucle:
	
	mov rdi,0
	jmp salida
error:
	mov rdi,1
salida:
	mov rax, 60
	syscall

rellenar:
	; No es necesario salvar variables, puesto que todos, a excepcion de rdi que ya fue salvado,
	; se declaran en el codigo que llama a esta funcion

	; Segun "letras.txt", cada letra ocupa 5 bytes, por ello, si empezamos en 'A' y queremos avanzar n letras,
	; Se multiplica por 5 a n y se le suma la posicion de A 
	mov rbx, rdi			; Se guarda la posicion de la letra respecto al alfabeto
	shl rbx, 2			; Mueve 2 bits a la izquierda (Multiplica por 2^2)
	add rbx, rdi			; Aumenta en 1
	add rbx, A			; Se agrega la posicion de la letra A

	; Segun letras.txt, hay 5 bytes por letra, pero solo se usan los 35 bits menos significativos.
        ; Por ello, para la busqueda de los bits que son 1 o 0, se suma 5 a la posicion inicial

	xor rsi, rsi			; Se usa rsi para obtener la posicion del bit que se quiere analizar

bucle_relleno:
	mov rax, rsi			; Se carga rsi en rax, este tendra guardado el numero del byte que se quiere analizar de los 5 bytes
	add rax, 5			; Se le agrega 5, debido a lo que se explico anteriormente

	mov rcx, rax			; Se carga el mismo numero en rcx, este tendra guardado el valor del bit que se quiere analizar
	shr rax, 3 			; Obtiene el byte, divide entre 8 (2^3)
	and rcx, 7 			; Obtiene el bit, modulo 8
	
					; Debido a que se quiere aislar el bit que se quiere analizar, se mueve este bit de tal manera que
					; sea el menos significativo. Luego, se usa AND para saber si ese bit es 1 o 0. Para poder aislarlo
					; es necesario restar a 7 la posicion del bit que se quiere analizar.
					
					; Por ejemplo: Si se tiene 10010101, y se quiere analizar la posicion 3 (100|1|0101), contando desde
					; el byte mas significativo, entonces debo mover todo 4 bytes a la derecha (7-3) y va a quedar
					; 00001001. De este solo se quiere el bit menos significativo, es por ello que se hace AND con 1,
					; quedando 00000001.
					
	mov ch, 7			; Se aprovecha que el valor obtenido ocupa menos de 4 bytes, por lo que esto no modifica ch
	sub ch, cl ; 7 - cl		; Se resta 7 al valor de la posicion del bit obtenida anteriormente 
	mov cl, ch			; Para poder hacer shift right con el registro de 4 bytes de c, se usa cl (Eso dice el manual (?))
		
	mov dl, byte[rbx+rax]		; En dl se guarda el byte que estamos analizando
	shr dl, cl 			; Mueve  a la derecha, de tal manera que el bit que se busca sea el menos significativo
	and dl, 1  			; Convierte el valor en 1 o 0

	cmp dl, 1			; En caso de ser 1, entonces, se pinta
	je es_uno

	jmp fin_bucle_relleno		; Fin del bucle en caso de no ser 1

es_uno:
					; Este usa rdi y rsi como pase de parametros (rdi contiene la posicion de la letra, rsi la posicion del bit)
	call pintar			; Se encarga de pintar ese bit en la letra

		
fin_bucle_relleno:
	inc rsi				; Aumenta en 1 la posicion actual del bit que se quiere analizar
	cmp rsi, 35			; Si es 35, ya se ha leido todos los bits necesarios
	jne bucle_relleno

	ret 

pintar:
	; No es necesario salvar variables puesto que no se modifican

	; Para poder pintar un bit, en la letra, es necesario calcular el byte a pintar.
	; La letra empieza con un bit que no se debe de modificar, entonces avanzamos una posicion.
	; Dentro de una fila de 10 bytes (9 bytes inicialmente con espacios, 1 byte reservadp), 
	; el byte que se pinta, siempre tiene una posicion par respecto a esta fila, empezando con 0;
	; los impares siempre son un espacio vacio. Es por ello que solo se pueden escribir 5 bytes
	; en cada fila.
	
	; Por ello, es necesario obtener el numero de fila que se esta escribiendo y la posicion 
	; dentro de esa fila que se debe escribir. Para obtener el numero de fila, debemos dividir
	; entre 5 la posicion del bit que se analizo anteriormente, y para obtener la posicion
	; dentro de esa fila que se debe escribir, se debe obtener el resto de la division que se realizo.

	; Luego, como cada fila tiene 10 bytes, entonces, al numero de fila se le multiplica por 10.
	; En el caso de la posicion dentro la fila que se debe escribir, se le multiplica por 2 para que siempre sea par
	
	;xor rdx, rdx			; Se limpia rdx por precaucion
	mov rax, rsi			; Para poder hacer una division, se carga rax con rsi
	mov rcx, 5			; Se carga un registro con 5 para hacer la division
	div rcx				; La division guarda el cociente en rax y el resto en rdx

	mov cl, 'A'			; Se inicializa la letra a pintarse
	add rcx, rdi			; Se le agrega la posicion respecto el alfabeto

	imul rax, 10			; Para no modificar rdx y no cargar un registro, se usa imul ( MUL puede modificar rdx )
	shl rdx, 1			; Se mueve un bit a la izquierda para multiplicar por 2
	add rax, rdx			; Sumamos la cantidad de bytes por fila y la posicion del byte a escribirse
	
	mov [letra+1+rax], cl		; A esta posicion se le coloca el valor de cl (La letra)

	ret

vaciar:
	; Vacia la letra para poder preparar la siguiente letra
	; No es necesario salvar ninguna variable
	
    	mov rdi, letra			; Se carga la posicion de la letra             
    	mov al, ' '         		; Se carga el caracter ' ' que reemplazara los caracteres regitrados

bucle_limpiar:
    	cmp byte[rdi], 10  		; Compara si la posicion actual es un cambio de linea
    	je saltar_byte   		; de ser asi, volvemos al bucle

    	mov byte [rdi], al  		; se modifica la posicion actual con un espacio ' '

saltar_byte:
    	inc rdi             		; Aumenta la posicion actual
    	cmp rdi, finletra            	; Compara si estamos en la posicion final
    	jnz bucle_limpiar  		; De no ser asi, volvemos al bucle
    	
    	ret	

letra:
	db 10
	db '         ', 10
	db '         ', 10
	db '         ', 10
	db '         ', 10
	db '         ', 10
	db '         ', 10
	db '         ', 10
	db 10
finletra:

END:	; address label representing the end of our program

