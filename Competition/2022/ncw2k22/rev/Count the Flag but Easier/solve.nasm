SECTION .text
global main
main:
        push    rbp
        mov     rbp, rsp
        mov     DWORD PTR [rbp-4], 20
        mov     DWORD PTR [rbp-8], 10
        mov     DWORD PTR [rbp-12], 20
        mov     eax, DWORD PTR [rbp-4]
        imul    eax, DWORD PTR [rbp-8]
        lea     ecx, [rax+2]
        mov     eax, DWORD PTR [rbp-12]
        mov     edx, eax
        sal     eax, 2
        sub     edx, eax
        lea     eax, [rcx+rdx]
        mov     DWORD PTR [rbp-16], eax
        sal     DWORD PTR [rbp-16], 20
        cmp     DWORD PTR [rbp-16], 100000000
        jg      .L2
        mov     eax, DWORD PTR [rbp-16]
        lea     edx, [rax+3]
        test    eax, eax
        cmovs   eax, edx
        sar     eax, 2
        mov     DWORD PTR [rbp-16], eax
        jmp     .L3
.L2:
        cmp     DWORD PTR [rbp-16], 100000000
        jle     .L4
        cmp     DWORD PTR [rbp-16], 500000000
        jg      .L4
        mov     eax, DWORD PTR [rbp-16]
        lea     edx, [rax+7]
        test    eax, eax
        cmovs   eax, edx
        sar     eax, 3
        mov     DWORD PTR [rbp-16], eax
        jmp     .L3
.L4:
        mov     eax, DWORD PTR [rbp-16]
        mov     edx, eax
        shr     edx, 31
        add     eax, edx
        sar     eax
        mov     DWORD PTR [rbp-16], eax
.L3:
        nop
        pop     rbp
        ret