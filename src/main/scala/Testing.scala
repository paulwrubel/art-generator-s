object Testing {

    def main(args: Array[String]): Unit = {

        val listTest = List("one", "two", "three")
        println(listTest)

        val listTest2 = List("one", "two", "three")
        val whatevenisthis = List(listTest, "one", 1, ("kill", 1, 'o', 'f', 'me))

        println(listTest2)
        println(listTest == listTest2)
        println(whatevenisthis)

        val whatevenisthisagain = whatevenisthis.head.asInstanceOf[List[String]].reverse ::
                whatevenisthis.tail.init ::
                whatevenisthis.last.asInstanceOf[Tuple5[String, Int, Char, Char, Symbol]].toString() :: Nil

        println(whatevenisthisagain)


  }

}
