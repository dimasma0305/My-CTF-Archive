int __cdecl main(int argc, const char **argv, const char **envp)
{
  int i; // [rsp+8h] [rbp-F8h]
  char s[32]; // [rsp+10h] [rbp-F0h] BYREF
  __int64 v6; // [rsp+30h] [rbp-D0h]
  __int64 v7; // [rsp+38h] [rbp-C8h]
  __int64 v8; // [rsp+40h] [rbp-C0h]
  __int64 v9; // [rsp+48h] [rbp-B8h]
  __int64 v10; // [rsp+50h] [rbp-B0h]
  __int64 v11; // [rsp+58h] [rbp-A8h]
  __int64 v12; // [rsp+60h] [rbp-A0h]
  __int64 v13; // [rsp+68h] [rbp-98h]
  int v14; // [rsp+70h] [rbp-90h]
  char v15[104]; // [rsp+80h] [rbp-80h] BYREF
  unsigned __int64 v16; // [rsp+E8h] [rbp-18h]

  v16 = __readfsqword(0x28u);
  qmemcpy(s, "irnh|xqc`z{gel]xibCO{iESQF{`jawQ", sizeof(s));
  v6 = 0x435141476F731F1CLL;
  v7 = 20043LL;
  v8 = 0LL;
  v9 = 0LL;
  v10 = 0LL;
  v11 = 0LL;
  v12 = 0LL;
  v13 = 0LL;
  v14 = 0;
  printf("[>] Flag: ");
  __isoc99_scanf("%s", v15);
  for ( i = 0; i < strlen(s); ++i )
  {
    if ( (v15[i] ^ (i + 10)) != s[i] )
    {
      puts("[!] Wrong!");
      exit(0);
    }
  }
  printf("[CORRECT] Flag: %s\n", v15);
  return 0;
}