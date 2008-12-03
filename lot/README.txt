
--------------------------------------------------------------------------------
			Localization Tools by dynamis
--------------------------------------------------------------------------------

If you cannot read japanese, try following commands:
 lot> ant
 lot> ant -p
 lot> ant examples
 lot> ant help -Dlang=english
and read the source code.


directory structure and file information:

build.xml			main build script file
build.*.xml			sub build script files
compare-locales.pl		perl script to compare en-US and l10n files
comm-central/			to be generated with: hg clone http://hg.mozilla.org/comm-central/
comm-central/mozilla		to be generated with: python comm-central/client.py checkout
config/				directory containing configuration files
config/example.of.user.conf	rename to user.conf to overwrite default properties
dest/				to be generated with packaging targets (destination directory of packages)
l10n/				directory to put l10n files for l10n-central
l10n/en-US			to be generated with en-US-to-l10n target (from mozilla-central/comm-central)
l10n/ja				directory to which hg clone http://hg.mozilla.org/l10n-central/ja
l10n/ja-JP-mac			directory to which hg clone http://hg.mozilla.org/l10n-central/ja-JP-mac
LpDiff.jar			diff tool for local files
README.txt			this file
res/				directory containing resource files for lot (need not edit)
src/				directory containing l10n src files
src/l10n			to be generated with: svn checkout svn+ssh://svn.mozilla.l10n.jp/usr/local/var/svn/l10n/trunk src/l10n
src/langpack			directory containing files used to create langpacks
temp/				temporary directory used with lot



--------------------------------------------------------------------------------
			Guide for Japanese Localizers
--------------------------------------------------------------------------------
Sorry, this section is in Japanese only.


Localization Tools (lot) を使って L10N 作業をするための簡単な流れを紹介します。
# あくまでも lot 環境下に全てのファイルをおいて作業したい場合の手順です。
# ant convert による L10N ファイルの変換以外は lot を使わなくても構いません。


# ~/.bash_profile, ~/.bashrc などで環境設定
# Mac OS 10.4 などで ant の出力が文字化けする場合は UTF8 を指定する
export ANT_OPTS=-Dfile.encoding=UTF8

# ~/.hgrg で Mercurial (HG) の設定
# HG を使う場合はユーザ、文字コード、diff オプション、ssl について設定
# https://developer.mozilla.org/en/L10n_on_Mercurial
# http://developer.mozilla.org/ja/docs/Mercurial_basics
[ui]
username = name <user@example.jp>

[defaults]
add    =  --encoding=utf-8
clone  = --encoding=utf-8
commit = --encoding=utf-8
init   = --encoding=utf-8
pull   = --encoding=utf-8
push   = --encoding=utf-8
remove = --encoding=utf-8
revert = --encoding=utf-8
update = --encoding=utf-8
diff   = -p -U 8

[web]
push_ssl = false


# ----- ここまでは設定ファイルの編集 ----- ここからは実行するコマンド -----

# lot の取得 (既に取得済みの場合は不要)
svn export http://svn.mozilla.l10n.jp/trunk/lot
cd lot

# ja 言語リソースファイルの取得 (SVN サーバのアカウントを持っている場合)
svn checkout svn+ssh://svn.mozilla.l10n.jp/usr/local/var/svn/l10n/trunk/ src/l10n/
# SVN サーバのアカウントを持っていない場合:
# svn checkout http://svn.mozilla.l10n.jp/trunk/ src/l10n/

# l10n-central リポジトリを clone (L10N HG にコミットしない場合は不要)
mkdir l10n
hg clone http://hg.mozilla.org/l10n-central/ja l10n/ja
hg clone http://hg.mozilla.org/l10n-central/ja-JP-mac l10n/ja-JP-mac
# 各レポジトリの .hg/hgrc の [path] セクションで push 時には ssh を使うように指定:
# default-push = ssh://hg.mozilla.org/l10n-central/ja

# comm-central/mozilla-central リポジトリを clone (en-US のファイルを参照せずに作業する場合は不要)
hg clone http://hg.mozilla.org/comm-central/
python comm-central/client.py checkout
# mobile-browser リポジトリを clone (Fennec の L10N をしない場合は不要)
hg clone http://hg.mozilla.org/mobile-browser



# ----- ここまでは初回のみ必要な準備 ----- ここからは毎回の更新処理 -----

# 最新の ja 言語リソースファイルと lot を取得
ant update
# あるいは svn update と src/l10n/lot で現在のディレクトリ上書きを個別に
# svn update src/l10n
# ant update-lot

# 古い en-US ファイルを取得 (en-US の差分を生成しない場合は不要)
# 対象リビジョンは前回のコミットログや後述の hg log コマンドなどで確認する
# 差分をとる古いリビジョン(前回作業時のリビジョン)を {MOZILLA_REV_OLD}, {COMM_REV_OLD} とする
python comm-central/client.py --mozilla-rev={MOZILLA_REV_OLD} --comm-rev={COMM_REV_OLD} checkout

# 言語リソースファイルを l10n HG ディレクトリ構造で l10n/en-US-revXXXXX+XXXX にコピー
ant en-US-to-l10n
mv l10n/en-US l10n/en-US-rev{MOZILLA_REV_OLD}+{COMM_REV_OLD}

# 最新の en-US ファイルを取得
python comm-central/client.py checkout

# 最新の en-US リビジョン確認
# ここで確認した最新リビジョンを {MOZILLA_REV_TIP}, {COMM_REV_TIP} とする
hg log -r tip comm-central/
hg log -r tip comm-central/mozilla
# コミットログなど確認せずにリビジョン番号だけを取得するには:
# hg log -r tip --template "{rev}\n" comm-central
# hg log -r tip --template "{rev}\n" comm-central/mozilla

# en-US 言語リソースファイルを l10n HG ディレクトリ構造で l10n/en-US にコピー
ant en-US-to-l10n
cp -R l10n/en-US l10n/en-US-rev{MOZILLA_REV_TIP}+{COMM_REV_TIP}

# en-US 言語リソースファイルの差分を生成
diff -ru l10n/en-US-rev{MOZILLA_REV_OLD}+{COMM_REV_OLD} l10n/en-US-rev{MOZILLA_REV_TIP}+{COMM_REV_TIP} > rev{MOZILLA_REV_OLD}-rev{MOZILLA_REV_TIP}.diff

# en-US の差分を見ながら ja を更新

# src/ja から l10n/ja, l10n/ja-JP-mac を生成
ant convert
# en-US との過不足チェック
ant compare-locales -Dlocale=ja
ant compare-locales -Dlocale=ja-JP-mac

# 言語パックを生成して動作チェック
ant auto buildfx
ant auot buildtb

# 問題なければ差分を確認して L10N SVN にコミット
cd src/l10n
svn up
svn status
svn diff
# en-US との同期をするときは対象リビジョンをコミットログに明記
svn commit -m "sync with en-US rev{MOZILLA_REV_TIP}+{COMM_REV_TIP}"
cd ../..

# 更に L10N HG にもコミット＆プッシュ
cd l10n/ja
hg pull
hg up
hg status
hg diff
hg commit -m "sync with en-US rev{MOZILLA_REV_TIP}+{COMM_REV_TIP}"
hg push
cd ../ja-JP-mac
hg pull
hg up
hg status
hg diff
hg commit -m "sync with en-US rev{MOZILLA_REV_TIP}+{COMM_REV_TIP}"
cd ../..

# l10n dashboard や tinderbox をチェック
# http://l10n.mozilla.org/dashboard/?locale=ja
# http://tinderbox.mozilla.org/Mozilla-l10n-ja/
# http://l10n.mozilla.org/dashboard/?locale=ja-JP-mac
# http://tinderbox.mozilla.org/Mozilla-l10n-ja-JP-mac/


