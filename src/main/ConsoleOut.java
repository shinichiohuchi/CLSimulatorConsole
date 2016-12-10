package main;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 計算過程のコンソール出力を管理するクラス。
 * @author Shinichi Oouchi
 * @version 1.0
 */
final class ConsoleOut {
  /**
   * 計算過程を表示するスイッチ。
   */
  private static boolean printSwitch = true;

  /**
   * コンビネータが保持するコンビネータのリストを表示するスイッチ。
   */
  private static boolean listSwitch = false;

  /**
   * インデント整形を行うスイッチ。
   */
  private static boolean indentSwitch = true;

  /**
   * 計算を追跡できるように挟むスリープタイム。
   */
  private static int waitTime = 0;

  /**
   * 計算のステップ数。
   */
  static int stepCount = 0;

  /**
   * コンストラクタを利用したスイッチの更新。
   * @param args
   */
  static final void setCommandLineOptions(String[] args) {
    List<String> argsList = Arrays.asList(args);

    printSwitch = (argsList.contains("-s") || argsList.contains("-silent")) ? false : true;
    listSwitch = (argsList.contains("-l") || argsList.contains("-list")) ? true : false;
    indentSwitch = (argsList.contains("-n") || argsList.contains("-noindent")) ? false : true;
    setWaitTime(argsList, "-w");
    setWaitTime(argsList, "-wait");
  }

  /**
   * 次の処理が実行されるまでの待ち時間を設定する。
   * @param argsList
   * @param options
   */
  private static final void setWaitTime(List<String> argsList, String options) {
    if (argsList.contains(options)) {
      int index = argsList.indexOf(options);
      index = Math.min(index + 1, argsList.size() - 1);
      String number = argsList.get(index);
      waitTime = number.matches("^[0-9]*") ? Integer.parseInt(number) : waitTime;
    }
  }

  /**
   * インデントを生成する。
   */
  private static final String makeIndent() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < Code.nestCount; i++) {
      sb.append("  ");
    }
    return new String(sb);
  }

  /**
   * 計算過程のコードを出力する。
   */
  static final void printCode(Code aCode) {
    if (printSwitch) {
      stepCount++;
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("S: %1$4d | ", stepCount));
      sb.append(String.format("CLT: %1$5d | ", aCode.getCLTermCount()));
      sb.append(String.format("N: %1$3d | ", Code.nestCount));
      String indent = indentSwitch ? makeIndent() : "";
      sb.append(String.format("Code: %s%s", indent, aCode.getCode()));
      System.out.println(new String(sb));
    }
  }

  /**
   * マクロ関数が保持しているリストを表示する。
   * @param list
   */
  static final void printList(LinkedList<String> list) {
    if (listSwitch) {
      System.out.println("Combinator list:              | List: " + list);
    }
  }

  /**
   * ウェイトを挟む。
   */
  static final void waitCalculate() {
    try {
      Thread.sleep(waitTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static final String WARNING = "<< WARNING >> ";

  /**
   * 警告文を表示する。
   * @param aCode
   */
  static final void printWarning(Code aCode) {
    if (!aCode.hasSameBracketCount()) {
      System.out.println(WARNING + "Count of bracket is not correct.");
    }
    if (aCode.hasUndefinedCombinator()) {
      System.out.println(WARNING + "Undefined Combinator exists.");
    }
  }

}
