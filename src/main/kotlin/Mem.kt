package cn.edu.nju.cs

object Mem {
    val funStack = ArrayList<FunctionStack>()

    operator fun get(name: String): MiniJavaObject{
        return funStack.last()[name]
    }

    operator fun set(text: String, value: MiniJavaObject) {
        funStack.last()[text] = value
    }

    fun pushLayer() {
        funStack.last().pushLayer()
    }

    fun popLayer(){
        funStack.last().popLayer()
    }

    fun pushStack() {
        funStack.add(FunctionStack())
    }

    fun popStack() {
        funStack.removeLast()
    }

    fun create(name: String, obj: MiniJavaObject) {
        funStack.last().create(name,obj)
    }
}