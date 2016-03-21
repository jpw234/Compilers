use io
use conv


main(args:int[][]) : int {
    input: int[][][] // 3-d array 
    input= {{{1,2} , {2,3}},
            {{3,4} , {5,6}},
            {{7,8} , {9,10}}}

    return input[1][0][0]

}
