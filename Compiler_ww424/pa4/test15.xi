use io

main() : int{
	
	d : int  ;
	e : int ;
    f : int = -3;
	d =  5 ; 
	e = 2 ;

	b1 : bool = f > 0;
	b2 : bool = e > 0;
	if (b1 & b2) {
		return f * d;
	}else{
		return d+e; 
	}
	

}