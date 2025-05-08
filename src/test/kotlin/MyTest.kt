import cn.edu.nju.cs.Main
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import kotlin.test.assertEquals

class MyTest {

    fun String.pew(x: Int): String {
        return this+"Process exits with the code $x."
    }

    fun assertTrimmedEquals(expected: String, actual: String) {
        assertEquals(expected.trim().replace("\r\n","\n"), actual.trim().replace("\r\n","\n"))
    }

    fun generalLegacy(fn: String){
        assertTrimmedEquals(File("tests/legacy/$fn.out").readText(),Main.run(File("tests/legacy/$fn.mj")))
    }

    fun general(fn: String){
        assertTrimmedEquals(File("tests/$fn.out").readText(),Main.run(File("tests/$fn.mj")))
    }

    @ParameterizedTest
    @ValueSource(strings = ["arraycast","arraynull","breakcontinue","builtin","callnull","charcast","elseif","funcarray",
        "loopreturn","matrixqpow","multibreak","multinew","overload","zerolength",
        "chararray","paramnew","reference"])
    fun danny(fn: String){
        generalLegacy("danny/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings = ["Brainfuck-Interpreter","Hanoi-Tower","Sudoku-Solver"])
    fun more(fn: String){
        generalLegacy("more/$fn")
    }


    @ParameterizedTest
    @ValueSource(strings = ["a1","a2","a3","a4","a5","a6","a7","a8"])
    fun lab1(fn: String){
        generalLegacy("lab1/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings = ["b1","b2","b3","b4","b5","b6","b7"])
    fun lab1Extra(fn: String){
        generalLegacy("lab1/$fn")
    }


    @ParameterizedTest
    @ValueSource(strings = ["1","2","3","4","5","6","7","8","9","10","11","12","13"])
    fun fuckTypes(fn: String){
        generalLegacy("fuck_types/$fn")
    }


    @ParameterizedTest
    @ValueSource(strings = ["1","2","3","4"])
    fun confuse(fn: String){
        generalLegacy("confuse/$fn")
    }


    @ParameterizedTest
    @ValueSource(strings = ["1","2","3","4"])
    fun self(fn: String){
        generalLegacy("self/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings = ["array","var","length","print","assert","atoi","to_char_array","tostring"])
    fun note(fn: String){
        generalLegacy("note/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings = ["hello_world","arth"])
    fun basic(fn: String){
        generalLegacy("basic/$fn")
    }


    @ParameterizedTest
    @ValueSource(strings = ["Public-1","Public-2","Public-3","Public-4","Public-5","Public-6","Public-7","Public-8","Public-9","Public-10"])
    fun public(fn: String){
        generalLegacy("public/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings = ["Public-1","Public-2","Public-3","Public-4","Public-5","Public-6","Public-7","Public-8","Public-9","Public-10"])
    fun public_lab3(fn: String){
        general("public/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings=["HashMap","LinkedList","Stack"])
    fun more_lab3(fn: String){
        general("more/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings=["cache"])
    fun basic_lab3(fn: String){
        general("basic/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings=["1","2","3","4","5"])
    fun note_lab3(fn: String){
        general("note/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings=["1","2","3","4","5","6"])
    fun instanceof_lab3(fn: String){
        general("instanceof/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings=["functional","skill","super","tostr","trie","super2"])
    fun self_lab3(fn: String){
        general("self/$fn")
    }


    @ParameterizedTest
    @ValueSource(strings=["1","2","3"])
    fun typecast_lab3(fn: String){
        general("typecast/$fn")
    }



    @ParameterizedTest
    @ValueSource(strings=["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19"])
    fun constructor_lab3(fn: String){
        general("constructor/$fn")
    }

    @ParameterizedTest
    @ValueSource(strings=["1","2","3"])
    fun eq_lab3(fn: String){
        general("eq/$fn")
    }
}