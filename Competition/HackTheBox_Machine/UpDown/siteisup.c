int __cdecl main(int argc, const char **argv, const char **envp)
{
  __gid_t rgid; // [rsp+28h] [rbp-8h]
  __uid_t ruid; // [rsp+2Ch] [rbp-4h]

  rgid = getegid();
  ruid = geteuid();
  setresgid(rgid, rgid, rgid);
  setresuid(ruid, ruid, ruid);
  puts("Welcome to 'siteisup.htb' application\n");
  system("/usr/bin/python /home/developer/dev/siteisup_test.py");
  return 0;
}
