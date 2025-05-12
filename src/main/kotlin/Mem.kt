package cn.edu.nju.cs

/**
 * Memory manager
 */
object Mem {
    private val funStack = ArrayList<FunctionStack>()

    /**
     * Shorthand for getting a variable in the current stack frame
     */
    operator fun get(name: String): MiniJavaObject {
        return funStack.last()[name]
    }

    /**
     * Shorthand for setting a variable in the current stack frame
     */
    operator fun set(text: String, value: MiniJavaObject) {
        funStack.last()[text] = value
    }

    fun pushLayer() {
        funStack.last().pushLayer()
    }

    fun popLayer() {
        funStack.last().popLayer()
    }

    fun pushStack() {
        funStack.add(FunctionStack())
    }

    fun popStack() {
        funStack.removeLast()
    }

    /**
     * Shorthand for creating a variable in the current stack frame
     */
    fun create(name: String, obj: MiniJavaObject) {
        funStack.last().create(name, obj)
    }

    operator fun contains(name: String): Boolean {
        return name in funStack.last().vars
    }
}