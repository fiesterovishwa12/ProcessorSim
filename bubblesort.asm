//Start of loop
jmp STARTLOOP
START:
// Zero swap count and pos
sub 5 5 5
sub 4 4 4
STARTLOOP:
// Load values
Ld 0 4 0
Ld 1 4 1
// If reached end of list (marked by 0) break
beq 1 10 ENDLOOP
// Compare a and b
cmp 2 0 1
addim 3 10 1
// if a <= b SKIP
bneq 2 3 SKIP
// Swap and increment swap count by 1
write 0 4 1
write 1 4 0
addim 5 10 1
SKIP:
addim 4 4 1
jmp STARTLOOP
ENDLOOP:
bneq 5 10 START
HALT
