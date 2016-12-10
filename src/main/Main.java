package main;

import static util.UtilKeyInput.closeStream;
import static util.UtilKeyInput.inputKey;
import static util.UtilKeyInput.inputSwitchOption;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * mainメソッド、および実行時のオプション設定を制御するクラス。<br>
 *
 * <p>コマンドライン引数の最初にファイル名を指定することで、
 * ファイルから1行ずつコンビネータ論理コードを取得し、実行する。<br>
 * ファイル名を指定しなかった場合や、存在しないファイルを指定した場合、
 * オプションのみを指定した場合はコンソールからコンビネータ論理コードを入力し、実行する。<br>
 * この時、オプション設定を省略した場合はデフォルトの設定が適用される。</p>
 *
 * <p>読み込むテキストファイルの先頭が"#"から始まる行は、コメント行として無視される。<br>
 * また、何も文字を記述していない空白行も同様に無視される。</p>
 *
 * {@literal @formatter:off}<br>
 *
 * 実行オプション:<br>
 * <ul>
 *   <li><p> -s[ilent]      - 計算プロセスを非表示にする。<br>
 *                            デフォルト: 表示</p></li>
 *   <li><p> -w[ait] number - 計算時にウェイトをはさむ。nubmerはミリ秒の整数。<br>
 *                            デフォルト: 0ミリ秒</p></li>
 *   <li><p> -l[ist]        - 計算時のコンビネータを表示する。<br>
 *                            デフォルト: 非表示</p></li>
 *   <li><p> -n[oindent]    - 括弧の中の計算に処理が移った際のコンソール表示でインデント整形を行わない。<br>
 *                            デフォルト: 整形する</p></li>
 * </ul>
 *
 * コンソール表示:<br>
 * <ul>
 *   <li><p> S[tep]         - 計算のステップ数。</p></li>
 *   <li><p> CLT[erm count] - 計算中のコンビネータコードの総数。</p></li>
 *   <li><p> N[est]         - 括弧の中にもぐっている数。</p></li>
 * </ul>
 *
 * {@literal @formatter:on}<br>
 *
 * @author Shinichi Oouchi
 * @version 1.0
 */
public final class Main {
  private static final String CODE = "Code   : ";
  private static final String RESULT = "Result: ";
  private static final String LINE = "------------------------------";

  /**
   * プログラム実行。
   * @param args コマンドライン引数
   */
  public static void main(String[] args) {
    ConsoleOut.setCommandLineOptions(args);

    if (0 < args.length) {
      File file = new File(args[0]);
      if (file.exists()) {
        // コマンドライン引数にファイル名を指定した場合
        // 引数のテキストファイルから1行ずつCLコードを読み込み実行する
        Path path = file.toPath();
        try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
          br.lines()
              .filter(l -> !l.startsWith("#") && l.length() != 0)
              .forEach(Main::executeCode);
        } catch (IOException e) {
          e.printStackTrace();
        }
        return;
      }
    }
    // ファイル指定がなかった場合
    String clCode = inputKey("Enter the Combinator Logic Code.");
    executeCode(clCode);
    closeStream();
  }

  /**
   * コンビネータ論理計算を実行する。
   * @param clcode コンビネータ論理コード
   */
  private final static void executeCode(String clcode) {
    Code code = new Code(clcode);

    if (code.hasSameBracketCount()) {
      // 括弧の数が等しく、正常に動作するコードの場合
      doCalculation(code);
    } else {
      // 括弧の数が不等で、正常に動作しないと思われるコードの場合
      System.out.println("Count of bracket is not correct.");

      boolean executeSwitch = inputSwitchOption("Forced to run the program? [y/n]");
      if (executeSwitch) {
        doCalculation(code);
      } else {
        System.out.println("Finish to calculate.");
      }
    }
  }

  /**
   * 計算を実行する。<br>
   * 読み込んだコードと計算結果も出力する。
   * @param aCode コンビネータ論理コード
   */
  private static final void doCalculation(Code code) {
    System.out.println(CODE + code.getCode());
    code.calculate();
    System.out.println(RESULT + code.getCode());
    ConsoleOut.printWarning(code);
    System.out.println();
    System.out.println(LINE);
    ConsoleOut.stepCount = 0;
  }
}
