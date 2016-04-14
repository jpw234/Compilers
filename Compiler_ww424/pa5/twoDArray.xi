use io
use conv

findOne(a:int[][]) : int {
	M: int = length(a)
	N: int = length(a[0])
	x: int = 0 
	y: int = x
	s: int = 0 
	while (x < M){
		while(y < N){
			if (a[x][y] == 1) s = s+1 
			y = y+1
		}
		x = x+1
		y = 0
	}
	return s
}

isEven(i: int): bool{
	
	return (i%2 == 0)
}

main(args:int[][]) {
	array : int [][]= {{1,1,0}, {0,1,0}, {1,0,0},{0,0,1},{1,1,0} }
	n : int = findOne(array)
	if (isEven(n)){
		println("There are " + unparseInt(n) + " ones")
	} else{
		println ("Ha"+" "+ unparseInt(n))
	}

}