package cn.edu.nju.cs

import cn.edu.nju.cs.MiniJavaParser.VariableInitializerContext

class MiniJavaClass(val name: String, val parent: String) {

    data class Field(val type: String, val inits: VariableInitializerContext?)
    val fields=HashMap<String, Field>()
    val methods=HashMap<String, MiniJavaMethod>()
    val constructors=HashMap<String,MiniJavaMethod>()

    var fieldLookupCache=HashMap<String,String>()
    var functionLookupCache=HashMap<String,String>()

    var cached=false
}