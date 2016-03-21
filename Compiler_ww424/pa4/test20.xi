use io

main():int {
	a:int = 1
	b:int = 1
	x:int[a+b][a+b]
	x[0][0] = 10
	x[1][0] = 11
	x[0][1] = 12
	x[1][1] = 13

	return x[a][b]
}