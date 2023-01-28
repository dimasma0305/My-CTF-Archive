lines = """Thank you, 
asdasdasdasdasdad
asdasdadasdsadasdasds
👶  Brute-force w/ hydra
. We've analyzed your answers and assigned you the appropriate role!""".split("\n")[1:]
print(lines)
for line in lines:
    try:
        line_reaction, role_name = line.strip().split(" ", 1)
        print(line_reaction, role_name)
    except ValueError:
        continue