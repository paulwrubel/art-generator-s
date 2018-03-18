
val map = Map(  "this" -> "that",
                "alsothis" -> "that",
                "notthis" -> "notthat")


for (elem <- map.filter(_._2 == "that").unzip._1) {

    print(elem + ", ")

}