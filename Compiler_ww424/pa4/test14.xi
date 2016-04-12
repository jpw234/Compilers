use io

main() : int[]{
	
	r: int[3] ;
	a : int = 1;
	b : int = 0 ;
	c : int = 2;
	x:int[2][2][3];
	x = {{{1,2,3},{4,5,6}},{{7,8,9},{10,12,13}}};
	d : int  ;
	e : int ;
    f : int = x[a][b][c] - 5;
	d = x[a][b][c] + 5 ; 
	e = x[a][b][c] * 2 ;
	

	r = {d,e,f};
	return r ;
	
}