#CLSimulatorConsole マニュアル

バージョン: 3.1  
作者: 大内真一  
作成日: 2016/12/11  
最終更新日: 2016/12/11  
実行ファイル名: CLSC.jar  
動作確認  
* OS:  
  Windows10 Pro 64bit  
  LinuxBean12.04
* プロセッサ:  
  2.00GHz Intel Core i7-3667U  
* メモリ:  
  8GB RAM  
* Javaバージョン:  
  1.8.0-111  

##概要
CLSimulatorConsoleはコンビネータ文字列(以下CLCode)を計算して表示するためのソフトウェアです。

コマンドライン引数からCLCodeを記述したファイルを渡すことで計算をします。  
または、ファイルを渡さなかった場合はキーボードからCLCodeの入力を促します。

コンビネータは実行ファイルと同階層のCombinators.csvというファイルから読み込まれます。  
このファイルが存在しなかった場合、初期でSKIBCの5つのコンビネータのみ定義したファイルを生成し、読み込みます。  
このファイルを編集することで新たにコンビネータを追加するが可能です。

##使い方
1. ターミナルから"java -jar CLSC.jar FileName.txt [-options]"と入力してください。  
 -optionsは後述のオプション一覧から確認してください。
2. FileName.txtには1行に1つのCLCodeを記述します。  
 読み込むファイルには以下の制約があります。

    1. '#'で始まる行はコメント行として無視されます。  
     ただし、インラインコメントといった使用はできません。  
     # Sxyz -> xz(yz)  
    2. 改行のみの行は無視されます。

##オプション一覧
-s[ilent] : 計算結果のみを表示します。  
-w[ait] number : 計算ステップ間の待ち時間をミリ秒単位で指定します。  
-l[ist] : 計算中のコンビネータの保持するリストを表示します。  
-n[oindent] : インデント整形を行いません。
