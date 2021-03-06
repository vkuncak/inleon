import leon.collection._

case class TinyCertifiedCompiler[A](op: (A, A) => A) {
  abstract class ByteCode
  case class Load(c: A) extends ByteCode // loads a constant in to the stack
  case class OpInst() extends ByteCode

  abstract class ExprTree
  case class Const(c: A) extends ExprTree
  case class Op(e1: ExprTree, e2: ExprTree) extends ExprTree

  def compile(e: ExprTree): List[ByteCode] = {
    e match {
      case Const(c) =>
        Cons(Load(c), Nil())
      case Op(e1, e2) =>
        (compile(e1) ++ compile(e2)) ++ Cons(OpInst(), Nil())
    }
  }

  def run(bytecode: List[ByteCode], S: List[A]): List[A] = {
    (bytecode, S) match {
      case (Cons(Load(c), tail), _) =>
        run(tail, c :: S) // adding elements to the head of the stack
      case (Cons(OpInst(), tail), Cons(x, Cons(y, rest))) =>
        run(tail, op(y, x) :: rest)
      case (Cons(_, tail), _) =>
        run(tail, S)
      case (Nil(), _) => // no bytecode to execute
        S
    }
  }

  def interpret(e: ExprTree): A = {
    e match {
      case Const(c) => c
      case Op(e1, e2) => op(interpret(e1), interpret(e2))
    }
  }
}
/*
  def runAppendLemma(l1: List[ByteCode], l2: List[ByteCode], S: List[A]): Boolean = {
    // lemma
    (run(l1 ++ l2, S) == run(l2, run(l1, S))) because
      // induction scheme (induct over l1)
      (l1 match {
        case Nil() => true
        case Cons(h, tail) =>
          (h, S) match {
            case (Load(c), _) =>
              runAppendLemma(tail, l2, Cons[A](c, S))
            case (OpInst(), Cons(x, Cons(y, rest))) =>
              runAppendLemma(tail, l2, Cons[A](op(y, x), rest))
            case (_, _) =>
              runAppendLemma(tail, l2, S)
            case _ => true
          }
      })
  }.holds

  def compileInterpretEquivalenceLemma(e: ExprTree, S: List[A]): Boolean = {
    // lemma
    (run(compile(e), S) == interpret(e) :: S) because 
      // induction scheme
      (e match {
        case Const(c) => true
        case Op(e1, e2) =>
          // lemma instantiation
          val c1 = compile(e1)
          val c2 = compile(e2)
          runAppendLemma((c1 ++ c2), Cons[ByteCode](OpInst(), Nil()), S) &&
            runAppendLemma(c1, c2, S) &&
            compileInterpretEquivalenceLemma(e1, S) &&
            compileInterpretEquivalenceLemma(e2, Cons[A](interpret(e1), S))
      })
  }.holds
}
*/
