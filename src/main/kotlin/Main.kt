package cn.edu.nju.cs

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import java.io.File

object Main {
    fun run(mjFile: File):String {
        val input = CharStreams.fromFileName(mjFile.absolutePath)
        val lexer: MiniJavaLexer = MiniJavaLexer(input)
        val tokenStream = CommonTokenStream(lexer)
        val parser: MiniJavaParser = MiniJavaParser(tokenStream)
        val pt: ParseTree = parser.compilationUnit()

        val x=MyVisitor()
        x.registerBuiltinFunctions()

        try {
            x.visit(pt)
        }catch(e: AssertException){
            x.pew(33)
            e.printStackTrace()
        }catch(e: TypeErrorException){
            x.pew(34)
            e.printStackTrace()
        }catch(e: IndexOutOfBoundsException){
            x.pew(34)
            e.printStackTrace()
        }catch(e: NullPointerException){
            x.pew(34)
            e.printStackTrace()
        }catch(e: IllegalStateException){
            x.pew(34)
            e.printStackTrace()
        }

        return x.outputBuffer
    }


    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 1) {
            System.err.println("Error: Only one argument is allowed: the path of MiniJava file.")
            throw RuntimeException()
        }

        val mjFile = File(args[0])
        run(mjFile)
    }
}