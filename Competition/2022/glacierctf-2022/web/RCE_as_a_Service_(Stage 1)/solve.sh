curl --request POST \
--url https://rce-as-a-service-1.ctf.glacierctf.com/rce \
--header 'Content-Type: application/json' \
--data '{
"Data": ["Alpha", "Beta", "Gamer"],
"Query": "(data) => data.Where(d => d.StartsWith(\"G\"))"
}'