  puts("Selamat datang di program MD5hash Generator");
  printf("Silahkan masukkan kata yang ingin digenerate : ");
  __isoc99_scanf("%s", v4);
  sprintf(s, "echo %s | md5sum", (const char *)v4);
  system(s);
  return 0;