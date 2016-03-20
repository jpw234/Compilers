use io

main(args:int[][]) {
    x :int = 6;
    y :int = 6;
    z :int = 7;

    b1 :bool = (y < z);
    b2 :bool = (x != y);
   	if (b1 == true){
   		print("b1 == true")
   	}
    a : int = f1();
    
    f2();
}

f1(): int {
  {
    return "hello"[0];
  }
}

f2(){
	{
	print("hoya")
	}
}