use conv
use io

addName (name : int[] , i : int ): int[] {
	
	if (i == 0){
		return "Ms. " + name
	}else{
		return "Mr. " + name
	}


}

main(args:int[][]) {

    gender : int[] = { 1,0,0,1 }
    name : int[][] = { "James" , "Mary", "Lauren","Michael" }
    n:int = length(gender)

    while (n > 0 ){
    	n = n-1
    	println(addName( name[n], gender[n]) )
    }
}
