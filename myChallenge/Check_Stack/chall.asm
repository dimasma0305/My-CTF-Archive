
global _start
section .text
_start:
    push 0
	push 0
	push 0
	push 0
    
    mov edi, esp
    
    mov al, 100
    add [edi], al
    inc edi
    mov al, 105
    add [edi], al
    inc edi
    mov al, 109
    add [edi], al
    inc edi
    mov al, 97
    add [edi], al
    inc edi
    mov al, 115
    add [edi], al
    inc edi
    mov al, 32
    add [edi], al
    inc edi
    mov al, 109
    add [edi], al
    inc edi
    mov al, 97
    add [edi], al
    inc edi
    mov al, 117
    add [edi], al
    inc edi
    mov al, 108
    add [edi], al
    inc edi
    mov al, 97
    add [edi], al
    inc edi
    mov al, 110
    add [edi], al
    inc edi
    mov al, 97
    add [edi], al
    inc edi
    mov eax, 1
    int 0x80