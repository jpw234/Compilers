use io
use conv

isOdd(a:int) : bool {
	return (a%2 == 1)
}

arith(a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int, k:int): int {
	m:int
	if(isOdd(b))
		m = a + b + c + d + e + f + g + h + k
	else
		m = a + b - c + d - e + f - g + h - k

	return m + 5;
}

main(args:int[][]) {
    a:int = 2
    b:int = 3
    c:int = 4
    d:int = (a+b)*c //20
    e:int = b*c //12
    f:int = (a-b)*c //-4
    g:int = a/(c-b) //2
    h:int = a + b + c + d + e //41
    k:int = -((f + 20)*1)*2 //32

    i:int = 0
    accum:int = 0
    while(i < 10){
    	accum = accum + arith(a+i, b+i, c+i, d+i, e+i, f+i, g+i, h+i, k+i);
    	i = i + 1
    }
    println("The Accumulated number is " + unparseInt(accum));
}