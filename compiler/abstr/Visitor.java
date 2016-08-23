package compiler.abstr;

import compiler.abstr.tree.*;

/**
 * @author sliva
 */
public interface Visitor {
	
	public void visit(AbsArrType    acceptor);
	public void visit(AbsAtomConst  acceptor);
	public void visit(AbsAtomType   acceptor);
	public void visit(AbsBinExpr    acceptor);
//  public void visit(AbsDef        acceptor);
	public void visit(AbsDefs       acceptor);
//  public void visit(AbsExpr       acceptor);
    public void visit(AbsExprs      acceptor);
    public void visit(AbsFor        acceptor);
	public void visit(AbsFunCall    acceptor);
	public void visit(AbsFunDef     acceptor);
	public void visit(AbsIfThen     accpetor);
	public void visit(AbsIfThenElse accpetor);
	public void visit(AbsPar        acceptor);
//  public void visit(AbsTree       acceptor);
//  public void visit(AbsType       acceptor);
	public void visit(AbsTypeDef    acceptor);
	public void visit(AbsTypeName   acceptor);
	public void visit(AbsUnExpr     acceptor);
	public void visit(AbsVarDef     acceptor);
	public void visit(AbsVarName    acceptor);
	public void visit(AbsWhere      acceptor);
	public void visit(AbsWhile      acceptor);

}
