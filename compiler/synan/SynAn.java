package compiler.synan;
import compiler.abstr.tree.*;
import compiler.Report;
import compiler.lexan.*;
import compiler.abstr.*;
import compiler.*;
import java.util.Vector;

/**
 * Sintaksni analizator.
 * 
 * @author sliva
 */
public class SynAn {

	/** Leksikalni analizator. */
	private LexAn lexAn;

	/** Ali se izpisujejo vmesni rezultati. */
	private boolean dump;
        private boolean nextFlag=true;
        private Symbol current;

	/**
	 * Ustvari nov sintaksni analizator.
	 * 
	 * @param lexAn
	 *            Leksikalni analizator.
	 * @param dump
	 *            Ali se izpisujejo vmesni rezultati.
	 */
	public SynAn(LexAn lexAn, boolean dump) {
		this.lexAn = lexAn;
		this.dump = dump;
                
	}

	/**
	 * Opravi sintaksno analizo.
	 */
	

	/**
	 * Izpise produkcijo v datoteko z vmesnimi rezultati.
	 * 
	 * @param production
	 *            Produkcija, ki naj bo izpisana.
	 */
	private void dump(String production) {
		if (!dump)
			return;
		if (Report.dumpFile() == null)
			return;
		Report.dumpFile().println(production);
	}
        
        public Symbol next(){
            if(this.nextFlag){
                this.nextFlag=false;
                this.current=this.lexAn.lexAn();
                return this.current;
            }
            return this.current;
        }
        public Symbol skip(Symbol arg){
            Symbol si = next();
            if(arg.token==next().token){
                this.nextFlag=true;
            }else{
                Report.warning("Error - invalid skip at "+ this.current + " " + next().lexeme + " "+next().position);
                System.exit(1);
            }
            return si;
        }

    public AbsTree parse() {   
            AbsTree r = null;
              switch(next().token){
                  case Token.KW_TYP:
                  case Token.KW_FUN:
                  case Token.KW_VAR:
                      dump("source -> defs");
                      r = defs();
                      break;
                  default:
                      Report.error("Syntax error in source state." + next().position + " symbol: "+next().lexeme);
                      System.exit(1);
              }
              
            return r;
	}
    
    private AbsDefs defs() {
        Vector<AbsDef> r = new Vector<AbsDef>();
        Symbol si = next();
        switch(next().token){
                  case Token.KW_TYP:
                  case Token.KW_FUN:
                  case Token.KW_VAR:
                      dump("defs -> def defs'");
                      AbsDef d=def();
                      r.add(d);
                      r=defs1(r);
                      return new AbsDefs(si.position,r);
                      //break;
                  default:
                      Report.error("Syntax error in definitions state." + next().position + " symbol: "+next().lexeme);
    }
        return new AbsDefs(si.position,r);

   

    }

    private AbsDef def() {
        AbsDef r = null;
        switch(next().token){
                  case Token.KW_TYP:
                      dump("def -> type_def");
                      r = type_def();
                      break;
                  case Token.KW_FUN:
                      dump("def -> fun_def");
                      r = fun_def();
                      break;
                  case Token.KW_VAR:
                      dump("def -> var_def");
                      r = var_def();
                      break;
                      
                  default:
                      Report.error("Syntax error in definitions state." + next().position + " symbol: "+next().lexeme+ " | Likely forgot ; or extra ;");
    }
        return r;
    }

    private Vector<AbsDef> defs1(Vector<AbsDef> r) {
        switch(next().token){
            case Token.SEMIC:
                dump("defs1 -> def defs'");
                skip(new Symbol(Token.SEMIC,"",0,0,0,0));
                AbsDef d = def();
                r.add(d);
                r = defs1(r);

                break;
            case Token.RBRACE:
                dump("defs' -> € ");
                break;
            case Token.EOF:
                dump("defs' -> $ ");
                break;
            default:
                 Report.error("Syntax error in definitions' state." + next().position + " symbol: "+next().lexeme + " | Most likely forgot ;");
                 
        
        }
        return r;
        
    }

    private AbsTypeDef type_def() {
        AbsTypeDef r = null;
        Symbol si;
        switch(next().token){
            
            case Token.KW_TYP:
                dump("type_def -> typ id : type");
                si = skip(new Symbol(Token.KW_TYP,"",0,0,0,0));
                si = skip(new Symbol(Token.IDENTIFIER,"",0,0,0,0));
                skip(new Symbol(Token.COLON,"",0,0,0,0));
                AbsType d = type();
                r = new AbsTypeDef(si.position,si.lexeme,d);
                break;
            default:
                 Report.error("Syntax error in type_def state." + next().position + " symbol: "+next().lexeme);
        }
        return r;
    }

    private AbsFunDef fun_def() {
        AbsFunDef r = null;
        Symbol si;
        switch(next().token){
            case Token.KW_FUN:
                dump("function_def -> fun id ( params ) : type = expr");
                
                si = skip(new Symbol(Token.KW_FUN,"",0,0,0,0));
                
                Symbol si2 = skip(new Symbol(Token.IDENTIFIER,"",0,0,0,0));
                
                skip(new Symbol(Token.LPARENT,"",0,0,0,0));
                
                Vector<AbsPar> p = params();
                
                skip(new Symbol(Token.RPARENT,"",0,0,0,0));
                skip(new Symbol(Token.COLON,"",0,0,0,0));
                
                AbsType t = type();
                
                skip(new Symbol(Token.ASSIGN,"",0,0,0,0));
                
                AbsExpr e = expr();
                
                r = new AbsFunDef(si.position,si2.lexeme,p,t,e);
                break;
            default:
                Report.error("Syntax error in fun_def state." + next().position + " symbol: "+next().lexeme);
        }
        return r;
    }

    private AbsVarDef var_def() {
        AbsVarDef r = null;
        Symbol si;
        switch(next().token){
            case Token.KW_VAR:
                dump("variable_def -> var id : type");
                si = skip(new Symbol(Token.KW_VAR,"",0,0,0,0));
                Symbol si2 = skip(new Symbol(Token.IDENTIFIER,"",0,0,0,0));
                skip(new Symbol(Token.COLON,"",0,0,0,0));
                AbsType t = type();
                r = new AbsVarDef(si.position,si2.lexeme,t);
                return r;
                //break;
            default:
                Report.error("Syntax error in var_def state." + next().position + " symbol: "+next().lexeme);
        }
        return r;
    }
    
    private AbsType type(){
        AbsType r = null;
        Symbol si;
        switch(next().token){
            case Token.IDENTIFIER:
                dump("type -> id");
                si = skip(new Symbol(Token.IDENTIFIER,"",0,0,0,0));
                r = new AbsTypeName(si.position,si.lexeme);
                break;
            case Token.LOGICAL:
                dump("type -> log");
                si = skip(new Symbol(Token.LOGICAL,"",0,0,0,0));
                r = new AbsAtomType(si.position,AbsAtomType.LOG);
                break;
            case Token.INTEGER:
                dump("type -> integer");
                si = skip(new Symbol(Token.INTEGER,"",0,0,0,0));
                r = new AbsAtomType(si.position,AbsAtomType.INT);
                break;
            case Token.STRING:
                dump("type -> string");
                si = skip(new Symbol(Token.STRING,"",0,0,0,0));
                r = new AbsAtomType(si.position,AbsAtomType.STR);
                break;
            case Token.KW_ARR:
                dump("type -> arr [ int_const ] type");
                Symbol si2;
                si = skip(new Symbol(Token.KW_ARR,"",0,0,0,0));
                skip(new Symbol(Token.LBRACKET,"",0,0,0,0));
                si2 = skip(new Symbol(Token.INT_CONST,"",0,0,0,0));
                skip(new Symbol(Token.RBRACKET,"",0,0,0,0));
                AbsType t = type();
                r = new AbsArrType(si.position,Integer.parseInt(si2.lexeme),t);//Not sure what this count is about
                break;
            default:
                Report.error("Syntax error in type state." + next().position + " symbol: "+next().lexeme);
        }
        return r;
    }

    private Vector<AbsPar> params() {
        Vector<AbsPar> r = new Vector<AbsPar>();
        switch(next().token){
            case Token.IDENTIFIER:
                dump("params -> param params'");
                AbsPar p = param();
                r.add(p);
                r = params1(r);
                break;
            default:
                Report.error("Syntax error in params state at " + next().position + " symbol: "+next().lexeme);
        
        }
        return r;
    }

    private AbsExpr expr() {
       AbsExpr r = null;
       Symbol si = next();
       switch(next().token){
           case Token.IDENTIFIER:
           case Token.LPARENT:
           case Token.LBRACE:
           case Token.ADD:
           case Token.SUB:
           case Token.NOT:
           case Token.LOG_CONST:
           case Token.INT_CONST:
           case Token.STR_CONST:
               dump("expr -> log_ior_expr expr''");
               r = log_ior_expr();
               r = expr2(r);
               break;
            default:
                Report.error("Syntax error in expr state." + next().position + " symbol: "+next().lexeme);
       }
       return r;
    }

    private AbsPar param() {
        AbsPar r = null;
        Symbol si;
        switch(next().token){
            case Token.IDENTIFIER:
                dump("param -> id : type");
                si = skip(new Symbol(Token.IDENTIFIER,"",0,0,0,0));
                skip(new Symbol(Token.COLON,"",0,0,0,0));
                
                AbsType t = type();
                
                r = new AbsPar(si.position,si.lexeme,t);
                
                break;
            default:
                Report.error("Syntax error in param state." + next().position + " symbol: "+next().lexeme);
        
        }
        return r;
    }

    private Vector<AbsPar> params1(Vector<AbsPar> r) {
        switch(next().token){
            case Token.RPARENT:
                dump("params' -> € ");
                break;
            case Token.COMMA:
                dump("params' -> , param params");
                skip(new Symbol(Token.COMMA,"",0,0,0,0));
                
                AbsPar p = param();
                
                r.add(p);
                
                r = params1(r);
                
                break;
            default:
                Report.error("Syntax error in params' state." + next().position + " symbol: "+next().lexeme);
        
        }
        return r;
    }

    private AbsExpr log_ior_expr() {
        AbsExpr r = null;
         Symbol si;
         switch(next().token){
           case Token.IDENTIFIER:
           case Token.LPARENT:
           case Token.LBRACE:
           case Token.ADD:
           case Token.SUB:
           case Token.NOT:
           case Token.LOG_CONST:
           case Token.INT_CONST:
           case Token.STR_CONST:
               dump("log_ior_expr -> log_and_expr log_ior_expr' ");
               r = log_and_expr();
               r = log_ior_expr1(r);
               break;
           default:
                Report.error("Syntax error in log_ior_expr state." + next().position + " symbol: "+next().lexeme);
       }
         return r;
    }

    private AbsExpr expr2(AbsExpr t1) {
        Symbol si;
        AbsExpr r = null;
       switch(next().token){
           case Token.SEMIC:
           case Token.COLON:
           case Token.RPARENT:
           case Token.ASSIGN:
           case Token.RBRACKET:
           case Token.COMMA:
           case Token.RBRACE:
           case Token.KW_THEN:
           case Token.KW_ELSE:
           case Token.EOF:
            dump("expr'' -> € ");
            return t1;
           case Token.LBRACE:
               dump("expr'' -> { where defs } ");
               si = skip(new Symbol(Token.LBRACE,"",0,0,0,0));
               skip(new Symbol(Token.KW_WHERE,"",0,0,0,0));
               
               AbsDefs d = defs();
               
               skip(new Symbol(Token.RBRACE,"",0,0,0,0));
               
               r = new AbsWhere(si.position,t1,d);
               break;
           default:
                Report.error("Syntax error in expr'' state." + next().position + " symbol: "+next().lexeme);
       }
       return r;
    }

    private AbsExpr log_and_expr() {
        AbsExpr r = null;
         switch(next().token){
           case Token.IDENTIFIER:
           case Token.LPARENT:
           case Token.LBRACE:
           case Token.ADD:
           case Token.SUB:
           case Token.NOT:
           case Token.LOG_CONST:
           case Token.INT_CONST:
           case Token.STR_CONST:
               dump("log_and_expr -> cmp_expr log_and_expr' ");
               r = cmp_expr();
               r = log_and_expr1(r);
               return r;
            default:
                Report.error("Syntax error in log_and_expr state." + next().position + " symbol: "+next().lexeme);
       }
         return r;
    }

    private AbsExpr log_ior_expr1(AbsExpr t1) {
       Symbol si;
       AbsExpr r = null;
       switch(next().token){
           case Token.SEMIC:
           case Token.COLON:
           case Token.RPARENT:
           case Token.ASSIGN:
           case Token.RBRACKET:
           case Token.COMMA:
           case Token.LBRACE:
           case Token.RBRACE:
           case Token.KW_THEN:
           case Token.KW_ELSE:
           case Token.EOF:
               dump("log_ior_expr' -> € ");
               return t1;
               //break;
           case Token.IOR:
               dump("log_ior_expr' -> | log_and_expr log_ior_expr' ");
               si = skip(new Symbol(Token.IOR,"",0,0,0,0));
               
               AbsExpr t2 = log_and_expr();
               r=new AbsBinExpr(si.position,AbsBinExpr.IOR,t1,t2);
               r=log_ior_expr1(r);
               break;
            default:
                Report.error("Syntax error in log_ior_expr' state." + next().position + " symbol: "+next().lexeme);
       
       }
       return r;
    }

    private AbsExpr cmp_expr() {
        AbsExpr r = null;
       switch(next().token){
           case Token.IDENTIFIER:
           case Token.LPARENT:
           case Token.LBRACE:
           case Token.ADD:
           case Token.SUB:
           case Token.NOT:
           case Token.LOG_CONST:
           case Token.INT_CONST:
           case Token.STR_CONST:
               dump("cmp_expr -> add_expr cmp_expr' ");
               r = add_expr();
               r = cmp_expr1(r);
               break;
           default:
                Report.error("Syntax error in cmp_expr state." + next().position + " symbol: "+next().lexeme);
       
       }
       return r;
    }

    private AbsExpr log_and_expr1(AbsExpr t1) {
       Symbol si;
       AbsExpr r = null;
       switch(next().token){
           case Token.SEMIC:
           case Token.COLON:
           case Token.RPARENT:
           case Token.ASSIGN:
           case Token.RBRACKET:
           case Token.COMMA:
           case Token.LBRACE:
           case Token.RBRACE:
           case Token.IOR:
           case Token.KW_THEN:
           case Token.KW_ELSE:
           case Token.EOF:
               dump("log_and_expr' -> € ");
               return t1;
               //break;
           case Token.AND:
               dump("log_and_expr' -> & cmd_expr log_and_expr' ");
               si = skip(new Symbol(Token.AND,"",0,0,0,0));
               
               AbsExpr t2 = cmp_expr();
               r = new AbsBinExpr(si.position,AbsBinExpr.AND,t1,t2);
               r = log_and_expr1(r);
               break;
            default:
                Report.error("Syntax error in log_and_expr1 state." + next().position + " symbol: "+next().lexeme);
               
       }
       return r;
    }

    private AbsExpr add_expr() {
        AbsExpr r = null;
       switch(next().token){
           case Token.IDENTIFIER:
           case Token.LPARENT:
           case Token.LBRACE:
           case Token.ADD:
           case Token.SUB:
           case Token.NOT:
           case Token.INT_CONST:
           case Token.STR_CONST:
           case Token.LOG_CONST:
               dump("add_expr -> mul_expr add_expr' ");
               r = mul_expr();
               r = add_expr1(r);
               break;
           default:
                Report.error("Syntax error in add_expr state." + next().position + " symbol: "+next().lexeme);
       }
       return r;
    }

    private AbsExpr cmp_expr1(AbsExpr t1) {
        Symbol si;
        AbsExpr r = null;
        switch(next().token){
            case Token.KW_THEN:
            case Token.KW_ELSE:
            case Token.EOF:
            case Token.AND:
            case Token.IOR:
            case Token.RBRACE:
            case Token.LBRACE:
            case Token.COMMA:
            case Token.RBRACKET:
            case Token.RPARENT:
            case Token.ASSIGN:
            case Token.COLON:
            case Token.SEMIC:
                dump("cmp_expr' -> € ");
                return t1;
            case Token.EQU:
                dump("cmp_expr' -> == add_expr");
                si = skip(new Symbol(Token.EQU,"",0,0,0,0));
                AbsExpr t2 = add_expr();
                r = new AbsBinExpr(si.position,AbsBinExpr.EQU,t1,t2);
                break;
            case Token.NEQ:
                dump("cmp_expr' -> != add_expr");
                si = skip(new Symbol(Token.NEQ,"",0,0,0,0));
                t2 = add_expr();
                r = new AbsBinExpr(si.position,AbsBinExpr.NEQ,t1,t2);
                break;
            case Token.GEQ:
                dump("cmp_expr' -> >= add_expr");
                si = skip(new Symbol(Token.GEQ,"",0,0,0,0));
                t2 = add_expr();
                r = new AbsBinExpr(si.position,AbsBinExpr.GEQ,t1,t2);
                break;
            case Token.LEQ:
                dump("cmp_expr' -> <= add_expr");
                si = skip(new Symbol(Token.LEQ,"",0,0,0,0));
                t2 = add_expr();
                r = new AbsBinExpr(si.position,AbsBinExpr.LEQ,t1,t2);
                break;
            case Token.LTH:
                dump("cmp_expr' -> < add_expr");
                si = skip(new Symbol(Token.LTH,"",0,0,0,0));
                t2 = add_expr();
                r = new AbsBinExpr(si.position,AbsBinExpr.LTH,t1,t2);
                break;
            case Token.GTH:
                dump("cmp_Expr' -> > add_expr");
                si = skip(new Symbol(Token.GTH,"",0,0,0,0));
                t2 = add_expr();
                r = new AbsBinExpr(si.position,AbsBinExpr.GTH,t1,t2);//NOTE - MIGHT BE WRONG! TODO
                break;
            default:
                Report.error("Syntax error in cmp_expr' state." + next().position + " symbol: "+next().lexeme);
                
            
        }
        return r;
    }

    private AbsExpr mul_expr() {
        AbsExpr r = null;
         switch(next().token){
           case Token.IDENTIFIER:
           case Token.LPARENT:
           case Token.LBRACE:
           case Token.ADD:
           case Token.SUB:
           case Token.NOT:
           case Token.INT_CONST:
           case Token.STR_CONST:
           case Token.LOG_CONST:
               dump("mul_expr -> prefix_expr mul_expr' ");
               r = prefix_expr();
               r = mul_expr1(r);
               break;
           default:
                Report.error("Syntax error in mul_expr state." + next().position + " symbol: "+next().lexeme);
       }
         return r;
    }

    private AbsExpr add_expr1(AbsExpr t1) {
        Symbol si;
        AbsExpr r = null;
        switch(next().token){
            case Token.SEMIC:
            case Token.COLON:
            case Token.RPARENT:
            case Token.ASSIGN:
            case Token.RBRACKET:
            case Token.COMMA:
            case Token.LBRACE:
            case Token.RBRACE:
            case Token.IOR:
            case Token.AND:
            case Token.NEQ:
            case Token.EQU:
            case Token.GEQ:
            case Token.LEQ:
            case Token.GTH:
            case Token.LTH:
            case Token.KW_THEN:
            case Token.KW_ELSE:
            case Token.EOF:
                dump("add_expr' -> € ");
                return t1;
                //break;
            case Token.ADD:
                dump("add_expr' -> + mul_expr add_expr'");
                si = skip(new Symbol(Token.ADD,"",0,0,0,0));
                 AbsExpr t2 = mul_expr();
                 r = new AbsBinExpr(si.position,AbsBinExpr.ADD,t1,t2);
                 r = add_expr1(r);
                break;
            case Token.SUB:
                dump("add_expr' -> - mul_expr add_expr'");
                si = skip(new Symbol(Token.SUB,"",0,0,0,0));
                t2 = mul_expr();
                r = new AbsBinExpr(si.position,AbsBinExpr.SUB,t1,t2);
                r = add_expr1(r);
                break;
            default:
                 Report.error("Syntax error in add_expr' state." + next().position + " symbol: "+next().lexeme);
                
        
        }
        return r;
    }

    private AbsExpr prefix_expr() {
        AbsExpr r = null;
        Symbol si;
         switch(next().token){
           case Token.IDENTIFIER:
           case Token.LPARENT:
           case Token.LBRACE:
           case Token.INT_CONST:
           case Token.STR_CONST:
           case Token.LOG_CONST:
               dump("prefix_expr -> postfix_expr ");
               r = postfix_expr();
               break;
           case Token.ADD:
               si=next();
               dump("prefix_expr -> + prefix_expr");
               skip(new Symbol(Token.ADD,"",0,0,0,0));
               r = prefix_expr();
               r = new AbsUnExpr(si.position,AbsUnExpr.ADD,r);
               break;
           case Token.SUB:
               si=next();
               dump("prefix_expr -> - prefix_expr");
               skip(new Symbol(Token.SUB,"",0,0,0,0));
               r = prefix_expr();
               r = new AbsUnExpr(si.position,AbsUnExpr.SUB,r);
               break;
           case Token.NOT:
               si=next();
               dump("prefix_expr -> ! prefix_expr");
               skip(new Symbol(Token.NOT,"",0,0,0,0));
               r = prefix_expr();
               r = new AbsUnExpr(si.position,AbsUnExpr.NOT,r);
               break;
               
           default:
                Report.error("Syntax error in prefix_expr state." + next().position + " symbol: "+next().lexeme);
       }
         return r;
    }

    private AbsExpr mul_expr1(AbsExpr t1) {
       AbsExpr r = null;
       AbsExpr t2;
       Symbol si;
       switch(next().token){
           case Token.COLON:
           case Token.SEMIC:
           case Token.RPARENT:
           case Token.ASSIGN:
           case Token.RBRACKET:
           case Token.COMMA:
           case Token.LBRACE:
           case Token.RBRACE:
           case Token.IOR:
           case Token.AND:
           case Token.EQU:
           case Token.NEQ:
           case Token.GEQ:
           case Token.GTH:
           case Token.LTH:
           case Token.LEQ:
           case Token.ADD:
           case Token.SUB:
           case Token.KW_THEN:
           case Token.KW_ELSE:
           case Token.EOF:
               dump("mul_expr' -> € ");
               return t1;
               //break;
           case Token.MUL:
               dump("mul_expr' -> * prefix_expr mul_expr'");
               si = skip(new Symbol(Token.MUL,"",0,0,0,0));
               t2 = prefix_expr();
               r = new AbsBinExpr(si.position,AbsBinExpr.MUL,t1,t2);
               r = mul_expr1(r);
               break;
           case Token.MOD:
               dump("mul_expr' -> % prefix_expr mul_expr'");
               si = skip(new Symbol(Token.MOD,"",0,0,0,0));
               t2 = prefix_expr();
               r = new AbsBinExpr(si.position,AbsBinExpr.MOD,t1,t2);
               r = mul_expr1(r);
               break;
           case Token.DIV:
               dump("mul_expr' -> / prefix_expr mul_expr'");
               si = skip(new Symbol(Token.DIV,"",0,0,0,0));
               t2 = prefix_expr();
               r = new AbsBinExpr(si.position,AbsBinExpr.DIV,t1,t2);
               r = mul_expr1(r);
               break;
           default:
               Report.error("Syntax error in mul_expr' state." + next().position + " symbol: "+next().lexeme);
       }
       return r;
    }

    private AbsExpr postfix_expr() {
        AbsExpr r = null;
        Symbol si;
        switch(next().token){
            case Token.IDENTIFIER:
            case Token.LPARENT:
            case Token.LBRACE:
            case Token.LOG_CONST:
            case Token.INT_CONST:
            case Token.STR_CONST:
                dump("postfix_expr -> atom_expr postfix_expr'");
                //r = atom_expr();
                //r = postfix_expr1();
                //v.add(0,t);
                //return new AbsExprs(new Position(t.position, v.get(v.size()-1).position), v);
                r=atom_expr();
                r=postfix_expr1(r);
                break;
            default:
                Report.error("Syntax error in postfix_expr state." + next().position + " symbol: "+next().lexeme);
        
        }
        return r;
    }

    private AbsExpr atom_expr() {
        AbsExpr r = null;
        Symbol si;
        Symbol si2;
        switch(next().token){
            case Token.IDENTIFIER:
                dump("atom_expr -> id atom_expr'");
                si = skip(new Symbol(Token.IDENTIFIER,"",0,0,0,0));
                r = atom_expr1(si);
                break;
            case Token.LPARENT:
                dump("atom_expr -> ( exprs )");
                
                si = skip(new Symbol(Token.LPARENT,"",0,0,0,0));
                
                Vector<AbsExpr> e = exprs();
                
                si2 = skip(new Symbol(Token.RPARENT,"",0,0,0,0));
                
                r = new AbsExprs(new Position(si.position,next().position),e);
                break;
            case Token.LBRACE:
                dump("atom_expr -> { atom_expr''");
                skip(new Symbol(Token.LBRACE,"",0,0,0,0));
                si=next();
                r = atom_expr2(si);
                break;
            case Token.INT_CONST:
                dump("atom_expr -> int_constant");
                r = new AbsAtomConst(next().position,AbsAtomConst.INT,next().lexeme);
                skip(new Symbol(Token.INT_CONST,"",0,0,0,0));
                break;
            case Token.STR_CONST:
                dump("atom_expr -> str_constant");
                 r = new AbsAtomConst(next().position,AbsAtomConst.STR,next().lexeme);
                skip(new Symbol(Token.STR_CONST,"",0,0,0,0));
                break;
            case Token.LOG_CONST:
                dump("atom_expr -> log_constant");
                r = new AbsAtomConst(next().position,AbsAtomConst.LOG,next().lexeme);
                skip(new Symbol(Token.LOG_CONST,"",0,0,0,0));
                break;
            default:
                Report.error("Syntax error in atom_expr state." + next().position + " symbol: "+next().lexeme);
        
        }
        return r;
    }

    private AbsExpr postfix_expr1(AbsExpr t1) {
        AbsExpr r = null;
        Symbol si;
        switch(next().token){
           case Token.COLON:
           case Token.SEMIC:
           case Token.RPARENT:
           case Token.ASSIGN:
           case Token.RBRACKET:
           case Token.COMMA:
           case Token.LBRACE:
           case Token.RBRACE:
           case Token.IOR:
           case Token.AND:
           case Token.EQU:
           case Token.NEQ:
           case Token.GEQ:
           case Token.GTH:
           case Token.LTH:
           case Token.LEQ:
           case Token.ADD:
           case Token.SUB:
           case Token.MUL:
           case Token.MOD:
           case Token.DIV:
           case Token.KW_THEN:
           case Token.KW_ELSE:
           case Token.EOF:
               dump("postfix_expr' -> € ");
               return t1;
               //break;
           case Token.LBRACKET:
               dump("postfix_expr' -> [ expr ] postfix_expr'");
               si = skip(new Symbol(Token.LBRACKET,"",0,0,0,0));
               
               AbsExpr t2 = expr();
               
               skip(new Symbol(Token.RBRACKET,"",0,0,0,0));
               
               r = new AbsBinExpr(si.position,AbsBinExpr.ARR,t1,t2);
               r = postfix_expr1(r);
               break;
           default:
               Report.error("Syntax error in postfix_expr' state." + next().position + " symbol: "+next().lexeme);
        
        }
        return r;
    }

    private AbsExpr atom_expr1(Symbol si) {
        AbsExpr r = null;
       switch(next().token){
           case Token.LBRACKET:
           case Token.COLON:
           case Token.SEMIC:
           case Token.RPARENT:
           case Token.ASSIGN:
           case Token.RBRACKET:
           case Token.COMMA:
           case Token.LBRACE:
           case Token.RBRACE:
           case Token.IOR:
           case Token.AND:
           case Token.EQU:
           case Token.NEQ:
           case Token.GEQ:
           case Token.GTH:
           case Token.LTH:
           case Token.LEQ:
           case Token.ADD:
           case Token.SUB:
           case Token.MUL:
           case Token.MOD:
           case Token.DIV:
           case Token.KW_THEN:
           case Token.KW_ELSE:
           case Token.EOF:
               dump("atom_expr' -> € ");
               r = new AbsVarName(si.position,si.lexeme);
               break;
           case Token.LPARENT:
               dump("atom_expr' -> ( exprs )");
               skip(new Symbol(Token.LPARENT,"",0,0,0,0));
               Vector<AbsExpr> e = exprs();
				
               Position pos=new Position(si.position,next().position);
				
               r = new AbsFunCall(pos,si.lexeme,e);
               skip(new Symbol(Token.RPARENT,"",0,0,0,0));
               break;
           default:
               Report.error("Syntax error in atom_expr' state." + next().position + " symbol: "+next().lexeme);
               
       
       }
       return r;
    }

    private Vector<AbsExpr> exprs() {
       Vector<AbsExpr> r = new Vector<AbsExpr>();
       Symbol si;
       switch(next().token){
           case Token.IDENTIFIER:
           case Token.LPARENT:
           case Token.LBRACE:
           case Token.ADD:
           case Token.SUB:
           case Token.NOT:
           case Token.STR_CONST:
           case Token.INT_CONST:
           case Token.LOG_CONST:
               dump("exprs -> expr exprs'");
               r.add(expr());
               r=exprs1(r);
               break;
           default:
               Report.error("Syntax error in exprs state." + next().position + " symbol: "+next().lexeme);
       }
       return r;
    }

    private AbsExpr atom_expr2(Symbol si) {
        AbsExpr r = null;
        Symbol si2;
        switch(next().token){
            case Token.IDENTIFIER:
            case Token.LPARENT:
            case Token.LBRACE:
            case Token.ADD:
            case Token.SUB:
            case Token.NOT:
            case Token.STR_CONST:
            case Token.INT_CONST:
            case Token.LOG_CONST:
                si=next();
                dump("atom_expr'' -> expr = expr }");
                AbsExpr t1 = expr();
                skip(new Symbol(Token.ASSIGN,"",0,0,0,0));
                AbsExpr t2 = expr();
                skip(new Symbol(Token.RBRACE,"",0,0,0,0));
                r = new AbsBinExpr(new Position(si.position,next().position),15,t1,t2);
                break;
            case Token.KW_IF:
                dump("atom_expr'' -> if expr then expr atom_expr'''");
                si = skip(new Symbol(Token.KW_IF,"",0,0,0,0));
                t1 = expr();
                skip(new Symbol(Token.KW_THEN,"",0,0,0,0));
                t2 = expr();
                AbsIfThen e =  new AbsIfThen(si.position,t1,t2);
                r = atom_expr3(e);
                break;
            case Token.KW_WHILE:
                dump("atom_expr'' -> while expr : expr }");
                si = skip(new Symbol(Token.KW_WHILE,"",0,0,0,0));
                t1 = expr();
                skip(new Symbol(Token.COLON,"",0,0,0,0));
                t2 = expr();
                skip(new Symbol(Token.RBRACE,"",0,0,0,0));
                r = new AbsWhile(si.position,t1,t2);
                break;
            case Token.KW_FOR:
                dump("atom_expr'' → for id = expr , expr , expr : expr }");
                si = skip(new Symbol(Token.KW_FOR,"",0,0,0,0));
                AbsVarName count = new AbsVarName(next().position,next().lexeme);
                si = skip(new Symbol(Token.IDENTIFIER,"",0,0,0,0));
                skip(new Symbol(Token.ASSIGN,"",0,0,0,0));
                t1 = expr();
                skip(new Symbol(Token.COMMA,"",0,0,0,0));
                t2 = expr();
                skip(new Symbol(Token.COMMA,"",0,0,0,0));
                AbsExpr t3 = expr();
                skip(new Symbol(Token.COLON,"",0,0,0,0));
                AbsExpr t4 = expr();
                skip(new Symbol(Token.RBRACE,"",0,0,0,0));
                r = new AbsFor(si.position,count,t1,t2,t3,t4);
                break;
            default:
                Report.error("Syntax error in atom_expr2 state." + next().position + " symbol: "+next().lexeme);
        
        }
        return r;
    }

    private Vector<AbsExpr> exprs1(Vector<AbsExpr> r) {
       switch(next().token){
           case Token.RPARENT:
               dump("exprs' -> €");
               break;
           case Token.COMMA:
               dump("exprs' -> , expr exprs'");
               skip(new Symbol(Token.COMMA,"",0,0,0,0));
               AbsExpr e = expr();
               r.add(e);
               r = exprs1(r);
               break;
           default:
                Report.error("Syntax error in exprs' state." + next().position + " symbol: "+next().lexeme);
       }
       return r;
    }

    private AbsExpr atom_expr3(AbsIfThen e) {
        AbsExpr r = null;
        Symbol si;
        switch(next().token){
            case Token.RBRACE:
                dump("atom_expr''' -> }");
                skip(new Symbol(Token.RBRACE,"",0,0,0,0));
                r = e;
                break;
            case Token.KW_ELSE:
                dump("atom_expr''' -> else expr }"); // might be wrongooo coz of }
                si = skip(new Symbol(Token.KW_ELSE,"",0,0,0,0));
                AbsExpr t1 = expr();
                skip(new Symbol(Token.RBRACE,"",0,0,0,0));
                r = new AbsIfThenElse(si.position,e.cond,e.thenBody,t1);
                break;
            default:
                Report.error("Syntax error in atom_expr''' state." + next().position + " symbol: "+next().lexeme);
        
        }
        return r;
    }
}
