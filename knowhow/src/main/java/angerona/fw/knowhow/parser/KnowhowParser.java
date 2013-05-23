/* Generated By:JavaCC: Do not edit this line. KnowhowParser.java */
package angerona.fw.knowhow.parser;

import java.io.StringReader;
import java.util.*;

import angerona.fw.knowhow.KnowhowStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.tweety.logicprogramming.asplibrary.syntax.*;

@SuppressWarnings("all")
public class KnowhowParser implements KnowhowParserConstants {
  private Logger LOG = LoggerFactory.getLogger(KnowhowParser.class);

  public KnowhowParser(String expr)
  {
    this(new StringReader(expr));
  }

  public static void main(String args []) throws ParseException
  {
        String expr = "(bel(Y, X), revisionRequest(Y, X), reason(X)) (excused, bel(Boss, excused), ) (attend_scm, excused, )";
        //String expr = "(target, (subtarget1, subtarget2), condition)";
    System.out.println("Using expresion :" + expr);

        KnowhowParser parser = new KnowhowParser(expr);
    try
    {
          List<KnowhowStatement > lst = parser.start();
          System.out.println("Parsing done...");
          System.out.println(lst.toString());
    }
    catch (Exception e)
    {
      System.out.println("NOK.");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    catch (Error e)
    {
      System.out.println("Oops.");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  final public List<KnowhowStatement> start() throws ParseException {
  KnowhowStatement stmt = null;
  List<KnowhowStatement > reval = new LinkedList<KnowhowStatement >();
    label_1:
    while (true) {
      jj_consume_token(LBRACE);
      stmt = statement();
     reval.add(stmt);
      jj_consume_token(RBRACE);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LBRACE:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
    }
    {if (true) return reval;}
    throw new Error("Missing return statement in function");
  }

  final public KnowhowStatement statement() throws ParseException {
  KnowhowStatement stmt = null;
  String elpStr = null;
  ELPAtom target = null;
  List<String > strings = null;
  Vector<ELPAtom> subtargets = new Vector<ELPAtom >();
  Vector<ELPAtom> conditions = new Vector<ELPAtom >();
    elpStr = elpString();
    target = new ELPAtom(elpStr + ".");
    LOG.debug("Target: " + elpStr);
    jj_consume_token(COMMA);
    strings = elpStringList();
      for(String str : strings) {
        subtargets.add(new ELPAtom(str + "."));
      }
      LOG.debug("Subtargets: " + subtargets);
    jj_consume_token(COMMA);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LBRACE:
    case CHAR:
    case UCHAR:
    case TEXT:
      strings = elpStringList();
      for(String str : strings) {
        conditions.add(new ELPAtom(str + "."));
      }
      LOG.debug("Condition: " + conditions);
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    {if (true) return new KnowhowStatement(target, subtargets, conditions);}
    throw new Error("Missing return statement in function");
  }

  final public List<String > elpStringList() throws ParseException {
  List<String > reval = new LinkedList<String >();
  String elpStr = "";
  Token token = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LBRACE:
      jj_consume_token(LBRACE);
      elpStr = elpString();
                  reval.add(elpStr);
          LOG.debug("ElpStringList found 1/n string: " + elpStr);
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[2] = jj_gen;
          break label_2;
        }
        jj_consume_token(COMMA);
        elpStr = elpString();
                    reval.add(elpStr);
                LOG.debug("ElpStringList found k/n string: " + elpStr);
      }
      jj_consume_token(RBRACE);
      break;
    case CHAR:
    case UCHAR:
    case TEXT:
      elpStr = elpString();
          reval.add(elpStr);
          LOG.debug("ElpStringList found one string: " + elpStr);
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return reval;}
    throw new Error("Missing return statement in function");
  }

  final public String elpString() throws ParseException {
  String reval = "";
  Token token = null;
  String elpStr = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CHAR:
      token = jj_consume_token(CHAR);
      break;
    case UCHAR:
      token = jj_consume_token(UCHAR);
      break;
    case TEXT:
      token = jj_consume_token(TEXT);
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    reval += token.image;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LBRACE:
      jj_consume_token(LBRACE);
      elpStr = elpString();
      reval += "(" + elpStr;
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[5] = jj_gen;
          break label_3;
        }
        jj_consume_token(COMMA);
        elpStr = elpString();
        reval += "," + elpStr;
      }
      jj_consume_token(RBRACE);
      reval += ")";
      break;
    default:
      jj_la1[6] = jj_gen;
      ;
    }
    {if (true) return reval;}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public KnowhowParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[7];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x100,0x5900,0x40,0x5900,0x5800,0x40,0x100,};
   }

  /** Constructor with InputStream. */
  public KnowhowParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public KnowhowParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new KnowhowParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public KnowhowParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new KnowhowParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public KnowhowParser(KnowhowParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(KnowhowParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[15];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 7; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 15; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
