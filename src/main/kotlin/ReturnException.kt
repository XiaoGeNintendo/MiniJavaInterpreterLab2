package cn.edu.nju.cs

data class ReturnException(val value: MiniJavaObject?) : RuntimeException() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}