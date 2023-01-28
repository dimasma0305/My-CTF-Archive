int __cdecl main(int argc, const char **argv, const char **envp)
{
  char v4[140]; // [rsp+0h] [rbp-90h] BYREF
  int v5; // [rsp+8Ch] [rbp-4h]

  _init__(argc, argv, envp);
  v5 = 31337;
  printf("[>] Nama: ");
  gets(v4);
  if ( v5 == '\xDE\xAD\xC0\xDE' )
  {
    putchar(10);
    puts("[*] Backdoor actived");
    puts("[*] Access granted...");
    system("/bin/sh");
  }
  else
  {
    printf("[*] Welcome %s!\n", v4);
  }
  return 0;
}