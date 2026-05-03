#!/bin/bash
set -e

# スクリプト自身のディレクトリを基準にパスを解決する
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# プロジェクトルートの .env を読み込む（存在する場合のみ）
ROOT_DIR="$(cd "${SCRIPT_DIR}/../.." && pwd)"
if [ -f "$ROOT_DIR/.env" ]; then
  set -a
  # shellcheck source=/dev/null
  source "$ROOT_DIR/.env"
  set +a
fi

docker run --rm \
  -e JAVA_TOOL_OPTIONS="-Xmx${OTP_HEAP_SIZE:-3g}" \
  -v "${SCRIPT_DIR}/data":/var/opentripplanner \
  docker.io/opentripplanner/opentripplanner:2.5.0 \
  --build --save
