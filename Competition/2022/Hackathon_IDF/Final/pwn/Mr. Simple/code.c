int __cdecl main(int argc, const char **argv, const char **envp)
{
  size_t v3; // rax
  size_t v4; // rax
  char buf[48]; // [rsp+10h] [rbp-80h] BYREF
  char s[48]; // [rsp+40h] [rbp-50h] BYREF
  char v8[32]; // [rsp+70h] [rbp-20h] BYREF

  setup(argc, argv, envp);
  strcpy(s, "Ini program yang sangat sederhana!\n");
  strcpy(buf, "Apakah kalian bisa mengeksploitasinya?\n");
  v3 = strlen(s);
  write(1, s, v3);
  v4 = strlen(buf);
  write(1, buf, v4);
  read(0, v8, 2000uLL);
  return 0;
}
