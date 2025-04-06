package cn.edu.nju.cs

object FunctionFactory {
    fun create2(name: String, returnType: String, typeA: String, typeB: String, body: (MiniJavaObject,MiniJavaObject)->MiniJavaObject):MiniJavaMethod{
        return MiniJavaMethod.NativeMethod(name, returnType, arrayListOf(Parameter("a", typeA), Parameter("b", typeB))) { args ->
            body(args[0], args[1])
        }
    }

    fun create1(name: String, returnType: String, typeA: String, body: (MiniJavaObject)->MiniJavaObject):MiniJavaMethod{
        return MiniJavaMethod.NativeMethod(name, returnType, arrayListOf(Parameter("a", typeA))) { args ->
            body(args[0])
        }
    }
}