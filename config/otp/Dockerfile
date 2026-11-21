# ビルド済みグラフ（graph.obj）を同梱した OTP イメージ
# 事前に data/graph.obj を生成しておくこと（CI: .github/workflows/publish-images.yml / ローカル: build-graph.sh）
# ビルドコンテキストは config/otp ディレクトリ
FROM docker.io/opentripplanner/opentripplanner:2.5.0
COPY data/graph.obj data/router-config.json data/build-config.json /var/opentripplanner/
EXPOSE 8080
CMD ["--load", "--serve"]
