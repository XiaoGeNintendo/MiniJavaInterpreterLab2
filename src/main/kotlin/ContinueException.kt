package cn.edu.nju.cs

class ContinueException : RuntimeException() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}