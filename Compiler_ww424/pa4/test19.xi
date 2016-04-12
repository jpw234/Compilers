use io

arrayInit():int {
	a:int = 3
	b:int = 4
	x:int[a+b]
	i: int = 0 
	while ( i < a+b){
		x[i] = 10 + i;
		i = i+1;
	}
	return x[6]
}