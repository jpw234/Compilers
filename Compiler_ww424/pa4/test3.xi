use io

main(args:int[][]):bool{
	a: int= {1,2,3}[0];
	s: int[] = "Hello";

	x: int = s[1]+a;

	b: bool, i:int = f(x,s[4]);
	return b;
	
}

f(p1:int, q1:int) : bool, int {
  b: bool = false;
  if(p1 > q1) b = true;
  
  return b, p1+q1
}

