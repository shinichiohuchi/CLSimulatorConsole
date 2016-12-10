package strategy;

import main.Code;

/**
 * 例外を処理するための戦略クラス。
 */
public abstract class Strategy {
  protected Code code;

  public Strategy() {
  }

  public abstract String calculate(String clterm);
}
