use io
use conv

sum(a:int, b:int):int {
    return a+b
}

main(args:int[][]) {
	x:int = 3
	y:int = 5
    print("Sum = " + unparseInt(sum(x, y)))
}