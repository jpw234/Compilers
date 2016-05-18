use io
use conv

main(args:int[][]) {
	a:int = foo(20,30)
	println("a = " + unparseInt(a) );
}


foo(x:int,y:int):int {
	z : int = x * y 
	b : int = x*2 - y
	c : int = x + 2 * y
	return x + x + y 

}