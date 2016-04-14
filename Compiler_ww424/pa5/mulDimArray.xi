use io
use conv

Init(a:int[][][], b:int[][][]) : int[][][] {
	d:int[a[-1]][a[0][-1]][a[0][0][-1]]
	i:int = 0
	j:int = 0
	k:int = 0
	while(i < a[-1]){
		j = 0
		while(j < a[0][-1]){
			k = 0
			while(k < a[0][0][-1]){
				d[i][j][k] = a[i][j][k] * b[i][j][k]
				k = k + 1
			}
			j = j + 1
		}
		i = i + 1
	}
	return d
}

main(args:int[][]) {
	m:int = 1
	n:int = 2
	a:int[n+m-m][n][m+n]
	b:int[][][] = {{ {9, 8, 7}, {6, 5, 4} }, { {1, 2, 3}, {10, 11, 12} }}
	i:int = 0
	j:int = 0
	k:int = 0
	while(i < 2*m){
		j = 0
		while(j < n){
			k = 0
			while(k < m+n){
				a[i][j][k] = b[i][j][k] * 2
				k = k + 1
			}
			j = j + 1
		}
		i = i + 1
	}
	c:int[][][] = Init(a, b)
	//print the 3D matrix
	i = 0
	print("{")
	while(i < 2){
		j = 0
		print("{")
		while(j < 2){
			k = 0
			print("{")
			while(k < 3){
				if(k != 2)
					print(unparseInt(c[i][j][k]) + ",")
				else
					print(unparseInt(c[i][j][k]))
				k = k + 1
			}
			if(j != 1)
				print("},")
			else
				print("}")
			j = j + 1
		}
		if(i != 1)
			print("},")
		else
			print("}")
		i = i + 1
	}
	print("}")
}