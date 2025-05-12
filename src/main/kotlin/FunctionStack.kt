package cn.edu.nju.cs

/**
 * Represents a stack frame
 *
 * @see Mem
 */
class FunctionStack {
    val vars = HashMap<String, ArrayList<MiniJavaObject>>()

    /**
     * The variables registered in the current layer
     */
    val regedHere = ArrayList<ArrayList<String>>()

    fun pushLayer() {
        regedHere.add(ArrayList())
    }

    fun popLayer() {
        for (reg in regedHere.last()) {
            vars[reg]!!.removeLast()
        }

        regedHere.removeLast()
    }

    operator fun get(name: String): MiniJavaObject {
        return (vars[name] ?: error("No such variable: $name")).last()
    }

    fun create(name: String, value: MiniJavaObject) {
        regedHere.last().add(name)
        if (name !in vars) {
            vars[name] = arrayListOf(value)
        } else {
            vars[name]!!.add(value)
        }
    }

    operator fun set(name: String, value: MiniJavaObject) {
        if (name !in vars) {
            error("Variable $name not defined. Create first you fool!!")
        }

        TypeChecker.checkAndThrow(vars[name]!!.last().type, value.type)
        vars[name]!!.last().value = value.value
    }
}
