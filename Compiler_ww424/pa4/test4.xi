use io

main(args:int[][]): bool[]{
	a5: int[][] = {{1,2,3,4,5},{1,2,3,4,0},{1,1,1,1,1}, {2,2,2,2,2}};
	a1: int = 1;
	a2: int = 0; 
	a6: int[][][][] = {{{{1,2,3,4},{1,2,3,4}},{{1,2,3,4},{1,2,3,4}}},
					   {{{1,2,3,4},{1,2,3,4}},{{1,2,3,4},{1,2,3,4}}}};
	
	x :int= a6[a1][a2][a5[2][3]][a1]
	_, y:int = f(a1,a5[1][3]);

	b: bool[2] ;
	
	b[0] = (x<y)
	b[1] = (x > 10)
	return b

	
}
f(p1:int, q1:int) : bool, int {
  b: bool = false;
  if(p1 > q1) b = true;
  
  return b, p1+q1;
}
