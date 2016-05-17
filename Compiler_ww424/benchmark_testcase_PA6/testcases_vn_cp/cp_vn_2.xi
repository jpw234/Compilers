use io
use conv

main(args:int[][]) {
	a: int = 2
	a = 2 
	b:int = a + 2 
	c:int = a + 2 
	d:int = a + 2
	e:int = b + c + d  
	if (e > 10){
		a = 0
		b = a + 3 
		c = a + 3
	}
	println("c = " + unparseInt(c));
}