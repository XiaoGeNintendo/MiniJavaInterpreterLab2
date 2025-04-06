package cn.edu.nju.cs

import cn.edu.nju.cs.MiniJavaParser.BlockContext

sealed class MiniJavaMethod(val name: String, val returnType: String, val parameters: ArrayList<Parameter>){
    class NativeMethod( name: String,  returnType: String,  parameters: ArrayList<Parameter>, val func: (ArrayList<MiniJavaObject>) -> MiniJavaObject): MiniJavaMethod(name, returnType, parameters)
    class Method( name: String,  returnType: String,  parameters: ArrayList<Parameter>, val body: BlockContext): MiniJavaMethod(name, returnType, parameters)
}
