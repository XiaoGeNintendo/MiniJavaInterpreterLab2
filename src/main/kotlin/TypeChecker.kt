package cn.edu.nju.cs

object TypeChecker {

    var classes = HashMap<String, MiniJavaClass>()

    fun isPrimitiveType(x: String): Boolean {
        return x in arrayOf("int", "char", "boolean", "string")
    }

    /**
     * @return `found instanceof required` as in Java
     */
    fun instanceof(required: String, found: String): Boolean {
        if (required == found) {
            return true
        }

        var now = found
        while (now != "Object" && now in classes) {
            if (now == required) {
                return true
            }
            now = classes[now]!!.parent
        }

        return now == required
    }

    fun check(required: String, found: String): Boolean {
        if (required == "*") {
            return true
        }
        if (found == "<null>") {
            return !isPrimitiveType(required)
        }
        if (required == "int" && found == "char") {
            return true
        }
        if (required == found) {
            return true
        }

        return instanceof(required, found)
    }

    fun checkAndThrow(required: String, found: String) {
        if (!check(required, found)) {
            throw TypeErrorException("Type mismatch: required $required, found $found")
        }
    }

    /**
     * Returns default value of a type
     */
    fun default(type: String): MiniJavaObject {
        return when (type) {
            "int" -> {
                MiniJavaObject("int", 0)
            }
            "char" -> {
                MiniJavaObject("char", 0)
            }
            "string" -> {
                MiniJavaObject("string", "")
            }
            "boolean" -> {
                MiniJavaObject("boolean", false)
            }
            else -> {
                MiniJavaObject(type, null, "<null>")
            }
        }
    }

    fun checkArrayAndThrow(type: String) {
        if (!type.endsWith("[]")) {
            throw TypeErrorException("Expected array but found $type")
        }
    }

    fun isTypeCastableAndThrow(to: String, from: String) {
        if (to == "int" && from == "char") {
            return
        }
        if (to == "char" && from == "int") {
            return
        }
        if (from == "<null>") {
            return
        }
        if (to == from) {
            return
        }
        if (instanceof(to, from)) {
            return
        }
        throw TypeErrorException("Cannot cast $from to $to")
    }

    /**
     * Determine if two types can be used in ==. Like a==b
     */
    fun typeEquatable(a: String, b: String): Boolean {
        return check(a, b) || check(b, a)
    }
}
