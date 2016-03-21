a(i:int, j:int): int, int {
  return i, (2 * j);
}
main(args: int[][]): int {
	x:int, y:int = a(2,3);
	return x + 5 * y;
}