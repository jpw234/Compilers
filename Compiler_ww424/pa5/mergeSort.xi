use io
use conv

merge(A:int[], start:int, mid:int, end:int){
	m:int = mid - start + 1;
	n:int = end - mid;
	p:int[m]
	q:int[n]
	i:int = 0
	j:int = 0
	//init
	while(i < m){
		p[i] = A[start+i];
		i = i + 1;
	}
	while(j < n){
		q[j] = A[mid+1+j];
		j = j + 1;
	}
	//merge
	i = 0;
	j = 0;
	k:int = start;
	while(i<m & j<n){
		if(p[i] <= q[j]){A[k] = p[i]; i = i + 1;}
		else {A[k] = q[j]; j = j + 1;}
		k = k + 1;
	}
	while(i<m) {A[k] = p[i]; i = i + 1; k = k + 1;}
	while(j<n) {A[k] = q[j]; j = j + 1; k = k + 1;}

}

mergeSort(A:int[], start:int, end:int){
	if(start < end){
		mid : int = (start + end) / 2;
		mergeSort(A, start, mid);
		mergeSort(A, mid+1, end);
		merge(A, start, mid, end);
	}
}

main(args:int[][]) {
	A:int[] = {60, 45, 37, 8, 5, 6, 2, 1, 10, 200, 1, 7, 6, 20}
	mergeSort(A, 0, A[-1]-1);
	i:int = 0;
	print("{");
	while(i < A[-1]){
		if(i != A[-1]-1)
			print(unparseInt(A[i]) + ",")
		else
			print(unparseInt(A[i]))
	}
	print("}");
}