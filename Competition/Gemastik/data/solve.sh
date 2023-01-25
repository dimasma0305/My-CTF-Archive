for i in {01..20}; do
    smbclient -L "\\\\192.168.57.2$i\\" -U "%" -N
done;