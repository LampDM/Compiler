package compiler.seman;

import compiler.Report;
import compiler.abstr.*;
import compiler.abstr.tree.*;
import compiler.frames.FrmDesc;
import compiler.frames.FrmFrame;
import compiler.frames.FrmLabel;
import compiler.seman.type.SemAtomType;
import compiler.seman.type.SemFunType;
import compiler.seman.type.SemType;
import java.util.*;


/**
 * Preverjanje in razresevanje imen (razen imen komponent).
 * 
 * @author sliva
 */
public class NameChecker implements Visitor {
    
    public NameChecker(){
    //Built in Functions
		// print Integer
		Vector<AbsPar> par = new Vector<AbsPar>();
                
		Vector<SemType> para = new Vector<SemType>();
                
		para.add(new SemAtomType(SemAtomType.INT));
                
		par.add(new AbsPar(null, "outInteger", new AbsAtomType(null, AbsAtomType.INT)));
		AbsFunDef out = new AbsFunDef(null, "outInteger", par, new AbsAtomType(null, AbsAtomType.INT), new AbsExpr(null) {
			@Override
			public void accept(Visitor visitor) {
				// TODO Auto-generated method stub
				
			}
		});
			try {
				SymbTable.ins("outInteger", out);
			} catch (SemIllegalInsertException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		SymbDesc.setType(out, new SemFunType(para, new SemAtomType(SemAtomType.VOID)));
		SymbDesc.setType(out.par(0), new SemAtomType(SemAtomType.INT));
		SymbDesc.setScope(out, 0);
		FrmFrame fm = new FrmFrame(out, 0);
		fm.numPars = 1;
		fm.sizePars = 4;
		fm.label = FrmLabel.newLabel("outInteger");
		FrmDesc.setFrame(out, fm);
                
                
                
                // print String
		par = new Vector<AbsPar>();
		para = new Vector<SemType>();
		
		para.add(new SemAtomType(SemAtomType.STR));
		par.add(new AbsPar(null, "outString", new AbsAtomType(null, AbsAtomType.STR)));
		
		out = new AbsFunDef(null, "outString", par, new AbsAtomType(null, AbsAtomType.STR), null);
			try {
				SymbTable.ins("outString", out);
			} catch (SemIllegalInsertException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		SymbDesc.setType(out, new SemFunType(para, new SemAtomType(SemAtomType.VOID)));
		SymbDesc.setType(out.par(0), new SemAtomType(SemAtomType.STR));
		SymbDesc.setScope(out, 0);
		fm = new FrmFrame(out, 0);
		fm.numPars = 1;
		fm.sizePars = 4;
		fm.label = FrmLabel.newLabel("outString");
		FrmDesc.setFrame(out, fm);
		
 
    
    }

    
    @Override
    public void visit(AbsDefs acceptor) {//AbsDef

        List<AbsTypeDef> defsT = new ArrayList<>();
        List<AbsVarDef> defsV = new ArrayList<>();
        List<AbsFunDef> defsF = new ArrayList<>();

        
        
        //0th pass - we sort them
        for(int k = 0;k<acceptor.numDefs();k++){
            AbsDef next = acceptor.def(k);
            
            if(next instanceof AbsTypeDef){
                defsT.add((AbsTypeDef)next);
            }
            
             if(next instanceof AbsVarDef){
                defsV.add((AbsVarDef)next);
            }
             
            
             if(next instanceof AbsFunDef){
                defsF.add((AbsFunDef)next);
            }
            
        }
        
        //1th pass - imena tipov
        
        for(AbsTypeDef o : defsT){
            try{
                SymbTable.ins(o.name,o);
            }catch(Exception exc){Report.error(o.position, "Error - "+"AbsTypeDef "+o.name+" already defined.");}
            
        }
        
        
        //2th pass - jedra tipov
        
        for(AbsTypeDef o : defsT){
            o.accept(this);
        }
        
        //3rd pass - spremenljivke
        
        for(AbsVarDef o : defsV){
            o.accept(this);
        }
        
        //4th pass - imena funkcij
        
        for(AbsFunDef o : defsF){
             try{
                    SymbTable.ins(o.name,o);
        }catch(Exception exc){Report.error(o.position, "Error - "+" AbsFunDef "+o.name+" already defined.");}
        }
        
        //5th pass - jedra funkcij
        
         for(AbsFunDef o : defsF){
             o.accept(this);
        }
        
        
    }
    
    @Override
    public void visit(AbsFunDef acceptor) {//AbsFunDef  
        
        //Added for Activation records
            //SymbDesc.setNameDef(acceptor, acceptor.name);
        //Added for Activation records - DM
        
        acceptor.type.accept(this);
        
        SymbTable.newScope();
        for(int k=0;k<acceptor.numPars();k++){
            acceptor.par(k).accept(this);
        }
        acceptor.expr.accept(this);
        SymbTable.oldScope();
    }
    
    @Override
    public void visit(AbsTypeDef acceptor) { //AbsTypeDef
        acceptor.type.accept(this);

    }
    
    @Override
    public void visit(AbsVarDef acceptor) {//AbsVarDef

       try{
                    SymbTable.ins(acceptor.name, acceptor);
                    acceptor.type.accept(this);
        }catch(Exception exc){Report.error(acceptor.position, "Error - "+"AbsVarDef "+acceptor.name+" already defined.");}
    }
    
    @Override
    public void visit(AbsPar acceptor) {
        try{
             SymbTable.ins(acceptor.name,acceptor);//Changed - DM 17 Maj
             acceptor.type.accept(this);
        }catch(Exception exc){Report.error(acceptor.position, "Error - "+"AbsVarDef"+acceptor.name+"already defined.");}
       
    }
    
    @Override
    public void visit(AbsArrType acceptor) {
        acceptor.type.accept(this);
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
    public void visit(AbsExprs acceptor) {
        for(int k=0;k<acceptor.numExprs();k++){
            acceptor.expr(k).accept(this);
        }
    }

    @Override
    public void visit(AbsFor acceptor) {
        acceptor.count.accept(this);
        acceptor.hi.accept(this);
        acceptor.lo.accept(this);
        acceptor.step.accept(this);
        acceptor.body.accept(this);

    }

    @Override
    public void visit(AbsFunCall acceptor) {

        if(SymbTable.fnd(acceptor.name)==null){
            
                Report.error(acceptor.position, "Error - function "+acceptor.name+" not defined!");
            

        }

        SymbDesc.setNameDef(acceptor, SymbTable.fnd(acceptor.name));
        
        for(int k = 0;k<acceptor.numArgs();k++){
            acceptor.arg(k).accept(this);
        }
    }


    @Override
    public void visit(AbsIfThen acceptor) {
        acceptor.cond.accept(this);
        acceptor.thenBody.accept(this);
    }

    @Override
    public void visit(AbsIfThenElse acceptor) {
        acceptor.cond.accept(this);
        acceptor.thenBody.accept(this);
        acceptor.elseBody.accept(this);

    }


    @Override
    public void visit(AbsTypeName acceptor) {
         if(SymbTable.fnd(acceptor.name)==null){
            Report.error(acceptor.position, "Error - type name "+acceptor.name+" not defined!");
            
        }
         SymbDesc.setNameDef(acceptor, SymbTable.fnd(acceptor.name));
    }

    @Override
    public void visit(AbsUnExpr acceptor) {
        acceptor.expr.accept(this);
    }

 

    @Override
    public void visit(AbsVarName acceptor) {
         if(SymbTable.fnd(acceptor.name)==null){
            Report.error(acceptor.position, "Error - var name "+acceptor.name+" not defined!");
        }
         
        SymbDesc.setNameDef(acceptor, SymbTable.fnd(acceptor.name));
    }

    @Override
    public void visit(AbsWhere acceptor) {
        SymbTable.newScope();
        acceptor.defs.accept(this);
        acceptor.expr.accept(this);
        SymbTable.oldScope();
    }

    @Override
    public void visit(AbsWhile acceptor) {
        acceptor.cond.accept(this);
        acceptor.body.accept(this);
    }



}
