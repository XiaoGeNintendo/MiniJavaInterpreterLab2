package cn.edu.nju.cs

import cn.edu.nju.cs.MiniJavaParser.ExpressionContext
import cn.edu.nju.cs.MiniJavaParser.StatementContext
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor

class MyVisitor : AbstractParseTreeVisitor<Any>(), MiniJavaParserVisitor<Any> {

    val unitObject = MiniJavaObject("?",Unit)
    val nullObject = MiniJavaObject("Any",null)

    private fun debug(x: Any) {
        System.err.println(x)
    }

    fun pew(x: Int){
        outputLn("Process exits with the code $x.")
    }

    var outputBuffer = ""
    private fun output(x: Any?) {
        print(x)
        outputBuffer+=x
    }
    private fun outputLn(x: Any?){
        println(x)
        outputBuffer+="$x\n"
    }

    private val methods = HashMap<String, ArrayList<MiniJavaMethod>>()


    fun registerBuiltinFunctions(){
        registerMethod(FunctionFactory.create2("#+","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i()+b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#+","string","char","string",{a,b->MiniJavaObject("string",a.value!!.c().toString()+b.value.toString())}))
        registerMethod(FunctionFactory.create2("#+","string","string","char",{a,b->MiniJavaObject("string",a.value.toString()+b.value!!.c().toString())}))
        registerMethod(FunctionFactory.create2("#+","string","*","string",{a,b->MiniJavaObject("string",a.value.toString()+b.value.toString())}))
        registerMethod(FunctionFactory.create2("#+","string","string","*",{a,b->MiniJavaObject("string",a.value.toString()+b.value.toString())}))
        registerMethod(FunctionFactory.create2("#-","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i()-b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#*","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i()*b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#/","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i()/b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#%","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i()%b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#<<","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i() shl b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#>>","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i() shr b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#>>>","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i() ushr b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#&","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i() and b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#|","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i() or b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#^","int","int","int",{a,b->MiniJavaObject("int",a.value!!.i() xor b.value!!.i())}))

        registerMethod(FunctionFactory.create2("#<","boolean","int","int",{a,b->MiniJavaObject("boolean",a.value!!.i()<b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#<=","boolean","int","int",{a,b->MiniJavaObject("boolean",a.value!!.i()<=b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#>","boolean","int","int",{a,b->MiniJavaObject("boolean",a.value!!.i()>b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#>=","boolean","int","int",{a,b->MiniJavaObject("boolean",a.value!!.i()>=b.value!!.i())}))

        registerMethod(FunctionFactory.create2("#==","boolean","int","int",{a,b->MiniJavaObject("boolean",a.value==b.value)}))
        registerMethod(FunctionFactory.create2("#==","boolean","string","string",{a,b->MiniJavaObject("boolean",a.value==b.value)}))
        registerMethod(FunctionFactory.create2("#==","boolean","boolean","boolean",{a,b->MiniJavaObject("boolean",a.value==b.value)}))
        registerMethod(FunctionFactory.create2("#==","boolean","*","*",{a,b -> if(!TypeChecker.typeEquatable(a.type,b.type)) throw TypeErrorException("Bad operand type for ${a.type}==${b.type}");MiniJavaObject("boolean", a.value===b.value) }))

        registerMethod(FunctionFactory.create2("#!=","boolean","int","int",{a,b->MiniJavaObject("boolean",a.value!!.i()!=b.value!!.i())}))
        registerMethod(FunctionFactory.create2("#!=","boolean","string","string",{a,b->MiniJavaObject("boolean",a.value!=b.value)}))
        registerMethod(FunctionFactory.create2("#!=","boolean","boolean","boolean",{a,b->MiniJavaObject("boolean",a.value!=b.value)}))
        registerMethod(FunctionFactory.create2("#!=","boolean","*","*",{a,b -> if(!TypeChecker.typeEquatable(a.type,b.type)) throw TypeErrorException("Bad operand type for ${a.type}!=${b.type}");MiniJavaObject("boolean", a.value!==b.value) }))

//        registerMethod(FunctionFactory.create2("#and","boolean","boolean","boolean",{a,b->MiniJavaObject("boolean",a.value as Boolean && b.value as Boolean)}))
//        registerMethod(FunctionFactory.create2("#or","boolean","boolean","boolean",{a,b->MiniJavaObject("boolean",a.value as Boolean || b.value as Boolean)}))

        registerMethod(FunctionFactory.create2("#=","int","int","int",{a,b->a.value=b.value;a.copy()}))
        registerMethod(FunctionFactory.create2("#=","char","char","char",{a,b->a.value=b.value;a.copy()}))
        registerMethod(FunctionFactory.create2("#=","string","string","string",{a,b->a.value=b.value;a.copy()}))
        registerMethod(FunctionFactory.create2("#=","boolean","boolean","boolean",{a,b->a.value=b.value;a.copy()}))
        registerMethod(FunctionFactory.create2("#=","Any","*","*",{a,b-> TypeChecker.checkAndThrow(a.type,b.type);a.value=b.value;a.copy()}))

        registerMethod(FunctionFactory.create2("#+=","int","int","int",{a,b->a.value=a.value!!.i()+b.value!!.i();a.copy()}))
        registerMethod(FunctionFactory.create2("#+=","string","string","char",{a,b->a.value=a.value.toString()+b.value!!.i().toByte().toInt().toChar();a.copy()}))
        registerMethod(FunctionFactory.create2("#+=","string","string","*",{a,b->a.value=a.value.toString()+b.value.toString();a.copy()}))

        registerMethod(FunctionFactory.create2("#-=","int","int","int",{a,b->a.value=a.value!!.i()-b.value!!.i();a.copy()}))
        registerMethod(FunctionFactory.create2("#*=","int","int","int",{a,b->a.value=a.value!!.i()*b.value!!.i();a.copy()}))
        registerMethod(FunctionFactory.create2("#/=","int","int","int",{a,b->a.value=a.value!!.i()/b.value!!.i();a.copy()}))
        registerMethod(FunctionFactory.create2("#%=","int","int","int",{a,b->a.value=a.value!!.i()%b.value!!.i();a.copy()}))
        registerMethod(FunctionFactory.create2("#<<=","int","int","int",{a,b->a.value=a.value!!.i() shl b.value!!.i();a.copy()}))
        registerMethod(FunctionFactory.create2("#>>=","int","int","int",{a,b->a.value=a.value!!.i() shr b.value!!.i();a.copy()}))
        registerMethod(FunctionFactory.create2("#>>>=","int","int","int",{a,b->a.value=a.value!!.i() ushr b.value!!.i();a.copy()}))
        registerMethod(FunctionFactory.create2("#&=","int","int","int",{a,b->a.value=a.value!!.i() and b.value!!.i();a.copy()}))
        registerMethod(FunctionFactory.create2("#|=","int","int","int",{a,b->a.value=a.value!!.i() or b.value!!.i();a.copy()}))
        registerMethod(FunctionFactory.create2("#^=","int","int","int",{a,b->a.value=a.value!!.i() xor b.value!!.i();a.copy()}))

        registerMethod(FunctionFactory.create1("assert","void","boolean",{a-> if(!(a.value as Boolean)) throw AssertException("Assertion failed"); unitObject}))
        registerMethod(FunctionFactory.create1("print","void","int",{a-> output(a.value!!);unitObject}))
        registerMethod(FunctionFactory.create1("print","void","char",{a-> output(a.value!!.c());unitObject}))
        registerMethod(FunctionFactory.create1("print","void","string",{a-> output(a.value!!);unitObject}))
        registerMethod(FunctionFactory.create1("print","void","boolean",{a-> output(a.value!!);unitObject}))
        registerMethod(FunctionFactory.create1("print","void","*",{a-> output(a.value);unitObject}))

        registerMethod(FunctionFactory.create1("println","void","int",{a-> outputLn(a.value!!);unitObject}))
        registerMethod(FunctionFactory.create1("println","void","char",{a-> outputLn(a.value!!.c());unitObject}))
        registerMethod(FunctionFactory.create1("println","void","string",{a-> outputLn(a.value!!);unitObject}))
        registerMethod(FunctionFactory.create1("println","void","boolean",{a-> outputLn(a.value!!);unitObject}))
        registerMethod(FunctionFactory.create1("println","void","*",{a-> outputLn(a.value);unitObject}))
        registerMethod(MiniJavaMethod.NativeMethod("println","void", arrayListOf(),{_->outputLn("");unitObject}))

        registerMethod(FunctionFactory.create1("atoi","int","string",{a->MiniJavaObject("int",a.value!!.toString().toInt())}))
        registerMethod(FunctionFactory.create1("itoa","string","int",{a->MiniJavaObject("string",a.value!!.toString())}))
        registerMethod(FunctionFactory.create1("to_string","string","char[]",{a->MiniJavaObject("string",(a.value as ArrayList<MiniJavaObject>).joinToString(separator = "") { it.value!!.c().toString() })}))
        registerMethod(FunctionFactory.create1("length","int","string",{a -> MiniJavaObject("int",(a.value as String).length)}))
        registerMethod(FunctionFactory.create1("length","int","*",{a -> TypeChecker.checkArrayAndThrow(a.type);MiniJavaObject("int",(a.value as ArrayList<*>).size)}))
        registerMethod(FunctionFactory.create1("to_char_array","char[]","string",{a->MiniJavaObject("char[]",a.value!!.toString().toCharArray().map { MiniJavaObject("char",it.code) })}))

    }

    private val findMethodCacheTable=HashMap<String,MiniJavaMethod>()

    private fun findMethod(name: String, param: List<MiniJavaObject>): MiniJavaMethod{

        val sig=name+param.joinToString { it.type }
        if(findMethodCacheTable.containsKey(sig)){
            return findMethodCacheTable[sig]!!
        }

        var minConversionCost=Int.MAX_VALUE
        var candidate:MiniJavaMethod?=null

        if(name !in methods){
            throw TypeErrorException("No such method: $name")
        }

        for(method in methods[name]!!){
            if(param.size!=method.parameters.size){
                continue
            }

            var ok=true
            var conversionCost=0
            for((index, i) in param.withIndex()){
                if(!TypeChecker.check(method.parameters[index].type,i.type)){
                    ok=false
                    break
                }
                if(method.parameters[index].type!=i.type){
                    conversionCost++
                }
            }

            if(!ok){
                continue
            }

            if(conversionCost<minConversionCost){
                minConversionCost=conversionCost
                candidate=method
            }
        }

        if(candidate==null){
            throw TypeErrorException("No such method: $name with parameters ${param.joinToString { it.type }}")
        }

        findMethodCacheTable[sig]=candidate
        return candidate
    }

    private fun callMethodInline(name: String, vararg param: MiniJavaObject):MiniJavaObject{
        val method=findMethod(name,param.asList())
        if(method is MiniJavaMethod.NativeMethod){
            return method.func(param.toCollection(ArrayList()))
        }else{
            TODO("Not supported")
        }
    }

    private fun registerMethod(method: MiniJavaMethod) {
        if (methods.containsKey(method.name)) {
            methods[method.name]!!.add(method)
        } else {
            methods[method.name] = arrayListOf(method)
        }
    }

    override fun visitCompilationUnit(ctx: MiniJavaParser.CompilationUnitContext) {
        for (x in ctx.methodDeclaration()) {
            visit(x)
        }

        pew(callMethod("main", arrayListOf()).value!!.i())
    }

    override fun visitMethodDeclaration(ctx: MiniJavaParser.MethodDeclarationContext) {
        val type = if (ctx.typeType() != null) visitTypeType(ctx.typeType()) else "void"
        val param = visitFormalParameters(ctx.formalParameters())
        val body = ctx.block()
        registerMethod(MiniJavaMethod.Method(ctx.identifier().text, type, param, body))
    }

    override fun visitVariableDeclarator(ctx: MiniJavaParser.VariableDeclaratorContext): MiniJavaObject {
        error("Should not reach here")
    }

    @Deprecated("Not type-safe")
    override fun visitVariableInitializer(ctx: MiniJavaParser.VariableInitializerContext?): Any {
        error("Deprecated")
    }

    fun visitVariableInitializer(ctx: MiniJavaParser.VariableInitializerContext, expectedType: String): MiniJavaObject {
        return if (ctx.expression() != null) {
            val exp=visitExpression(ctx.expression())

            //special judge
            if(exp.isIntLiteral && expectedType=="char" && exp.type=="int" && exp.value!!.i()<=Byte.MAX_VALUE && exp.value!!.i()>=Byte.MIN_VALUE){
                return exp.copy(type=expectedType)
            }

            TypeChecker.checkAndThrow(expectedType,exp.type)
            exp.copy(type=expectedType)
        } else {
            TypeChecker.checkArrayAndThrow(expectedType)

            visitArrayInitializer(ctx.arrayInitializer(),expectedType.dropLast(2))
        }
    }

    @Deprecated("Not type-safe")
    override fun visitArrayInitializer(ctx: MiniJavaParser.ArrayInitializerContext?): Any {
        error("Deprecated")
    }

    fun visitArrayInitializer(ctx: MiniJavaParser.ArrayInitializerContext, baseType: String): MiniJavaObject {
        val list = ArrayList<MiniJavaObject>()
        for (x in ctx.variableInitializer()) {
            val z=visitVariableInitializer(x,baseType)
            TypeChecker.checkAndThrow(baseType,z.type)
            list.add(z.copy(type=baseType))
        }

        return MiniJavaObject("$baseType[]", list) //type is unknown at the moment
    }

    override fun visitFormalParameters(ctx: MiniJavaParser.FormalParametersContext): ArrayList<Parameter> {
        if (ctx.formalParameterList() == null) {
            return arrayListOf()
        }
        val list = ArrayList<Parameter>()
        ctx.formalParameterList().formalParameter().forEach {
            list.add(visitFormalParameter(it))
        }

        return list
    }

    override fun visitFormalParameterList(ctx: MiniJavaParser.FormalParameterListContext): MiniJavaObject {
        error("Should not reach here")
    }

    override fun visitFormalParameter(ctx: MiniJavaParser.FormalParameterContext): Parameter {
        val type = visitTypeType(ctx.typeType())
        val name = ctx.identifier().text
        return Parameter(name, type)
    }

    override fun visitLiteral(ctx: MiniJavaParser.LiteralContext): MiniJavaObject {
        if(ctx.STRING_LITERAL()!=null){
            return MiniJavaObject("string", ctx.STRING_LITERAL().text.substring(1,ctx.STRING_LITERAL().text.length-1))
        }else if(ctx.DECIMAL_LITERAL()!=null){
            val v=ctx.DECIMAL_LITERAL().text.toInt()
            return MiniJavaObject("int",v, isIntLiteral = true)
        }else if(ctx.BOOL_LITERAL()!=null){
            return MiniJavaObject("boolean",ctx.BOOL_LITERAL().text.toBoolean())
        }else if(ctx.CHAR_LITERAL()!=null) {
            return MiniJavaObject("char", ctx.CHAR_LITERAL().text[1].code)
        }else if(ctx.NULL_LITERAL()!=null){
            return nullObject
        }else{
            error("Should not reach here")
        }
    }

    override fun visitBlock(ctx: MiniJavaParser.BlockContext) {
        Mem.pushLayer()
        for (x in ctx.blockStatement()) {
            visitBlockStatement(x)
        }
        Mem.popLayer()
    }

    override fun visitBlockStatement(ctx: MiniJavaParser.BlockStatementContext) {
        if (ctx.localVariableDeclaration() != null) {
            visitLocalVariableDeclaration(ctx.localVariableDeclaration())
        } else if (ctx.statement() != null) {
            visitStatement(ctx.statement())
        }
    }

    override fun visitLocalVariableDeclaration(ctx: MiniJavaParser.LocalVariableDeclarationContext) {
        if (ctx.variableDeclarator() != null) {
            //it's a normal init
            val type = visitTypeType(ctx.typeType())
            val name = ctx.variableDeclarator().identifier().text
            val value = if (ctx.variableDeclarator().variableInitializer() != null)
                visitVariableInitializer(ctx.variableDeclarator().variableInitializer(),type)
            else
                TypeChecker.default(type).copy(type=type)

            Mem.create(name,value)
        } else {
            //it's a var statement
            val exp = visitExpression(ctx.expression())
            if(exp==nullObject){
                throw TypeErrorException("No null in var plz.")
            }

            Mem.create(ctx.identifier().text,exp.copy())
        }
    }

    override fun visitIdentifier(ctx: MiniJavaParser.IdentifierContext): String {
        return ctx.text
    }

    private fun visitIf(ctx: MiniJavaParser.StatementContext) {
        val tf = visitParExpression(ctx.parExpression())
        if (tf) {
            visitStatement(ctx.statement(0))
        } else {
            if (ctx.statement(1) != null) {
                visitStatement(ctx.statement(1))
            }
        }
    }

    private fun visitWhile(ctx: StatementContext) {
        while (true) {
            val tf = visitParExpression(ctx.parExpression())
            if (!tf) {
                break
            }

            try {
                visitStatement(ctx.statement(0))
            } catch (_: ContinueException) {

            } catch (_: BreakException) {
                break
            }
        }
    }

    private fun visitFor(ctx: StatementContext) {
        Mem.pushLayer()

        visitForInit(ctx.forControl().forInit())
        while (true) {
            if(ctx.forControl().expression()!=null) {
                val cond = visitExpression(ctx.forControl().expression()).value as Boolean
                if (!cond) {
                    break
                }
            }
            try {
                visitStatement(ctx.statement(0))
            } catch (_: BreakException) {
                break
            } catch (_: ContinueException) {

            }

            if (ctx.forControl().expressionList() != null) {
                visitExpressionList(ctx.forControl().expressionList())
            }
        }
        Mem.popLayer()
    }

    override fun visitStatement(ctx: MiniJavaParser.StatementContext) {
        if (ctx.block() != null) {
            visitBlock(ctx.block())
        } else if (ctx.IF() != null) {
            visitIf(ctx)
        } else if (ctx.WHILE() != null) {
            visitWhile(ctx)
        } else if (ctx.FOR() != null) {
            visitFor(ctx)
        } else if (ctx.RETURN() != null) {
            if (ctx.expression() != null) {
                val exp = visitExpression(ctx.expression())
                throw ReturnException(exp)
            } else {
                throw ReturnException(null)
            }
        } else if (ctx.BREAK() != null) {
            throw BreakException()
        } else if (ctx.CONTINUE() != null) {
            throw ContinueException()
        } else if (ctx.expression() != null) {
            visitExpression(ctx.expression())
        } else {
            //Do nothing
        }
    }

    override fun visitParExpression(ctx: MiniJavaParser.ParExpressionContext): Boolean {
        return visitExpression(ctx.expression()).value as Boolean
    }

    override fun visitForControl(ctx: MiniJavaParser.ForControlContext): MiniJavaObject {
        error("Should not reach here")
    }

    override fun visitForInit(ctx: MiniJavaParser.ForInitContext) {
        if (ctx.expressionList() != null) {
            visitExpressionList(ctx.expressionList())
        } else {
            visitLocalVariableDeclaration(ctx.localVariableDeclaration())
        }
    }

    override fun visitExpressionList(ctx: MiniJavaParser.ExpressionListContext):ArrayList<MiniJavaObject> {
        val list = ArrayList<MiniJavaObject>()
        ctx.expression().forEach {
            list.add(visitExpression(it))
        }
        return list
    }

    private fun visitExpArray(ctx: MiniJavaParser.ExpressionContext): MiniJavaObject {
        val arr = visitExpression(ctx.expression(0))
        val index = visitExpression(ctx.expression(1))
        TypeChecker.checkArrayAndThrow(arr.type)
        TypeChecker.checkAndThrow("int", index.type)

        return (arr.value as ArrayList<*>)[index.value as Int] as MiniJavaObject
    }

    private fun visitExpPostfix(ctx: MiniJavaParser.ExpressionContext): MiniJavaObject {
        val obj = visitExpression(ctx.expression(0))
        TypeChecker.checkAndThrow("int", obj.type)

        val addon = if (ctx.postfix.text == "++") 1 else -1

        obj.value = (obj.value as Int) + addon
        return obj.copy(value = (obj.value as Int) - addon)
    }

    private fun visitExpPrefix(ctx: MiniJavaParser.ExpressionContext): MiniJavaObject {
        val obj = visitExpression(ctx.expression(0))
        if (ctx.prefix.text == "++") {
            TypeChecker.checkAndThrow("int", obj.type)
            obj.value = obj.value!!.i() + 1

            return obj.copy()
        } else if (ctx.prefix.text == "--") {
            TypeChecker.checkAndThrow("int", obj.type)
            obj.value = obj.value!!.i() - 1

            return obj.copy()
        } else if (ctx.prefix.text == "+") {
            TypeChecker.checkAndThrow("int", obj.type)
            return obj.copy()
        } else if (ctx.prefix.text == "-") {
            TypeChecker.checkAndThrow("int", obj.type)
            return obj.copy(value = -obj.value!!.i())
        } else if (ctx.prefix.text == "~") {
            TypeChecker.checkAndThrow("int", obj.type)
            return obj.copy(value = obj.value!!.i().inv())
        } else if (ctx.prefix.text == "not") {
            TypeChecker.checkAndThrow("boolean", obj.type)
            return obj.copy(value = !(obj.value as Boolean))
        } else {
            throw IllegalStateException("Unknown prefix operator ${ctx.prefix.text}")
        }
    }

    override fun visitExpression(ctx: MiniJavaParser.ExpressionContext): MiniJavaObject {
        if (ctx.primary() != null) {
            return visitPrimary(ctx.primary())
        } else if (ctx.LBRACK() != null) {
            return visitExpArray(ctx)
        } else if (ctx.methodCall() != null) {
            return visitMethodCall(ctx.methodCall())
        } else if (ctx.postfix != null) {
            return visitExpPostfix(ctx)
        } else if (ctx.prefix != null) {
            return visitExpPrefix(ctx)
        } else if (ctx.typeType() != null) {
            TypeChecker.isTypeCastableAndThrow(visitTypeType(ctx.typeType()), visitExpression(ctx.expression(0)).type)
            return visitExpression(ctx.expression(0)).copy(type = visitTypeType(ctx.typeType()))
        } else if (ctx.NEW() != null) {
            return visitCreator(ctx.creator())
        } else if(ctx.bop.text=="?") {
            val x = visitExpression(ctx.expression(0))
            TypeChecker.checkAndThrow("boolean", x.type)
            if (x.value as Boolean) {
                return visitExpression(ctx.expression(1))
            } else {
                return visitExpression(ctx.expression(2))
            }
        } else if(ctx.bop.text=="and") {
            val a = visitExpression(ctx.expression(0))
            if (!(a.value as Boolean)) {
                return a.copy()
            }
            return visitExpression(ctx.expression(1)).copy()
        } else if(ctx.bop.text=="or"){
            val a = visitExpression(ctx.expression(0))
            if (a.value as Boolean) {
                return a.copy()
            }
            return visitExpression(ctx.expression(1)).copy()
        } else {
            //binary operation
            return callMethodInline(
                "#${ctx.bop.text}",
                visitExpression(ctx.expression(0)),
                visitExpression(ctx.expression(1))
            )
        }
    }

    override fun visitPrimary(ctx: MiniJavaParser.PrimaryContext): MiniJavaObject {
        if(ctx.expression()!=null){
            return visitExpression(ctx.expression())
        }else if(ctx.literal()!=null){
            return visitLiteral(ctx.literal())
        }else if(ctx.identifier()!=null){
            return Mem[visitIdentifier(ctx.identifier())]
        }else{
            error("Should not reach here")
        }
    }

    private fun callMethod(name: String, arg: ArrayList<MiniJavaObject>): MiniJavaObject {
        return callMethod(findMethod(name, arg) as MiniJavaMethod.Method, arg)
    }

    private fun callMethod(method:MiniJavaMethod.Method, arg: ArrayList<MiniJavaObject>):MiniJavaObject{
        Mem.pushStack()
        Mem.pushLayer()
        for((index, i) in arg.withIndex()){
            Mem.create(method.parameters[index].name,i.copy(type = method.parameters[index].type))
        }

        try{
            visitBlock(method.body)
        }catch(e: ReturnException){
            if(e.value==null){
                if(method.returnType!="void"){
                    throw TypeErrorException("Expected return but found void")
                }
                Mem.popStack()
                return unitObject
            }

            TypeChecker.checkAndThrow(method.returnType, e.value.type)
            Mem.popStack()

            return e.value.copy(type=method.returnType)
        }
        //implicit return
        if(method.returnType!="void"){
            throw TypeErrorException("Expected return but found void")
        }

        Mem.popStack()
        return unitObject
    }

    override fun visitMethodCall(ctx: MiniJavaParser.MethodCallContext): MiniJavaObject {
        val name=visitIdentifier(ctx.identifier())
        val arg=visitArguments(ctx.arguments())
        val method=findMethod(name,arg)
        if(method is MiniJavaMethod.NativeMethod){
            return method.func(arg)
        }

        //it's a normal function
        return callMethod(method as MiniJavaMethod.Method ,arg)
    }

    override fun visitCreator(ctx: MiniJavaParser.CreatorContext): MiniJavaObject {
        val name=visitCreatedName(ctx.createdName())
        val dimension=ctx.arrayCreatorRest().LBRACK().size

        val type=name+"[]".repeat(dimension)

        if(ctx.arrayCreatorRest().arrayInitializer()!=null){
            return visitArrayInitializer(ctx.arrayCreatorRest().arrayInitializer(),type.dropLast(2))
        }

        //prepare expression count
        val dimensionArr = ctx.arrayCreatorRest().expression().reversed()
        return prepareArrayOf(type,dimensionArr)
    }

    /**
     * Waste of time err
     */
    private fun prepareArrayOf(type: String, dimensionArr: List<ExpressionContext>):MiniJavaObject{
        if(dimensionArr.isEmpty()){
            if(type.endsWith("[]")){
                return nullObject.copy(type=type)
            }
            return TypeChecker.default(type)
        }

        val countExp=visitExpression(dimensionArr.last())
        TypeChecker.checkAndThrow("int",countExp.type)
        val count=countExp.value!!.i()

        val list=ArrayList<MiniJavaObject>()
        for(i in 1..count){
            list.add(prepareArrayOf(type.dropLast(2),dimensionArr.dropLast(1)))
        }

        return MiniJavaObject(type,list)
    }

    override fun visitCreatedName(ctx: MiniJavaParser.CreatedNameContext): String {
        return ctx.text
    }

    override fun visitArrayCreatorRest(ctx: MiniJavaParser.ArrayCreatorRestContext): MiniJavaObject {
        error("Should not reach here")
    }

    override fun visitTypeType(ctx: MiniJavaParser.TypeTypeContext): String {
        var s = ctx.primitiveType().text
        ctx.LBRACK().forEach { _ ->
            s += "[]"
        }
        return s
    }

    override fun visitPrimitiveType(ctx: MiniJavaParser.PrimitiveTypeContext): String {
        return ctx.text
    }

    override fun visitArguments(ctx: MiniJavaParser.ArgumentsContext): ArrayList<MiniJavaObject> {
        if(ctx.expressionList()!=null) {
            return visitExpressionList(ctx.expressionList())
        }else{
            return arrayListOf()
        }
    }
}