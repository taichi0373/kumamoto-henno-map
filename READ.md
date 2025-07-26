・Webサーバの環境構築（ローカル）
1. XAMPP8.2.12 のダウンロード・起動
2. 本ディレクトリ（navi_project）を xampp/htdocs/ に配置する
3. phpmyadminでデータベース"navi_project"を作成後、sql/navi_project.sql のSQLファイルをインポート
※データベース名を変更する場合は php/db_connection.php の dbname も変更する


・経路探索サーバの環境構築
txt/otp.txt に記載


・GTFSデータ
バスきたくまさん：https://km.bus-vision.jp/kumamoto/view/opendataKuma.html
GTFSデータリポジトリ：https://gtfs-data.jp/search?pref=%E7%86%8A%E6%9C%AC%E7%9C%8C
研究用GTFSデータ公開ページ：https://gtfs-gis.jp/gtfs4research/


・pythonディレクトリについて
→ GTFS-JPデータの加工
※ GTFS-JPデータのフォーマット（バスきたくまさん）を全て新フォーマットに更新する
詳細：https://www.mlit.go.jp/sogoseisaku/transport/content/001418523.pdf


・php/vendor, .env, composer.json, composer.lock について
→ Yahoo!ローカルサーチAPIを使用して、さくらのVPS上で公開する際に、APIキーを直接コード内に記載しないために作成しています
ローカル環境でのみ使用する場合は、コード内に直接記述した方が無駄なエラーも起きないので良いと思います

・ライセンス・規約
研究用GTFSデータ公開ページ（JR九州・南阿蘇鉄道） → 調査・研究目的のみで使用可
Yahoo!ローカルサーチAPI → キャッシュ・保存の禁止（データベースへの格納不可）
熊本県HPの著作権・規約についての問い合わせ
>
>
本日県庁ホームページよりお問い合わせいただきました件で、ご連絡させていただきました。
「運転免許自主返納者への特典」につきましては、本県のくらしの安全課所管でしたので、確認いたしましたところ、利用について問題ないと確認が取れました。
ご質問３点について、以下の通り回答します。

1. 熊本県ホームページに掲載されている特典情報を卒業研究で開発するシステムにおいて利用することは可能でしょうか。　
    ⇒可能です。
2. 利用が可能な場合、必要な手続きや条件がございましたらご教示いただけますでしょうか。
    ⇒特にありません。
3. システム内で熊本県ホームページを出典として明記する際の注意点や指示がございましたらお知らせください。
    ⇒出典：熊本県ホームページ「運転免許証自主返納者への特典」のご紹介 https://www.pref.kumamoto.jp/soshiki/54/51729.html　など、利用のページ名を明記いただければと思います。
>
>
※OTP・GTFSデータ・API 等はオープンソースであっても、ライセンス・出典の記載や規約があるため各自で調べておくことを推奨します


・システム利用のアンケート調査結果
xlsx/アンケート調査結果.xlsx


・その他・備考
特典データは、基本的に毎年更新が必要です
コミュニティバスデータも、路線が変更・削除されている場合があるため確認した方が良いと思います
GTFS-JPデータには有効期限があるので更新が必要です（feed_info.txtの"feed_end_date"に記載されている）
Yahoo!ローカルサーチAPIの使用は以下URLから発行してください（クレカ登録は不要）
https://e.developer.yahoo.co.jp/dashboard/


・さくらのVPSでの初期設定 参考資料
https://zenn.dev/zurukumo/articles/8d9a654c2e1f72
https://sumire-server.blog.jp/archives/14443424.html
https://sumire-server.blog.jp/archives/13072494.html
