void _init()
{
    if (__gmon_start__ != 0)
    {
        __gmon_start__();
    }
}

int64_t sub_1030()
{
    int64_t var_8 = 0;
    int64_t var_10 = 0;
    /* jump -> nullptr */
}

void __cxa_finalize(void* d)
{
    /* tailcall */
    return __cxa_finalize(d);
}

void free(void* mem)
{
    /* tailcall */
    return free(mem);
}

int32_t puts(char const* str)
{
    /* tailcall */
    return puts(str);
}

void __stack_chk_fail() __noreturn
{
    /* tailcall */
    return __stack_chk_fail();
}

void setbuf(FILE* fp, char* buf)
{
    /* tailcall */
    return setbuf(fp, buf);
}

int32_t printf(char const* format, ...)
{
    /* tailcall */
    return printf();
}

ssize_t read(int32_t fd, void* buf, uint64_t nbytes)
{
    /* tailcall */
    return read(fd, buf, nbytes);
}

char* fgets(char* buf, int32_t n, FILE* fp)
{
    /* tailcall */
    return fgets(buf, n, fp);
}

int32_t getchar()
{
    /* tailcall */
    return getchar();
}

int64_t malloc(uint64_t bytes)
{
    /* tailcall */
    return malloc(bytes);
}

int32_t atoi(char const* nptr)
{
    /* tailcall */
    return atoi(nptr);
}

int32_t __isoc99_scanf(char const* format, ...)
{
    /* tailcall */
    return __isoc99_scanf();
}

int64_t _start(int64_t arg1, int64_t arg2, void (* arg3)()) __noreturn
{
    int64_t rax;
    int64_t var_8 = rax;
    __libc_start_main(main, __return_addr, &arg_8, __libc_csu_init, __libc_csu_fini, arg3, &var_8);
    /* no return */
}

void deregister_tm_clones()
{
    return;
}

void register_tm_clones()
{
    return;
}

void __do_global_dtors_aux()
{
    if (completed.8061 != 0)
    {
        return;
    }
    if (__cxa_finalize != 0)
    {
        __cxa_finalize(__dso_handle);
    }
    deregister_tm_clones();
    completed.8061 = 1;
}

int64_t frame_dummy()
{
    /* tailcall */
    return register_tm_clones();
}

int32_t main(int32_t argc, char** argv, char** envp) __noreturn
{
    int32_t var_c = argc;
    char** var_18 = argv;
    disable_buffering();
    while (true)
    {
        menu();
        int32_t rax_3 = get_option();
        if (rax_3 == 4)
        {
            view_note();
        }
        else
        {
            if (rax_3 <= 4)
            {
                if (rax_3 == 3)
                {
                    puts("option 3");
                    continue;
                }
                else if (rax_3 <= 3)
                {
                    if (rax_3 == 1)
                    {
                        add_note();
                        continue;
                    }
                    else if (rax_3 == 2)
                    {
                        remove_note();
                        continue;
                    }
                }
            }
            puts("Invalid option");
        }
    }
}

int64_t disable_buffering()
{
    setbuf(stdin, nullptr);
    setbuf(stdout, nullptr);
    return setbuf(stderr, nullptr);
}

int64_t menu()
{
    puts("1- Add note");
    puts("2- Remove note");
    puts("3- Edit note");
    return puts("4- View note");
}

uint64_t get_option()
{
    void* fsbase;
    int64_t rax = *(int64_t*)((char*)fsbase + 0x28);
    int32_t var_14 = 0;
    printf("Enter an option: ");
    int32_t var_18;
    __isoc99_scanf(&data_2067, &var_18);
    int32_t var_14_1 = getchar();
    uint64_t rax_4 = ((uint64_t)var_18);
    if ((rax ^ *(int64_t*)((char*)fsbase + 0x28)) == 0)
    {
        return rax_4;
    }
    __stack_chk_fail();
    /* no return */
}

int64_t add_note()
{
    void* fsbase;
    int64_t rax = *(int64_t*)((char*)fsbase + 0x28);
    int32_t var_34 = 0;
    int32_t var_30 = 0;
    int64_t var_28 = 0;
    int32_t var_2c = 0;
    if (created_entries > 2)
    {
        puts("Maximum notes reached");
    }
    else
    {
        printf("Size: ");
        void var_1a;
        fgets(&var_1a, 8, stdin);
        int32_t rax_3 = atoi(&var_1a);
        if ((rax_3 == 0 || (rax_3 != 0 && rax_3 > 0x200)))
        {
            puts("Invalid size");
        }
        if ((rax_3 != 0 && rax_3 <= 0x200))
        {
            void* rax_5 = malloc(((uint64_t)rax_3));
            if (rax_5 != 0)
            {
                printf("Note content: ");
                *(int8_t*)((char*)rax_5 + (((int64_t)read(0, rax_5, ((uint64_t)rax_3))) + 1)) = 0;
                *(int64_t*)((((int64_t)created_entries) << 3) + &entries) = rax_5;
                created_entries = (created_entries + 1);
                puts("Note added");
            }
            else
            {
                printf("Error occured while allocating m…");
            }
        }
    }
    int64_t rax_22 = (rax ^ *(int64_t*)((char*)fsbase + 0x28));
    if (rax_22 == 0)
    {
        return rax_22;
    }
    __stack_chk_fail();
    /* no return */
}

int64_t remove_note()
{
    void* fsbase;
    int64_t rax = *(int64_t*)((char*)fsbase + 0x28);
    int32_t var_14 = 0;
    printf("Note index: ");
    __isoc99_scanf(&data_2067, &var_14);
    free(*(int64_t*)((((uint64_t)var_14) << 3) + &entries));
    created_entries = (created_entries - 1);
    puts("Note removed");
    int64_t rax_9 = (rax ^ *(int64_t*)((char*)fsbase + 0x28));
    if (rax_9 == 0)
    {
        return rax_9;
    }
    __stack_chk_fail();
    /* no return */
}

int64_t view_note()
{
    void* fsbase;
    int64_t rax = *(int64_t*)((char*)fsbase + 0x28);
    int32_t var_14 = 0;
    printf("Index: ");
    __isoc99_scanf(&data_2067, &var_14);
    if (var_14 > 3)
    {
        puts("Invalid index");
    }
    else if (*(int64_t*)((((int64_t)var_14) << 3) + &entries) != 0)
    {
        int64_t rdx_2 = (((int64_t)var_14) << 3);
        printf("This note is located at: %p", *(int64_t*)(rdx_2 + &entries), rdx_2);
        puts(*(int64_t*)((((int64_t)var_14) << 3) + &entries));
    }
    else
    {
        puts("This note has been deleted alrea…");
    }
    int64_t rax_15 = (rax ^ *(int64_t*)((char*)fsbase + 0x28));
    if (rax_15 == 0)
    {
        return rax_15;
    }
    __stack_chk_fail();
    /* no return */
}

void __libc_csu_init()
{
    _init();
    int64_t rbx_1 = 0;
    do
    {
        int64_t rdx;
        int64_t rsi;
        int32_t rdi;
        &__init_array_start[rbx_1](((uint64_t)rdi), rsi, rdx);
        rbx_1 = (rbx_1 + 1);
    } while (1 != rbx_1);
}

void __libc_csu_fini()
{
    return;
}

int64_t _fini()
{
    return;
}
