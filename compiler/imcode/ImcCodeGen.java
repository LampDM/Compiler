package compiler.imcode;

import compiler.Report;
import java.util.*;

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
import compiler.frames.FrmDesc;
import compiler.frames.*;
import compiler.seman.SymbDesc;
import compiler.abstr.tree.AbsTree;
import compiler.seman.type.SemArrType;
import java.util.Stack;

public class ImcCodeGen implements Visitor {

    private FrmFrame currentFrame = null;
    public LinkedList<ImcChunk> chunks;
    static HashMap<AbsTree, ImcCode> CodeMap = new HashMap<>();
    

    public ImcCodeGen() {
        chunks = new LinkedList<ImcChunk>();
    }
    
    public ImcChunk getChunk(String name){
        ImcChunk ret=null;
        for(ImcChunk c : chunks){
            if(c instanceof ImcDataChunk){
                ImcDataChunk dc=(ImcDataChunk)c;
                if(dc.label.name().equals(name)) return dc;
                
            }else
            if(c instanceof ImcCodeChunk){
                ImcCodeChunk cc=(ImcCodeChunk)c;
                if(cc.frame.label.name().equals(name)) return cc;
            }
        }
        
        Report.error("Error - no "+name+" class detected!");
        System.exit(1);
        
        return ret;
    }

    @Override
    public void visit(AbsArrType acceptor) {

    }

    @Override
    public void visit(AbsAtomType acceptor) {
    }

    @Override
    public void visit(AbsFor acceptor) {//OK - for V=B1,B2,B3 : S    - ex. for V=0,100,1 : S (pojdi od 0 do 100, pristevaj 1 ob vsaki iteracij)

        acceptor.count.accept(this);
        acceptor.lo.accept(this);
        acceptor.hi.accept(this);
        acceptor.step.accept(this);
        acceptor.body.accept(this);

        ImcSEQ exprs = new ImcSEQ();

        FrmLabel LabelCond = FrmLabel.newLabel();

        FrmLabel LabelBody = FrmLabel.newLabel();

        FrmLabel LabelEnd = FrmLabel.newLabel();

        ImcExpr V = (ImcExpr) CodeMap.get(acceptor.count);
        ImcExpr B1 = (ImcExpr) CodeMap.get(acceptor.lo);
        ImcExpr B2 = (ImcExpr) CodeMap.get(acceptor.hi);
        ImcExpr B3 = (ImcExpr) CodeMap.get(acceptor.step);

        exprs.stmts.add(new ImcMOVE(V, B1));

        exprs.stmts.add(new ImcLABEL(LabelCond));

        exprs.stmts.add(new ImcCJUMP(new ImcBINOP(ImcBINOP.LTH, V, B2), LabelBody, LabelEnd));

        exprs.stmts.add(new ImcLABEL(LabelBody));

        //------------------------------------------------Body
        ImcCode expr = CodeMap.get(acceptor.body);
        
        if(expr instanceof ImcStmt){
            exprs.stmts.add((ImcStmt) expr);
        }else{
            exprs.stmts.add(new ImcEXP((ImcExpr) expr));
        }

        //------------------------------------------------Body
        
        exprs.stmts.add(new ImcMOVE(V, new ImcBINOP(ImcBINOP.ADD, V, B3)));

        exprs.stmts.add(new ImcJUMP(LabelCond));

        exprs.stmts.add(new ImcLABEL(LabelEnd));

        CodeMap.put(acceptor, exprs);
    }

    @Override
    public void visit(AbsFunCall acceptor) {//OK 

        ImcExpr loc = new ImcTEMP(currentFrame.FP);

        FrmFrame fun = FrmDesc.getFrame(SymbDesc.getNameDef(acceptor));

        for (int k = 0; k <= currentFrame.level - fun.level; k++) {
            loc = new ImcMEM(loc);
        }
        
        ImcCALL fc = new ImcCALL(fun.label);
        
        fc.args.add(loc);

        for (int k = 0; k < acceptor.numArgs(); k++) {
            acceptor.arg(k).accept(this);
            fc.args.add((ImcExpr) CodeMap.get(acceptor.arg(k)));
        }

        CodeMap.put(acceptor, fc);
    }

    @Override
    public void visit(AbsFunDef acceptor) {//OK 
        
        FrmFrame temp = currentFrame;

        currentFrame = FrmDesc.getFrame(acceptor);

        acceptor.expr.accept(this);

        ImcExpr ex = (ImcExpr) CodeMap.get(acceptor.expr);

        chunks.add(new ImcCodeChunk(currentFrame, new ImcMOVE(new ImcTEMP(currentFrame.RV), ex)));

        currentFrame = temp;
    }

    @Override
    public void visit(AbsVarName acceptor) {//OK 
        FrmAccess access = FrmDesc.getAccess(SymbDesc.getNameDef(acceptor));

        if (access instanceof FrmLocAccess) {
            

            ImcExpr loc = new ImcTEMP(currentFrame.FP);
            
            FrmLocAccess var = (FrmLocAccess) access;
            
            for (int i = 0; i < currentFrame.level - var.frame.level; i++) {
                loc = new ImcMEM(loc);
            }
            
            CodeMap.put(acceptor, new ImcMEM(new ImcBINOP(ImcBINOP.ADD, (ImcExpr) loc, new ImcCONST(var.offset))));
        }

        if (access instanceof FrmParAccess) {
            
            ImcExpr loc = new ImcTEMP(currentFrame.FP);
            FrmParAccess par = (FrmParAccess) access;
            
            for (int k = 0; k < currentFrame.level - par.frame.level; k++) {
                loc = new ImcMEM(loc);
            }

            CodeMap.put(acceptor, new ImcMEM(new ImcBINOP(ImcBINOP.ADD, (ImcExpr) loc, new ImcCONST(par.offset))));
        }

        if (access instanceof FrmVarAccess) {
            CodeMap.put(acceptor, new ImcMEM(new ImcNAME(((FrmVarAccess) access).label)));
        }

    }

    @Override
    public void visit(AbsBinExpr acceptor) {//OK 

        acceptor.expr1.accept(this);
        acceptor.expr2.accept(this);

        ImcExpr expr1 = (ImcExpr) CodeMap.get(acceptor.expr1);
        ImcExpr expr2 = (ImcExpr) CodeMap.get(acceptor.expr2);

        switch (acceptor.oper) {

            case AbsBinExpr.ASSIGN:
                ImcTEMP temp = new ImcTEMP(new FrmTemp());
                ImcSEQ moves = new ImcSEQ();
                
                moves.stmts.add(new ImcMOVE(temp, ((ImcMEM) expr1).expr));
                moves.stmts.add(new ImcMOVE(new ImcMEM(temp), expr2));
                CodeMap.put(acceptor, new ImcESEQ(moves, new ImcMEM(temp)));
                break;

            case AbsBinExpr.ARR:

                if (expr1 instanceof ImcESEQ) {
                    expr1 = ((ImcESEQ) expr1).expr;
                }
                
                int extype=((SemArrType) SymbDesc.getType(acceptor.expr1).actualType()).type.size();
                
                CodeMap.put(acceptor, new ImcMEM(new ImcBINOP(ImcBINOP.ADD, ((ImcMEM) expr1).expr, new ImcBINOP(ImcBINOP.MUL, expr2, new ImcCONST(extype)))));
                break;
            case AbsBinExpr.MOD:

                
                ImcTEMP r = new ImcTEMP(new FrmTemp());
                ImcTEMP l = new ImcTEMP(new FrmTemp());
                
                ImcSEQ exprs = new ImcSEQ();

                exprs.stmts.add(new ImcMOVE(l, expr1));
                exprs.stmts.add(new ImcMOVE(r, expr2));

                //Premaknemo v svoje tempe nato pa ImcBinop
                //Ex. 10 mod 3 --->  10 - ( (10/3)*3 )
                CodeMap.put(acceptor, new ImcESEQ(exprs, new ImcBINOP(ImcBINOP.SUB, l, new ImcBINOP(ImcBINOP.MUL, new ImcBINOP(ImcBINOP.DIV, l, r), r))));
                break;
            default:
                CodeMap.put(acceptor, new ImcBINOP(acceptor.oper, expr1, expr2));
                break;
        }

    }

    @Override
    public void visit(AbsDefs acceptor) {//OK 
        for (int k = 0; k < acceptor.numDefs(); k++) {
            acceptor.def(k).accept(this);
        }
    }

    @Override
    public void visit(AbsExprs acceptor) {//OK 

        for (int i = 0; i < acceptor.numExprs(); i++) {
            acceptor.expr(i).accept(this);
        }

        if (acceptor.numExprs() == 1) {
            CodeMap.put(acceptor, CodeMap.get(acceptor.expr(0)));
            return;
        }

        ImcSEQ exprs = new ImcSEQ();

        for (int k = 0; k < acceptor.numExprs() - 1; k++) {
            ImcCode expr = CodeMap.get(acceptor.expr(k));

            if(expr instanceof ImcStmt){
                exprs.stmts.add((ImcStmt) expr);
            }else{
                exprs.stmts.add(new ImcEXP((ImcExpr) expr));
            }
        }

        if (CodeMap.get(acceptor.expr(acceptor.numExprs() - 1)) instanceof ImcExpr)// SEQ vs ESEQ
        {//ESEQ - prednostno sepravi kadar prvi izraz expresssions upliva na zadnji rezultat PREDAVANJA
            CodeMap.put(acceptor, new ImcESEQ(exprs, (ImcExpr) CodeMap.get(acceptor.expr(acceptor.numExprs() - 1))));
        } else {
            //If it's statement it's all good and we only good a normal SEQ
            exprs.stmts.add((ImcStmt) CodeMap.get(acceptor.expr(acceptor.numExprs() - 1)));
            CodeMap.put(acceptor, exprs);
        }
    }

    @Override
    public void visit(AbsAtomConst acceptor) {//OK 

        switch (acceptor.type) {
            case AbsAtomConst.INT:
                CodeMap.put(acceptor, new ImcCONST(Integer.parseInt(acceptor.value)));
                break;
            case AbsAtomConst.LOG:
                CodeMap.put(acceptor, new ImcCONST(acceptor.value.equals("true") ? 1 : 0));
                break;
            case AbsAtomConst.STR://OK - address of string - that's why it's quatro
                FrmLabel naslovStringa = FrmLabel.newLabel();
                chunks.add(new ImcDataChunk(naslovStringa, 4));
                CodeMap.put(acceptor, new ImcNAME(naslovStringa));
                break;
        }

    }

    @Override
    public void visit(AbsIfThen acceptor) {//OK 

        acceptor.cond.accept(this);
        acceptor.thenBody.accept(this);
        
        
        ImcSEQ exprs = new ImcSEQ();
        
        FrmLabel LabelTrue = FrmLabel.newLabel();

        FrmLabel LabelFalse = FrmLabel.newLabel();

        
        exprs.stmts.add(new ImcCJUMP((ImcExpr) CodeMap.get(acceptor.cond), LabelTrue, LabelFalse));

        exprs.stmts.add(new ImcLABEL(LabelTrue));

        ImcCode thenExpr = CodeMap.get(acceptor.thenBody);

        if(thenExpr instanceof ImcStmt){
            exprs.stmts.add((ImcStmt) thenExpr);
        }else{
            exprs.stmts.add(new ImcEXP((ImcExpr) thenExpr));
        }
        
        exprs.stmts.add(new ImcLABEL(LabelFalse));

        CodeMap.put(acceptor, exprs);
    }

    @Override
    public void visit(AbsIfThenElse acceptor) {//OK 

        acceptor.cond.accept(this);
        acceptor.thenBody.accept(this);
        acceptor.elseBody.accept(this);

        FrmLabel LabelTrue = FrmLabel.newLabel();
        FrmLabel LabelFalse = FrmLabel.newLabel();
        
        ImcSEQ exprs = new ImcSEQ();

        FrmLabel LabelEnd = FrmLabel.newLabel();

        exprs.stmts.add(new ImcCJUMP((ImcExpr) CodeMap.get(acceptor.cond), LabelTrue, LabelFalse));
        exprs.stmts.add(new ImcLABEL(LabelTrue));

        ImcCode thenExpr = CodeMap.get(acceptor.thenBody);
        
        if(thenExpr instanceof ImcStmt){
            exprs.stmts.add((ImcStmt) thenExpr);
        }else{
            exprs.stmts.add(new ImcEXP((ImcExpr) thenExpr));
        }
        

        exprs.stmts.add(new ImcJUMP(LabelEnd));/// SHOULD JUMP
        
        exprs.stmts.add(new ImcLABEL(LabelFalse));

        ImcCode elseExpr = CodeMap.get(acceptor.elseBody);

        if(elseExpr instanceof ImcStmt){
            exprs.stmts.add((ImcStmt) elseExpr);
        }else{
            exprs.stmts.add(new ImcEXP((ImcExpr) elseExpr));
        }

        exprs.stmts.add(new ImcLABEL(LabelEnd));

        CodeMap.put(acceptor, exprs);
    }

    @Override
    public void visit(AbsPar acceptor) {

    }//OK

    @Override
    public void visit(AbsTypeDef acceptor) {
    }//OK

    @Override
    public void visit(AbsTypeName acceptor) {
    }//OK

    @Override
    public void visit(AbsUnExpr acceptor) {//OK 

        acceptor.expr.accept(this);

        switch (acceptor.oper) {
            case AbsUnExpr.ADD:
                CodeMap.put(acceptor, CodeMap.get(acceptor.expr));
                break;

            case AbsUnExpr.SUB://Sepravi 0-ImcExpr
                CodeMap.put(acceptor, new ImcBINOP(ImcBINOP.SUB, new ImcCONST(0), (ImcExpr) CodeMap.get(acceptor.expr)));
                break;

            case AbsUnExpr.NOT:// EQU 0 0 --> 1  EQU 1 0 --> 0 Ce je true ga naredi false in obratno.
                CodeMap.put(acceptor, new ImcBINOP(ImcBINOP.EQU, (ImcExpr) CodeMap.get(acceptor.expr), new ImcCONST(0)));
                break;
        }

    }

    @Override
    public void visit(AbsVarDef acceptor) {//OK 

        if (currentFrame == null) {
            chunks.add(new ImcDataChunk(((FrmVarAccess) FrmDesc.getAccess(acceptor)).label, SymbDesc.getType(acceptor).size()));
        }

    }

    @Override
    public void visit(AbsWhere acceptor) {//OK 
        acceptor.defs.accept(this);
        acceptor.expr.accept(this);

        CodeMap.put(acceptor, CodeMap.get(acceptor.expr));
    }

    @Override
    public void visit(AbsWhile acceptor) {//OK 
        acceptor.cond.accept(this);
        acceptor.body.accept(this);

        FrmLabel LabelStart = FrmLabel.newLabel();

        ImcSEQ exprs = new ImcSEQ();



        exprs.stmts.add(new ImcLABEL(LabelStart));
        
        FrmLabel LabelBody = FrmLabel.newLabel();
        FrmLabel LabelEnd = FrmLabel.newLabel();
        
        ImcCode expr = CodeMap.get(acceptor.body);

        exprs.stmts.add(new ImcCJUMP((ImcExpr) CodeMap.get(acceptor.cond), LabelBody, LabelEnd));
        exprs.stmts.add(new ImcLABEL(LabelBody));

        //Check if it returns or not, Statement vs Exp - Exp vraca statement pa ne
        if (expr instanceof ImcStmt) {
            exprs.stmts.add((ImcStmt) expr);
        } else {
            exprs.stmts.add(new ImcEXP((ImcExpr) expr));
        }

        exprs.stmts.add(new ImcJUMP(LabelStart));
        exprs.stmts.add(new ImcLABEL(LabelEnd));

        CodeMap.put(acceptor, exprs);
    }

}
