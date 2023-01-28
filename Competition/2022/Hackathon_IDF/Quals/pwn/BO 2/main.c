int __cdecl main(int argc, const char **argv, const char **envp)
{
  char v4[128]; // [rsp+0h] [rbp-80h] BYREF

  _init__(argc, argv, envp);
  printf("[>] Nama: ");
  gets(v4);
  printf("[*] Welcome, %s!\n", v4);
  return 0;
}