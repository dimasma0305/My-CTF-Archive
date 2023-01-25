#include <fcntl.h>
#include <stdio.h>

int main(int argc, char const *argv[])
{
    int *file = open("/etc/passwd", O_RDONLY);
    printf(file);
    return 0;
}
