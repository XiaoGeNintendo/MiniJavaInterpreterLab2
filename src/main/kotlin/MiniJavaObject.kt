package cn.edu.nju.cs

class MiniJavaObject(val type: String, var value: Any?, val isIntLiteral:Boolean = false){

    /**
     * Maybe frequently used if the object is an Object
     */
    fun valueAsMap() = value as HashMap<String,MiniJavaObject>

    fun getRealClass() = valueAsMap()["Object::#class"]!!.value.toString()

    fun copy(type: String = this.type, value: Any? = this.value, isIntLiteral: Boolean = false):MiniJavaObject{
        return MiniJavaObject(type,value,isIntLiteral)
    }

    override fun toString(): String {
        if(type=="char"){
            return value!!.c().toString()
        }else{
            return value.toString()
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MiniJavaObject

        if (isIntLiteral != other.isIntLiteral) return false
        if (type != other.type) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    fun deSuper(classes: HashMap<String,MiniJavaClass>):MiniJavaObject{
        val thatSuper=this.copy(type=classes[this.getRealClass()]!!.parent)
        thatSuper.valueAsMap()["Object::#class"]=MiniJavaObject("String",thatSuper.type)
        return thatSuper
    }
}
