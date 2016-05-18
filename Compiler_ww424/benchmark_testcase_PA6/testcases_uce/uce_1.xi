use io
use conv

main(args:int[][]) {
	a:int = runwhile(20)
	println("a = " + unparseInt(a) + " Stop");
}


runwhile(a:int):int {
	while (a > 0){
		a = a -2 
		if (a == 4){
			return a ;}
	} 
	b : int = 10 
	a = a + b
	return a;
}