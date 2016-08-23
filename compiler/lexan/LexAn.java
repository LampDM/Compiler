package compiler.lexan;

import compiler.*;
import java.io.*;
import java.util.LinkedList;
/**
 * Leksikalni analizator.
 * 
 * @author sliva
 */   
public class LexAn {
	
	/** Ali se izpisujejo vmesni rezultati. */
	public boolean dump;
        public String source;
        public int position;
        public int line;
        public int charsRead;
        public String currentSymbol;
        public String phase;
        public String prevphase;
        public LinkedList<Symbol> symbols;
        public Reader buffer;
        public String[] keywords;
        public String[] datatypes;
        public String[] others;
        public LinkedList<Symbol> flushList;
        public int a;
	/**
	 * Ustvari nov leksikalni analizator.
	 * 
	 * @param sourceFileName
	 *            Ime izvorne datoteke.
	 * @param dump
	 *            Ali se izpisujejo vmesni rezultati.
	 */
	public LexAn(String sourceFileName, boolean dump) {
		this.source = sourceFileName;
		this.dump = dump;
                this.keywords = new String[]{"arr", "else", "for", "fun", "if", "then", "typ", "var" ,"where" ,"while"};
                this.datatypes = new String[]{"logical", "integer", "string"};
                this.others= new String[]{"+", "-", "*", "/", "%", "&", "|", "!", "==", "!=", "<", ">", "<=", ">=", "(", ")", "[", "]", "{", "}", ":", ";", ".", ",", "="};
                this.charsRead=-1; //Total chars already read of file
                this.position=-1; //Current row position of lexer
                this.line=0; //Current line of lexer
                this.currentSymbol=""; //Current symbol being analyzed
                this.phase="waiting"; //Current phase - waiting - processing - processingint - processingstr - processingstr2 - comment - other
                this.prevphase="waiting";//Legacy - not being used
                this.flushList= new LinkedList();//Used to return leftover others
                try{
                       InputStream in = new FileInputStream(this.source);
                       Reader reader = new InputStreamReader(in);
                       this.buffer= new BufferedReader(reader);
                       
                }catch(Exception exc){}
             
                
	}
	
	/**
	 * Vrne naslednji simbol iz izvorne datoteke. Preden vrne simbol, ga izpise
	 * na datoteko z vmesnimi rezultati.
	 * 
	 * @return Naslednji simbol iz izvorne datoteke.
	 */
	public Symbol lexAn()  {
            
           try{
               start1:
                while(true){
                    
                
                if(this.flushList.isEmpty()){    
                    a=buffer.read();
                
                //POSITION AND ROW CALCULATION
                this.position++;
                this.charsRead++;
                this.position= a==10 ? 0 : this.position;
                this.line= a==10 ? this.line+1 : this.line;
                //POSITION AND ROW CALCULATION - END
                }else{

                        Symbol si = flushList.removeFirst();
                        dump(si);
                        a=32;
                        return si ;//Any previous returns will be processed + - / etc.
                }
                   
                
               
                start2:
                   while(true){
                       
                      
                       
                //COMMENT PROCESSING
                if(a==35 && !(this.phase.equals("processingstr")||this.phase.equals("processingstr2"))){//Start of comment
                    this.phase="comment";
                    continue start1;
                }else
                    
                if(this.phase.equals("comment") && (a==10 || a==-1) ){//End of comment
                    this.phase="waiting";
                }else
                    
                if(this.phase.equals("comment")){//Processing comment
                    continue start1;
                }else
                    
                if(false){//In case more options are needed
                
                }else
                //COMMENT PROCESSING END    
                    
                //RESERVED WORD PROCESSING
                if((Character.isLetter((char)a) || a==95) && this.phase.equals("waiting")){ //If is letter or underscore, phase waiting
                    this.currentSymbol=this.currentSymbol+(char)a;
                    this.phase="processing";
                    continue start1;
                }else
                
                if(((Character.isLetter((char)a)) || Character.isDigit((char)a) || a==95) && this.phase.equals("processing")){ //If is letter or digit or underscore, phase processing
                    this.currentSymbol=this.currentSymbol+(char)a;
                    continue start1;
                }else
                
                if((a<=32) && this.phase.equals("processing")){ //If is whitespace, phase processing
                    
                    this.phase="waiting";
                    //System.out.println( "Symbol: "+this.currentSymbol + " Line: "+(this.line+1) + " EndPosition: "+this.position+" StartPosition: "+(this.position-this.currentSymbol.length()) );

                    return IsReservedWord(this);
                }else
                //RESERVED WORD PROCESSING - END
                    
                //INTEGER PROCESSING 
                if((Character.isDigit((char)a) ) && this.phase.equals("waiting")){ //If we find a number - could be Integer
                    this.phase="processingint";
                    this.currentSymbol=this.currentSymbol+(char)a;
                    continue start1;
                }else
                    
                if(Character.isDigit((char)a) && this.phase.equals("processingint") ){ //If we find another digit in processingint mode 
                    if(this.currentSymbol.equals("0") && this.currentSymbol.charAt(0)==(char)48){ // If we find a digit after a zero ex. 0001234 
                    }else{
                        this.currentSymbol=this.currentSymbol+(char)a;
                        continue start1;
                    }
                   
                }else
                if((a<=32) && this.phase.equals("processingint")){ //If we hit whitespace when in processingint mode
                    
                    return IsInteger(this); //If we found an integer
                }else
                //INTEGER PROCESSING - END
                    
                //PRIMARY STRING PROCESSING ( 'string' )
                 if(a==39 && this.phase.equals("waiting")){//If we hit ' begining of string in waiting mode 
                    this.phase="processingstr";
                    this.currentSymbol=this.currentSymbol+(char)a;
                    continue start1; 
                 }else
                     
                 if(this.phase.equals("processingstr") && a==39)//If we hit ' end of string or something else
                 {//Check if is actually not end
                    this.buffer.mark(5);//Set return point
                    if(this.buffer.read()==39){//If we want ' in string we write '' 
                        this.currentSymbol=this.currentSymbol+(char)a+(char)a;//Fixed to display '' in string
                        continue start1;
                    }else{
                        this.buffer.reset();
                        this.currentSymbol=this.currentSymbol+(char)a;
                        return IsString(this);
                    }
                    
                 }else
                     
                 if(this.phase.equals("processingstr")){ //If we're just processing a string
                     if(a==-1){//If we reach end of file without ' throw error
                          Report.warning(this.line+1, this.position, "Lexer error - symbol: " +this.currentSymbol+(char)a+(char)buffer.read()+" Missing closing single quote ' " );
                          System.exit(1);
                     }else
                     if(a==10)//If string is multi-line throw error
                     {
                        Report.warning(this.line+1, this.position, "Lexer error - symbol: " +this.currentSymbol+(char)a+(char)buffer.read()+" String is multi-line ' " );
                        System.exit(1);
                     }else{
                        this.currentSymbol=this.currentSymbol+(char)a;//Normal primary string processing
                        continue start1;
                     }
                  
                 }else
                     
                 //SECONDARY STRING PROCESSING ( "string" ) - LEGACY, could be mistake
                     
//                 if(a==34 && this.phase.equals("waiting")){//If we hit " begining of string in waiting mode 
//                    this.phase="processingstr2";
//                    this.currentSymbol=this.currentSymbol+(char)a;
//                    continue start1; 
//                 }else
//                 
//                 if(this.phase.equals("processingstr2") && a==34){//If we hit " end of string
//                    this.currentSymbol=this.currentSymbol+(char)a;
//                    return IsString(this);
//                 }else
//                     
//                 if(this.phase.equals("processingstr2")){//If we're just processing a string
//                      if(a==-1){//If we reach end of file without " throw error
//                         System.err.printf("Lexer error - missing closing double quote \" \n" );
//                     }else{
//                        this.currentSymbol=this.currentSymbol+(char)a;//Normal secondary string processing
//                        continue start1;
//                     }
//                 }else
                //STRING PROCESSING - END
                
                //OTHER PROCESSING
                if(false){//In case more ifs are needed
                    
                }
               
                
               
              
                
                if((a==-1)){ //End of file situation
                    if(this.phase.equals("waiting")){
                        return new Symbol(0,"",this.line,this.position,this.line,this.position);
                    }else
                        
                    if(this.phase.equals("processing"))
                    {
                        return IsReservedWord(this);
                        
                    }else
                        
                    if(this.phase.equals("processingint")){
            
                        return IsInteger(this);
                    }
                    
                }
                    
                if((a<=32) && this.phase.equals("waiting") && (a!=-1)){ //If is whitespace, while waiting
                    continue start1;
                }else if(((a>32) && (a!=-1))){
                    if(IsOther(this)){
                         continue start2;
                    }
                   
                }
                    
                    Report.warning(this.line+1, this.position, "Lexer error - symbol: " +this.currentSymbol+(char)a+(char)buffer.read()+" Phase: "+this.phase );
                    System.exit(1);
                   }
                
                  
                  
                }
           }catch(Exception exc){  
               System.out.println(exc.fillInStackTrace());
               for( Object x : exc.getStackTrace()){
                   System.out.println(x);
               }
           System.err.println("Error - "+exc);
           System.exit(1);
           return new Symbol(-2,"ERR",this.line,this.position,this.line,this.position); }
           
                   
	          
                   
           
	}

	/**
	 * Izpise simbol v datoteko z vmesnimi rezultati.
	 * 
	 * @param symb
	 *            Simbol, ki naj bo izpisan.
	 */
	private void dump(Symbol symb) {
		if (! dump) return;
		if (Report.dumpFile() == null) return;
		if (symb.token == Token.EOF)
			Report.dumpFile().println(symb.toString());
		else
			Report.dumpFile().println("[" + symb.position.toString() + "] " + symb.toString());
	}

    private Symbol IsReservedWord(LexAn aThis) {
       for(int k = 0;k<aThis.keywords.length;k++){ //Keyword check
           if(aThis.keywords[k].equals(aThis.currentSymbol)){
              
                Symbol si = new Symbol(k+33,this.currentSymbol,this.line,this.position-this.currentSymbol.length(),this.line,this.position);
                dump(si);
                this.currentSymbol="";
                return si;
           }
       }
       
       for(int k=0;k<aThis.datatypes.length;k++){ //Datatypes check
           if(aThis.datatypes[k].equals(aThis.currentSymbol)){
               
                Symbol si = new Symbol(k+30,this.currentSymbol,this.line,this.position-this.currentSymbol.length(),this.line,this.position);
                dump(si);
                this.currentSymbol="";
                return si;
           }
       }
       
       if(aThis.currentSymbol.equals("true") || aThis.currentSymbol.equals("false")){ // Logical constant check
           Symbol si = new Symbol(2,this.currentSymbol,this.line,this.position-this.currentSymbol.length(),this.line,this.position);
           dump(si);
           this.currentSymbol="";
           return si;
       }
           
       
       
       
     
       
     
       
       Symbol si = new Symbol(1,this.currentSymbol,this.line,this.position-this.currentSymbol.length(),this.line,this.position);
       dump(si);
       this.currentSymbol="";
       return si;
       
    }

    private boolean IsOther(LexAn aThis) {
        Symbol si;
        
        try{
            this.buffer.mark(5);
            
        
        
        while(true){
        switch(a){
            //!
         case 33:
            if(this.buffer.read()==61){//Check if the symbol is actually !=
                this.position++;
                this.charsRead++;
                si = new Symbol(9,"!=",this.line,this.position,this.line,this.position+1);
                a=32;
                flushList.add(si);
                return true;
             }else{//Return to ! if it is not
                si = new Symbol(7,"!",this.line,this.position-1,this.line,this.position);
                this.buffer.reset();
                a=32;
                flushList.add(si);
                return true;
             }
            //%
         case 37:
            si = new Symbol(16,"%",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
            //& 
         case 38:
            si = new Symbol(5,"&",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
            //(
         case 40:
            si = new Symbol(19,"(",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
            //)
         case 41:
            si = new Symbol(20,")",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
            //*
         case 42:
            si = new Symbol(14,"*",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
            //+
         case 43:
              si = new Symbol(17,"+",this.line,this.position-1,this.line,this.position);
              a=32;
              flushList.add(si);
              return true;
            // , 
         case 44:
            si = new Symbol(28,",",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
           // - 
         case 45:
              si = new Symbol(18,"-",this.line,this.position-1,this.line,this.position);
              a=32;
              if(!flushList.isEmpty()){//Ugly fix for bug of more -, ex. ,,.,.,.,,,.,.,.,.
              
              }else{
                  flushList.add(si);
              }

              return true;
           // .  
         case 46:
            si = new Symbol(25,".",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
           // /  
         case 47:
            si = new Symbol(15,"/",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
           // :  
         case 58:
            si = new Symbol(26,":",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
           // ;  
         case 59:
            si = new Symbol(27,";",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
           // <  
         case 60:
            if(this.buffer.read()==61){//Check if the symbol is actually <=
                this.position++;
                this.charsRead++;
                si = new Symbol(12,"<=",this.line,this.position,this.line,this.position+1);
                a=32;
                flushList.add(si);
                return true;
             }else{//Return to < if it is not
                si = new Symbol(10,"<",this.line,this.position-1,this.line,this.position);
                this.buffer.reset();
                a=32;
                flushList.add(si);
                return true;
             }
            
           // =  
         case 61:
             if(this.buffer.read()==61){//Check if the symbol is actually ==
                this.position++;
                this.charsRead++;
                si = new Symbol(8,"==",this.line,this.position,this.line,this.position+1);
                a=32;
                flushList.add(si);
                return true;
             }else{//Return to = if it is not
                si = new Symbol(29,"=",this.line,this.position-1,this.line,this.position);
                this.buffer.reset();
                a=32;
                flushList.add(si);
                return true;
             }
            
           // >  
         case 62:
            if(this.buffer.read()==61){//Check if the symbol is actually >=
                this.position++;
                this.charsRead++;
                si = new Symbol(13,">=",this.line,this.position,this.line,this.position+1);
                a=32;
                flushList.add(si);
                return true;
             }else{//Return to = if it is not
                si = new Symbol(11,">",this.line,this.position-1,this.line,this.position);
                this.buffer.reset();
                a=32;
                flushList.add(si);
                return true;
             }
           // [  
         case 91:
            si = new Symbol(21,"[",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
           // ]  
         case 93:
            si = new Symbol(22,"]",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
           // {  
         case 123:
            si = new Symbol(23,"{",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
           // |  
         case 124:
            si = new Symbol(6,"|",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
           // }  
         case 125:
            si = new Symbol(24,"}",this.line,this.position-1,this.line,this.position);
            a=32;
            flushList.add(si);
            return true;
             
         default:
            return false;
                 
        }
        }
        }catch(Exception exc){return false;}
    }


    private Symbol IsInteger(LexAn aThis) {
                Symbol si = new Symbol(3,this.currentSymbol,this.line,this.position-this.currentSymbol.length(),this.line,this.position);
                dump(si);
                this.currentSymbol="";
                this.phase="waiting";
                return si;
    }

    private Symbol IsString(LexAn aThis) {
                Symbol si = new Symbol(4,this.currentSymbol,this.line,this.position-this.currentSymbol.length(),this.line,this.position);
                dump(si);
                this.currentSymbol="";
                this.phase="waiting";
                return si;
    }
    
 

}

