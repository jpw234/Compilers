use io

foo():int {
	a: int ;
	a = - 123;
	b:int = 5;

	c: int = a + b ; 
	return c
}

main(args:int[][]){
	a: int=1 ; b: int =31;
	c: int = 2
	d: int = 5
	e: int = 6
	f: int = 8
	g: int = 9 
	h: int = 10
	x:int = a + -b * c *>> d / e % f + g - h;

	if (x > 10){
		print("heyhey")
	}
}