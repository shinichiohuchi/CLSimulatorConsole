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

##Overview

##Usage
1. Type "java -jar clsc.jar FileName.txt -options" on terminal.

2. FileName.txt is CLCode file.

   Format is this.
   # comment is usable.
   Sxyz
   # empty line is usable

3. -options is this.

   -s[ilent] : Show only calculation result.

   -w[ait] number : Wait number millstime every calculation step.

   -l[ist] : Show list that combinator has.

   -n[oindent] : Hide indent format.
