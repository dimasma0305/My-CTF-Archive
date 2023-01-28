def get_nth_fibonacci_number(n):
    current_numb = 1
    previous_numb = 1
    sequence = []
    for i in range(n+1):
        current_numb = current_numb + previous_numb
        previous_numb = current_numb - previous_numb
        sequence.append(current_numb)
    return sequence

print(get_nth_fibonacci_number(10))