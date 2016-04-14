use io
use conv

fabnocci(i : int ): int {
    if (i <= 1){
    	return 1
    }else{
    	return fabnocci(i-1) + fabnocci(i-2)
    }
}

main(args:int[][]) {
	A0:int = fabnocci(0)
	A1:int = fabnocci(1)
	A2:int = fabnocci(2)
	A3:int = fabnocci(3)
	A4:int = fabnocci(4)
	A5:int = fabnocci(5)
	A6:int = fabnocci(6)
	A7:int = fabnocci(7)
	A8:int = fabnocci(8)
	A9:int = fabnocci(9)
	A10:int = fabnocci(10)
	A11:int = fabnocci(11)
	A12:int = fabnocci(12)
	A13:int = fabnocci(13)
	A14:int = fabnocci(14)
	A15:int = fabnocci(15)
	A16:int = fabnocci(16)
	A17:int = fabnocci(17)
	A18:int = fabnocci(18)
	A19:int = fabnocci(19)
	a:int = A0 + A1
	b:int = A1 + A2
	c:int = A2 + A3
	d:int = A3 + A4
	e:int = A4 + A5
	f:int = A5 + A6
	g:int = A6 + A7
	h:int = A7 + A8
	i:int = A8 + A9
	j:int = A9 + A10
	k:int = A10 + A11
	l:int = A11 + A12
	m:int = A12 + A13
	n:int = A13 + A14
	o:int = A14 + A15
	p:int = A15 + A16
	q:int = A16 + A17
	r:int = A17 + A18
	s:int = A18 + A19
	println("Sum is " + unparseInt(a+b+c+d+e+f+g+h+i+j+k+l+m+n+o+p+q+r+s));
}