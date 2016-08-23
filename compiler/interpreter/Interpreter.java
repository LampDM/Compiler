package compiler.interpreter;

import java.util.*;

import compiler.*;
import compiler.frames.*;
import compiler.imcode.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



public class Interpreter {

	public static boolean debug = true;
        public  ImcCodeGen imcodegen;
	/*--- staticni del navideznega stroja ---*/
	
	/** Pomnilnik navideznega stroja. */
	public static HashMap<Integer, Object> mems = new HashMap<Integer, Object>();
	
        /** Preslikava label globalnih spremenljivk in konstant v naslove.  */
	public HashMap<String,Integer> globals = new HashMap<String,Integer>();
        
	public static void stM(Integer address, Object value) {
		if (debug) System.out.println(" [" + address.toString() + "] <= " + value);
		mems.put(address, value);
	}

	public static Object ldM(Integer address) {
		Object value = mems.get(address);
		if (debug) System.out.println(" [" + address + "] => " + value);
		return value;
	}
	
	/** Kazalec na vrh klicnega zapisa. */
	private static int fp = 60000;

	/** Kazalec na dno klicnega zapisa. */
	private static int sp = 60000;
        
	/** Kazalec na kopico. */
        private static int hp = 1000;
        
        
	/*--- dinamicni del navideznega stroja ---*/
	
	/** Zacasne spremenljivke (`registri') navideznega stroja. */
	public HashMap<FrmTemp, Object> temps = new HashMap<FrmTemp, Object>();
                
    public Interpreter(ImcCodeGen imcodegen) {
        
        this.imcodegen=imcodegen;
        
        /* Linearization */
        for(int k = 0;k<this.imcodegen.chunks.size();k++){
            ImcChunk ch = this.imcodegen.chunks.get(k);
            if(ch instanceof ImcCodeChunk){
                ImcCodeChunk c = (ImcCodeChunk)ch;
                c.lincode=(ImcStmt)lin(c.imcode);
                this.imcodegen.chunks.set(k, c);
                
            }else
            /* Give addresses to static variables */
            if(this.imcodegen.chunks.get(k) instanceof ImcDataChunk){
                
                ImcDataChunk dataChunk = (ImcDataChunk)ch;
		this.globals.put(dataChunk.label.name(), hp);
		hp = hp + dataChunk.size;
            }
        }
        
        execFun("_main");
        
        
    }
    
    	private ImcCode lin (ImcCode node) {
		if ( node instanceof ImcStmt ) 
			return ((ImcStmt) node).linear();
		if ( node instanceof ImcExpr)
			return ((ImcExpr) node).linear();
		return null;
	}
        

        private void execFun(String name){

                ImcCodeChunk c = (ImcCodeChunk)this.imcodegen.getChunk(name);
                FrmFrame frame = c.frame;
                
                ImcSEQ code = c.lincode.linear();
                
                HashMap<FrmTemp,Object> outerTemps = temps;
		temps = new HashMap<FrmTemp,Object>();
                
                
                //int localfp=fp;
                //int localsp=sp;
                
		if (debug) {
			System.out.println("[START OF " + frame.label.name() + "]");
		}

                //Prolog
		stM(sp - frame.sizeLocs - 4 , fp);
		fp = sp;
		sp = sp - frame.size();
               
                stT(frame.FP, fp);
                

                
                //First argument of main is zero by default
                if(name.equals("_main")){
                    stM(fp+4,0);
                }

                
		if (debug) {
			System.out.println("[FP=" + fp + "]");
			System.out.println("[SP=" + sp + "]");
		}
                
                //Jedro
		int pc = 0;
		Object result = null;

		while (pc < code.stmts.size()) {
			if (debug) System.out.println("pc=" + pc);
			ImcCode instruction = code.stmts.get(pc);
                        
                        
			result = execute(instruction);
			if (result instanceof ImcLABEL) {
                            
				for (pc = 0; pc < code.stmts.size(); pc++) {
					instruction = code.stmts.get(pc);
                                        if(instruction instanceof ImcLABEL){
                                            ImcLABEL il = (ImcLABEL) instruction;
                                            ImcLABEL rl = (ImcLABEL) result;
        
                                            if(il.label.name().equals(rl.label.name())){
                                                break;
                                            }

                                        }

				}
			}
                        else{
                            pc++;
                        }
				
		}
                
                //fp=localfp;
                //sp = localsp;
                
		//Epilog
                sp=sp+frame.size();
                fp = (Integer) ldM(sp - frame.sizeLocs - 4 );

		if (debug) {
			System.out.println("[FP=" + fp + "]");
			System.out.println("[SP=" + sp + "]");
		}
		
		
                
		if (debug) {
			System.out.println("[RV=" + result + "]");
		}
                
                stM(sp+4, result);
                
		if (debug) {
			System.out.println("[END OF " + frame.label.name() + "]");
		}
                
                temps = outerTemps;
        }
		
	public void stT(FrmTemp temp, Object value) {
		if (debug) System.out.println(" " + temp.name() + " <= " + value);
		temps.put(temp, value);
	}
        
	public Object ldT(FrmTemp temp) {
		Object value = temps.get(temp);
		if (debug) System.out.println(" " + temp.name() + " => " + value);
		return value;
	}
	
	/*--- Izvajanje navideznega stroja. ---*/
	
	public Object execute(ImcCode instruction) {
		if (instruction instanceof ImcBINOP) {
			ImcBINOP instr = (ImcBINOP) instruction;
			Object leftValue = execute(instr.limc);
			Object rightValue = execute(instr.rimc);

			switch (instr.op) {
			case ImcBINOP.IOR:
				return ((((Integer) leftValue).intValue() != 0) || (((Integer) rightValue).intValue() != 0) ? 1 : 0);
			case ImcBINOP.AND:
				return ((((Integer) leftValue).intValue() != 0) && (((Integer) rightValue).intValue() != 0) ? 1 : 0);
			case ImcBINOP.EQU:
				return (((Integer) leftValue).intValue() == ((Integer) rightValue).intValue() ? 1 : 0);
			case ImcBINOP.NEQ:
				return (((Integer) leftValue).intValue() != ((Integer) rightValue).intValue() ? 1 : 0);
			case ImcBINOP.LTH:
				return (((Integer) leftValue).intValue() < ((Integer) rightValue).intValue() ? 1 : 0);
			case ImcBINOP.GTH:
				return (((Integer) leftValue).intValue() > ((Integer) rightValue).intValue() ? 1 : 0);
			case ImcBINOP.LEQ:
				return (((Integer) leftValue).intValue() <= ((Integer) rightValue).intValue() ? 1 : 0);
			case ImcBINOP.GEQ:
				return (((Integer) leftValue).intValue() >= ((Integer) rightValue).intValue() ? 1 : 0);
			case ImcBINOP.ADD:
				return (((Integer) leftValue).intValue() + ((Integer) rightValue).intValue());
			case ImcBINOP.SUB:
				return (((Integer) leftValue).intValue() - ((Integer) rightValue).intValue());
			case ImcBINOP.MUL:
				return (((Integer) leftValue).intValue() * ((Integer) rightValue).intValue());
			case ImcBINOP.DIV:
				return (((Integer) leftValue).intValue() / ((Integer) rightValue).intValue());
			case ImcBINOP.MOD:
				return (((Integer) leftValue).intValue() % ((Integer) rightValue).intValue());

			}
			Report.error("Internal error - BINOP Interpreting phase!");
			return null;
		}
		
		if (instruction instanceof ImcCALL) {
			ImcCALL instr = (ImcCALL) instruction;
                        
			int offset = 0;
                        
			for (ImcCode arg : instr.args) {
                            
				stM(sp + offset, execute(arg));
				offset += 4;
			}
                        
                        // output functions
			if ( instr.label.name().equals("_outInteger") || 
					instr.label.name().equals("_outString")) {
				
				System.out.println( ldM(sp + 4) );
				return null;
			}
                        
			// input functions
			if ( instr.label.name().equals("_inputInteger" ) ) {
				Scanner sc = new Scanner(System.in);
				int value = sc.nextInt();
				sc.close();
				return value;
			}
                        
			if ( instr.label.name().equals("_inputString")) {
				Scanner sc = new Scanner(System.in);
				String value = sc.next();
				sc.close();
				return value;
			}
                        
                        execFun(instr.label.name());
			return ldM(sp+4);
		}
		
		if (instruction instanceof ImcCJUMP) {
                        
			ImcCJUMP instr = (ImcCJUMP) instruction;
			Object cond = execute(instr.cond);
			if (cond instanceof Integer) {
				if (((Integer) cond).intValue() != 0){
                                    return new ImcLABEL(instr.trueLabel);
                                
                                }else{

                                    return new ImcLABEL(instr.falseLabel);
                                }
					
			}
			else Report.error("CJUMP: illegal condition type.");
		}
		
		if (instruction instanceof ImcCONST) {
			ImcCONST instr = (ImcCONST) instruction;
			return new Integer(instr.value);
		}
		
//		if (instruction instanceof ImCONSTr) {
//			ImCONSTr instr = (ImCONSTr) instruction;
//			return new Float(instr.realValue);
//		}
//		
//		if (instruction instanceof ImcCONSTs) {
//			ImCONSTs instr = (ImCONSTs) instruction;
//			return new String(instr.stringValue);
//		}
		
		if (instruction instanceof ImcJUMP) {              
			ImcJUMP instr = (ImcJUMP) instruction;
			return new ImcLABEL(instr.label);
		}
		
		if (instruction instanceof ImcLABEL) {
                    return null;
                    
		}
		
		if (instruction instanceof ImcMEM) {
			ImcMEM instr = (ImcMEM) instruction;
			return ldM((Integer) execute(instr.expr));
		}
		
		if (instruction instanceof ImcMOVE) {
                        
			ImcMOVE instr = (ImcMOVE) instruction;
                        
			if (instr.dst instanceof ImcTEMP) {
                            
				FrmTemp temp = ((ImcTEMP) instr.dst).temp;

				Object srcValue = execute(instr.src);

				stT(temp, srcValue);
				return srcValue;
			}

			if (instr.dst instanceof ImcMEM) {
				Object dstValue = execute(((ImcMEM) instr.dst).expr);
				Object srcValue = execute(instr.src);
				stM((Integer) dstValue, srcValue);
				return srcValue;
			}
		}

		if (instruction instanceof ImcNAME) {
			ImcNAME instr = (ImcNAME) instruction;
                        
                        ImcDataChunk dc = (ImcDataChunk)imcodegen.getChunk(instr.label.name());
                        
			if (instr.label.name().equals("FP")) return fp;
			if (instr.label.name().equals("SP")) return sp;

                        return this.globals.get(dc.label.name());
		}
		
		if (instruction instanceof ImcTEMP) {
                        
			ImcTEMP instr = (ImcTEMP) instruction;
			return ldT(instr.temp);
		}
		
		return null;
	}
	
	
}