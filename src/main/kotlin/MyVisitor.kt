package cn.edu.nju.cs

import cn.edu.nju.cs.MiniJavaParser.*
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor

class MyVisitor : AbstractParseTreeVisitor<Any>(), MiniJavaParserVisitor<Any> {
    /**
     * Represents the return value of a void method
     */
    val unitObject = MiniJavaObject("?", Unit)

    val nullObject = MiniJavaObject("<null>", null)

    /**
     * Placeholder object for super
     */
    val superObject = MiniJavaObject("I PROMISE TO PAY THE BEARAR ON DEMAND THE POWER OF SUPER", null)


    fun pew(x: Int) {
        outputLn("Process exits with the code $x.")
    }

    /**
     * Used in unit tests to compare output with standard output
     */
    var outputBuffer = ""
    private fun output(x: Any?) {
        print(x)
        outputBuffer += x
    }

    private fun outputLn(x: Any?) {
        println(x)
        outputBuffer += "$x\n"
    }

    private val methods = HashMap<String, ArrayList<MiniJavaMethod>>()
    private val classes = HashMap<String, MiniJavaClass>()

    /**
     * The current class we are visiting.
     * Used for attaching methods and fields to class
     */
    private var currentClass: String? = null

    fun registerBuiltinFunctions() {
        //We can treat all binary operators as methods
        //Note that and/or and :? needs to be handled differently due to short-circuit evaluation

        registerMethod(
            FunctionFactory.create2(
                "#+",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() + b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#+",
                "string",
                "char",
                "string",
                { a, b -> MiniJavaObject("string", a.value!!.c().toString() + b.value.toString()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#+",
                "string",
                "string",
                "char",
                { a, b -> MiniJavaObject("string", a.value.toString() + b.value!!.c().toString()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#+",
                "string",
                "*",
                "string",
                { a, b -> MiniJavaObject("string", a.value.toString() + b.value.toString()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#+",
                "string",
                "string",
                "*",
                { a, b -> MiniJavaObject("string", a.value.toString() + b.value.toString()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#-",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() - b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#*",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() * b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#/",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() / b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#%",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() % b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#<<",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() shl b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#>>",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() shr b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#>>>",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() ushr b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#&",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() and b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#|",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() or b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#^",
                "int",
                "int",
                "int",
                { a, b -> MiniJavaObject("int", a.value!!.i() xor b.value!!.i()) })
        )

        registerMethod(
            FunctionFactory.create2(
                "#<",
                "boolean",
                "int",
                "int",
                { a, b -> MiniJavaObject("boolean", a.value!!.i() < b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#<=",
                "boolean",
                "int",
                "int",
                { a, b -> MiniJavaObject("boolean", a.value!!.i() <= b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#>",
                "boolean",
                "int",
                "int",
                { a, b -> MiniJavaObject("boolean", a.value!!.i() > b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#>=",
                "boolean",
                "int",
                "int",
                { a, b -> MiniJavaObject("boolean", a.value!!.i() >= b.value!!.i()) })
        )

        registerMethod(
            FunctionFactory.create2(
                "#==",
                "boolean",
                "int",
                "int",
                { a, b -> MiniJavaObject("boolean", a.value == b.value) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#==",
                "boolean",
                "string",
                "string",
                { a, b -> MiniJavaObject("boolean", a.value == b.value) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#==",
                "boolean",
                "boolean",
                "boolean",
                { a, b -> MiniJavaObject("boolean", a.value == b.value) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#==",
                "boolean",
                "*",
                "*",
                { a, b ->
                    if (!TypeChecker.typeEquatable(
                            a.type,
                            b.type
                        )
                    ) throw TypeErrorException("Bad operand type for ${a.type}==${b.type}");MiniJavaObject(
                    "boolean",
                    a.value === b.value
                )
                })
        )

        registerMethod(
            FunctionFactory.create2(
                "#!=",
                "boolean",
                "int",
                "int",
                { a, b -> MiniJavaObject("boolean", a.value!!.i() != b.value!!.i()) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#!=",
                "boolean",
                "string",
                "string",
                { a, b -> MiniJavaObject("boolean", a.value != b.value) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#!=",
                "boolean",
                "boolean",
                "boolean",
                { a, b -> MiniJavaObject("boolean", a.value != b.value) })
        )
        registerMethod(
            FunctionFactory.create2(
                "#!=",
                "boolean",
                "*",
                "*",
                { a, b ->
                    if (!TypeChecker.typeEquatable(
                            a.type,
                            b.type
                        )
                    ) throw TypeErrorException("Bad operand type for ${a.type}!=${b.type}");MiniJavaObject(
                    "boolean",
                    a.value !== b.value
                )
                })
        )

        registerMethod(FunctionFactory.create2("#=", "int", "int", "int", { a, b -> a.value = b.value;a.copy() }))
        registerMethod(FunctionFactory.create2("#=", "char", "char", "char", { a, b -> a.value = b.value;a.copy() }))
        registerMethod(
            FunctionFactory.create2(
                "#=",
                "string",
                "string",
                "string",
                { a, b -> a.value = b.value;a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#=",
                "boolean",
                "boolean",
                "boolean",
                { a, b -> a.value = b.value;a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#=",
                "Any",
                "*",
                "*",
                { a, b ->
                    TypeChecker.checkAndThrow(a.type, b.type);a.value = b.value;a.realType = b.realType;a.copy()
                })
        )

        registerMethod(
            FunctionFactory.create2(
                "#+=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() + b.value!!.i();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#+=",
                "string",
                "string",
                "char",
                { a, b -> a.value = a.value.toString() + b.value!!.i().toByte().toInt().toChar();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#+=",
                "string",
                "string",
                "*",
                { a, b -> a.value = a.value.toString() + b.value.toString();a.copy() })
        )

        registerMethod(
            FunctionFactory.create2(
                "#-=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() - b.value!!.i();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#*=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() * b.value!!.i();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#/=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() / b.value!!.i();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#%=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() % b.value!!.i();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#<<=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() shl b.value!!.i();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#>>=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() shr b.value!!.i();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#>>>=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() ushr b.value!!.i();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#&=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() and b.value!!.i();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#|=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() or b.value!!.i();a.copy() })
        )
        registerMethod(
            FunctionFactory.create2(
                "#^=",
                "int",
                "int",
                "int",
                { a, b -> a.value = a.value!!.i() xor b.value!!.i();a.copy() })
        )

        registerMethod(
            FunctionFactory.create1(
                "assert",
                "void",
                "boolean",
                { a -> if (!(a.value as Boolean)) throw AssertException("Assertion failed"); unitObject })
        )

        registerMethod(FunctionFactory.create1("print", "void", "int", { a -> output(a.value!!);unitObject }))
        registerMethod(FunctionFactory.create1("print", "void", "char", { a -> output(a.value!!.c());unitObject }))
        registerMethod(FunctionFactory.create1("print", "void", "string", { a -> output(a.value!!);unitObject }))
        registerMethod(FunctionFactory.create1("print", "void", "boolean", { a -> output(a.value!!);unitObject }))
        registerMethod(FunctionFactory.create1("print", "void", "Object", { a ->
            if (a.value == null) {
                output("null")
                return@create1 unitObject
            }
            val clz = classes[a.realType]!!
            val name = clz.functionLookupCache["to_string()"]!!
            val res = callMethod(name, arrayListOf(), a)
            output(res.value.toString())
            unitObject
        }))
        registerMethod(FunctionFactory.create1("print", "void", "*", { a -> output(a.value);unitObject }))

        registerMethod(FunctionFactory.create1("println", "void", "int", { a -> outputLn(a.value!!);unitObject }))
        registerMethod(FunctionFactory.create1("println", "void", "char", { a -> outputLn(a.value!!.c());unitObject }))
        registerMethod(FunctionFactory.create1("println", "void", "string", { a -> outputLn(a.value!!);unitObject }))
        registerMethod(FunctionFactory.create1("println", "void", "boolean", { a -> outputLn(a.value!!);unitObject }))
        registerMethod(FunctionFactory.create1("println", "void", "Object", { a ->
            if (a.value == null) {
                outputLn("null")
                return@create1 unitObject
            }
            val clz = classes[a.realType]!!
            val name = clz.functionLookupCache["to_string()"]!!
            val res = callMethod(name, arrayListOf(), a)
            outputLn(res.value.toString())
            unitObject
        }))
        registerMethod(FunctionFactory.create1("println", "void", "*", { a -> outputLn(a.value);unitObject }))
        registerMethod(FunctionFactory.create0("println", "void", { outputLn("");unitObject }))

        registerMethod(
            FunctionFactory.create1(
                "atoi",
                "int",
                "string",
                { a -> MiniJavaObject("int", a.value!!.toString().toInt()) })
        )
        registerMethod(
            FunctionFactory.create1(
                "itoa",
                "string",
                "int",
                { a -> MiniJavaObject("string", a.value!!.toString()) })
        )
        registerMethod(
            FunctionFactory.create1(
                "to_string",
                "string",
                "char[]",
                { a ->
                    MiniJavaObject(
                        "string",
                        (a.value as ArrayList<MiniJavaObject>).joinToString(separator = "") {
                            it.value!!.c().toString()
                        })
                })
        )
        registerMethod(
            FunctionFactory.create1(
                "length",
                "int",
                "string",
                { a -> MiniJavaObject("int", (a.value as String).length) })
        )
        registerMethod(
            FunctionFactory.create1(
                "length",
                "int",
                "*",
                { a -> TypeChecker.checkArrayAndThrow(a.type);MiniJavaObject("int", (a.value as ArrayList<*>).size) })
        )
        registerMethod(
            FunctionFactory.create1(
                "to_char_array",
                "char[]",
                "string",
                { a ->
                    MiniJavaObject(
                        "char[]",
                        a.value!!.toString().toCharArray().map { MiniJavaObject("char", it.code) })
                })
        )

    }

    private val findMethodCacheTable = HashMap<String, MiniJavaMethod>()

    private fun findMethod(name: String, param: List<MiniJavaObject>): MiniJavaMethod {

        val sig = name + param.joinToString { it.type }
        if (findMethodCacheTable.containsKey(sig)) {
            return findMethodCacheTable[sig]!!
        }

        var minConversionCost = Int.MAX_VALUE
        var candidate: MiniJavaMethod? = null

        if (name !in methods) {
            error("No such method: $name")
        }

        for (method in methods[name]!!) {
            if (param.size != method.parameters.size) {
                continue
            }

            var ok = true
            var conversionCost = 0
            for ((index, i) in param.withIndex()) {
                if (!TypeChecker.check(method.parameters[index].type, i.type)) {
                    ok = false
                    break
                }
                if (method.parameters[index].type != i.type) {
                    conversionCost++
                }
            }

            if (!ok) {
                continue
            }

            if (conversionCost < minConversionCost) {
                minConversionCost = conversionCost
                candidate = method
            }
        }

        if (candidate == null) {
            error("No such method: $name with parameters ${param.joinToString { it.type }}")
        }

        findMethodCacheTable[sig] = candidate
        return candidate
    }

    /**
     * Calls a native method with given parameters
     */
    @Deprecated("Can be completely replaced by callMethod")
    private fun callMethodInline(name: String, vararg param: MiniJavaObject): MiniJavaObject {
        val method = findMethod(name, param.asList()) as MiniJavaMethod.NativeMethod

        return method.func(param.toCollection(ArrayList()))
    }

    private fun registerMethod(method: MiniJavaMethod) {
        if (methods.containsKey(method.name)) {
            methods[method.name]!!.add(method)
        } else {
            methods[method.name] = arrayListOf(method)
        }
    }

    /**
     * Registers the base class of all: Object
     */
    private fun addObjectClass() {
        val obj = MiniJavaClass("Object", "OBJECT'S PARENT? YOU SHOULD NEVER REACH HERE!!!")
        obj.cached = true //avoid calling it in calculateClassCache

        //don't add to_string() here. It's wrong according to doc

        //create manual constructor
        val f = FunctionFactory.create0("Object::#new", "void") { unitObject }
        obj.constructors["#new()"] = f
        registerMethod(f)

        classes["Object"] = obj
    }

    override fun visitCompilationUnit(ctx: CompilationUnitContext) {
        addObjectClass()

        for (x in ctx.methodDeclaration()) {
            visitMethodDeclaration(x)
        }
        for (x in ctx.classDeclaration()) {
            visitClassDeclaration(x)
        }

        calculateClassCache()
        //Save reference to type checker
        TypeChecker.classes = classes

        //TODO delete me when uploading
//        for((name, clz) in classes){
//            println("Class $name:")
//            println("Fields:"+clz.fields)
//            println("Methods:"+clz.methods)
//            println("Constructors:"+clz.constructors)
//            println("FieldCache:"+clz.fieldLookupCache)
//            println("FunctionCache:"+clz.functionLookupCache)
//        }
//
//        for((name, f) in methods){
//            println("Methods with $name:")
//            println(f.joinToString { it.getSignature() })
//        }

        pew(callMethod("main", arrayListOf(), null).value!!.i())
    }

    private fun calculateClassCache() {
        for ((name, obj) in classes) {
            if (!obj.cached) {
                calculateClassCache(name)
            }
        }
    }

    private fun calculateClassCache(name: String) {
        val obj = classes[name]!!

        if (obj.cached) {
            return
        }

        val par = classes[obj.parent]!!

        calculateClassCache(obj.parent)

        //copy father's cache first
        obj.fieldLookupCache = par.fieldLookupCache.clone() as HashMap<String, String>
        obj.functionLookupCache = par.functionLookupCache.clone() as HashMap<String, String>

        for ((fieldName, field) in obj.fields) {
            obj.fieldLookupCache[fieldName] = "$name::$fieldName"
        }
        //actually name is sig
        for ((methodName, method) in obj.methods) {
            obj.functionLookupCache[methodName] = "$name::$methodName".replace("\\(.*?\\)".toRegex(), "")
        }

        obj.cached = true
    }

    override fun visitClassDeclaration(ctx: ClassDeclarationContext) {
        val name = visitIdentifier(ctx.identifier())
        val parent =
            if (ctx.parentClassDeclaration() != null) visitParentClassDeclaration(ctx.parentClassDeclaration()) else "Object"
        val classObj = MiniJavaClass(name, parent)

        if (name in classes) {
            error("Class $name already exists")
        }

        classes[name] = classObj
        currentClass = name
        visitClassBody(ctx.classBody())

        if (classObj.constructors.isEmpty()) {
            //create manual constructor
            val f = FunctionFactory.create0("$name::#new", "void") { unitObject }
            classObj.constructors["#new()"] = f
            registerMethod(f)
        }

        currentClass = null
    }

    override fun visitParentClassDeclaration(ctx: ParentClassDeclarationContext): String {
        return visitIdentifier(ctx.identifier())
    }

    override fun visitClassBody(ctx: ClassBodyContext) {
        for (i in ctx.classBodyDeclaration()) {
            visitClassBodyDeclaration(i)
        }
    }

    override fun visitClassBodyDeclaration(ctx: ClassBodyDeclarationContext) {
        if (ctx.methodDeclaration() != null) {
            visitMethodDeclaration(ctx.methodDeclaration())
        } else if (ctx.fieldDeclaration() != null) {
            visitFieldDeclaration(ctx.fieldDeclaration())
        } else if (ctx.constructorDeclaration() != null) {
            visitConstructorDeclaration(ctx.constructorDeclaration())
        }
    }

    override fun visitFieldDeclaration(ctx: FieldDeclarationContext) {
        val type = visitTypeType(ctx.typeType())
        val name = visitIdentifier(ctx.variableDeclarator().identifier())
        val inits = ctx.variableDeclarator().variableInitializer()

        val clz = classes[currentClass]!!
        clz.fields[name] = MiniJavaClass.Field(type, inits)
        clz.fieldOrder.add(name)
    }

    override fun visitConstructorDeclaration(ctx: ConstructorDeclarationContext) {
        val name = visitIdentifier(ctx.identifier())
        if (name != currentClass) {
            error("Constructor name should be same as class name: $name != expected $currentClass")
        }

        val param = visitFormalParameters(ctx.formalParameters())
        val body = ctx.block()

        val m = MiniJavaMethod.Method("$currentClass::#new", "void", param, body)
        registerMethod(m)
        classes[currentClass]!!.constructors[m.getSignature().removePrefix("$currentClass::")] = m
    }

    override fun visitMethodDeclaration(ctx: MethodDeclarationContext) {
        val type = if (ctx.typeType() != null) visitTypeType(ctx.typeType()) else "void"
        val param = visitFormalParameters(ctx.formalParameters())
        val body = ctx.block()

        if (currentClass == null) {
            registerMethod(MiniJavaMethod.Method(ctx.identifier().text, type, param, body))
        } else {
            val m = MiniJavaMethod.Method("$currentClass::${ctx.identifier().text}", type, param, body)
            registerMethod(m)
            classes[currentClass]!!.methods[m.getSignature().removePrefix("$currentClass::")] = m
        }
    }

    override fun visitVariableDeclarator(ctx: VariableDeclaratorContext): MiniJavaObject {
        error("Should not reach here")
    }

    @Deprecated("Not type-safe")
    override fun visitVariableInitializer(ctx: VariableInitializerContext?): Any {
        error("Deprecated")
    }

    fun visitVariableInitializer(ctx: VariableInitializerContext, expectedType: String): MiniJavaObject {
        return if (ctx.expression() != null) {
            val exp = visitExpression(ctx.expression())

            //special judge for int literal conversion
            if (exp.isIntLiteral && expectedType == "char" && exp.type == "int" && exp.value!!.i() <= Byte.MAX_VALUE && exp.value!!.i() >= Byte.MIN_VALUE) {
                return exp.copy(type = expectedType)
            }

            TypeChecker.checkAndThrow(expectedType, exp.type)
            exp.copy(type = expectedType)
        } else {
            TypeChecker.checkArrayAndThrow(expectedType)

            visitArrayInitializer(ctx.arrayInitializer(), expectedType.dropLast(2))
        }
    }

    @Deprecated("Not type-safe")
    override fun visitArrayInitializer(ctx: ArrayInitializerContext?): Any {
        error("Deprecated")
    }

    fun visitArrayInitializer(ctx: ArrayInitializerContext, baseType: String): MiniJavaObject {
        val list = ArrayList<MiniJavaObject>()
        for (x in ctx.variableInitializer()) {
            val z = visitVariableInitializer(x, baseType)
            TypeChecker.checkAndThrow(baseType, z.type)
            list.add(z.copy(type = baseType))
        }

        return MiniJavaObject("$baseType[]", list)
    }

    override fun visitFormalParameters(ctx: FormalParametersContext): ArrayList<Parameter> {
        if (ctx.formalParameterList() == null) {
            return arrayListOf()
        }
        val list = ArrayList<Parameter>()
        ctx.formalParameterList().formalParameter().forEach {
            list.add(visitFormalParameter(it))
        }

        return list
    }

    override fun visitFormalParameterList(ctx: FormalParameterListContext): MiniJavaObject {
        error("Should not reach here")
    }

    override fun visitFormalParameter(ctx: FormalParameterContext): Parameter {
        val type = visitTypeType(ctx.typeType())
        val name = ctx.identifier().text
        return Parameter(name, type)
    }

    override fun visitLiteral(ctx: LiteralContext): MiniJavaObject {
        if (ctx.STRING_LITERAL() != null) {
            return MiniJavaObject(
                "string",
                ctx.STRING_LITERAL().text.substring(1, ctx.STRING_LITERAL().text.length - 1)
            )
        } else if (ctx.DECIMAL_LITERAL() != null) {
            val v = ctx.DECIMAL_LITERAL().text.toInt()
            return MiniJavaObject("int", v, isIntLiteral = true)
        } else if (ctx.BOOL_LITERAL() != null) {
            return MiniJavaObject("boolean", ctx.BOOL_LITERAL().text.toBoolean())
        } else if (ctx.CHAR_LITERAL() != null) {
            return MiniJavaObject("char", ctx.CHAR_LITERAL().text[1].code)
        } else if (ctx.NULL_LITERAL() != null) {
            return nullObject
        } else {
            error("Should not reach here")
        }
    }

    /**
     * Visit a block in the rule of constructor
     *
     * If ctx is `null`, the rule of an empty constructor will be applied. (i.e. will call super() + init fields)
     */
    fun visitBlockAsConstructor(ctx: BlockContext?, that: MiniJavaObject) {
        Mem.pushLayer()
        Mem.create("this", that)
//        Mem.create("super",that.deSuper(classes))

        val clz = classes[that.type]!!

        val callSuper = {
            if (that.type != "Object") {
                callMethod("${clz.parent}::#new", arrayListOf(), that)
            }
        }

        val initFields = {
            for (fieldName in clz.fieldOrder) {
                val field = clz.fields[fieldName]!!
                val conv = clz.fieldLookupCache[fieldName]!!
                if (field.inits == null) {
                    //do nth
//                    that.valueAsMap()[conv]=TypeChecker.default(field.type)
                } else {
                    that.valueAsMap()[conv] = visitVariableInitializer(field.inits, field.type)
                }
            }
        }

        //no statement in constructor
        if (ctx == null || ctx.blockStatement().isEmpty()) {
            //call super()
            callSuper()
            //initialize fields
            initFields()

            Mem.popLayer()
            return
        }

        val first = ctx.blockStatement(0).statement()?.expression()?.methodCall()

        if (first?.SUPER() == null && first?.THIS() == null) {
            //user did not call super/this explicitly

            //call super()
            callSuper()
            //initialize fields
            initFields()

            //run all statements
            for (i in ctx.blockStatement()) {
                visitBlockStatement(i)
            }
        } else if (first.SUPER() != null) {
            //user called super
            visitBlockStatement(ctx.blockStatement(0))

            //init fields
            initFields()

            //run remaining statement
            for (i in ctx.blockStatement().drop(1)) {
                visitBlockStatement(i)
            }
        } else if (first.THIS() != null) {
            //user delegated to another constructor no need to do anything
            for (i in ctx.blockStatement()) {
                visitBlockStatement(i)
            }
        }

        Mem.popLayer()
    }

    override fun visitBlock(ctx: BlockContext) {
        Mem.pushLayer()
        for (x in ctx.blockStatement()) {
            visitBlockStatement(x)
        }
        Mem.popLayer()
    }

    override fun visitBlockStatement(ctx: BlockStatementContext) {
        if (ctx.localVariableDeclaration() != null) {
            visitLocalVariableDeclaration(ctx.localVariableDeclaration())
        } else if (ctx.statement() != null) {
            visitStatement(ctx.statement())
        }
    }

    override fun visitLocalVariableDeclaration(ctx: LocalVariableDeclarationContext) {
        if (ctx.variableDeclarator() != null) {
            //it's a normal init
            val type = visitTypeType(ctx.typeType())
            val name = ctx.variableDeclarator().identifier().text
            val value = if (ctx.variableDeclarator().variableInitializer() != null)
                visitVariableInitializer(ctx.variableDeclarator().variableInitializer(), type)
            else
                TypeChecker.default(type)

            Mem.create(name, value)
        } else {
            //it's a var statement
            val exp = visitExpression(ctx.expression())
            if (exp.type == "<null>") {
                throw TypeErrorException("No null in var plz.")
            }

            Mem.create(ctx.identifier().text, exp.copy())
        }
    }

    override fun visitIdentifier(ctx: IdentifierContext): String {
        return ctx.text
    }

    private fun visitIf(ctx: StatementContext) {
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

        if (ctx.forControl().forInit() != null) {
            visitForInit(ctx.forControl().forInit())
        }

        while (true) {
            if (ctx.forControl().expression() != null) {
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

    override fun visitStatement(ctx: StatementContext) {
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

    override fun visitParExpression(ctx: ParExpressionContext): Boolean {
        return visitExpression(ctx.expression()).value as Boolean
    }

    override fun visitForControl(ctx: ForControlContext): MiniJavaObject {
        error("Should not reach here")
    }

    override fun visitForInit(ctx: ForInitContext) {
        if (ctx.expressionList() != null) {
            visitExpressionList(ctx.expressionList())
        } else {
            visitLocalVariableDeclaration(ctx.localVariableDeclaration())
        }
    }

    override fun visitExpressionList(ctx: ExpressionListContext): ArrayList<MiniJavaObject> {
        val list = ArrayList<MiniJavaObject>()
        ctx.expression().forEach {
            list.add(visitExpression(it))
        }
        return list
    }

    private fun visitExpArray(ctx: ExpressionContext): MiniJavaObject {
        val arr = visitExpression(ctx.expression(0))
        val index = visitExpression(ctx.expression(1))
        TypeChecker.checkArrayAndThrow(arr.type)
        TypeChecker.checkAndThrow("int", index.type)

        return (arr.value as ArrayList<*>)[index.value as Int] as MiniJavaObject
    }

    private fun visitExpPostfix(ctx: ExpressionContext): MiniJavaObject {
        val obj = visitExpression(ctx.expression(0))
        TypeChecker.checkAndThrow("int", obj.type)

        val addon = if (ctx.postfix.text == "++") 1 else -1

        obj.value = (obj.value as Int) + addon
        return obj.copy(value = (obj.value as Int) - addon)
    }

    private fun visitExpPrefix(ctx: ExpressionContext): MiniJavaObject {
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
            error("Unknown prefix operator ${ctx.prefix.text}")
        }
    }

    private fun visitInstanceOf(ctx: ExpressionContext): MiniJavaObject {
        val obj = visitExpression(ctx.expression(0))
        val type = visitTypeType(ctx.typeType())

        if (TypeChecker.isPrimitiveType(obj.type) || TypeChecker.isPrimitiveType(type)) {
            throw TypeErrorException("`${obj.type} instanceof $type` is invalid: primitive types")
        }
        if (!TypeChecker.typeEquatable(obj.type, type)) {
            throw TypeErrorException("`${obj.type} instanceof $type` is invalid: type not equatable")
        }

        val ans = TypeChecker.instanceof(type, obj.realType)
        return MiniJavaObject("boolean", ans)
    }

    override fun visitExpression(ctx: ExpressionContext): MiniJavaObject {
        if (ctx.primary() != null) {
            return visitPrimary(ctx.primary())
        } else if (ctx.LBRACK() != null) {
            return visitExpArray(ctx)
        } else if (ctx.bop != null && ctx.bop.text == ".") {
            return visitDot(ctx)
        } else if (ctx.INSTANCEOF() != null) {
            return visitInstanceOf(ctx)
        } else if (ctx.methodCall() != null) {
            return visitMethodCall(ctx.methodCall(), if ("this" in Mem) Mem["this"] else null)
        } else if (ctx.postfix != null) {
            return visitExpPostfix(ctx)
        } else if (ctx.prefix != null) {
            return visitExpPrefix(ctx)
        } else if (ctx.typeType() != null) {
            //type cast

            if (!TypeChecker.typeEquatable(visitTypeType(ctx.typeType()), visitExpression(ctx.expression(0)).type)) {
                throw TypeErrorException("`${ctx.typeType().text} is not equatable to ${visitExpression(ctx.expression(0)).type}` in type convert")
            }
            TypeChecker.isTypeCastableAndThrow(
                visitTypeType(ctx.typeType()),
                visitExpression(ctx.expression(0)).realType
            )
            return visitExpression(ctx.expression(0)).copy(type = visitTypeType(ctx.typeType()))
        } else if (ctx.NEW() != null) {
            return visitCreator(ctx.creator())
        } else if (ctx.bop.text == "?") {
            val x = visitExpression(ctx.expression(0))
            TypeChecker.checkAndThrow("boolean", x.type)
            return if (x.value as Boolean) {
                visitExpression(ctx.expression(1))
            } else {
                visitExpression(ctx.expression(2))
            }
        } else if (ctx.bop.text == "and") {
            val a = visitExpression(ctx.expression(0))
            if (!(a.value as Boolean)) {
                return a.copy()
            }
            return visitExpression(ctx.expression(1)).copy()
        } else if (ctx.bop.text == "or") {
            val a = visitExpression(ctx.expression(0))
            if (a.value as Boolean) {
                return a.copy()
            }
            return visitExpression(ctx.expression(1)).copy()
        } else {
            //binary operation
            return callMethod(
                "#${ctx.bop.text}",
                arrayListOf(visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1))),
                null
            )
        }
    }

    private fun visitDot(ctx: ExpressionContext): MiniJavaObject {
        var obj = visitExpression(ctx.expression(0))
        val isSuper = obj == superObject
        if (isSuper) {
            //retrieve super object. We treat it as this but with type=decl(this).super
            obj = Mem["this"].toSuper(classes)
        }

        if (obj.value == null) {
            throw NullPointerException("$obj is null")
        }

        if (ctx.identifier() != null) {
            //it's a variable visit

            val clz = classes[obj.type] ?: error("No such type: ${obj.type}")

            val name = visitIdentifier(ctx.identifier())
            val convertedName = clz.fieldLookupCache[name] ?: error("No such field $name in declared class ${obj.type}")

            return obj.valueAsMap()[convertedName]!!
        } else {
            //it's a method call
            return visitMethodCall(ctx.methodCall(), obj, isSuper)
        }
    }

    override fun visitPrimary(ctx: PrimaryContext): MiniJavaObject {
        if (ctx.expression() != null) {
            return visitExpression(ctx.expression())
        } else if (ctx.literal() != null) {
            return visitLiteral(ctx.literal())
        } else if (ctx.identifier() != null) {
            //variable. First find in local then in `this`

            val name = visitIdentifier(ctx.identifier())
            if (name in Mem) {
                return Mem[name]
            } else {
                val that: MiniJavaObject = Mem["this"]
                val clz = classes[that.type]!!
                val transformedName = clz.fieldLookupCache[name]!!
                return that.valueAsMap()[transformedName]!!
            }
        } else if (ctx.THIS() != null) {
            return Mem["this"]
        } else if (ctx.SUPER() != null) {
            return superObject
        } else {
            error("Should not reach here")
        }
    }

    /**
     * The ultimate entrance of method call: `this.name(arg)`
     */
    private fun callMethod(name: String, arg: ArrayList<MiniJavaObject>, that: MiniJavaObject?): MiniJavaObject {
        val method = findMethod(name, arg)
        if (method is MiniJavaMethod.NativeMethod) {
            if (that != null) {
                arg.add(that) //push `this` in convenience
            }
            if (name.endsWith("::#new")) {
                //constructor: all native constructors are all empty constructors.
                Mem.pushStack()
                visitBlockAsConstructor(null, that!!.copy(type = name.getType()!!))
                Mem.popStack()
            }

            //FUCK!!!!! Special judge for print/println to match doc's stupid requirement
            if (name == "print" && arg.isNotEmpty() && TypeChecker.instanceof("Object", arg[0].type)) {
                val clz = classes[arg[0].type]!!
                if ("to_string()" !in clz.functionLookupCache) {
                    if (arg[0].value == null) {
                        output("null")
                    } else {
                        output(arg[0].realType)
                    }
                    return unitObject
                }
            }
            if (name == "println" && arg.isNotEmpty() && TypeChecker.instanceof("Object", arg[0].type)) {
                val clz = classes[arg[0].type]!!
                if ("to_string()" !in clz.functionLookupCache) {
                    if (arg[0].value == null) {
                        outputLn("null")
                    } else {
                        outputLn(arg[0].realType)
                    }
                    return unitObject
                }
            }
            return method.func(arg)
        } else {
            //normal method
            return callMethod(method as MiniJavaMethod.Method, arg, that?.copy(type = method.name.getType()!!))
        }
    }

    /**
     * Calls a normal method with `this` set to `that`
     */
    private fun callMethod(
        method: MiniJavaMethod.Method,
        arg: ArrayList<MiniJavaObject>,
        that: MiniJavaObject?
    ): MiniJavaObject {
        Mem.pushStack()
        Mem.pushLayer()

        //create local variable for arguments
        for ((index, i) in arg.withIndex()) {
            Mem.create(method.parameters[index].name, i.copy(type = method.parameters[index].type))
        }

        //create this as a variable
        if (that != null) {
            Mem.create("this", that)
        }

        try {
            val isConstructorCall = method.name.endsWith("::#new")
            if (isConstructorCall) {
                visitBlockAsConstructor(method.body, that!!)
            } else {
                visitBlock(method.body)
            }
        } catch (e: ReturnException) {
            if (e.value == null) { //`return;` - return without value
                if (method.returnType != "void") {
                    throw TypeErrorException("Expected return but found void")
                }
                Mem.popStack()
                return unitObject
            }

            TypeChecker.checkAndThrow(method.returnType, e.value.type)
            Mem.popStack()

            return e.value.copy(type = method.returnType)
        }
        //implicit return
        if (method.returnType != "void") {
            throw TypeErrorException("Expected returning `${method.returnType}` but did not find statement")
        }

        Mem.popStack()
        return unitObject
    }

    override fun visitMethodCall(ctx: MethodCallContext): MiniJavaObject {
        return visitMethodCall(ctx, null)
    }

    /**
     * @param that - the object that calls the method.
     * **If the function is called via `super.xxx()` then decl(that) is already the super type according to `visitDot`**
     * @param isSuper - whether it is in the form of `super.xxx()`
     * @see visitDot
     */
    fun visitMethodCall(ctx: MethodCallContext, that: MiniJavaObject?, isSuper: Boolean = false): MiniJavaObject {
        var name = if (ctx.THIS() != null) {
            "#new"
        } else if (ctx.SUPER() != null) {
            "#new"
        } else {
            visitIdentifier(ctx.identifier())
        }

        val arg = visitArguments(ctx.arguments())

        if (that != null) {
            val clzName = if (isSuper) that.type else that.realType
            val searchClz = classes[that.type] ?: error("No such class: ${that.type}")

            if (ctx.THIS() != null) {
                name = "${that.type}::#new"
            } else if (ctx.SUPER() != null) {
                name = "${classes[that.type]!!.parent}::#new"
            } else {
                //look for it in lookup cache. Note we need to use the same way as in `findMethod`
                var ans = ""
                var minConversionCount = Int.MAX_VALUE
                for ((sig, _) in searchClz.functionLookupCache) {
                    //a stupid patch to retrieve function names and arguments.

                    val funcName = sig.replace("\\(.*?\\)".toRegex(), "")
                    if (funcName != name) {
                        continue
                    }
                    val param = sig.substringAfter("(").substringBefore(")").split(",").map { it.trim() }
                        .filter { it.isNotEmpty() }
                    if (param.size != arg.size) {
                        continue
                    }
                    var ok = true
                    var conversionCount = 0
                    for ((index, i) in arg.withIndex()) {
                        if (!TypeChecker.check(param[index], i.type)) {
                            ok = false
                            break
                        }
                        if (param[index] != i.type) {
                            conversionCount++
                        }
                    }
                    if (!ok) {
                        continue
                    }
                    if (conversionCount < minConversionCount) {
                        minConversionCount = conversionCount
                        ans = sig
                    }
                }

                if (ans != "") {
                    name = classes[clzName]!!.functionLookupCache[ans]!!
                }
//                if(toLookup in clz.functionLookupCache){
//                    name = clz.functionLookupCache[toLookup] !!
//                }
            }
        }

        return callMethod(name, arg, if ("::" in name) that else null)
    }

    override fun visitCreator(ctx: CreatorContext): MiniJavaObject {
        if (ctx.arrayCreatorRest() != null) {
            //it's an array

            val name = visitCreatedName(ctx.createdName())
            val dimension = ctx.arrayCreatorRest().LBRACK().size

            val type = name + "[]".repeat(dimension)

            if (ctx.arrayCreatorRest().arrayInitializer() != null) {
                return visitArrayInitializer(ctx.arrayCreatorRest().arrayInitializer(), type.dropLast(2))
            }

            //prepare expression count
            val dimensionArr = ctx.arrayCreatorRest().expression().reversed()
            return prepareArrayOf(type, dimensionArr)
        } else {
            //it's class

            val name = visitCreatedName(ctx.createdName())
            val arg = if (ctx.classCreatorRest().expressionList() != null) {
                visitExpressionList(ctx.classCreatorRest().expressionList())
            } else {
                arrayListOf()
            }

            val newObject = MiniJavaObject(name, HashMap<String, MiniJavaObject>())

            //init new obj with default value
            initObject(newObject, newObject.realType)

            //call constructor
            callMethod("$name::#new", arg, newObject)
            return newObject
        }
    }

    /**
     * Set every field in an object to `TypeChecker.default`
     */
    fun initObject(obj: MiniJavaObject, type: String) {
        if (type == "Object") {
            return
        }
        val clz = classes[type] ?: error("No such type: $type")

        initObject(obj, clz.parent)

        for ((fieldName, field) in clz.fields) {
            obj.valueAsMap()["$type::$fieldName"] = TypeChecker.default(field.type)
        }
    }

    /**
     * Create an empty array of type `type` with dimension `dimensionArr`
     *
     * Waste of time err...
     */
    private fun prepareArrayOf(type: String, dimensionArr: List<ExpressionContext>): MiniJavaObject {
        if (dimensionArr.isEmpty()) {
            if (type.endsWith("[]")) {
                return nullObject.copy(type = type)
            }
            return TypeChecker.default(type)
        }

        val countExp = visitExpression(dimensionArr.last())
        TypeChecker.checkAndThrow("int", countExp.type)
        val count = countExp.value!!.i()

        val list = ArrayList<MiniJavaObject>()
        for (i in 1..count) {
            list.add(prepareArrayOf(type.dropLast(2), dimensionArr.dropLast(1)))
        }

        return MiniJavaObject(type, list)
    }

    override fun visitCreatedName(ctx: CreatedNameContext): String {
        return ctx.text
    }

    override fun visitClassCreatorRest(ctx: ClassCreatorRestContext?): Any {
        TODO("Not yet implemented")
    }

    override fun visitArrayCreatorRest(ctx: ArrayCreatorRestContext): MiniJavaObject {
        error("Should not reach here")
    }

    override fun visitTypeType(ctx: TypeTypeContext): String {
        var s = if (ctx.primitiveType() != null) ctx.primitiveType().text else ctx.identifier().text
        ctx.LBRACK().forEach { _ ->
            s += "[]"
        }
        return s
    }

    override fun visitPrimitiveType(ctx: PrimitiveTypeContext): String {
        return ctx.text
    }

    override fun visitArguments(ctx: ArgumentsContext): ArrayList<MiniJavaObject> {
        return if (ctx.expressionList() != null) {
            visitExpressionList(ctx.expressionList())
        } else {
            arrayListOf()
        }
    }
}