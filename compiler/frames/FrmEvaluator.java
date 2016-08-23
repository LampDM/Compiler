package compiler.frames;
import compiler.abstr.*;
import compiler.abstr.tree.AbsArrType;
import compiler.abstr.tree.AbsAtomConst;
import compiler.abstr.tree.AbsAtomType;
import compiler.abstr.tree.AbsBinExpr;
import compiler.abstr.tree.AbsDefs;
import compiler.abstr.tree.AbsExprs;
import compiler.abstr.tree.AbsFor;
import compiler.abstr.tree.AbsFunCall;
import compiler.abstr.tree.AbsFunDef;
import compiler.abstr.tree.AbsIfThen;
import compiler.abstr.tree.AbsIfThenElse;
import compiler.abstr.tree.AbsPar;
import compiler.abstr.tree.AbsTypeDef;
import compiler.abstr.tree.AbsTypeName;
import compiler.abstr.tree.AbsUnExpr;
import compiler.abstr.tree.AbsVarDef;
import compiler.abstr.tree.AbsVarName;
import compiler.abstr.tree.AbsWhere;
import compiler.abstr.tree.AbsWhile;
import compiler.seman.SymbDesc;
import java.util.Stack;

public class FrmEvaluator implements Visitor {

    static int level = 0;
    static Stack<FrmFrame> FrmStack = new Stack<>();
    
    @Override
    public void visit(AbsArrType acceptor) {
        
    }

    @Override
    public void visit(AbsAtomConst acceptor) {
        
    }

    @Override
    public void visit(AbsAtomType acceptor) {
        
    }

    @Override
    public void visit(AbsBinExpr acceptor) {
        acceptor.expr1.accept(this);
        acceptor.expr2.accept(this);
    }

    @Override
    public void visit(AbsDefs acceptor) {
        for(int k=0;k<acceptor.numDefs();k++){
            acceptor.def(k).accept(this);
        }
    }
    
    @Override
    public void visit(AbsVarDef acceptor) {
        if(FrmStack.isEmpty()){
            FrmDesc.setAccess(acceptor, new FrmVarAccess(acceptor));
        }else{
            FrmFrame f = (FrmFrame)FrmStack.pop();
            FrmDesc.setAccess(acceptor, new FrmLocAccess(acceptor,f));
            FrmStack.push(f);
            
        }
            
    }
    
    @Override
    public void visit(AbsFunDef acceptor) {
        FrmFrame frame = new FrmFrame(acceptor,level);
        FrmStack.push(frame);
        
        for(int k = 0;k<acceptor.numPars();k++){
            
            acceptor.par(k).accept(this);
        }
        
        
        level++;
        acceptor.expr.accept(this);
        level--;
        
        FrmDesc.setFrame(acceptor, (FrmFrame)FrmStack.pop());
        
        
        
    }

    @Override
    public void visit(AbsExprs acceptor) {
        for(int k=0;k<acceptor.numExprs();k++){
            acceptor.expr(k).accept(this);
        }
    }

    @Override
    public void visit(AbsFor acceptor) {
        acceptor.body.accept(this);
    }

    @Override
    public void visit(AbsFunCall acceptor) {
          FrmFrame f = (FrmFrame)FrmStack.pop();
          
          
          int argsSum=4;
          for(int k = 0;k<acceptor.numArgs();k++){
              acceptor.arg(k).accept(this);
              argsSum+=SymbDesc.getType(acceptor.arg(k)).size();
          }
          
          f.sizeArgs=Math.max(f.sizeArgs,argsSum);
          

          //Fixed - double check next time.
          //MAX f(5) + f(5) - DM   ----- max od imput(sizeof(SL) ... sum(sizeof(args))) out( sizeof(out))
          
          FrmStack.push(f);

     
    }

  

    @Override
    public void visit(AbsIfThen acceptor) {
        acceptor.thenBody.accept(this);
    }

    @Override
    public void visit(AbsIfThenElse acceptor) {
        acceptor.thenBody.accept(this);
        acceptor.thenBody.accept(this);
    }

    @Override
    public void visit(AbsPar acceptor) {
        FrmDesc.setAccess(acceptor, new FrmParAccess(acceptor,FrmStack.peek()));
        
    }

    @Override
    public void visit(AbsTypeDef acceptor) {
        
    }

    @Override
    public void visit(AbsTypeName acceptor) {
        
    }

    @Override
    public void visit(AbsUnExpr acceptor) {
        acceptor.expr.accept(this);
    }

   

    @Override
    public void visit(AbsVarName acceptor) {
        
    }

    @Override
    public void visit(AbsWhere acceptor) {
        acceptor.expr.accept(this);
        acceptor.defs.accept(this);
    }

    @Override
    public void visit(AbsWhile acceptor) {
        acceptor.body.accept(this);
        
    }
	
	
	
}
