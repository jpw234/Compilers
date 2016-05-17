use io
use conv

main(args:int[][]) {
    a:int = 1
    b:int = 2
    c:int = a + 1
    d:int = a + b + 1
    f:int
    if(b > 5){
    	f = a + b + 1
    }
    else{
    	f = a + 1
    }
    print(unparseInt(f))
}