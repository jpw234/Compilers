use io
use conv

// this function is to test the concatination from arrays with different declaration method
// this code would return the maximam value from two arrays

//Find the maxima from an array, return the index and maxima value 
getMaxIndex(a:int[]) : int,int {
	max:int = 0 
	ind:int = 0
	n:int = length(a)
	i:int = 0 
	while (i < n){
		if (a[i] > max){
			max = a[i]
			ind = i
		}
		i = i + 1
	}
	return max, ind
}

// Define two arrays, one with literal declaration, the other with static size
main(args:int[][]) {
	array1 : int [] = { 4,2,9,0,-2,3,-5,-9,10,2,4,5,6,7,3}
	array2 : int [10]
	i: int = 0
	l: int = length(array1) + length(array2) 
	// assign the values into the array
	while (i < 10){
		array2[i] = -2 * 4 + i * 2
		i= i+1}
	//get the index and maxima
	m:int , ind:int = getMaxIndex(array1 + array2)   
	println ("The index of maxima is at index " + unparseInt(ind) +" the value = "+ unparseInt(m))

}