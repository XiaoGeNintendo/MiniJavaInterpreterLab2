package cn.edu.nju.cs

class BreakException:RuntimeException() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}