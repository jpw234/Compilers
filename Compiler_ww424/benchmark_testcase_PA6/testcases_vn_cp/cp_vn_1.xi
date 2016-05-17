use io
use conv

main(args:int[][]) {
	a:int = 2
	b:int = a + 2 
	c:int = a + 2 
	d:int = c + 3
	e:int = b + 3 
	while(a < 20){
		a = a + 2
		b = a + 2 
		e = b + c + d
	}
	println("e = " + unparseInt(e));
}