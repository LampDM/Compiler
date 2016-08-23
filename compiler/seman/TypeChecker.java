package compiler.seman;

import java.util.*;

import compiler.*;
import compiler.abstr.*;
import compiler.abstr.tree.*;
import compiler.seman.type.*;

/**
 * Preverjanje tipov.
 * 
 * @author sliva
 */
public class TypeChecker implements Visitor {

    
    enum Race { typDef, typ, funname, funexpr };
    
    private Race phase;
    
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
        
        phase=Race.typDef;
        for(AbsTypeDef o : defsT){
            
          o.accept(this);
            
        }
        
        
        //2th pass - jedra tipov
        phase=Race.typ;
        for(AbsTypeDef o : defsT){
            o.accept(this);
        }
        
        //3rd pass - spremenljivke
        
        for(AbsVarDef o : defsV){
            o.accept(this);
        }

        //4th pass - imena funkcij
        phase=Race.funname;
        for(AbsFunDef o : defsF){
            o.accept(this);
        }
        
        //5th pass - jedra funkcij
         phase=Race.funexpr;
         for(AbsFunDef o : defsF){
             o.accept(this);
        }
    }
    
    @Override
    public void visit(AbsFunDef acceptor) {//AbsFunDef  
        
        if(phase==Race.funname){
            Vector<SemType> pars = new Vector<SemType>();
            for(int k=0;k<acceptor.numPars();k++){
                
                acceptor.par(k).accept(this);
                pars.add(SymbDesc.getType(acceptor.par(k).type));
        }

        
        acceptor.type.accept(this);
            
        SemType s = SymbDesc.getType(acceptor.type);
        SymbDesc.setType(acceptor, new SemFunType(pars,s));
        
        
        
        
        }else if(phase==Race.funexpr){
            acceptor.expr.accept(this);
            
            SemType t1=SymbDesc.getType(acceptor.type);
            SemType t2=SymbDesc.getType(acceptor.expr);
            
            

            if(!t1.sameStructureAs(t2)){
                Report.error(acceptor.position, "Error - FUNCT type and inner EXPR type do not match! found: "+t1+" "+t2);
            }

            
            
        }
        
        
        
        
        

    }
    //OBHODI12
    @Override
    public void visit(AbsTypeDef acceptor) { //AbsTypeDef
        
        	if ( phase == Race.typDef ){
                    SymbDesc.setType( acceptor, new SemTypeName(acceptor.name) );

                
                }	
		else if ( phase == Race.typ ) {
			
			if ( SymbDesc.getType(acceptor.type) != null )
				return;
			
			acceptor.type.accept(this);
                        
                        ((SemTypeName) SymbDesc.getType(acceptor)).setType(SymbDesc.getType(acceptor.type));

		}
                
    }
    
    @Override
    public void visit(AbsVarDef acceptor) {//AbsVarDef
            acceptor.type.accept(this);
            try{
                 SymbDesc.setType(acceptor, SymbDesc.getType(acceptor.type));
            }catch(Exception e){}
       
    }
    
    @Override
    public void visit(AbsAtomType acceptor) {
        SymbDesc.setType(acceptor, new SemAtomType(acceptor.type));
        
     
    }
    
    @Override
    public void visit(AbsPar acceptor) {
        acceptor.type.accept(this);
        
        SymbDesc.setType(acceptor, SymbDesc.getType(acceptor.type));
        //SymbDesc.setType(acceptor, SymbDesc.getType(acceptor.type));

            
    }
    
    @Override
    public void visit(AbsArrType acceptor) {
       acceptor.type.accept(this);
       SymbDesc.setType(acceptor, new SemArrType(acceptor.length, SymbDesc.getType(acceptor.type)));//Great Soviet engineering!
    }

    @Override
    public void visit(AbsBinExpr acceptor)  {
        
        
        
        
        SemType t1 = null;
        SemType t2 = null;
        
        //EXPR1
        acceptor.expr1.accept(this);
        t1 = SymbDesc.getType(acceptor.expr1);
        
        

        //EXPR2
        acceptor.expr2.accept(this);
        t2 = SymbDesc.getType(acceptor.expr2);


                
        switch(acceptor.oper){
            case 0://IOR
            case 1://AND
               if(t1.sameStructureAs(t2)){
                   SemAtomType intcmp = (SemAtomType)t1.actualType();
                   if(intcmp.type==0){
                       SymbDesc.setType(acceptor, new SemAtomType(0));
                   }else{Report.error(acceptor.position, "Error - wrong types for LOGICAL operation! found: "+t1+" "+t2);}
               }else{Report.error(acceptor.position, "Error - types in LOGICAL operation do not match! found: "+t1+" "+t2);}
                break;
            case 2://EQU
            case 3://NEQ
            case 4://LEQ
            case 5://GEQ
            case 6://LTH
            case 7://GTH
                 if(t1.sameStructureAs(t2)){
                   SemAtomType boolcmp = (SemAtomType)t1.actualType();
                   if(boolcmp.type==1){
                       SymbDesc.setType(acceptor, new SemAtomType(0));
                   }else{Report.error(acceptor.position, "Error - wrong types for LOGICAL operation! found: "+t1+" "+t2);}
               }else{Report.error(acceptor.position, "Error - types in LOGICAL operation do not match! found: "+t1+" "+t2);}               
                break;
            case 8://ADD
            case 9://SUB
            case 10://MUL
            case 11://DIV
            case 12://MOD

                 if(t1.sameStructureAs(t2)){
                   SemAtomType intcmp = (SemAtomType)t1.actualType();
                   if(intcmp.type==1){
                       SymbDesc.setType(acceptor, new SemAtomType(1));
                   }else{Report.error(acceptor.position, "Error - wrong types for INTEGER operation! found: "+t1+" "+t2);}
               }else{Report.error(acceptor.position, "Error - types in INTEGER operation do not match! found: "+t1+" "+t2);}
                break;
            case 15://ASSIGN
                if(t1.sameStructureAs(t2)){
                    SymbDesc.setType(acceptor, t2.actualType());
                }else{Report.error(acceptor.position,"Error - types in ASSIGMENT operation do not match! found: "+t1+" "+t2);}
                break;
            case 14://ARR
                SemArrType art = (SemArrType) t1;
                SymbDesc.setType(acceptor, art.type.actualType());
                break;
            
        }
       
    }
    
    @Override
    public void visit(AbsUnExpr acceptor) {
        
        switch(acceptor.oper){
            case 0://Plus +
            case 1://Minus -
                SymbDesc.setType(acceptor, new SemAtomType(1));
                break;
                
            case 4://Not !
                SymbDesc.setType(acceptor, new SemAtomType(0));
                break;
        }

        acceptor.expr.accept(this);
              
        //Prefix
        SemType t1 = SymbDesc.getType(acceptor);

        //EXPR
        SemType t2 = SymbDesc.getType(acceptor.expr);
        System.out.println(t2);
        if(!t1.sameStructureAs(t2)){
            Report.error(acceptor.position,"Error - types in PREFIX (AbsUnExpr) do not match! found: "+t1+" "+t2);
        }  

    }
    
    @Override
    public void visit(AbsAtomConst acceptor) {
       SymbDesc.setType(acceptor, new SemAtomType(acceptor.type));
    }
    
      @Override
    public void visit(AbsVarName acceptor) {
           Object def = SymbDesc.getNameDef(acceptor);
           
            if(SymbDesc.getNameDef(acceptor) instanceof compiler.abstr.tree.AbsPar){
                AbsPar p1 = (AbsPar)SymbDesc.getNameDef(acceptor);
                SymbDesc.setType(acceptor, SymbDesc.getType(p1.type));
                
            }else{//Else block is original
                AbsVarDef d1 = (AbsVarDef)SymbDesc.getNameDef(acceptor);//Changed DM 17 Maj
                SymbDesc.setType(acceptor, SymbDesc.getType(d1.type));
            }
            
            
           
           
    }

 

    @Override
    public void visit(AbsExprs acceptor) {
        for(int k=0;k<acceptor.numExprs();k++){
            acceptor.expr(k).accept(this);

            
            SymbDesc.setType(acceptor,SymbDesc.getType(acceptor.expr(k)));
        }
    }

    @Override
    public void visit(AbsFor acceptor) {
        acceptor.count.accept(this);
        acceptor.hi.accept(this);
        acceptor.lo.accept(this);
        acceptor.step.accept(this);
        acceptor.body.accept(this);
        SemAtomType id = (SemAtomType)SymbDesc.getType(acceptor.count).actualType();
        SemAtomType hi = (SemAtomType)SymbDesc.getType(acceptor.count).actualType();
        SemAtomType lo = (SemAtomType)SymbDesc.getType(acceptor.count).actualType();
        if(id.type==1 && hi.type==1 && lo.type==1){
            SymbDesc.setType(acceptor, new SemAtomType(3));
        }

    }

    @Override
    public void visit(AbsFunCall acceptor) {
        
        
        AbsDef f1 = SymbDesc.getNameDef(acceptor);
        SemFunType ft = (SemFunType)SymbDesc.getType(f1);
        SymbDesc.setType(acceptor, ft.resultType);
        
        for(int k = 0;k<acceptor.numArgs();k++){
            acceptor.arg(k).accept(this);
            
            Object rez=SymbDesc.getType(acceptor.arg(k));
            
            if(rez instanceof SemAtomType){
                SemAtomType partest = (SemAtomType)rez;
                SemAtomType partest2 = (SemAtomType)ft.getParType(k);
            
            
            

            
            
            if(partest.type==partest2.type){
                //OK
            }else{
                Report.error(acceptor.position, "Error - invalid type of par "+k+" in fun "+acceptor.name+" Expected: "+partest2+" Recieved: "+partest);
            }
            }
        }        
        
        

    }


    @Override
    public void visit(AbsIfThen acceptor) {
        acceptor.cond.accept(this);
        acceptor.thenBody.accept(this);
        SemAtomType t1 = (SemAtomType)SymbDesc.getType(acceptor.cond).actualType();
        if(t1.type==0){
            SymbDesc.setType(acceptor, new SemAtomType(3));
        }
    }

    @Override
    public void visit(AbsIfThenElse acceptor) {
        acceptor.cond.accept(this);
        acceptor.thenBody.accept(this);
        acceptor.elseBody.accept(this);
        SemAtomType t1 = (SemAtomType)SymbDesc.getType(acceptor.cond).actualType();
        if(t1.type==0){
            SymbDesc.setType(acceptor, new SemAtomType(3));
        }

    }


    @Override
    public void visit(AbsTypeName acceptor) {
       
        if (SymbDesc.getType( SymbDesc.getNameDef(acceptor)) == null ){
            Report.error("Error - undefined type " + acceptor.name + " at: " + acceptor.position.toString());
        }
                
		SymbDesc.setType(acceptor, SymbDesc.getType(SymbDesc.getNameDef(acceptor)));
    }

 

 

  

    @Override
    public void visit(AbsWhere acceptor) {
        
        acceptor.defs.accept(this);
        acceptor.expr.accept(this);
        

        
        SemAtomType t1 = (SemAtomType)SymbDesc.getType(acceptor.expr).actualType();
        SymbDesc.setType(acceptor, new SemAtomType(t1.type));
        
        

    }

    @Override
    public void visit(AbsWhile acceptor) {
        acceptor.cond.accept(this);
        acceptor.body.accept(this);
        SemAtomType t1 = (SemAtomType)SymbDesc.getType(acceptor.cond).actualType();
        if(t1.type==0){
            SymbDesc.setType(acceptor, new SemAtomType(3));
        }
    }



}

