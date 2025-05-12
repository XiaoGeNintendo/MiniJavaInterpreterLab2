package cn.edu.nju.cs

class MiniJavaObject(
    val type: String,
    var value: Any?,
    var realType: String = type,
    val isIntLiteral: Boolean = false
) {

    /**
     * Maybe frequently used if the object is an Object
     */
    fun valueAsMap() = value as HashMap<String, MiniJavaObject>

    fun copy(
        type: String = this.type,
        value: Any? = this.value,
        realType: String = this.realType,
        isIntLiteral: Boolean = false
    ): MiniJavaObject {
        return MiniJavaObject(type, value, realType, isIntLiteral)
    }

    override fun toString(): String {
        return if (type == "char") {
            value!!.c().toString()
        } else {
            value.toString()
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

    /**
     * Returns this object but with real type and type set to the parent class
     */
    @Deprecated("Not work as intended. Use toSuper instead")
    fun deSuper(classes: HashMap<String, MiniJavaClass>): MiniJavaObject {
        val p = classes[this.realType]!!.parent
        val thatSuper = this.copy(type = p, realType = p)
        return thatSuper
    }

    /**
     * Returns this object but with decl_type set to the parent class of current decl_type
     */
    fun toSuper(classes: HashMap<String, MiniJavaClass>): MiniJavaObject {
        val p = classes[this.type]!!.parent
        val thatSuper = this.copy(type = p)
        return thatSuper
    }
}
