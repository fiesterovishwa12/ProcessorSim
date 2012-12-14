// a = 23, b = 0
addim 0 0 5
SKIP:
// b++
addim 1 1 1
cmp 2 0 1
addim 3 10 1
// if a <= b SKIP
beq 2 3 SKIP
write 1 10 0
