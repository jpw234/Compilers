use io
use conv

main(args:int[][]) {
	pre1 :int = 1 
	pre2 :int = pre1+1
	i : int = 3
	ret : int = 0
	while (i<10){
		ret = pre1+pre2
		pre2 = ret
		pre1 = pre2
		i = i+1
	}
	println("steps = " + unparseInt(ret));
}