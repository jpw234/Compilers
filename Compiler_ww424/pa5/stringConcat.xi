use conv
use io
//testing the string concatination 

// add prefix before a name with given gender (0-> female, 1-> male)
addName (name : int[] , i : int ): int[] {	
	if (i == 0){
		return "Ms. " + name
	}else{
		return "Mr. " + name
	}
}

// Running addName and print out the result from addName
main(args:int[][]) {

    gender : int[] = { 1,0,0,1 }
    name : int[][] = { "James" , "Mary", "Lauren","Michael" }
    n:int = length(gender)

    while (n > 0 ){
    	n = n-1
    	println(addName( name[n], gender[n]) )
    }
}
