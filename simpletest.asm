// Simple program to test the architecture
ld 0 10 0
// data + 12
addim 1 0 12
write 1 10 1
// Test arithmetic
// data + data + 12
add 1 0 1
write 1 10 2
// data - 15
addim 1 10 15
sub 1 0 1
write 1 10 3
//data * data
mul 2 0 0
write 2 10 4
// data * 3 / data
ld 1 10 10
addim 1 10 3
mul 2 0 1
div 2 2 0
write 2 10 5
// data & 110
ld 1 10 10
addim 1 10 6
& 2 0 1
write 2 10 6
// data &im 001
&im 2 0 1
write 2 10 7
// data | 110
or 2 0 1
write 2 10 8
// data |im 001
orim 2 0 1
write 2 10 9
// data xor 110
xor 2 0 1
write 2 10 10
// data >> 2
>> 2 0 2
write 2 10 11
// data << 2
<< 2 0 2
write 2 10 12
// Test cmp
// 1
cmp 2 1 0
write 2 10 13
// 0
cmp 2 0 0
write 2 10 14
// -1
cmp 2 0 1
write 2 10 15
// Test cmp i
// 1
cmpi 2 0 1
write 2 10 16
// 0
cmpi 2 0 2
write 2 10 17
// -1
cmpi 2 0 16
write 2 10 18
// Test bew result = data
ld 1 10 20
addim 1 20 4
beq 0 1 SKIP1
write 0 10 19
beq 0 0 SKIP1
write 1 10 19
SKIP1:
// Test benq result = data
bneq 1 1 SKIP2
write 0 10 20
bneq 0 1 SKIP2
write 1 10 20
SKIP2:
// Test jmp result = data
write 0 10 21
jmp END
write 1 10 21
END:
HALT
