package cn.edu.nju.cs

class MiniJavaObject(val type: String, var value: Any?, val isIntLiteral:Boolean = false){



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
}
