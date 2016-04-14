use io
use conv

main(args:int[][]) {
	i:int = 1024
	a:int = i * i * i * i * i * i;//2^60
	j:int = 0
	while(a > 1){
		a = a / 2;
		j = j + 1;
	}
	println("j = " + unparseInt(j));
}