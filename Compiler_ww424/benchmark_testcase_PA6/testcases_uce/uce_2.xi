use io
use conv

main(args:int[][]) {
	a:int = foo(20,40)
	println("a = " + unparseInt(a) );
}


foo(x:int,y:int):int {
	a : int = x + y 
	b : int = a 
	if (a > 50){
		return a; 
	}
	else{
		b = b + 2 ;
		return b ; 
	}
}