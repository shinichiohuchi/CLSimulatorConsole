package strategy;

import main.Code;

public final class BracketStrategy extends Strategy {
  public BracketStrategy(Code aCode) {
    code = aCode;
  }

  /**
   * 文字列の先頭が'('だった場合に実行されるメソッド<br>
   * 括弧の中の文字列に対して計算を行い、計算が終了したものを返す
   */
  @Override
  public String calculate(String clterm) {
    // CLTermの一番外に存在する括弧の削除
    StringBuilder sb = new StringBuilder(clterm);
    sb.deleteCharAt(0);
    int length = sb.length();
    sb.deleteCharAt(length - 1);

    // 括弧内の文字列で再びCode.calculateを実行
    Code code2 = new Code(new String(sb));
    Code.nestCount++;
    code2.calculate();
    Code.nestCount--;
    String result = code2.getCode();
    return result;
  }
}
