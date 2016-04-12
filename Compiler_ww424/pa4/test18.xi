use io

main():int {
	x:int[] = {1,2,7,4}
	x[2] = 3
	b:int[] = x
	return b[2]
}