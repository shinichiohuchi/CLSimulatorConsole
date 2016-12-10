package strategy;

import main.Code;

/**
 * 変数関数クラス。<br>
 * 変数の条件は<br>
 * 1. 小文字で始まる。
 * 2. 数字や'_'を使うことができる。
 * 3. 小文字の'h'を使うことはできない。
 */
public final class VariableStrategy extends Strategy {
  public VariableStrategy(Code aCode) {
    code = aCode;
  }

  /**
   * 文字列の先頭が変数だった場合に実行されるメソッド<br>
   * .
   * 変数が見つかった場合、何も行わずにループを抜け出す。
   */
  @Override
  public String calculate(String clterm) {
    return "";
  }
}
