// Kernel 1 - hydro fragment
// DO 1 L = 1,Loop
// DO 1 k = 1,n
// 1       X(k)= Q + Y(k)*(R*ZX(k+10) + T*ZX(k+11))
// ref 1

// int l = 1
addim 0 0 1
// Reference 1
addim 1 1 1
START:

// int k = 0
sub 3 3 3
START2:
// q at mem 0
// r at mem 1
// t at mem 2
// n at mem 3
// z is array length 20 from mem 10 -> 29
// y is array length 20 from mem 30 -> 49
// x is array length 20 from mem 50 -> 69
// v is array length 20 from mem 70 -> 89
// x[k] = q + y[k]*( r*z[k+10] + t*z[k+11] )

// q
ld 4 99 0

//z[k+10]
ld 5 3 19
//r
ld 6 99 1
mul 5 5 6
//z[k+11]
ld 6 3 20
//t
ld 7 99 2
mul 6 6 7
add 5 5 6

//y[k]
ld 6 3 29
mul 5 5 6

add 4 4 5

write 4 3 50

// k = 6
addim 3 3 1
cmpi 2 3 6
bneq 2 1 START2

// Loop = 6
addim 0 0 1
cmpi 2 0 6
bneq 2 1 START

HALT
