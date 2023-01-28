flag=$(xxd -p flag.txt | tr -d "\n" | fold -w2 | tr '[:lower:]' '[:upper:]')
bin=$(echo "obase=2; ibase=16; $flag" | bc | numfmt --format=%08f )
bin=$(echo $bin | tr -d " " | fold -w1)
j=0
for i in $bin;
do
    r=$(( $i % 2 ))
    if [ $r -ne 0 ]
    then
        cp hitam.jpg $j.jpg 
    else
        cp putih.jpg $j.jpg
    fi
    echo $j
    j=$((j+1))
done

rm flag.txt