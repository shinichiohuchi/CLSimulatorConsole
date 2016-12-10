package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import strategy.BracketStrategy;
import strategy.VariableStrategy;

/**
 * コンビネータ論理計算を行うクラス。
 * @author Shinichi Oouchi
 * @version 3.1
 */
public class Code {
  /**
   * マクロ関数のリスト。
   */
  static List<MacroCombinator> macroFunctionList;

  /**
   * 先頭のコンビネータが引数不足に直面するまでループするのを制御するためのスイッチ。
   */
  boolean loopSwitch = true;

  /**
   * 括弧の中にもぐっている数。
   */
  public static int nestCount = 0;

  /**
   * 計算に用いる可変長コンビネータ論理文字列。
   */
  private StringBuilder codeSb;

  /**
   * CodeSbの先頭に存在するコンビネータ論理項を取り出した物。
   */
  private String clterm;

  /**
   * 変数の正規表現パターン
   */
  private static final Pattern variablePattern = Pattern.compile("^[a-z][_0-9]*");

  /**
   * コンストラクタ。
   * @param string コンビネータ論理文字列
   */
  public Code(String string) {
    codeSb = new StringBuilder(string);
  }

  /**
   * コンビネータ定義ファイルの読み込み。
   * ファイルが存在しなかった場合は
   * SKIBCコンビネータのみ定義したファイルを出力して読み込む。
   */
  static {
    File file = new File("Combinators.csv");
    if (!file.exists()) {
      // ファイルが存在しなかった場合、
      // SKIBCコンビネータのみ定義した関数ファイルを出力する。
      try (PrintWriter pw = new PrintWriter(
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")))) {
        pw.println("# --------------------------------------------------");
        pw.println("# << Definition of Combinators >>");
        pw.println("# CombinatorName, ArgsCount, Format");
        pw.println("# --------------------------------------------------");
        pw.println("S, 3, 02(12)");
        pw.println("K, 2, 0");
        pw.println("I, 1, 0");
        pw.println("B, 3, 0(12)");
        pw.println("C, 3, 021");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Path path = file.toPath();
    try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
      macroFunctionList = br.lines()
          .filter(l -> !l.startsWith("#") && l.length() != 0)
          .map(m -> m.replaceAll("[ ||　||\t]", "").split(","))
          .map(MacroCombinator::new)
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 括弧の数が等しければ、trueを返す。
   * @param code 調べる対象の文字列
   * @return
   *         括弧の数が0で終了した場合、trueを返す。<br>
   *         括弧の数が0で終了しなかった場合、falseを返す。<br>
   *         括弧の数が0を下回った場合、falseを返す。
   */
  final boolean hasSameBracketCount() {
    int count = 0;
    String code = new String(codeSb);
    for (char ch : code.toCharArray()) {
      if (ch == '(') {
        count++;
      }
      if (ch == ')') {
        count--;
      }
      if (count < 0) {
        return false;
      }
    }
    if (count == 0) {
      return true;
    }
    return false;
  }

  /**
   * CLTermの数を数える
   */
  final int getCLTermCount() {
    int clTermCount = 0;
    StringBuilder copySb = new StringBuilder(codeSb);
    while (true) {
      if (0 < codeSb.length()) {
        pollCLTerm();
        clTermCount++;
      } else {
        break;
      }
    }
    codeSb = new StringBuilder(copySb);
    return clTermCount;
  }

  /**
   * 先頭の括弧でくくられた項を返す(括弧自体も含む)。
   * @param target 対象文字列
   * @return 括弧で括られた文字列
   */
  private final String getCompoundTerm(String target) {
    return getCompoundTerm(target, 0);
  }

  /**
   * indexの位置から開始する括弧で括られた項を返す(括弧自体も含む)。
   * @param target 対象文字列
   * @param index 括弧の開始位置
   * @return 括弧で括られた文字列
   */
  private final String getCompoundTerm(String target, int index) {
    int k = 0;
    StringBuilder sb = new StringBuilder();
    for (char ch : target.toCharArray()) {
      k = ch == '(' ? ++k : k;
      k = ch == ')' ? --k : k;
      sb.append(ch);
      // ')'を見つけ、最終的にネストの回数が0になった時、括弧の終わりと判定し、ループを抜ける。
      if (k == 0) {
        break;
      }
    }
    return new String(sb);
  }

  /**
   * コンビネータ論理項(CLTerm)を返す。
   * 取り出すCLTermはそれぞれ以下のいずれかである。
   *
   * {@literal @formatter:off}<br>
   *
   * <ul>
   *   <li><p> 1. 括弧で括られた括弧を含む文字列</p></li>
   *   <li><p> 2. 小文字1つで始まる、アンダースコアや数字が0個以上連続する文字列</p></li>
   *   <li><p> 3. テキストファイルで定義したマクロ関数の名前と一致する文字列</p></li>
   * </ul>
   *
   * {@literal @formatter:on}<br>
   *
   * 上記のいずれとも一致しない文字列の場合は、未定義のコンビネータとして、警告文を表示する。
   * @param target 対象文字列
   * @return CLTerm
   */
  private final String getCLTerm() {
    String code = new String(codeSb);
    // コードの先頭の文字を取り出して判定
    char top = code.charAt(0);
    if (top == '(') {
      // 括弧の場合
      return getCompoundTerm(code);
    }
    if (Character.isLowerCase(top)) {
      // 小文字で始まる変数項の場合
      Matcher m = variablePattern.matcher(code);
      if (m.find()) {
        return m.group();
      }
    }
    for (MacroCombinator macro : macroFunctionList) {
      // マクロ定義した関数の場合
      String functionName = macro.name;
      if (code.startsWith(functionName)) {
        return code.substring(0, functionName.length());
      }
    }
    // 未定義の関数の場合
    String sub = code.substring(0, 1);
    return sub;
  }

  /**
   * CLTermをcodeSbから取り出す。
   * @return 取り出したCLTerm
   */
  public final String pollCLTerm() {
    if (0 < codeSb.length()) {
      String clt = getCLTerm();
      codeSb.delete(0, clt.length());
      return clt;
    }
    return "";
  }

  /**
   * 計算中のプログラムの先頭の文字列が、マクロで定義した関数名と一致するかどうかを調べ、
   * 一致した場合はマクロ関数を実行する。
   * 一致しなかった場合は、括弧で始まる場合か、変数の場合であると判定する。
   * 最終的にloopSwitchをfalseにし、計算用に文字列を取り出した場合は復元して終了する。
   */
  private final void functionDo() {
    clterm = pollCLTerm();

    String result = "";
    for (MacroCombinator macro : macroFunctionList) {
      if (clterm.startsWith(macro.name)) {
        // result = macro.calculate(this);
        result = new MacroCombinator(macro).calculate(this);
        codeSb.insert(0, result);
        return;
      }
    }

    if (0 < clterm.length()) {
      if (clterm.startsWith("(")) {
        result = new BracketStrategy(this).calculate(clterm);
      }
      if (clterm.startsWith("[a-z]")) {
        result = new VariableStrategy(this).calculate(clterm);
      }
      codeSb.insert(0, result);
    }
    loopSwitch = 0 < result.length();
  }

  /**
   * コンビネータ論理で計算を行う。
   */
  public final void calculate() {
    while (loopSwitch) {
      ConsoleOut.printCode(this);
      functionDo();
      ConsoleOut.waitCalculate();
    }
    codeSb.insert(0, clterm);
  }

  /**
   * コードを返す。
   * @return 保持するCLCode
   */
  public final String getCode() {
    return new String(codeSb);
  }

  /**
   * 未定義のコンビネータを持つかどうかを調べる。
   * @return
   */
  final boolean hasUndefinedCombinator() {
    String code = new String(codeSb);
    // コードの先頭の文字を取り出して判定
    char top = code.charAt(0);
    if (top == '(') {
      return false;
    }
    if (Character.isLowerCase(top)) {
      // 小文字で始まる変数項の場合
      Matcher m = variablePattern.matcher(code);
      if (m.find()) {
        return false;
      }
    }
    for (MacroCombinator macro : macroFunctionList) {
      // マクロ定義した関数の場合
      String functionName = macro.name;
      if (code.startsWith(functionName)) {
        return false;
      }
    }
    // 未定義の関数の場合
    return true;
  }
}
