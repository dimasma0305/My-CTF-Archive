int __cdecl main(int argc, const char **argv, const char **envp)
{
  char v4; // [rsp+17h] [rbp-FB9h] BYREF
  int v5; // [rsp+18h] [rbp-FB8h]
  int v6; // [rsp+1Ch] [rbp-FB4h]
  int v7[6]; // [rsp+20h] [rbp-FB0h] BYREF
  unsigned __int64 v8; // [rsp+FC8h] [rbp-8h]

  v8 = __readfsqword(0x28u);
  _init__(argc, argv, envp);
  v5 = 0;
  v6 = 0;
  do
    __isoc99_scanf("%d%c", &v7[v5++], &v4);
  while ( v4 != 10 );
  if ( v7[0] != 1337 )
    exit(0);
  if ( v7[2] != 1337 * v7[1] )
    exit(0);
  if ( v7[3] + v7[4] != 1337 )
    exit(0);
  if ( v7[5] - v7[3] != 10 )
    exit(0);
  if ( v7[5] != v7[4] + v7[0] )
    exit(0);
  printf("[+] Flag: ");
  system("cat flag.txt");
  return 0;
}