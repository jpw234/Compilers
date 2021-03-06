use io
use conv

// first to test functions with more than 7 arguments
// also testing the literal array declaration and concatination


//Merge is to take 7 seperate integer array and combine together to get a bigger array
merge(a:int[] , b:int[], c:int[],d:int[],e:int[],f:int[],g:int[]) : int[] {
	return a+b+c+d+e+f+g
}

//compute the mean of an array
mean(a:int[]): int{
	n :int = length(a)
	N :int = n 
	sum :int = 0
	while (n > 0){
		sum = sum + a[n-1]
		n = n - 1
	}
	return sum/N
}
// Define 7 integer arrays by literal declaration
// And merge them together for computation of mean 
// Print out the average at the last
main(args:int[][]) {
	a : int [] = {2,3,4,5,6} 
	b : int [] = {1,2,3,4}
	c : int [] = {11,12,13,14,15}
	d : int [] = {7,1,2,9,10,3}
	e : int [] = {3,2,5,6,4,9}
	f : int [] = {3,7,12,0,3,2,3}
	g : int [] = {2,3,4,5,6,7}

	mergeArray : int[] = merge(a,b,c,d,e,f,g)
	ave:int = mean(mergeArray)

	println("The average of the merged array is " + unparseInt(ave));
}