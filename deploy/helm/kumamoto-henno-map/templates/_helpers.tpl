{{/*
リソース名の接頭辞
*/}}
{{- define "khm.fullname" -}}
{{- .Release.Name | trunc 50 | trimSuffix "-" -}}
{{- end -}}

{{/*
共通ラベル（アップグレードで変わるためセレクタには使わない）
*/}}
{{- define "khm.commonLabels" -}}
helm.sh/chart: {{ printf "%s-%s" .Chart.Name .Chart.Version }}
app.kubernetes.io/part-of: {{ .Chart.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{/*
コンポーネントに属さないリソースのラベル
*/}}
{{- define "khm.labels" -}}
{{ include "khm.commonLabels" . }}
app.kubernetes.io/name: {{ .Chart.Name }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{/*
コンポーネント単位のセレクタラベル
*/}}
{{- define "khm.selectorLabels" -}}
app.kubernetes.io/name: {{ .root.Chart.Name }}
app.kubernetes.io/instance: {{ .root.Release.Name }}
app.kubernetes.io/component: {{ .component }}
{{- end -}}

{{- define "khm.componentLabels" -}}
{{ include "khm.commonLabels" .root }}
{{ include "khm.selectorLabels" . }}
{{- end -}}

{{/*
イメージ参照（image.tag が空なら global.imageTag を使う）
*/}}
{{- define "khm.image" -}}
{{- $tag := default .root.Values.global.imageTag .image.tag -}}
{{- printf "%s:%s" .image.repository (required "global.imageTag（コミット SHA）を指定してください" $tag) -}}
{{- end -}}

{{/*
Secret 名
*/}}
{{- define "khm.secretName" -}}
{{- default (printf "%s-secret" (include "khm.fullname" .)) .Values.secrets.existingSecret -}}
{{- end -}}

{{- define "khm.imagePullSecrets" -}}
{{- with .Values.global.imagePullSecrets }}
imagePullSecrets:
{{- range . }}
  - name: {{ . }}
{{- end }}
{{- end }}
{{- end -}}

{{/*
DB 接続先（クラスタ内 / 外部）
*/}}
{{- define "khm.dbHost" -}}
{{- if .Values.db.enabled -}}
{{- printf "%s-db" (include "khm.fullname" .) -}}
{{- else -}}
{{- required "db.enabled=false のときは externalDb.host を指定してください" .Values.externalDb.host -}}
{{- end -}}
{{- end -}}

{{- define "khm.dbPort" -}}
{{- if .Values.db.enabled -}}5432{{- else -}}{{ .Values.externalDb.port }}{{- end -}}
{{- end -}}

{{- define "khm.dbName" -}}
{{- if .Values.db.enabled -}}{{ .Values.db.name }}{{- else -}}{{ .Values.externalDb.name }}{{- end -}}
{{- end -}}

{{- define "khm.dbUsername" -}}
{{- if .Values.db.enabled -}}{{ .Values.db.username }}{{- else -}}{{ .Values.externalDb.username }}{{- end -}}
{{- end -}}

{{- define "khm.dbSslParams" -}}
{{- if not .Values.db.enabled -}}{{ .Values.externalDb.sslParams }}{{- end -}}
{{- end -}}

{{/*
DB 初期化 SQL の ConfigMap キー（キーに "/" は使えないため "_" に置換）
*/}}
{{- define "khm.dbInitKey" -}}
{{- trimPrefix "files/database/" . | replace "/" "_" -}}
{{- end -}}
