use conv
use io

fabnocci(i : int ): int {
    if (i <= 1){
    	return 1
    }else{
    	return fabnocci(i-1) + fabnocci(i-2)
    }
}

main(args:int[][]) {

    size : int = 20
    ret : int[size] 
    count :int  = 0 
    while (count < size+1 ){
    	ret[count] = fabnocci(count)
    	println(unparseInt(ret[count]))
    	count = count+1 
    }

}
