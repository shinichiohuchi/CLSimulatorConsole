package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * コンソールからキーボード入力を促す際の汎用メソッドクラス。
 * @author Shinichi Oouchi
 * @version 1.1
 */
public class UtilKeyInput {
  private static final InputStreamReader isr = new InputStreamReader(System.in);
  private static final BufferedReader br = new BufferedReader(isr);

  /**
   * InputStreamReaderとBufferedReaderをクローズする。
   */
  public static final void closeStream() {
    try {
      br.close();
      isr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * キーボード入力を呼び出す。
   * @param text 入力するキー内容の説明用テキスト
   * @return 入力された文字列
   */
  public static final String inputKey(String text) {
    System.out.println(text);
    String input = null;
    try {
      input = br.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return input;
  }

  /**
   * yesかnoかの入力(y/n)を促す。<br>
   * どちらでもない場合は再入力を促す。
   * @param text コンソールで説明する際に表示するテキスト
   * @return y/nに対応した真偽値
   */
  public static final boolean inputSwitchOption(String text) {
    while (true) {
      String input = inputKey(text);
      if ("y".equals(input)) {
        return true;
      } else if ("n".equals(input)) {
        return false;
      } else {
        showReEnter();
      }
    }
  }

  /**
   * キーボードから整数入力を促す。<br>
   * 入力された値が整数でない場合は再入力を促す。
   * @param text 入力するキー内容の説明用テキスト
   * @return 入力された整数
   */
  public static final int inputNumber(String text) {
    while (true) {
      String input = inputKey(text);
      if (input.matches("^[0-9]*")) {
        return Integer.parseInt(input);
      } else {
        showReEnter();
      }
    }
  }

  /**
   * 入力したキーが適当でなかった場合に表示するテキスト。
   */
  private static final void showReEnter() {
    System.out.println("Input is wrong.");
    System.out.println("Please Re-Enter.");
  }
}
