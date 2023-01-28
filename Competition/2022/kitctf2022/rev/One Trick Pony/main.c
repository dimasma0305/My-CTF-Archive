#include <stdlib.h>
#include <stdio.h>
#include <string.h>

int main() {
    char* secret_msg = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    size_t msg_len = strlen(secret_msg);


    unsigned char* random_bytes = malloc(msg_len);
    printf("[");
    for (size_t i = 0; i < msg_len; i++) {
        random_bytes[i] = rand();
        printf("%d, ", random_bytes[i]);
    }
    printf("]\n");

    unsigned char* enc = malloc(msg_len);
    for (size_t i = 0; i < msg_len; i++) {
        enc[i] = secret_msg[i] ^ random_bytes[i];
    }

    printf("This is my message to you: ");
    for (size_t i = 0; i < msg_len; i++) {
        printf("%02X", enc[i]);
    }
    printf("\n");
}