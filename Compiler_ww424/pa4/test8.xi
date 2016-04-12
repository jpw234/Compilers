use io

main(args:int[][]): bool[] {
    x :int = 6;
    y :int = 6;
    z :int = 7;

    b1 :bool = (y < z);
    b2 :bool = (x != y);

    return {b1,b2};
}

